package site.siredvin.template

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import java.util.function.Consumer

object FabricTemplateClient : ClientModInitializer {
    override fun onInitializeClient() {
        TemplateClientCore.onInit()
        ModelLoadingRegistry.INSTANCE.registerModelProvider { _: ResourceManager, out: Consumer<ResourceLocation> ->
            TemplateClientCore.registerExtraModels(out)
        }
    }
}
