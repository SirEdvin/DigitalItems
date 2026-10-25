package site.siredvin.digitalitems.testmod

import net.minecraft.core.component.DataComponents
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.network.chat.Component
import net.minecraft.util.datafix.DataFixTypes
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.saveddata.SavedData
import net.minecraft.world.level.storage.DimensionDataStorage
import site.siredvin.digitalitems.common.data.DigitalItemsSavedData
import java.io.File

object PersistenceVersionFixture {
    fun namedStack(): ItemStack = ItemStack(Items.DIAMOND_SWORD, 321).apply {
        set(DataComponents.CUSTOM_NAME, Component.literal("Persisted digitized stack"))
        damageValue = 17
    }

    @Suppress("NULLABILITY_MISMATCH_BASED_ON_JAVA_ANNOTATIONS")
    fun reload(directory: File, helper: GameTestHelper): DigitalItemsSavedData = DimensionDataStorage(directory, DataFixers.getDataFixer(), helper.level.registryAccess()).computeIfAbsent(
        SavedData.Factory(DigitalItemsSavedData::create, DigitalItemsSavedData::load, null as DataFixTypes?),
        "digitalitems",
    )
}
