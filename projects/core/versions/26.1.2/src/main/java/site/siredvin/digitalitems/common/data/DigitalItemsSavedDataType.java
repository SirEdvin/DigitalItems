package site.siredvin.digitalitems.common.data;

import com.mojang.serialization.Codec;
import net.minecraft.resources.Identifier;
import net.minecraft.world.level.saveddata.SavedDataType;

final class DigitalItemsSavedDataType {
    private DigitalItemsSavedDataType() {}

    static SavedDataType<DigitalItemsSavedData> create(Codec<DigitalItemsSavedData> codec) {
        // Native storage supports null for mod data without a vanilla data fixer.
        return new SavedDataType<>(Identifier.parse("digitalitems:digitalitems"), DigitalItemsSavedData::new, codec, null);
    }
}
