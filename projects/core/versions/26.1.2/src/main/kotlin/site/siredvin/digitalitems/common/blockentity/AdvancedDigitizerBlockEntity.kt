package site.siredvin.digitalitems.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.digitalitems.client.AdvancedDigitizerMenu
import site.siredvin.digitalitems.common.setup.ModBlockEntityTypes
import site.siredvin.digitalitems.computercraft.AdvancedDigitizerPeripheral
import site.siredvin.digitalitems.data.ModText
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner

class AdvancedDigitizerBlockEntity(pos: BlockPos, state: BlockState) :
    DigitizerInventoryBlockEntity<AdvancedDigitizerPeripheral<BlockEntityPeripheralOwner<AdvancedDigitizerBlockEntity>>>(ModBlockEntityTypes.ADVANCED_DIGITIZER.get(), pos, state, 8, "storedItemStacks"),
    MenuProvider {
    override fun createPeripheral(side: Direction): AdvancedDigitizerPeripheral<BlockEntityPeripheralOwner<AdvancedDigitizerBlockEntity>> = AdvancedDigitizerPeripheral(BlockEntityPeripheralOwner(this))
    override fun createMenu(id: Int, inventory: Inventory, player: Player): AbstractContainerMenu = AdvancedDigitizerMenu(id, inventory, this, data)
    override fun getDisplayName(): Component = ModText.ADVANCED_DIGITIZER.text
}
