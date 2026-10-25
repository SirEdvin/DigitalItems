package site.siredvin.digitalitems.common.blockentity

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.network.chat.Component
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.digitalitems.client.DigitizerMenu
import site.siredvin.digitalitems.common.setup.ModBlockEntityTypes
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral
import site.siredvin.digitalitems.data.ModText
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner

class DigitizerBlockEntity(pos: BlockPos, state: BlockState) :
    DigitizerInventoryBlockEntity<DigitizerPeripheral<BlockEntityPeripheralOwner<DigitizerBlockEntity>>>(ModBlockEntityTypes.DIGITIZER.get(), pos, state, 1, "storedItemStack"),
    MenuProvider {
    override fun createPeripheral(side: Direction): DigitizerPeripheral<BlockEntityPeripheralOwner<DigitizerBlockEntity>> = DigitizerPeripheral(BlockEntityPeripheralOwner(this))
    override fun createMenu(id: Int, inventory: Inventory, player: Player): AbstractContainerMenu = DigitizerMenu(id, inventory, this, data)
    override fun getDisplayName(): Component = ModText.DIGITIZER.text
}
