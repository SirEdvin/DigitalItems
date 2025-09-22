package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import site.siredvin.broccolium.modules.data.lang.toStatTranslationKey
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.digitalitems.computercraft.AdvancedDigitizerPeripheral
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral

class ModUaLanguageProvider(
    output: PackOutput,
) : ModLanguageProvider(output, "uk_ua") {
    override fun addTranslations() {
        add(ModBlocks.DIGITIZER.get(), "Цифровізатор")
        add(ModBlocks.ADVANCED_DIGITIZER.get(), "Покращений цифровізатор")
        add(ModText.DIGITIZER, "Цифровізатор")
        add(ModText.ADVANCED_DIGITIZER, "Покращений цифровізатор")
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

        add(AdvancementTexts.ADVANCED_DIGITIZER, "Покращений цифровізатор")
        add(AdvancementTexts.ADVANCED_DIGITIZER_DESCRIPTION, "Він як звичаний, тільки краще! Може цифровізувати рідини та енергії, а також працює віддалено!")
        add(AdvancementTexts.DIGITIZE_FLUID, "Цифровізація рідин")
        add(AdvancementTexts.DIGITIZE_FLUID_DESCRIPTION, "Оцифруй 4 відра будь-якої рідини")
        add(AdvancementTexts.DIGITIZE_LAVA, "Цифровізація лави")
        add(AdvancementTexts.DIGITIZE_LAVA_DESCRIPTION, "Оцифруй 4 відра лаву")
        add(AdvancementTexts.FLUID_OVERDIGITALIZATION, "Стільки рідини достаньо, щоб наповнити океан?")
        add(AdvancementTexts.FLUID_OVERDIGITALIZATION_DESCRIPTION, "Ти оцифрував 9001 відро рідин? Цього точно має бути достаьно")
        add(AdvancementTexts.DIGITIZE_ENERGY, "Цифровізація енергії")
        add(AdvancementTexts.DIGITIZE_ENERGY_DESCRIPTION, "Безпровідна доставка енергії на новому рівні")
        add(AdvancementTexts.ENERGY_OVERDIGITALIZATION, "Зарядка хмарка")
        add(AdvancementTexts.ENERGY_OVERDIGITALIZATION_DESCRIPTION, "Ти оцифрував 1073741823 одиниць енергії. Цього певно достатньо, щоб вбити будь-яку цифрову сутність. Дякувати богу, що у нас тут таких немає.")

        add(ModStats.DIGITALIZED_ITEMS.get().value.toStatTranslationKey(), "Кількість предметів, що були оцифровані")
        add(ModStats.DIGITALIZED_ORES.get().value.toStatTranslationKey(), "Кількість руди, що була оцифрована")
        add(ModStats.DIGITALIZED_STARS.get().value.toStatTranslationKey(), "Кількість зірок Незеру, що була оцифрована")
        add(ModStats.DIGITALIZED_FLUIDS.get().value.toStatTranslationKey(), "Кількість рідини, що була оцифрована")
        add(ModStats.DIGITALIZED_LAVA.get().value.toStatTranslationKey(), "Кількість лави, що була оцифрована")
        add(ModStats.DIGITALIZED_ENERGY.get().value.toStatTranslationKey(), "Кількість енергії, що була оцифрована")

        addTurtle(DigitizerPeripheral.ID, "Цифровізуюча")
        addPocket(DigitizerPeripheral.ID, "Цифровізуючий")
        addTurtle(AdvancedDigitizerPeripheral.ID, "Покращена цифровізуюча")
        addPocket(AdvancedDigitizerPeripheral.ID, "Покращений цифровізуючий")
    }
}
