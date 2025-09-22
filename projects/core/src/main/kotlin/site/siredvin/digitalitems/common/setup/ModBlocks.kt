package site.siredvin.digitalitems.common.setup

import site.siredvin.digitalitems.common.blocks.AdvancedDigitizer
import site.siredvin.digitalitems.common.blocks.Digitizer
import site.siredvin.digitalitems.xplat.ModPlatform

object ModBlocks {

    val DIGITIZER = ModPlatform.registerBlock(
        "digitizer",
        { Digitizer() },
    )
    val ADVANCED_DIGITIZER = ModPlatform.registerBlock(
        "advanced_digitizer",
        { AdvancedDigitizer() },
    )
    fun doSomething() {}
}
