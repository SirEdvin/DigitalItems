package site.siredvin.digitalitems.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import site.siredvin.broccolium.modules.platform.PlatformRegistries

class DigitizedItem : DigitizedSomething<ItemStack> {

    constructor(id: ByteArrayWrapper, item: ItemStack, currentTime: Long, player: ServerPlayer?) : super(id, item, currentTime, player)

    constructor(compoundTag: CompoundTag) : super(compoundTag)

    override fun fromCompound(tag: CompoundTag): ItemStack {
        val id = tag.getString("id")
        val item = PlatformRegistries.ITEMS.tryGet(ResourceLocation(id)) ?: return ItemStack.EMPTY
        val baseStack = item.defaultInstance
        baseStack.count = tag.getInt("Count")
        if (tag.contains("tag")) {
            baseStack.tag = tag.getCompound("tag")
        }
        if (baseStack.item.canBeDepleted()) {
            baseStack.damageValue = baseStack.damageValue
        }
        return baseStack
    }

    override fun toCompound(): CompoundTag {
        val id = PlatformRegistries.ITEMS.getKey(something.item)
        val tag = CompoundTag()
        tag.putString("id", id.toString())
        tag.putInt("Count", something.count)
        if (something.tag != null) {
            tag.put("tag", something.tag!!.copy())
        }
        return tag
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
}
