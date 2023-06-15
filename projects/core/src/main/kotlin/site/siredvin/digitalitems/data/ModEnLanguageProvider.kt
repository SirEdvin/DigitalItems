package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import site.siredvin.digitalitems.common.setup.Blocks

class ModEnLanguageProvider(
    output: PackOutput,
) : ModLanguageProvider(output, "en_us") {
    override fun addTranslations() {
        add(Blocks.DIGITIZER.get(), "Digitizer")
        add(ModText.DIGITIZER, "Digitizer")
        add(ModText.CREATIVE_TAB, "Digital Items")
    }
}
