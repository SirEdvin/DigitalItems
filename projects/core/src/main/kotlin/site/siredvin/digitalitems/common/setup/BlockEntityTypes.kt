package site.siredvin.digitalitems.common.setup

import site.siredvin.digitalitems.common.blockentity.DigitizerBlockEntity
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform

object BlockEntityTypes {
    val DIGITIZER = ModPlatform.registerBlockEntity(
        "digitizer",
    ) { PeripheraliumPlatform.createBlockEntityType(::DigitizerBlockEntity, Blocks.DIGITIZER.get()) }

    fun doSomething() {}
}
