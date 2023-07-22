package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.peripheralium.data.language.toStatTranslationKey

class ModDeLanguageProvider(
    output: PackOutput,
) : ModLanguageProvider(output, "de_de") {

    override fun addTranslations() {
        add(ModBlocks.DIGITIZER.get(), "Digitalisierer")
        add(ModText.DIGITIZER, "Digitalisierer")
        add(ModText.CREATIVE_TAB, "Digitalisierer")

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

        add(ModStats.DIGITALIZED_ITEMS.get().value.toStatTranslationKey(), "Amount of digitized items")
        add(ModStats.DIGITALIZED_ORES.get().value.toStatTranslationKey(), "Amount of digitized ores")
        add(ModStats.DIGITALIZED_STARS.get().value.toStatTranslationKey(), "Amount of digitized nether stars")
    }
}
