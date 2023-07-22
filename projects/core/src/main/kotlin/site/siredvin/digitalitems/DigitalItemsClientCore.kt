package site.siredvin.digitalitems

import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.resources.ResourceLocation
import site.siredvin.digitalitems.client.DigitizerScreen
import site.siredvin.digitalitems.common.setup.ModMenus
import java.util.function.Consumer

object DigitalItemsClientCore {
    private val EXTRA_MODELS = emptyArray<String>()

    fun registerExtraModels(register: Consumer<ResourceLocation>) {
        EXTRA_MODELS.forEach { register.accept(ResourceLocation(DigitalItemsCore.MOD_ID, it)) }
    }

    fun onInit() {
        MenuScreens.register(ModMenus.DIGITIZER.get(), ::DigitizerScreen)
    }
}
