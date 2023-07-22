package site.siredvin.digitalitems.common.setup

import site.siredvin.digitalitems.modId
import site.siredvin.digitalitems.xplat.ModPlatform

object ModStats {
    val DIGITALIZED_ITEMS = ModPlatform.registerCustomStat(modId("items"))
    val DIGITALIZED_ORES = ModPlatform.registerCustomStat(modId("ores"))
    val DIGITALIZED_STARS = ModPlatform.registerCustomStat(modId("stars"))

    fun doSomething() {}
}
