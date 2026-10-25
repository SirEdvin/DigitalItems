package site.siredvin.digitalitems.common.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import java.nio.ByteBuffer
import java.util.Optional

class DigitizedItem(id: ByteArrayWrapper, item: ItemStack, currentTime: Long, player: ServerPlayer?) : DigitizedSomething<ItemStack>(id, item, currentTime, player) {
    override fun fromCompound(tag: CompoundTag): ItemStack = error("Use the registry-aware DigitizedItem codec")
    override fun toCompound(): CompoundTag = error("Use the registry-aware DigitizedItem codec")
    override fun shrink(amount: Int) = something.shrink(amount)
    override fun grow(amount: Int) = something.grow(amount)
    override val somethingTagName: String get() = "itemStack"
    override val isEmpty: Boolean get() = something.isEmpty
    override val amount: Long get() = something.count.toLong()

    companion object {
        // Store one native stack plus a separate count: digital quantities exceed native stack limits.
        val CODEC: Codec<DigitizedItem> = RecordCodecBuilder.create { builder ->
            builder.group(
                Codec.BYTE_BUFFER.fieldOf("id").forGetter { ByteBuffer.wrap(it.id.byteArray) },
                ItemStack.CODEC.fieldOf("itemStack").forGetter { it.something.copyWithCount(1) },
                Codec.intRange(1, Int.MAX_VALUE).fieldOf("count").forGetter { it.something.count },
                Codec.LONG.fieldOf("digitizedAt").forGetter { it.digitizedAt },
                Codec.LONG.fieldOf("lastRefresh").forGetter { it.lastRefresh },
                Codec.LONG.fieldOf("decaysAt").forGetter { it.decaysAt },
                UUIDUtil.CODEC.optionalFieldOf("ownerUUID").forGetter { Optional.ofNullable(it.ownerUUID) },
            ).apply(builder) { id, stack, count, digitizedAt, lastRefresh, decaysAt, owner ->
                val bytes = ByteArray(id.remaining())
                id.get(bytes)
                DigitizedItem(ByteArrayWrapper(bytes), stack.copyWithCount(count), digitizedAt, null).also {
                    it.lastRefresh = lastRefresh
                    it.decaysAt = decaysAt
                    it.ownerUUID = owner.orElse(null)
                }
            }
        }
    }
}
