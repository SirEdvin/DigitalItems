package site.siredvin.digitalitems.data

import net.minecraft.Util
import net.minecraft.data.registries.VanillaRegistries
import site.siredvin.broccolium.modules.data.api.GeneratorSink
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
        generator.add(::ModPocketUpgradeDataProvider)
        generator.add(::ModTurtleUpgradeDataProvider)
        val completablefuture = CompletableFuture.supplyAsync(
            { VanillaRegistries.createLookup() },
            Util.backgroundExecutor(),
        )
        generator.add {
            ModAdvancements(it, completablefuture)
        }
    }
}
