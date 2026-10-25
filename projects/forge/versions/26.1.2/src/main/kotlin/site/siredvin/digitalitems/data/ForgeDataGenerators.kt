package site.siredvin.digitalitems.data

import net.neoforged.bus.api.SubscribeEvent
import net.neoforged.fml.common.EventBusSubscriber
import net.neoforged.neoforge.data.event.GatherDataEvent
import site.siredvin.broccolium.modules.data.ForgeGeneratorSink
import site.siredvin.digitalitems.DigitalItemsCore

@EventBusSubscriber(modid = DigitalItemsCore.MOD_ID)
object ForgeDataGenerators {
    @SubscribeEvent
    fun client(event: GatherDataEvent.Client) = ModDataProviders.add(ForgeGeneratorSink(event.generator, event))

    @SubscribeEvent
    fun server(event: GatherDataEvent.Server) = ModDataProviders.add(ForgeGeneratorSink(event.generator, event))
}
