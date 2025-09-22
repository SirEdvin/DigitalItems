package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.tweakium.modules.data.ComputerLanguageProvider
import java.util.stream.Stream

abstract class ModLanguageProvider(output: PackOutput, locale: String) :
    ComputerLanguageProvider(
        output,
        DigitalItemsCore.MOD_ID,
        locale,
        ModPlatform.holder,
        *ModText.entries.toTypedArray(),
        *AdvancementTexts.entries.toTypedArray(),
    ) {

    companion object {
        private val extraExpectedKeys: MutableList<String> = mutableListOf()

        fun addExpectedKey(key: String) {
            extraExpectedKeys.add(key)
        }
    }

    override fun getExpectedKeys(): Stream<String> = Stream.concat(super.getExpectedKeys(), extraExpectedKeys.stream())
}
