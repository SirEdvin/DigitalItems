package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import site.siredvin.digitalitems.common.setup.Blocks

class ModUaLanguageProvider(
    output: PackOutput,
) : ModLanguageProvider(output, "uk_ua") {
    override fun addTranslations() {
        add(Blocks.DIGITIZER.get(), "Цифровізатор")
        add(ModText.DIGITIZER, "Цифровізатор")
        add(ModText.CREATIVE_TAB, "Цифрові елементи")
    }
}
