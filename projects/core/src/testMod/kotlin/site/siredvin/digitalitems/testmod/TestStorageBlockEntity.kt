package site.siredvin.digitalitems.testmod

import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.world.level.block.state.BlockState
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorageProvider
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorageProvider
import site.siredvin.tweakium.modules.peripheral.blockentity.PeripheralBlockEntity
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner

class TestStorageBlockEntity(pos: BlockPos, state: BlockState) :
    PeripheralBlockEntity<TestStoragePeripheral>(DigitalItemsTestContent.BLOCK_ENTITY.get(), pos, state),
    AgnosticEnergyStorageProvider,
    AgnosticFluidStorageProvider {
    override val energyStorage: AgnosticEnergyStorage = TestEnergyStorage(
        100_000,
        AgnosticEnergyStack(PlatformToolkit.get().commonEnergy, 0),
    )
    override val fluidStorage: AgnosticFluidStorage = TestFluidStorage(100_000.0)

    override fun createPeripheral(side: Direction): TestStoragePeripheral = TestStoragePeripheral(BlockEntityPeripheralOwner(this))
}
