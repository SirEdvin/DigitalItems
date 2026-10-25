package site.siredvin.digitalitems.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.protocol.game.ClientboundBlockEntityDataPacket
import net.minecraft.world.Container
import net.minecraft.world.SimpleContainer
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.SimpleContainerData
import net.minecraft.world.inventory.Slot
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.storage.ValueInput
import net.minecraft.world.level.storage.ValueOutput
import site.siredvin.broccolium.modules.base.api.ISyncingBlockEntity
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.storage.item.ContainerWrapper
import site.siredvin.tweakium.modules.peripheral.api.IOwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.blockentity.PeripheralBlockEntity

abstract class DigitizerInventoryBlockEntity<T : IOwnedPeripheral<*>>(
    type: BlockEntityType<*>,
    pos: BlockPos,
    state: BlockState,
    size: Int,
    private val inventoryTag: String,
) : PeripheralBlockEntity<T>(type, pos, state),
    Container,
    ISyncingBlockEntity {
    private val inventory = object : SimpleContainer(size) {
        override fun setChanged() = pushInternalDataChangeToClient()
    }
    val storage = ContainerWrapper(inventory)
    val data = SimpleContainerData(8)

    init {
        setCurrentEnergy(480000)
        setMaxEnergy(480000)
    }

    fun setCurrentEnergy(energy: Int) {
        data[0] = energy and -0x1000000 shr 16
        data[1] = energy and 0x00ff0000 shr 12
        data[2] = energy and 0x0000ff00 shr 8
        data[3] = energy and 0x000000ff
    }

    fun setMaxEnergy(energy: Int) {
        data[4] = energy and -0x1000000 shr 16
        data[5] = energy and 0x00ff0000 shr 12
        data[6] = energy and 0x0000ff00 shr 8
        data[7] = energy and 0x000000ff
    }

    private fun loadItems(items: List<ItemStack>) {
        require(items.size == containerSize) { "Invalid digitizer inventory size: ${items.size}" }
        items.forEachIndexed { slot, stack -> inventory.setItem(slot, stack) }
    }

    override fun loadAdditional(input: ValueInput) {
        super.loadAdditional(input)
        input.read(inventoryTag, ITEMS_CODEC).ifPresent(::loadItems)
    }

    override fun saveAdditional(output: ValueOutput) {
        super.saveAdditional(output)
        output.store(inventoryTag, ITEMS_CODEC, inventory.items)
    }

    override fun getUpdateTag(provider: HolderLookup.Provider): CompoundTag = super.getUpdateTag(provider).also { tag ->
        tag.put(inventoryTag, ITEMS_CODEC.encodeStart(provider.createSerializationContext(NbtOps.INSTANCE), inventory.items).getOrThrow())
    }

    override fun getUpdatePacket(): ClientboundBlockEntityDataPacket = ClientboundBlockEntityDataPacket.create(this)

    override fun loadInternalData(data: CompoundTag, state: BlockState?): BlockState {
        data[inventoryTag]?.let { tag ->
            val registries = checkNotNull(level) { "Detached direct inventory load requires native ValueInput" }.registryAccess()
            loadItems(ITEMS_CODEC.parse(registries.createSerializationContext(NbtOps.INSTANCE), tag).getOrThrow())
        }
        return state ?: blockState
    }

    override fun saveInternalData(data: CompoundTag): CompoundTag = data.also {
        val registries = checkNotNull(level) { "Detached direct inventory save requires native ValueOutput" }.registryAccess()
        it.put(inventoryTag, ITEMS_CODEC.encodeStart(registries.createSerializationContext(NbtOps.INSTANCE), inventory.items).getOrThrow())
    }

    override fun pushInternalDataChangeToClient(state: BlockState?) {
        val world = level ?: return
        if (!world.isClientSide) {
            setChanged()
            val updated = state ?: blockState
            world.sendBlockUpdated(blockPos, blockState, updated, Block.UPDATE_ALL)
        }
    }

    override fun triggerRenderUpdate() = PlatformToolkit.get().triggerRenderUpdate(this)
    override fun clearContent() = inventory.clearContent()
    override fun getContainerSize(): Int = inventory.containerSize
    override fun isEmpty(): Boolean = inventory.isEmpty
    override fun getItem(slot: Int): ItemStack = inventory.getItem(slot)
    override fun removeItem(slot: Int, count: Int): ItemStack = inventory.removeItem(slot, count)
    override fun removeItemNoUpdate(slot: Int): ItemStack = inventory.removeItemNoUpdate(slot)
    override fun setItem(slot: Int, stack: ItemStack) = inventory.setItem(slot, stack)
    override fun stillValid(player: Player): Boolean = Container.stillValidBlockEntity(this, player)
    fun buildSlot(x: Int, y: Int, slot: Int = 0): Slot = Slot(inventory, slot, x, y)

    companion object {
        private val ITEMS_CODEC = ItemStack.OPTIONAL_CODEC.listOf()
    }
}
