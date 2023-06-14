package site.siredvin.digitalitems

import net.fabricmc.api.ClientModInitializer
import net.fabricmc.fabric.api.client.model.ModelLoadingRegistry
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.packs.resources.ResourceManager
import java.util.function.Consumer

object FabricDigitalItemsClient : ClientModInitializer {
    override fun onInitializeClient() {
        DigitalItemsClientCore.onInit()
        ModelLoadingRegistry.INSTANCE.registerModelProvider { _: ResourceManager, out: Consumer<ResourceLocation> ->
            DigitalItemsClientCore.registerExtraModels(out)
        }
    }
}
