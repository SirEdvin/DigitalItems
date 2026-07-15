package site.siredvin.digitalitems.common.data

import net.minecraft.core.HolderLookup
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack

class DigitizedItem : DigitizedSomething<ItemStack> {

    constructor(id: ByteArrayWrapper, item: ItemStack, currentTime: Long, player: ServerPlayer?) : super(id, item, currentTime, player)

    constructor(compoundTag: CompoundTag, registries: HolderLookup.Provider) : super(
        ByteArrayWrapper(compoundTag.getByteArray("id")),
        deserializeStack(compoundTag, registries),
        compoundTag.getLong("digitizedAt"),
        null,
    ) {
        lastRefresh = compoundTag.getLong("lastRefresh")
        decaysAt = compoundTag.getLong("decaysAt")
        ownerUUID = if (compoundTag.hasUUID("ownerUUID")) compoundTag.getUUID("ownerUUID") else null
    }

    override fun fromCompound(tag: CompoundTag): ItemStack {
        error("ItemStack deserialization requires registry access")
    }

    override fun toCompound(): CompoundTag = error("ItemStack serialization requires registry access")

    fun serialize(compoundTag: CompoundTag, registries: HolderLookup.Provider) {
        compoundTag.putByteArray("id", id.byteArray)
        compoundTag.put(somethingTagName, something.copyWithCount(1).save(registries))
        compoundTag.putInt("count", something.count)
        compoundTag.putLong("digitizedAt", digitizedAt)
        compoundTag.putLong("lastRefresh", lastRefresh)
        compoundTag.putLong("decaysAt", decaysAt)
        ownerUUID?.let { compoundTag.putUUID("ownerUUID", it) }
    }

    override fun shrink(amount: Int) {
        something.shrink(amount)
    }

    override fun grow(amount: Int) {
        something.grow(amount)
    }

    override val somethingTagName: String
        get() = "itemStack"
    override val isEmpty: Boolean
        get() = something.isEmpty
    override val amount: Long
        get() = something.count.toLong()

    companion object {
        private fun deserializeStack(compoundTag: CompoundTag, registries: HolderLookup.Provider): ItemStack = ItemStack.parseOptional(registries, compoundTag.getCompound("itemStack")).also {
            if (compoundTag.contains("count")) it.count = compoundTag.getInt("count")
        }
    }
}
