package site.siredvin.digitalitems.common.data

import net.minecraft.nbt.CompoundTag
import net.minecraft.nbt.ListTag
import net.minecraft.nbt.Tag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.common.configuration.ModConfig
import java.util.*
import java.util.function.Consumer

class DigitalItemsSavedData : SavedData() {
    private var digitizedItems: MutableMap<ByteArrayWrapper, DigitizedItem> = mutableMapOf()
    private var digitizedFluids: MutableMap<ByteArrayWrapper, DigitizedFluid> = mutableMapOf()
    private var digitizedEnergy: MutableMap<ByteArrayWrapper, DigitizedEnergy> = mutableMapOf()

    private fun <T : DigitizedSomething<*>> pruneSomething(l: Level, map: MutableMap<ByteArrayWrapper, T>) {
        if (!ModConfig.enableDecay) {
            return
        }
        val currentTime = l.gameTime
        val it: MutableIterator<Map.Entry<ByteArrayWrapper, T>> = map.entries.iterator()
        it.forEachRemaining { (_, value): Map.Entry<ByteArrayWrapper, T> ->
            if (currentTime >= value.decaysAt) {
                it.remove()
            }
        }
        setDirty()
    }

    // remove digitized items which decayed; but only if decay is enabled
    fun prune(l: Level) {
        pruneSomething(l, digitizedItems)
        pruneSomething(l, digitizedEnergy)
        pruneSomething(l, digitizedFluids)
    }

    fun add(item: DigitizedItem) {
        digitizedItems[item.id] = item
        setDirty()
    }

    fun add(item: DigitizedFluid) {
        digitizedFluids[item.id] = item
        setDirty()
    }

    fun add(item: DigitizedEnergy) {
        digitizedEnergy[item.id] = item
        setDirty()
    }

    fun get(id: ByteArrayWrapper): DigitizedItem? = digitizedItems[id]

    fun pop(id: ByteArrayWrapper): DigitizedItem? {
        val result = digitizedItems.remove(id)
        if (result != null) {
            setDirty()
        }
        return result
    }

    fun getFluid(id: ByteArrayWrapper): DigitizedFluid? = digitizedFluids[id]

    fun popFluid(id: ByteArrayWrapper): DigitizedFluid? {
        val result = digitizedFluids.remove(id)
        if (result != null) {
            setDirty()
        }
        return result
    }

    fun getEnergy(id: ByteArrayWrapper): DigitizedEnergy? = digitizedEnergy[id]

    fun popEnergy(id: ByteArrayWrapper): DigitizedEnergy? {
        val result = digitizedEnergy.remove(id)
        if (result != null) {
            setDirty()
        }
        return result
    }

    private fun <T : DigitizedSomething<*>> saveSomething(map: MutableMap<ByteArrayWrapper, T>): ListTag {
        val items = ListTag()
        map.values.filter { !it.isEmpty }.forEach(
            Consumer { digitizedItem: T ->
                val digitizedItemTag = CompoundTag()
                digitizedItem.serialize(digitizedItemTag)
                items.add(digitizedItemTag)
            },
        )
        return items
    }

    override fun save(tag: CompoundTag): CompoundTag {
        tag.put("items", saveSomething(digitizedItems))
        tag.put("fluids", saveSomething(digitizedFluids))
        tag.put("energies", saveSomething(digitizedEnergy))
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

        fun create(): DigitalItemsSavedData = DigitalItemsSavedData()

        fun load(tag: CompoundTag): DigitalItemsSavedData {
            val data = create()
            if (tag.contains("items") && tag["items"] is ListTag) {
                val list = Objects.requireNonNull(tag["items"]) as ListTag
                list.forEach(
                    Consumer { tag1: Tag ->
                        val di = DigitizedItem(Objects.requireNonNull(tag1) as CompoundTag)
                        if (!di.isEmpty) {
                            data.digitizedItems[di.id] = di
                        }
                    },
                )
            }
            if (tag.contains("fluids") && tag["fluids"] is ListTag) {
                val list = Objects.requireNonNull(tag["fluids"]) as ListTag
                list.forEach(
                    Consumer { tag1: Tag ->
                        val di = DigitizedFluid(Objects.requireNonNull(tag1) as CompoundTag)
                        if (!di.isEmpty) {
                            data.digitizedFluids[di.id] = di
                        }
                    },
                )
            }
            if (tag.contains("energies") && tag["energies"] is ListTag) {
                val list = Objects.requireNonNull(tag["energies"]) as ListTag
                list.forEach(
                    Consumer { tag1: Tag ->
                        val di = DigitizedEnergy(Objects.requireNonNull(tag1) as CompoundTag)
                        if (!di.isEmpty) {
                            data.digitizedEnergy[di.id] = di
                        }
                    },
                )
            }
            return data
        }
    }
}
