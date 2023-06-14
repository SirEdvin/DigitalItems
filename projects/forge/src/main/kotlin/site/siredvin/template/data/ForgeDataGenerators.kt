package site.siredvin.template.data

import net.minecraftforge.data.event.GatherDataEvent
import net.minecraftforge.eventbus.api.SubscribeEvent
import net.minecraftforge.fml.common.Mod
import site.siredvin.peripheralium.data.ForgeDataGenerators
import site.siredvin.template.TemplateCore

@Mod.EventBusSubscriber(modid = TemplateCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ForgeDataGenerators {
    @SubscribeEvent
    fun genData(event: GatherDataEvent) {
        val generator = event.generator
        ModDataProviders.add(
            ForgeDataGenerators.ForgeGeneratorSink(
                generator.getVanillaPack(true),
                event.existingFileHelper,
                event.lookupProvider,
            ),
        )
    }
}
