package site.siredvin.digitalitems.data

import site.siredvin.peripheralium.data.language.TextRecord
import site.siredvin.digitalitems.DigitalItemsCore

enum class ModText : TextRecord {
    CREATIVE_TAB,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", DigitalItemsCore.MOD_ID, name.lowercase())
    }
}
