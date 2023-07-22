package site.siredvin.digitalitems.common.setup

import site.siredvin.digitalitems.common.blocks.Digitizer
import site.siredvin.digitalitems.xplat.ModPlatform

object ModBlocks {

    val DIGITIZER = ModPlatform.registerBlock(
        "digitizer",
        { Digitizer() },
    )
    fun doSomething() {}
}
