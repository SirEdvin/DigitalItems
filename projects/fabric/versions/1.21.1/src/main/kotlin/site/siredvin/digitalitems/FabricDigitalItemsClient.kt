package site.siredvin.digitalitems

import dan200.computercraft.api.client.FabricComputerCraftAPIClient
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.fabricmc.api.ClientModInitializer

object FabricDigitalItemsClient : ClientModInitializer {
    override fun onInitializeClient() {
        DigitalItemsClientCore.onInit()
        DigitalItemsClientCore.onModelRegister { type, modeller ->
            @Suppress("UNCHECKED_CAST")
            FabricComputerCraftAPIClient.registerTurtleUpgradeModeller(
                type as UpgradeType<ITurtleUpgrade>,
                modeller as TurtleUpgradeModeller<ITurtleUpgrade>,
            )
        }
    }
}
