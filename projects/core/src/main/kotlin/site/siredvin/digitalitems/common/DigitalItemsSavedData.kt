package site.siredvin.digitalitems.common

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.common.configuration.ModConfig
import java.nio.ByteBuffer
import java.util.*
import java.util.function.Consumer

class DigitalItemsSavedData : SavedData() {
    private var digitizedItems: HashMap<ByteBuffer, DigitizedItem> = HashMap<ByteBuffer, DigitizedItem>()

    // remove digitized items which decayed; but only if decay is enabled
    fun prune(l: Level) {
        if (!ModConfig.enableDecay) {
            return
        }
        val currentTime = l.gameTime
        val it: MutableIterator<Map.Entry<ByteBuffer, DigitizedItem>> = digitizedItems.entries.iterator()
        it.forEachRemaining { (_, value): Map.Entry<ByteBuffer, DigitizedItem> ->
            if (currentTime >= value.decaysAt) {
                it.remove()
            }
        }
        setDirty()
    }

    fun add(item: DigitizedItem) {
        digitizedItems[item.id] = item
        setDirty()
    }

    fun get(id: ByteBuffer): DigitizedItem? {
        return digitizedItems[id]
    }

    fun pop(id: ByteBuffer): DigitizedItem? {
        val result = digitizedItems.remove(id)
        if (result != null) {
            setDirty()
        }
        return result
    }

    override fun save(tag: CompoundTag): CompoundTag {
        val items = ListTag()
        digitizedItems.values.forEach(
            Consumer { digitizedItem: DigitizedItem ->
                val digitizedItemTag = CompoundTag()
                digitizedItem.serialize(digitizedItemTag)
                items.add(digitizedItemTag)
            },
        )
        tag.put("items", items)
        return tag
    }

    companion object {
        private var instance: DigitalItemsSavedData? = null
        fun getFrom(l: Level): DigitalItemsSavedData {
            if (l !is ServerLevel) {
                throw IllegalCallerException("may only be called server side!")
            }
            if (instance != null) {
                return instance!!
            }
            instance = l.server.overworld().dataStorage.computeIfAbsent({ tag: CompoundTag -> load(tag) }, { create() }, DigitalItemsCore.MOD_ID)
            instance!!.prune(l)
            return instance!!
        }

        fun create(): DigitalItemsSavedData {
            return DigitalItemsSavedData()
        }

        fun load(tag: CompoundTag): DigitalItemsSavedData {
            val data = create()
            if (tag.contains("items") && tag["items"] is ListTag) {
                val list = Objects.requireNonNull(tag["items"]) as ListTag
                list.forEach(
                    Consumer { tag1: Tag ->
                        val di = DigitizedItem(Objects.requireNonNull(tag1) as CompoundTag)
                        data.digitizedItems[di.id] = di
                    },
                )
            }
            return data
        }
    }
}
