package site.siredvin.digitalitems.common.data

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.level.Level
import net.minecraft.world.level.material.Fluids
import site.siredvin.broccolium.modules.platform.PlatformRegistries
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStack
import site.siredvin.broccolium.modules.storage.fluid.AgnosticFluidStorageLookup
import site.siredvin.broccolium.modules.storage.fluid.FluidStorageUtils
import site.siredvin.broccolium.modules.storage.fluid.api.AgnosticFluidStorage
import site.siredvin.digitalitems.common.configuration.ModConfig
import site.siredvin.digitalitems.common.setup.ModCriterias
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation

class DigitizedFluidStrategy : DigitizedSomethingStrategy<AgnosticFluidStack, DigitizedFluid>() {
    override val mode: String
        get() = "item"
    override val stackLimit: Long
        get() = ModConfig.itemStackLimit.toLong()

    override fun getByIdRaw(id: ByteArrayWrapper, sd: DigitalItemsSavedData): DigitizedFluid? = sd.getFluid(id)

    override fun pop(digitizedSomething: DigitizedFluid, sd: DigitalItemsSavedData) {
        sd.popFluid(digitizedSomething.id)
    }

    override fun singleRepresent(something: AgnosticFluidStack): Any = LuaRepresentation.forFluidStack(something)

    override fun isSame(
        something: AgnosticFluidStack,
        another: AgnosticFluidStack,
    ): Boolean = FluidStorageUtils.canStack(something, another)

    override fun isEmpty(something: AgnosticFluidStack): Boolean = something.isEmpty

    override fun amount(something: AgnosticFluidStack): Long = something.amount

    private fun extractFromStorage(target: AgnosticFluidStorage, filter: Any?, limit: Int?, simulate: Boolean): AgnosticFluidStack {
        if (simulate) {
            val stack = when (filter) {
                null -> {
                    target.getFluids().asSequence().filter { !it.isEmpty }.first()
                }
                else -> {
                    target.getFluids().asSequence().filter { PlatformRegistries.FLUIDS.getKey(it.fluid).toString() == filter }.first()
                }
            }
            return stack.copyWithCount(limit?.toLong() ?: stack.amount)
        }
        if (filter == null) {
            return target.takeFluid({ true }, limit?.toLong() ?: Long.MAX_VALUE)
        }
        return target.takeFluid({ PlatformRegistries.FLUIDS.getKey(it.fluid).toString() == filter }, limit?.toLong() ?: Long.MAX_VALUE)
    }

    override fun extractFromSelf(
        owner: IPeripheralOwner,
        filter: Any?,
        limit: Int?,
        simulate: Boolean,
    ): AgnosticFluidStack = throw LuaException("Digitizer itself is invalid target for fluid extraction")

    override fun extract(
        access: IComputerAccess,
        level: Level,
        source: String,
        filter: Any?,
        limit: Int?,
        simulate: Boolean,
    ): AgnosticFluidStack {
        val peripheral = access.getAvailablePeripheral(source) ?: throw LuaException("Cannot find $source")
        val storage = AgnosticFluidStorageLookup.extractFluidStorageFromUnknown(level, peripheral.target) ?: throw LuaException("$source is not fluid storage")
        return extractFromStorage(storage, filter, limit, simulate)
    }

    private fun storeInStorage(target: AgnosticFluidStorage, something: AgnosticFluidStack, limit: Int): Int {
        val realLimit = limit.toLong().coerceAtMost(something.amount)
        val stackToStore = if (something.amount != realLimit) {
            something.copyWithCount(realLimit)
        } else {
            something.copy()
        }
        val amountToStore = stackToStore.amount
        val reminder = target.storeFluid(stackToStore)
        return (reminder.amount + (something.amount - amountToStore)).toInt()
    }

    override fun storeInSelf(
        owner: IPeripheralOwner,
        something: AgnosticFluidStack,
        limit: Int,
    ): Int = throw LuaException("Digitizer itself is invalid target for fluid storage")

    override fun store(
        access: IComputerAccess,
        level: Level,
        destination: String,
        something: AgnosticFluidStack,
        limit: Int,
    ): Int {
        val peripheral = access.getAvailablePeripheral(destination) ?: throw LuaException("Cannot find $destination")
        val storage = AgnosticFluidStorageLookup.extractFluidStorageFromUnknown(level, peripheral.target) ?: throw LuaException("$destination is not fluid storage")
        return storeInStorage(storage, something, limit)
    }

    override fun put(
        id: ByteArrayWrapper,
        something: AgnosticFluidStack,
        peripheralOwner: IPeripheralOwner,
        sd: DigitalItemsSavedData,
    ) {
        val item = DigitizedFluid(
            id,
            something,
            peripheralOwner.level!!.gameTime,
            peripheralOwner.owner as? ServerPlayer,
        )
        sd.add(item)
        sd.setDirty()
    }

    override fun awardDigitization(
        something: AgnosticFluidStack,
        player: ServerPlayer?,
    ) {
        if (player != null) {
            player.awardStat(ModStats.DIGITALIZED_FLUIDS.get(), something.amount.toInt())
            ModCriterias.DIGITALIZE_FLUIDS.trigger(player)
            if (something.fluid.isSame(Fluids.LAVA)) {
                player.awardStat(ModStats.DIGITALIZED_LAVA.get(), something.amount.toInt())
                ModCriterias.DIGITALIZE_LAVA.trigger(player)
            }
        }
    }
}
