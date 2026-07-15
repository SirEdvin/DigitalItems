package site.siredvin.digitalitems.testmod

import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner
import site.siredvin.tweakium.modules.plugins.EnergyPlugin
import site.siredvin.tweakium.modules.plugins.FluidStoragePlugin

class TestStoragePeripheral(owner: BlockEntityPeripheralOwner<TestStorageBlockEntity>) : OwnedPeripheral<BlockEntityPeripheralOwner<TestStorageBlockEntity>>("digitalitems_test_storage", owner) {
    init {
        addPlugin(EnergyPlugin(owner.blockEntity.energyStorage))
        addPlugin(FluidStoragePlugin(owner.level!!, owner.blockEntity.fluidStorage, Double.MAX_VALUE))
    }

    override val isEnabled = true
}
