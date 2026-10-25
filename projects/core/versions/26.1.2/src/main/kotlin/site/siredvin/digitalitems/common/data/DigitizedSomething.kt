package site.siredvin.digitalitems.common.data

import net.minecraft.core.UUIDUtil
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import site.siredvin.digitalitems.common.configuration.ModConfig
import java.util.UUID

abstract class DigitizedSomething<T> {
    var id: ByteArrayWrapper
    var something: T
    var digitizedAt: Long
    var lastRefresh: Long
    var decaysAt: Long
    var ownerUUID: UUID?

    constructor(id: ByteArrayWrapper, item: T, currentTime: Long, player: ServerPlayer?) {
        this.id = id
        something = item
        digitizedAt = currentTime
        lastRefresh = currentTime
        decaysAt = currentTime + ModConfig.decayTicks
        ownerUUID = player?.uuid
    }

    constructor(tag: CompoundTag) {
        id = ByteArrayWrapper(tag.getByteArray("id").orElseThrow())
        something = fromCompound(tag.getCompound(somethingTagName).orElseThrow())
        digitizedAt = tag.getLong("digitizedAt").orElseThrow()
        lastRefresh = tag.getLong("lastRefresh").orElseThrow()
        decaysAt = tag.getLong("decaysAt").orElseThrow()
        ownerUUID = tag.read("ownerUUID", UUIDUtil.CODEC).orElse(null)
    }

    abstract fun fromCompound(tag: CompoundTag): T
    abstract fun toCompound(): CompoundTag
    abstract fun shrink(amount: Int)
    abstract fun grow(amount: Int)
    abstract val somethingTagName: String
    abstract val isEmpty: Boolean
    abstract val amount: Long

    fun serialize(tag: CompoundTag) {
        tag.putByteArray("id", id.byteArray)
        tag.put(somethingTagName, toCompound())
        tag.putLong("digitizedAt", digitizedAt)
        tag.putLong("lastRefresh", lastRefresh)
        tag.putLong("decaysAt", decaysAt)
        ownerUUID?.let { tag.store("ownerUUID", UUIDUtil.CODEC, it) }
    }

    fun decayed(level: Level): Boolean = ModConfig.enableDecay && decaysAt <= level.server!!.overworld().gameTime

    fun refresh(currentTime: Long) {
        lastRefresh = currentTime
        decaysAt = currentTime + ModConfig.decayTicks
    }
}
