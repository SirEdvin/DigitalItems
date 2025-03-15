package site.siredvin.digitalitems.data

import site.siredvin.broccolium.modules.data.api.TextRecord
import site.siredvin.digitalitems.DigitalItemsCore

enum class ModText : TextRecord {
    CREATIVE_TAB,
    DIGITIZER,
    ;

    override val textID: String by lazy {
        String.format("text.%s.%s", DigitalItemsCore.MOD_ID, name.lowercase())
    }
}
