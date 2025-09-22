package site.siredvin.digitalitems.common.data

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStack
import site.siredvin.broccolium.modules.storage.energy.AgnosticEnergyStorageLookup
import site.siredvin.broccolium.modules.storage.energy.EnergyStorageUtils
import site.siredvin.broccolium.modules.storage.energy.api.AgnosticEnergyStorage
import site.siredvin.digitalitems.common.configuration.ModConfig
import site.siredvin.digitalitems.common.setup.ModCriterias
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation

class DigitizedEnergyStrategy : DigitizedSomethingStrategy<AgnosticEnergyStack, DigitizedEnergy>() {
    override val mode: String
        get() = "item"
    override val stackLimit: Long
        get() = ModConfig.itemStackLimit.toLong()

    override fun getByIdRaw(id: ByteArrayWrapper, sd: DigitalItemsSavedData): DigitizedEnergy? = sd.getEnergy(id)

    override fun pop(digitizedSomething: DigitizedEnergy, sd: DigitalItemsSavedData) {
        sd.popEnergy(digitizedSomething.id)
    }

    override fun singleRepresent(something: AgnosticEnergyStack): Any = LuaRepresentation.forEnergyStack(something)

    override fun isSame(
        something: AgnosticEnergyStack,
        another: AgnosticEnergyStack,
    ): Boolean = EnergyStorageUtils.canStack(something, another)

    override fun isEmpty(something: AgnosticEnergyStack): Boolean = something.isEmpty

    override fun amount(something: AgnosticEnergyStack): Long = something.amount

    private fun extractFromStorage(target: AgnosticEnergyStorage, filter: Any?, limit: Int?, simulate: Boolean): AgnosticEnergyStack {
        if (simulate) {
            val stack = target.energy
            if (stack.unit.name != filter) {
                return stack.copyWithCount(0)
            }
            return stack.copyWithCount(limit?.toLong() ?: stack.amount)
        }
        if (filter == null) {
            return target.takeEnergy({ true }, limit?.toLong() ?: Long.MAX_VALUE)
        }
        return target.takeEnergy({ it.unit.name == filter }, limit?.toLong() ?: Long.MAX_VALUE)
    }

    override fun extractFromSelf(
        owner: IPeripheralOwner,
        filter: Any?,
        limit: Int?,
        simulate: Boolean,
    ): AgnosticEnergyStack = throw LuaException("Digitizer itself is invalid target for energy extraction")

    override fun extract(
        access: IComputerAccess,
        level: Level,
        source: String,
        filter: Any?,
        limit: Int?,
        simulate: Boolean,
    ): AgnosticEnergyStack {
        val peripheral = access.getAvailablePeripheral(source) ?: throw LuaException("Cannot find $source")
        val storage = AgnosticEnergyStorageLookup.extractEnergyStorageFromUnknown(level, peripheral.target) ?: throw LuaException("$source is not energy storage")
        return extractFromStorage(storage, filter, limit, simulate)
    }

    private fun storeInStorage(target: AgnosticEnergyStorage, something: AgnosticEnergyStack, limit: Int): Int {
        val realLimit = limit.toLong().coerceAtMost(something.amount)
        val stackToStore = if (something.amount != realLimit) {
            something.copyWithCount(realLimit)
        } else {
            something.copy()
        }
        val amountToStore = stackToStore.amount
        val reminder = target.storeEnergy(stackToStore)
        return (reminder.amount + (something.amount - amountToStore)).toInt()
    }

    override fun storeInSelf(
        owner: IPeripheralOwner,
        something: AgnosticEnergyStack,
        limit: Int,
    ): Int = throw LuaException("Digitizer itself is invalid target for energy storage")

    override fun store(
        access: IComputerAccess,
        level: Level,
        destination: String,
        something: AgnosticEnergyStack,
        limit: Int,
    ): Int {
        val peripheral = access.getAvailablePeripheral(destination) ?: throw LuaException("Cannot find $destination")
        val storage = AgnosticEnergyStorageLookup.extractEnergyStorageFromUnknown(level, peripheral.target) ?: throw LuaException("$destination is not energy storage")
        return storeInStorage(storage, something, limit)
    }

    override fun put(
        id: ByteArrayWrapper,
        something: AgnosticEnergyStack,
        peripheralOwner: IPeripheralOwner,
        sd: DigitalItemsSavedData,
    ) {
        val item = DigitizedEnergy(
            id,
            something,
            peripheralOwner.level!!.gameTime,
            peripheralOwner.owner as? ServerPlayer,
        )
        sd.add(item)
        sd.setDirty()
    }

    override fun awardDigitization(
        something: AgnosticEnergyStack,
        player: ServerPlayer?,
    ) {
        if (player != null) {
            player.awardStat(ModStats.DIGITALIZED_ENERGY.get(), something.amount.toInt())
            ModCriterias.DIGITALIZE_ENERGY.trigger(player)
        }
    }
}
