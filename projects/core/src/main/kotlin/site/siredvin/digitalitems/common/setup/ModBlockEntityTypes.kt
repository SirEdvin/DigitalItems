package site.siredvin.digitalitems.common.setup

import site.siredvin.digitalitems.common.blockentity.DigitizerBlockEntity
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform

object ModBlockEntityTypes {
    val DIGITIZER = ModPlatform.registerBlockEntity(
        "digitizer",
    ) { PeripheraliumPlatform.createBlockEntityType(::DigitizerBlockEntity, ModBlocks.DIGITIZER.get()) }

    fun doSomething() {}
}
