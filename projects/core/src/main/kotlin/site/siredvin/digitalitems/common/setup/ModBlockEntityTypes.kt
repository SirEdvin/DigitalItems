package site.siredvin.digitalitems.common.setup

import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.digitalitems.common.blockentity.DigitizerBlockEntity
import site.siredvin.digitalitems.xplat.ModPlatform

object ModBlockEntityTypes {
    val DIGITIZER = ModPlatform.registerBlockEntity(
        "digitizer",
    ) { PlatformToolkit.get().createBlockEntityType(::DigitizerBlockEntity, ModBlocks.DIGITIZER.get()) }

    fun doSomething() {}
}
