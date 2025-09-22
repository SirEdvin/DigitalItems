package site.siredvin.digitalitems.common.setup

import site.siredvin.digitalitems.client.AdvancedDigitizerMenu
import site.siredvin.digitalitems.client.DigitizerMenu
import site.siredvin.digitalitems.xplat.ModPlatform

object ModMenus {
    val DIGITIZER = ModPlatform.registerMenu(
        "digitizer",
        ::DigitizerMenu,
    )
    val ADVANCED_DIGITIZER = ModPlatform.registerMenu(
        "advanced_digitizer",
        ::AdvancedDigitizerMenu,
    )

    fun doSomething() {}
}
