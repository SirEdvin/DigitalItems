package site.siredvin.digitalitems

import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.client.event.RegisterMenuScreensEvent
import site.siredvin.digitalitems.client.DigitizerScreen
import site.siredvin.digitalitems.common.setup.ModMenus

@EventBusSubscriber(modid = DigitalItemsCore.MOD_ID, value = [Dist.CLIENT])
object ForgeDigitalItemsClient {
    @SubscribeEvent
    fun registerScreens(event: RegisterMenuScreensEvent) {
        event.register(ModMenus.DIGITIZER.get(), ::DigitizerScreen)
        event.register(ModMenus.ADVANCED_DIGITIZER.get(), ::DigitizerScreen)
    }
}
