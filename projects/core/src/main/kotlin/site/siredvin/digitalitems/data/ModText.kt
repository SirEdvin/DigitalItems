package site.siredvin.digitalitems.data

import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.peripheralium.data.language.TextRecord

enum class ModText : TextRecord {
    CREATIVE_TAB,
    DIGITIZER,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", DigitalItemsCore.MOD_ID, name.lowercase())
    }
}
