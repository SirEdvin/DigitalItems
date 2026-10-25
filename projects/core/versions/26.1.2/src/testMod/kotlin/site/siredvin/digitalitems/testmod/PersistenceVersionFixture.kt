package site.siredvin.digitalitems.testmod

import net.minecraft.core.component.DataComponentPatch
import net.minecraft.core.component.DataComponents
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.NbtOps
import net.minecraft.network.chat.Component
import net.minecraft.util.datafix.DataFixers
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.level.material.Fluids
import net.minecraft.world.level.storage.SavedDataStorage
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.digitalitems.common.data.DigitalItemsSavedData
import site.siredvin.digitalitems.common.data.DigitizedItem
import java.io.File

object PersistenceVersionFixture {
    fun serializeItem(item: DigitizedItem, helper: GameTestHelper): CompoundTag = DigitizedItem.CODEC.encodeStart(helper.level.registryAccess().createSerializationContext(NbtOps.INSTANCE), item).getOrThrow() as CompoundTag

    fun fluidStack(): AgnosticFluidStack = AgnosticFluidStack(Fluids.WATER, 1234.0, DataComponentPatch.builder().set(DataComponents.CUSTOM_NAME, Component.literal("Persisted fluid")).build())

    fun namedStack(): ItemStack = ItemStack(Items.DIAMOND_SWORD, 321).apply {
        set(DataComponents.CUSTOM_NAME, Component.literal("Persisted digitized stack"))
        damageValue = 17
    }

    fun reload(directory: File, helper: GameTestHelper): DigitalItemsSavedData = SavedDataStorage(directory.toPath(), DataFixers.getDataFixer(), helper.level.registryAccess()).use {
        requireNotNull(it.get(DigitalItemsSavedData.TYPE)) { "Copied native digital data must exist" }
    }
}
