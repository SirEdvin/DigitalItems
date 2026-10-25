package site.siredvin.digitalitems.data

import net.minecraft.Util
import net.minecraft.data.registries.VanillaRegistries
import site.siredvin.broccolium.modules.data.api.GeneratorSink
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.tweakium.modules.data.upgrades
import java.util.concurrent.CompletableFuture

object ModDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add(::ModRecipeProvider)
        generator.lootTable(ModLootTableProvider.getTables())
        generator.models(ModBlockModelProvider::addModels, ModItemModelProvider::addModels)
        generator.upgrades(DigitalItemsCore.MOD_ID, ModPocketUpgradeDataProvider, ModTurtleUpgradeDataProvider)
        generator.add(::ModEnLanguageProvider)
        generator.add(::ModUaLanguageProvider)
        val registries = CompletableFuture.supplyAsync(VanillaRegistries::createLookup, Util.backgroundExecutor())
        generator.add { ModAdvancements(it, registries) }
    }
}
