package site.siredvin.digitalitems.common.data;

import net.minecraft.world.level.saveddata.SavedData;

public final class DigitalItemsSavedDataType {
    private DigitalItemsSavedDataType() {}

    public static SavedData.Factory<DigitalItemsSavedData> createFactory() {
        // Native mod SavedData has no vanilla fixer; Kotlin 2.4 treats its annotation as non-null.
        return new SavedData.Factory<>(DigitalItemsSavedData.Companion::create, DigitalItemsSavedData.Companion::load, null);
    }
}
