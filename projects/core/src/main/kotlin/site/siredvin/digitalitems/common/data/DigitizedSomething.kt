package site.siredvin.digitalitems.common.data

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
        this.something = item
        digitizedAt = currentTime
        lastRefresh = currentTime
        decaysAt = currentTime + ModConfig.decayTicks
        ownerUUID = player?.uuid
    }

    constructor(compoundTag: CompoundTag) {
        id = ByteArrayWrapper(compoundTag.getByteArray("id"))
        something = fromCompound(compoundTag.getCompound(somethingTagName))
        digitizedAt = compoundTag.getLong("digitizedAt")
        lastRefresh = compoundTag.getLong("lastRefresh")
        decaysAt = compoundTag.getLong("decaysAt")
        ownerUUID = if (compoundTag.hasUUID("ownerUUID")) {
            compoundTag.getUUID("ownerUUID")
        } else {
            null
        }
    }

    abstract fun fromCompound(tag: CompoundTag): T
    abstract fun toCompound(): CompoundTag
    abstract fun shrink(amount: Int)
    abstract fun grow(amount: Int)
    abstract val somethingTagName: String
    abstract val isEmpty: Boolean
    abstract val amount: Long

    fun serialize(compoundTag: CompoundTag) {
        compoundTag.putByteArray("id", id.byteArray)
        compoundTag.put(somethingTagName, toCompound())
        compoundTag.putLong("digitizedAt", digitizedAt)
        compoundTag.putLong("lastRefresh", lastRefresh)
        compoundTag.putLong("decaysAt", decaysAt)
        if (ownerUUID != null) {
            compoundTag.putUUID("ownerUUID", ownerUUID!!)
        }
    }

    fun decayed(level: Level): Boolean {
        if (!ModConfig.enableDecay) {
            return false
        }
        return decaysAt <= level.server!!.overworld().gameTime
    }

    fun refresh(currentTime: Long) {
        this.lastRefresh = currentTime
        this.decaysAt = currentTime + ModConfig.decayTicks
    }
}
