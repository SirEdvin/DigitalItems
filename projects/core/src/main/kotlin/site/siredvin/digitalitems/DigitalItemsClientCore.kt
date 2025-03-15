package site.siredvin.digitalitems

import net.minecraft.client.gui.screens.MenuScreens
import site.siredvin.digitalitems.client.DigitizerScreen
import site.siredvin.digitalitems.common.setup.ModMenus

object DigitalItemsClientCore {
    fun onInit() {
        MenuScreens.register(ModMenus.DIGITIZER.get(), ::DigitizerScreen)
    }
}
