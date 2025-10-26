package site.siredvin.digitalitems.common.data

import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import site.siredvin.digitalitems.computercraft.AdvancedDigitizerPeripheral.Companion.rand
import site.siredvin.digitalitems.wrap
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner

abstract class DigitizedSomethingStrategy<T, V : DigitizedSomething<T>> {

    protected abstract val mode: String
    protected abstract val stackLimit: Long
    protected abstract fun getByIdRaw(id: ByteArrayWrapper, sd: DigitalItemsSavedData): V?
    protected abstract fun pop(digitizedSomething: V, sd: DigitalItemsSavedData)
    protected abstract fun singleRepresent(something: T): Any
    protected abstract fun isSame(something: T, another: T): Boolean
    protected abstract fun isEmpty(something: T): Boolean
    protected abstract fun amount(something: T): Long

    protected abstract fun extractFromSelf(owner: IPeripheralOwner, filter: Any?, limit: Long?, simulate: Boolean = false): T
    protected abstract fun extract(access: IComputerAccess, level: Level, source: String, filter: Any?, limit: Long?, simulate: Boolean = false): T
    protected abstract fun storeInSelf(owner: IPeripheralOwner, something: T, limit: Long): Long
    protected abstract fun store(access: IComputerAccess, level: Level, destination: String, something: T, limit: Long): Long
    protected abstract fun put(id: ByteArrayWrapper, something: T, peripheralOwner: IPeripheralOwner, sd: DigitalItemsSavedData)
    protected abstract fun awardDigitization(something: T, player: ServerPlayer?)

    open val limitLimit: Long
        get() = stackLimit / 2

    fun digitize(access: IComputerAccess, source: String, filter: Any?, limit: Long?, destination: ByteArrayWrapper?, peripheralOwner: IPeripheralOwner): MethodResult {
        val level = peripheralOwner.level!!
        val data: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(level)

        val target = if (destination != null) {
            val targetCandidate = getById(destination, data, level)
            if (targetCandidate == null || targetCandidate.isEmpty) {
                return MethodResult.of(null, "Destination doesn't exists anymore")
            }
            targetCandidate
        } else {
            null
        }

        val something = if (source == "self") {
            extractFromSelf(peripheralOwner, filter, limit, simulate = true)
        } else {
            extract(access, level, source, filter, limit, simulate = true)
        }

        if (isEmpty(something)) {
            return MethodResult.of(null, "There is nothing to digitize")
        }

        if (target != null) {
            if (!isSame(target.something, something)) {
                return MethodResult.of(null, "Can't merge found item into destination, they are different")
            }
            if (target.amount >= stackLimit) {
                return MethodResult.of(null, "Target stack is full")
            }
            // Magically limit everything to 64, because we don't want to digitize more than one stack of item
            val realLimit = (limit ?: limitLimit).coerceAtMost((stackLimit - target.amount))
            val realSomething = if (source == "self") {
                extractFromSelf(peripheralOwner, filter, realLimit)
            } else {
                extract(access, level, source, filter, realLimit)
            }
            target.refresh(level.gameTime)
            target.grow(amount(realSomething).toInt())
            awardDigitization(realSomething, peripheralOwner.owner as? ServerPlayer)
            data.setDirty()
            return MethodResult.of(target.id.byteArray)
        }
        val id = ByteArray(16)
        rand.nextBytes(id)
        val realSomething = if (source == "self") {
            extractFromSelf(peripheralOwner, filter, limit)
        } else {
            extract(access, level, source, filter, limit)
        }
        if (isEmpty(realSomething)) {
            return MethodResult.of(null, "There is nothing to digitize")
        }
        put(id.wrap(), realSomething, peripheralOwner, data)
        awardDigitization(realSomething, peripheralOwner.owner as? ServerPlayer)
        return MethodResult.of(id)
    }

    fun rematerialize(access: IComputerAccess, id: ByteArrayWrapper, limit: Long?, destination: String, peripheralOwner: IPeripheralOwner): MethodResult {
        val level = peripheralOwner.level!!
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(level)
        val something = getById(id, sd, level)
        if (something == null || something.isEmpty) {
            return MethodResult.of(null, "Nothing to rematerialize")
        }
        val realLimit = limit ?: something.amount
        val leftAmount = if (destination == "self") {
            storeInSelf(peripheralOwner, something.something, realLimit)
        } else {
            store(access, level, destination, something.something, realLimit)
        }
        val storedAmount = (something.amount - leftAmount.toLong()).toInt()
        if (storedAmount == 0) {
            return MethodResult.of(null, "Unable to place rematerialized object")
        }
        something.shrink(storedAmount)
        if (something.isEmpty) {
            pop(something, sd)
        }
        sd.setDirty()
        return MethodResult.of(storedAmount)
    }

    fun refresh(id: ByteArrayWrapper, level: Level): Boolean {
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(level)
        val something = getById(id, sd, level) ?: return false
        val currentTime: Long = level.gameTime
        something.refresh(currentTime)
        sd.setDirty()
        return true
    }

    fun getById(id: ByteArrayWrapper, sd: DigitalItemsSavedData, level: Level): V? {
        val something = getByIdRaw(id, sd) ?: return null
        if (something.decayed(level)) {
            sd.pop(id)
            return null
        }
        return something
    }

    fun represent(something: DigitizedSomething<T>, level: Level): MutableMap<String, Any> {
        val root = mutableMapOf<String, Any>()
        val currentTime: Long = level.gameTime
        root["currentTime"] = currentTime
        root["digitizedAt"] = something.digitizedAt
        root["decaysAt"] = something.decaysAt
        root["lastRefresh"] = something.lastRefresh
        root[mode] = singleRepresent(something.something)
        return root
    }
}
