package site.siredvin.digitalitems

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = DigitalItemsCore.MOD_ID, value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.MOD)
object ForgeDigitalItemsClient {

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        DigitalItemsClientCore.onInit()
    }

    @SubscribeEvent
    fun registerModels(event: RegisterAdditional) {
        DigitalItemsClientCore.registerExtraModels { model: ResourceLocation ->
            event.register(model)
        }
    }
}
