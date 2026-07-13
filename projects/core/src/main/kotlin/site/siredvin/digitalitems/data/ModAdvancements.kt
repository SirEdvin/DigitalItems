@file:Suppress("DEPRECATION", "KotlinRedundantDiagnosticSuppress")

package site.siredvin.digitalitems.data

import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.advancements.AdvancementType
import net.minecraft.advancements.critereon.InventoryChangeTrigger
import net.minecraft.core.HolderLookup
import net.minecraft.data.PackOutput
import net.minecraft.data.advancements.AdvancementProvider
import net.minecraft.data.advancements.AdvancementSubProvider
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.Items
import net.minecraft.world.level.block.Blocks
import site.siredvin.broccolium.modules.data.api.TextRecord
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.common.criteria.DigitalizeEnergyCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeFluidCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeItemCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeLavaCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeOreCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeStarCriteria
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.modId
import site.siredvin.digitalitems.saveWithID
import java.util.concurrent.CompletableFuture
import java.util.function.Consumer

object ModAdvancementsSubProvider : AdvancementSubProvider {
    private val stoneTexture = ResourceLocation.withDefaultNamespace("textures/gui/advancements/backgrounds/stone.png")

    override fun generate(p0: HolderLookup.Provider, p1: Consumer<AdvancementHolder>) {
        val inTheGame = Advancement.Builder.advancement().display(
            ModBlocks.DIGITIZER.get().asItem(),
            AdvancementTexts.DIGITIZER.text,
            AdvancementTexts.DIGITIZER_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).addCriterion("have_item", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.DIGITIZER.get().asItem())).saveWithID(p1, modId("root"))
        val startingUp = Advancement.Builder.advancement().display(
            Blocks.DIRT,
            AdvancementTexts.STARTING_UP.text,
            AdvancementTexts.STARTING_UP_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(inTheGame).addCriterion("digitalize_something", DigitalizeItemCriteria.digitilizeSome(64)).saveWithID(p1, modId("starting_up"))
        val continiusDigitalization = Advancement.Builder.advancement().display(
            Blocks.STONE,
            AdvancementTexts.CONTINIOUS_DIGITALIZATION.text,
            AdvancementTexts.CONTINIOUS_DIGITALIZATION_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(startingUp).addCriterion("digitalize_something", DigitalizeItemCriteria.digitilizeSome(256)).saveWithID(p1, modId("continious_digitalization"))
        Advancement.Builder.advancement().display(
            Blocks.BEACON,
            AdvancementTexts.OVERDIGITALIZATION.text,
            AdvancementTexts.OVERDIGITALIZATION_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.CHALLENGE,
            true,
            true,
            false,
        ).parent(continiusDigitalization).addCriterion("digitalize_something", DigitalizeItemCriteria.digitilizeSome(9001)).saveWithID(p1, modId("overdigitalization"))
        Advancement.Builder.advancement().display(
            Items.NETHER_STAR,
            AdvancementTexts.DIGITILIZE_THE_STARS.text,
            AdvancementTexts.DIGITILIZE_THE_STARS_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(inTheGame).addCriterion("digitalize_something", DigitalizeStarCriteria.digitilizeSome(1)).saveWithID(p1, modId("digitilize_the_stars"))

        val miningStartup = Advancement.Builder.advancement().display(
            Blocks.IRON_ORE,
            AdvancementTexts.MINING_STARTUP.text,
            AdvancementTexts.MINING_STARTUP_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(inTheGame).addCriterion("digitilize_something", DigitalizeOreCriteria.digitilizeSome(64)).saveWithID(p1, modId("mining_startup"))
        val miningBusiness = Advancement.Builder.advancement().display(
            Blocks.GOLD_ORE,
            AdvancementTexts.MINING_BUSINESS.text,
            AdvancementTexts.MINING_BUSINESS_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(miningStartup).addCriterion("digitilize_something", DigitalizeOreCriteria.digitilizeSome(256)).saveWithID(p1, modId("mining_business"))
        Advancement.Builder.advancement().display(
            Blocks.DIAMOND_ORE,
            AdvancementTexts.MINING_INDUSTRY.text,
            AdvancementTexts.MINING_INDUSTRY_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(miningBusiness).addCriterion("digitilize_something", DigitalizeOreCriteria.digitilizeSome(2_000)).saveWithID(p1, modId("mining_industry"))

        val advancedGame = Advancement.Builder.advancement().display(
            ModBlocks.ADVANCED_DIGITIZER.get().asItem(),
            AdvancementTexts.ADVANCED_DIGITIZER.text,
            AdvancementTexts.ADVANCED_DIGITIZER_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(inTheGame).addCriterion("have_item", InventoryChangeTrigger.TriggerInstance.hasItems(ModBlocks.ADVANCED_DIGITIZER.get().asItem())).saveWithID(p1, modId("advanced_digitizer"))
        val waterDigitize = Advancement.Builder.advancement().display(
            Items.WATER_BUCKET,
            AdvancementTexts.DIGITIZE_FLUID.text,
            AdvancementTexts.DIGITIZE_FLUID_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(advancedGame).addCriterion("digitize_something", DigitalizeFluidCriteria.digitilizeSome(4000)).saveWithID(p1, modId("digitize_fluid"))
        val lavaDigitize = Advancement.Builder.advancement().display(
            Items.LAVA_BUCKET,
            AdvancementTexts.DIGITIZE_LAVA.text,
            AdvancementTexts.DIGITIZE_LAVA_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(waterDigitize).addCriterion("digitize_something", DigitalizeLavaCriteria.digitilizeSome(4000)).saveWithID(p1, modId("digitize_lava"))
        Advancement.Builder.advancement().display(
            Blocks.OBSIDIAN,
            AdvancementTexts.FLUID_OVERDIGITALIZATION.text,
            AdvancementTexts.FLUID_OVERDIGITALIZATION_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(lavaDigitize).addCriterion("digitize_something", DigitalizeFluidCriteria.digitilizeSome(9001 * 1000)).saveWithID(p1, modId("fluid_overdigitize"))

        val energyDigitize = Advancement.Builder.advancement().display(
            Items.LIGHTNING_ROD,
            AdvancementTexts.DIGITIZE_ENERGY.text,
            AdvancementTexts.DIGITIZE_ENERGY_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(advancedGame).addCriterion("digitize_something", DigitalizeEnergyCriteria.digitilizeSome(4000)).saveWithID(p1, modId("digitize_energy"))
        Advancement.Builder.advancement().display(
            Blocks.CONDUIT,
            AdvancementTexts.ENERGY_OVERDIGITALIZATION.text,
            AdvancementTexts.ENERGY_OVERDIGITALIZATION_DESCRIPTION.text,
            stoneTexture,
            AdvancementType.TASK,
            true,
            true,
            false,
        ).parent(energyDigitize).addCriterion("digitize_something", DigitalizeEnergyCriteria.digitilizeSome(Int.MAX_VALUE / 2)).saveWithID(p1, modId("energy_overdigitize"))
    }
}

class ModAdvancements(
    packOutput: PackOutput,
    holderLookup: CompletableFuture<HolderLookup.Provider>,
) : AdvancementProvider(packOutput, holderLookup, listOf(ModAdvancementsSubProvider))

enum class AdvancementTexts : TextRecord {
    DIGITIZER,
    DIGITIZER_DESCRIPTION,
    STARTING_UP,
    STARTING_UP_DESCRIPTION,
    CONTINIOUS_DIGITALIZATION,
    CONTINIOUS_DIGITALIZATION_DESCRIPTION,
    OVERDIGITALIZATION,
    OVERDIGITALIZATION_DESCRIPTION,
    DIGITILIZE_THE_STARS,
    DIGITILIZE_THE_STARS_DESCRIPTION,
    MINING_STARTUP,
    MINING_STARTUP_DESCRIPTION,
    MINING_BUSINESS,
    MINING_BUSINESS_DESCRIPTION,
    MINING_INDUSTRY,
    MINING_INDUSTRY_DESCRIPTION,
    ADVANCED_DIGITIZER,
    ADVANCED_DIGITIZER_DESCRIPTION,
    DIGITIZE_FLUID,
    DIGITIZE_FLUID_DESCRIPTION,
    DIGITIZE_LAVA,
    DIGITIZE_LAVA_DESCRIPTION,
    FLUID_OVERDIGITALIZATION,
    FLUID_OVERDIGITALIZATION_DESCRIPTION,
    DIGITIZE_ENERGY,
    DIGITIZE_ENERGY_DESCRIPTION,
    ENERGY_OVERDIGITALIZATION,
    ENERGY_OVERDIGITALIZATION_DESCRIPTION,
    ;

    override val textID: String by lazy {
        String.format("advencement.%s.%s", DigitalItemsCore.MOD_ID, name.lowercase())
    }
}
