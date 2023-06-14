package site.siredvin.template

import net.minecraft.resources.ResourceLocation
import net.minecraftforge.api.distmarker.Dist
import net.minecraftforge.client.event.ModelEvent.RegisterAdditional
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent

@Mod.EventBusSubscriber(modid = TemplateCore.MOD_ID, value = [Dist.CLIENT], bus = Mod.EventBusSubscriber.Bus.MOD)
object ForgeTemplateClient {

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        TemplateClientCore.onInit()
    }

    @SubscribeEvent
    fun registerModels(event: RegisterAdditional) {
        TemplateClientCore.registerExtraModels { model: ResourceLocation ->
            event.register(model)
        }
    }
}
