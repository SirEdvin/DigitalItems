package site.siredvin.digitalitems

import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = DigitalItemsCore.MOD_ID, value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.MOD)
object ForgeDigitalItemsClient {

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        DigitalItemsClientCore.onInit()
    }
}
