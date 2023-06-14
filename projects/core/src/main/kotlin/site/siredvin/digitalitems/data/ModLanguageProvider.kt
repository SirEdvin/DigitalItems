package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import site.siredvin.peripheralium.data.language.LanguageProvider
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.xplat.ModPlatform
import java.util.stream.Stream

abstract class ModLanguageProvider(output: PackOutput, locale: String) : LanguageProvider(
    output,
    DigitalItemsCore.MOD_ID,
    locale,
    ModPlatform.holder,
    *ModText.values(),
) {

    companion object {
        private val extraExpectedKeys: MutableList<String> = mutableListOf()

        fun addExpectedKey(key: String) {
            extraExpectedKeys.add(key)
        }
    }

    override fun getExpectedKeys(): Stream<String> {
        return Stream.concat(super.getExpectedKeys(), extraExpectedKeys.stream())
    }
}
