package site.siredvin.digitalitems.data

import net.minecraft.Util
import net.minecraft.data.registries.VanillaRegistries
import site.siredvin.peripheralium.data.blocks.GeneratorSink
import java.util.concurrent.CompletableFuture

object ModDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add {
            ModRecipeProvider(it)
        }
        generator.lootTable(ModLootTableProvider.getTables())
        generator.models(ModBlockModelProvider::addModels, ModItemModelProvider::addModels)
        generator.add(::ModEnLanguageProvider)
        generator.add(::ModUaLanguageProvider)
        // TODO: meh?
        val completablefuture = CompletableFuture.supplyAsync(
            { VanillaRegistries.createLookup() },
            Util.backgroundExecutor(),
        )
        generator.add {
            ModAdvancements(it, completablefuture)
        }
    }
}
