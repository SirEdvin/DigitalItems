package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import site.siredvin.broccolium.modules.data.lang.toStatTranslationKey
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.digitalitems.computercraft.AdvancedDigitizerPeripheral
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral

class ModEnLanguageProvider(
    output: PackOutput,
) : ModLanguageProvider(output, "en_us") {
    override fun addTranslations() {
        add(ModBlocks.DIGITIZER.get(), "Digitizer")
        add(ModBlocks.ADVANCED_DIGITIZER.get(), "Advanced digitizer")
        add(ModText.DIGITIZER, "Digitizer")
        add(ModText.ADVANCED_DIGITIZER, "Advanced digitizer")
        add(ModText.CREATIVE_TAB, "Digital Items")

        add(AdvancementTexts.DIGITIZER, "Digitizer")
        add(AdvancementTexts.DIGITIZER_DESCRIPTION, "Digitizer will help you to digitize items, you know")
        add(AdvancementTexts.STARTING_UP, "Starting up")
        add(AdvancementTexts.STARTING_UP_DESCRIPTION, "Digitize some items, 64 should be enough to start")
        add(AdvancementTexts.CONTINIOUS_DIGITALIZATION, "Continuous digitization")
        add(AdvancementTexts.CONTINIOUS_DIGITALIZATION_DESCRIPTION, "Seems digitization now on industry level")
        add(AdvancementTexts.OVERDIGITALIZATION, "Overdigitization")
        add(AdvancementTexts.OVERDIGITALIZATION_DESCRIPTION, "I can't believe this! You digitize over 9000 items!")
        add(AdvancementTexts.DIGITILIZE_THE_STARS, "Digitize the stars")
        add(AdvancementTexts.DIGITILIZE_THE_STARS_DESCRIPTION, "Are you really gonna trust this strange technology something so expensive?")
        add(AdvancementTexts.MINING_STARTUP, "Mining startup")
        add(AdvancementTexts.MINING_STARTUP_DESCRIPTION, "Digitizer pretty useful for transporting ores, try it!")
        add(AdvancementTexts.MINING_BUSINESS, "Mining business")
        add(AdvancementTexts.MINING_BUSINESS_DESCRIPTION, "Digitizing ores now become a routine")
        add(AdvancementTexts.MINING_INDUSTRY, "Mining industry")
        add(AdvancementTexts.MINING_INDUSTRY_DESCRIPTION, "It is even possible to get so many ore block in game? More then 2000 digitized")

        add(AdvancementTexts.ADVANCED_DIGITIZER, "Advanced digitizer")
        add(AdvancementTexts.ADVANCED_DIGITIZER_DESCRIPTION, "It is like simple digitizer, but much better! Works remotely and with fluids and energies!")
        add(AdvancementTexts.DIGITIZE_FLUID, "Fluid digitization")
        add(AdvancementTexts.DIGITIZE_FLUID_DESCRIPTION, "Digitize 4 equivalent of bucket of any fluid")
        add(AdvancementTexts.DIGITIZE_LAVA, "Lava digitization")
        add(AdvancementTexts.DIGITIZE_LAVA_DESCRIPTION, "Digitize 4 equivalent of bucket of lava")
        add(AdvancementTexts.FLUID_OVERDIGITALIZATION, "How many is needed for an ocean?")
        add(AdvancementTexts.FLUID_OVERDIGITALIZATION_DESCRIPTION, "You digitized 9001 buckets of fluid? Isn't this enough for a ocean?")
        add(AdvancementTexts.DIGITIZE_ENERGY, "Energy digitization")
        add(AdvancementTexts.DIGITIZE_ENERGY_DESCRIPTION, "Wireless energy transferring on new level")
        add(AdvancementTexts.ENERGY_OVERDIGITALIZATION, "Charging the cloud")
        add(AdvancementTexts.ENERGY_OVERDIGITALIZATION_DESCRIPTION, "You digitized 1073741823 units of energy. It is probably enough to kill any digital entity.Thank that god we don't have them here")

        add(ModStats.DIGITALIZED_ITEMS.get().value.toStatTranslationKey(), "Amount of digitized items")
        add(ModStats.DIGITALIZED_ORES.get().value.toStatTranslationKey(), "Amount of digitized ores")
        add(ModStats.DIGITALIZED_STARS.get().value.toStatTranslationKey(), "Amount of digitized nether stars")
        add(ModStats.DIGITALIZED_FLUIDS.get().value.toStatTranslationKey(), "Amount of digitized fluids")
        add(ModStats.DIGITALIZED_LAVA.get().value.toStatTranslationKey(), "Amount of digitized lava")
        add(ModStats.DIGITALIZED_ENERGY.get().value.toStatTranslationKey(), "Amount of digitized energy")

        addUpgrades(DigitizerPeripheral.ID, "Digitizing")
        addUpgrades(AdvancedDigitizerPeripheral.ID, "Advanced digitizing")
    }
}
