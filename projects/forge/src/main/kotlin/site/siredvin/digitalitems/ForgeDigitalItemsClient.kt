package site.siredvin.digitalitems

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.neoforged.api.distmarker.Dist
import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.fml.event.lifecycle.FMLClientSetupEvent

@EventBusSubscriber(modid = DigitalItemsCore.MOD_ID, value = [Dist.CLIENT], bus = EventBusSubscriber.Bus.MOD)
object ForgeDigitalItemsClient {

    @SubscribeEvent
    fun onClientSetup(event: FMLClientSetupEvent) {
        event.enqueueWork(DigitalItemsClientCore::onInit)
    }

    @SubscribeEvent
    fun registerTurtleModels(event: RegisterTurtleModellersEvent) {
        DigitalItemsClientCore.onModelRegister { serializer, model ->
            @Suppress("UNCHECKED_CAST")
            event.register(
                serializer as UpgradeType<ITurtleUpgrade>,
                model as TurtleUpgradeModeller<ITurtleUpgrade>,
            )
        }
    }
}
