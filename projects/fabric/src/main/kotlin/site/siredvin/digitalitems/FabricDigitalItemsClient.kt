package site.siredvin.digitalitems

import dan200.computercraft.api.client.FabricComputerCraftAPIClient
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.fabricmc.api.ClientModInitializer

object FabricDigitalItemsClient : ClientModInitializer {
    override fun onInitializeClient() {
        DigitalItemsClientCore.onInit()
        DigitalItemsClientCore.onModelRegister { serializer, modeller ->
            @Suppress("UNCHECKED_CAST")
            FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(serializer as TurtleUpgradeSerialiser<ITurtleUpgrade>, modeller)
        }
    }
}
