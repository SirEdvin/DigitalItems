package site.siredvin.digitalitems.common.setup

import site.siredvin.digitalitems.client.DigitizerMenu
import site.siredvin.digitalitems.xplat.ModPlatform

object ModMenus {
    val DIGITIZER = ModPlatform.registerMenu(
        "digitizer",
        ::DigitizerMenu,
    )

    fun doSomething() {}
}
