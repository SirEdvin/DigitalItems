package site.siredvin.digitalitems.common.data

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.nbt.CompoundTag
import net.minecraft.server.level.ServerLevel
import net.minecraft.world.level.Level
import net.minecraft.world.level.saveddata.SavedData
import site.siredvin.digitalitems.common.configuration.ModConfig

class DigitalItemsSavedData : SavedData() {
    private val digitizedItems = mutableMapOf<ByteArrayWrapper, DigitizedItem>()
    private val digitizedFluids = mutableMapOf<ByteArrayWrapper, DigitizedFluid>()
    private val digitizedEnergy = mutableMapOf<ByteArrayWrapper, DigitizedEnergy>()
    private var pruned = false

    fun prune(level: Level) {
        if (!ModConfig.enableDecay) return
        val now = level.server!!.overworld().gameTime
        val changed = listOf(digitizedItems, digitizedFluids, digitizedEnergy)
            .map { map -> map.entries.removeIf { now >= it.value.decaysAt } }.any { it }
        if (changed) setDirty()
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
    fun getFluid(id: ByteArrayWrapper): DigitizedFluid? = digitizedFluids[id]
    fun getEnergy(id: ByteArrayWrapper): DigitizedEnergy? = digitizedEnergy[id]
    fun pop(id: ByteArrayWrapper): DigitizedItem? = digitizedItems.remove(id).also { if (it != null) setDirty() }
    fun popFluid(id: ByteArrayWrapper): DigitizedFluid? = digitizedFluids.remove(id).also { if (it != null) setDirty() }
    fun popEnergy(id: ByteArrayWrapper): DigitizedEnergy? = digitizedEnergy.remove(id).also { if (it != null) setDirty() }

    companion object {
        private val FLUID_CODEC = CompoundTag.CODEC.xmap(::DigitizedFluid) { value -> CompoundTag().also(value::serialize) }
        private val ENERGY_CODEC = CompoundTag.CODEC.xmap(::DigitizedEnergy) { value -> CompoundTag().also(value::serialize) }
        val CODEC: Codec<DigitalItemsSavedData> = RecordCodecBuilder.create { builder ->
            builder.group(
                DigitizedItem.CODEC.listOf().fieldOf("items").forGetter { it.digitizedItems.values.filterNot { value -> value.isEmpty } },
                FLUID_CODEC.listOf().fieldOf("fluids").forGetter { it.digitizedFluids.values.filterNot { value -> value.isEmpty } },
                ENERGY_CODEC.listOf().fieldOf("energies").forGetter { it.digitizedEnergy.values.filterNot { value -> value.isEmpty } },
            ).apply(builder) { items, fluids, energies ->
                DigitalItemsSavedData().also { data ->
                    items.forEach { data.digitizedItems[it.id] = it }
                    fluids.forEach { data.digitizedFluids[it.id] = it }
                    energies.forEach { data.digitizedEnergy[it.id] = it }
                }
            }
        }
        val TYPE = DigitalItemsSavedDataType.create(CODEC)

        fun getFrom(level: Level): DigitalItemsSavedData {
            check(level is ServerLevel) { "may only be called server side!" }
            // Server-owned storage avoids leaking cached data across integrated-server sessions.
            return level.server.dataStorage.computeIfAbsent(TYPE).also {
                if (!it.pruned) {
                    it.prune(level)
                    it.pruned = true
                }
            }
        }
    }
}
