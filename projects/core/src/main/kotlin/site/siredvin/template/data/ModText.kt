package site.siredvin.template.data

import site.siredvin.peripheralium.data.language.TextRecord
import site.siredvin.template.TemplateCore

enum class ModText : TextRecord {
    CREATIVE_TAB,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", TemplateCore.MOD_ID, name.lowercase())
    }
}
