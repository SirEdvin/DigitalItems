package site.siredvin.digitalitems.testmod

import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.network.chat.Component
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.storage.DimensionDataStorage
import site.siredvin.digitalitems.common.data.DigitalItemsSavedData
import java.io.File

object PersistenceVersionFixture {
    fun namedStack(): ItemStack = ItemStack(Items.DIAMOND_SWORD, 321).apply {
        setHoverName(Component.literal("Persisted digitized stack"))
        damageValue = 17
    }

    fun reload(directory: File, helper: GameTestHelper): DigitalItemsSavedData = DimensionDataStorage(directory, DataFixers.getDataFixer()).computeIfAbsent(
        DigitalItemsSavedData::load,
        DigitalItemsSavedData::create,
        "digitalitems",
    )
}
