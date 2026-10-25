package site.siredvin.digitalitems.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.nbt.CompoundTag
import net.minecraft.network.chat.Component
import net.minecraft.world.Container
import net.minecraft.world.MenuProvider
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.digitalitems.client.DigitizerMenu
import site.siredvin.digitalitems.common.setup.ModBlockEntityTypes
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral
import site.siredvin.digitalitems.data.ModText
import site.siredvin.tweakium.modules.peripheral.blockentity.MutablePeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner

class DigitizerBlockEntity(pos: BlockPos, state: BlockState) :
    MutablePeripheralBlockEntity<DigitizerPeripheral<BlockEntityPeripheralOwner<DigitizerBlockEntity>>>(ModBlockEntityTypes.DIGITIZER.get(), pos, state),
    Container,
    MenuProvider {

    companion object {
        private const val STORED_ITEM_STACK_TAG = "storedItemStack"
    }

    class ExtraSimpleStorage(private val blockEntity: DigitizerBlockEntity) : SimpleContainer(1) {
        override fun setChanged() {
            blockEntity.pushInternalDataChangeToClient()
        }

        override fun canPlaceItem(slot: Int, stack: ItemStack): Boolean = true
    }

    private val inventory = ExtraSimpleStorage(this)
    val storage = ContainerWrapper(inventory)
    var data = SimpleContainerData(8)

    fun setCurrentEnergy(energy: Int) {
        data[0] = energy and -0x1000000 shr 16
        data[1] = energy and 0x00ff0000 shr 12
        data[2] = energy and 0x0000ff00 shr 8
        data[3] = energy and 0x000000ff
    }

    fun setMaxEnergy(maxEnergy: Int) {
        data[4] = maxEnergy and -0x1000000 shr 16
        data[5] = maxEnergy and 0x00ff0000 shr 12
        data[6] = maxEnergy and 0x0000ff00 shr 8
        data[7] = maxEnergy and 0x000000ff
    }

    init {
        setCurrentEnergy(480000)
        setMaxEnergy(480000)
    }

    override fun createPeripheral(side: Direction): DigitizerPeripheral<BlockEntityPeripheralOwner<DigitizerBlockEntity>> = DigitizerPeripheral(BlockEntityPeripheralOwner(this))

    override fun loadInternalData(data: CompoundTag, state: BlockState?): BlockState {
        if (data.contains(STORED_ITEM_STACK_TAG)) {
            val itemList = data.getList(STORED_ITEM_STACK_TAG, 10)
            if (itemList.isEmpty()) {
                inventory.clearContent()
            } else {
                inventory.fromTag(itemList)
            }
        }
        return state ?: blockState
    }

    override fun saveInternalData(data: CompoundTag): CompoundTag {
        data.put(STORED_ITEM_STACK_TAG, inventory.createTag())
        return data
    }

    override fun clearContent() {
        inventory.clearContent()
    }

    override fun getContainerSize(): Int = inventory.containerSize

    override fun isEmpty(): Boolean = inventory.isEmpty

    override fun getItem(p0: Int): ItemStack = inventory.getItem(p0)

    override fun removeItem(p0: Int, p1: Int): ItemStack = inventory.removeItem(p0, p1)

    override fun removeItemNoUpdate(p0: Int): ItemStack = inventory.removeItemNoUpdate(p0)

    override fun setItem(p0: Int, p1: ItemStack) {
        inventory.setItem(p0, p1)
    }

    override fun stillValid(p0: Player): Boolean = inventory.stillValid(p0)

    override fun createMenu(p0: Int, p1: Inventory, p2: Player): AbstractContainerMenu = DigitizerMenu(p0, p1, this, data)

    override fun getDisplayName(): Component = ModText.DIGITIZER.text

    fun buildSlot(x: Int, y: Int): Slot = Slot(inventory, 0, x, y)
}
