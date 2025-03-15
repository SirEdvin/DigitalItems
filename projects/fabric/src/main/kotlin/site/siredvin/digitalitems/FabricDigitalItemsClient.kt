package site.siredvin.digitalitems

import net.fabricmc.api.ClientModInitializer

object FabricDigitalItemsClient : ClientModInitializer {
    override fun onInitializeClient() {
        DigitalItemsClientCore.onInit()
    }
}
