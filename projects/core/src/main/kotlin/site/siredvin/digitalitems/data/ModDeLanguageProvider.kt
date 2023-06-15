package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import site.siredvin.digitalitems.common.setup.Blocks

class ModDeLanguageProvider(
    output: PackOutput,
) : ModLanguageProvider(output, "de_de") {

    override fun addTranslations() {
        add(Blocks.DIGITIZER.get(), "Digitalisierer")
        add(ModText.DIGITIZER, "Digitalisierer")
        add(ModText.CREATIVE_TAB, "Digitalisierer")
    }
}
