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
import site.siredvin.tweakium.modules.peripheral.api.ISidedPeripheral
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation

class DigitizedFluidStrategy : DigitizedSomethingStrategy<AgnosticFluidStack, DigitizedFluid>() {
    override val mode: String
        get() = "fluid"
    override val stackLimit: Long
        get() = ModConfig.fluidStackLimit

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

    override fun amount(something: AgnosticFluidStack): Long = something.amount.toLong()

    private fun extractFromStorage(target: AgnosticFluidStorage, filter: Any?, limit: Long?, simulate: Boolean): AgnosticFluidStack {
        if (filter == null) {
            return target.take({ true }, limit?.toDouble() ?: Double.MAX_VALUE, simulate)
        }
        return target.take({ PlatformRegistries.FLUIDS.getKey(it.fluid).toString() == filter }, limit?.toDouble() ?: Double.MAX_VALUE, simulate)
    }

    override fun extractFromSelf(
        owner: IPeripheralOwner,
        filter: Any?,
        limit: Long?,
        simulate: Boolean,
    ): AgnosticFluidStack = throw LuaException("Digitizer itself is invalid target for fluid extraction")

    override fun extract(
        access: IComputerAccess,
        level: Level,
        source: String,
        filter: Any?,
        limit: Long?,
        simulate: Boolean,
    ): AgnosticFluidStack {
        val peripheral = access.getAvailablePeripheral(source) ?: throw LuaException("Cannot find $source")
        val direction = if (peripheral is ISidedPeripheral) peripheral.side else null
        val storage = AgnosticFluidStorageLookup.extractFromUnknown(level, peripheral.target, direction) ?: throw LuaException("$source is not fluid storage")
        return extractFromStorage(storage, filter, limit, simulate)
    }

    private fun storeInStorage(target: AgnosticFluidStorage, something: AgnosticFluidStack, limit: Long): Long {
        val realLimit: Long = limit.coerceAtMost(something.amount.toLong())
        val stackToStore = if (something.amount.toLong() != realLimit) {
            something.copyWithCount(realLimit.toDouble())
        } else {
            something.copy()
        }
        val amountToStore = stackToStore.amount
        val reminder = target.store(stackToStore, false)
        return (reminder.amount + (something.amount - amountToStore)).toLong()
    }

    override fun storeInSelf(
        owner: IPeripheralOwner,
        something: AgnosticFluidStack,
        limit: Long,
    ): Long = throw LuaException("Digitizer itself is invalid target for fluid storage")

    override fun store(
        access: IComputerAccess,
        level: Level,
        destination: String,
        something: AgnosticFluidStack,
        limit: Long,
    ): Long {
        val peripheral = access.getAvailablePeripheral(destination) ?: throw LuaException("Cannot find $destination")
        val direction = if (peripheral is ISidedPeripheral) peripheral.side else null
        val storage = AgnosticFluidStorageLookup.extractFromUnknown(level, peripheral.target, direction) ?: throw LuaException("$destination is not fluid storage")
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
