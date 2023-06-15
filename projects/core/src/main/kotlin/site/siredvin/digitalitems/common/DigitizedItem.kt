package site.siredvin.digitalitems.common

import net.minecraft.nbt.CompoundTag
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.digitalitems.common.configuration.ModConfig
import java.nio.ByteBuffer

class DigitizedItem {
    var id: ByteBuffer
    var item: ItemStack
    var digitizedAt: Long
    var lastRefresh: Long
    var decaysAt: Long

    constructor(id: ByteArray, item: ItemStack, currentTime: Long) {
        this.id = ByteBuffer.wrap(id)
        this.item = item
        digitizedAt = currentTime
        lastRefresh = currentTime
        decaysAt = currentTime + ModConfig.decayTicks
    }

    constructor(compoundTag: CompoundTag) {
        id = ByteBuffer.wrap(compoundTag.getByteArray("id"))
        item = ItemStack.of(compoundTag.getCompound("itemStack"))
        digitizedAt = compoundTag.getLong("digitizedAt")
        lastRefresh = compoundTag.getLong("lastRefresh")
        decaysAt = compoundTag.getLong("decaysAt")
    }

    fun serialize(compoundTag: CompoundTag) {
        compoundTag.putByteArray("id", getBytes(id))
        compoundTag.put("itemStack", item.save(CompoundTag()))
        compoundTag.putLong("digitizedAt", digitizedAt)
        compoundTag.putLong("lastRefresh", lastRefresh)
        compoundTag.putLong("decaysAt", decaysAt)
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

    companion object {
        private fun getBytes(buf: ByteBuffer): ByteArray {
            if (buf.hasArray()) {
                return buf.array()
            }
            val bytes = ByteArray(buf.remaining())
            buf[bytes]
            return bytes
        }
    }
}
