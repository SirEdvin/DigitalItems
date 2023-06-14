package site.siredvin.template.data

import site.siredvin.peripheralium.data.blocks.GeneratorSink

object ModDataProviders {
    fun add(generator: GeneratorSink) {
        generator.add {
            ModRecipeProvider(it)
        }
        generator.lootTable(ModLootTableProvider.getTables())
        generator.models(ModBlockModelProvider::addModels, ModItemModelProvider::addModels)
        generator.add(::ModEnLanguageProvider)
        generator.add(::ModUaLanguageProvider)
    }
}
