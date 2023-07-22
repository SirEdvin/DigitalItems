package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.peripheralium.data.language.toStatTranslationKey

class ModUaLanguageProvider(
    output: PackOutput,
) : ModLanguageProvider(output, "uk_ua") {
    override fun addTranslations() {
        add(ModBlocks.DIGITIZER.get(), "Цифровізатор")
        add(ModText.DIGITIZER, "Цифровізатор")
        add(ModText.CREATIVE_TAB, "Цифрові елементи")

        add(AdvancementTexts.DIGITIZER, "Цифровізатор")
        add(AdvancementTexts.DIGITIZER_DESCRIPTION, "Цифровізатор допомагає оцифровувати різні предмети, хто б міг подумати")
        add(AdvancementTexts.STARTING_UP, "Найважче - це почати")
        add(AdvancementTexts.STARTING_UP_DESCRIPTION, "Оцифруйте кілька предметів, 64 має бути достатньо для початку")
        add(AdvancementTexts.CONTINIOUS_DIGITALIZATION, "Потокова цифровізація")
        add(AdvancementTexts.CONTINIOUS_DIGITALIZATION_DESCRIPTION, "Схоже хтось поставив оцифровування на потік!")
        add(AdvancementTexts.OVERDIGITALIZATION, "Перецифровізація")
        add(AdvancementTexts.OVERDIGITALIZATION_DESCRIPTION, "В це неможливо повірити, більше 9000 предметів було оцифровано!")
        add(AdvancementTexts.DIGITILIZE_THE_STARS, "Цифрова зірка")
        add(AdvancementTexts.DIGITILIZE_THE_STARS_DESCRIPTION, "Ви справді хочете довірити цифровізатору щось таке дороге?")
        add(AdvancementTexts.MINING_STARTUP, "Шахтер-початківець")
        add(AdvancementTexts.MINING_STARTUP_DESCRIPTION, "Цифровізатор може бути корисний для транспортування руди з шахт, спробуй!")
        add(AdvancementTexts.MINING_BUSINESS, "Досвічений шахтер")
        add(AdvancementTexts.MINING_BUSINESS_DESCRIPTION, "Оцифровка руди вже стала рутиною")
        add(AdvancementTexts.MINING_INDUSTRY, "Працівник гірничодобувної промисловості")
        add(AdvancementTexts.MINING_INDUSTRY_DESCRIPTION, "Це взагалі можливо зібрати так багато руди? Більше ніж 2000 вже оцифровано")

        add(ModStats.DIGITALIZED_ITEMS.get().value.toStatTranslationKey(), "Кількість предметів, що були оцифровані")
        add(ModStats.DIGITALIZED_ORES.get().value.toStatTranslationKey(), "Кількість руди, що була оцифрована")
        add(ModStats.DIGITALIZED_STARS.get().value.toStatTranslationKey(), "Кількість зірок Незеру, що була оцифрована")
    }
}
