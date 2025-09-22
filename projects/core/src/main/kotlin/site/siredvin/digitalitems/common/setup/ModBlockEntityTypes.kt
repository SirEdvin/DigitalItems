package site.siredvin.digitalitems.common.setup

import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.digitalitems.common.blockentity.AdvancedDigitizerBlockEntity
import site.siredvin.digitalitems.common.blockentity.DigitizerBlockEntity
import site.siredvin.digitalitems.xplat.ModPlatform

object ModBlockEntityTypes {
    val DIGITIZER = ModPlatform.registerBlockEntity(
        "digitizer",
    ) { PlatformToolkit.get().createBlockEntityType(::DigitizerBlockEntity, ModBlocks.DIGITIZER.get()) }
    val ADVANCED_DIGITIZER = ModPlatform.registerBlockEntity(
        "advanced_digitizer",
    ) { PlatformToolkit.get().createBlockEntityType(::AdvancedDigitizerBlockEntity, ModBlocks.ADVANCED_DIGITIZER.get()) }

    fun doSomething() {}
}
