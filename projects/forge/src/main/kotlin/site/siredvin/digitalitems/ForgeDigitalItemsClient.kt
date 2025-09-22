package site.siredvin.digitalitems

import dan200.computercraft.api.client.turtle.RegisterTurtleModellersEvent
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
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

    @SubscribeEvent
    fun registerTurtleModels(event: RegisterTurtleModellersEvent) {
        DigitalItemsClientCore.onModelRegister { serializer, model ->
            @Suppress("UNCHECKED_CAST")
            event.register(serializer as TurtleUpgradeSerialiser<ITurtleUpgrade>, model)
        }
    }
}
