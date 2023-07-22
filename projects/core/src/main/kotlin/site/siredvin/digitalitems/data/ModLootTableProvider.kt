package site.siredvin.digitalitems.data

import net.minecraft.data.loot.LootTableProvider
import net.minecraft.data.loot.LootTableSubProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.storage.loot.LootTable
import net.minecraft.world.level.storage.loot.parameters.LootContextParamSets
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.peripheralium.data.blocks.LootTableHelper
import java.util.function.BiConsumer

object ModLootTableProvider {
    fun getTables(): List<LootTableProvider.SubProviderEntry> {
        return listOf(
            LootTableProvider.SubProviderEntry({
                LootTableSubProvider {
                    registerBlocks(it)
                }
            }, LootContextParamSets.BLOCK),
        )
    }

    fun registerBlocks(consumer: BiConsumer<ResourceLocation, LootTable.Builder>) {
        val helper = LootTableHelper(ModPlatform.holder)
        helper.dropSelf(consumer, ModBlocks.DIGITIZER)
        helper.validate()
    }
}
