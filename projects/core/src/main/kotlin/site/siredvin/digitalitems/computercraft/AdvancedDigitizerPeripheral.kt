package site.siredvin.digitalitems.computercraft

import dan200.computercraft.api.lua.IArguments
import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import dan200.computercraft.api.lua.MethodResult
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.resources.ResourceLocation
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.assertBetween
import site.siredvin.digitalitems.common.configuration.ModConfig
import site.siredvin.digitalitems.common.data.DigitalItemsSavedData
import site.siredvin.digitalitems.common.data.DigitizedEnergyStrategy
import site.siredvin.digitalitems.common.data.DigitizedFluidStrategy
import site.siredvin.digitalitems.common.data.DigitizedItemStrategy
import site.siredvin.digitalitems.common.data.DigitizedSomething
import site.siredvin.digitalitems.common.data.DigitizedSomethingStrategy
import site.siredvin.digitalitems.toSafeArray
import site.siredvin.digitalitems.wrap
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner
import site.siredvin.tweakium.modules.plugins.InventoryPlugin
import site.siredvin.tweakium.modules.plugins.SuppliedRudimentInventoryPlugin
import java.nio.ByteBuffer
import java.security.SecureRandom
import kotlin.jvm.optionals.getOrDefault
import kotlin.jvm.optionals.getOrNull

class AdvancedDigitizerPeripheral<T : IPeripheralOwner>(peripheralOwner: T) :
    OwnedPeripheral<T>(
        TYPE,
        peripheralOwner,
    ) {

    companion object {
        val rand = SecureRandom()
        const val TYPE = "advanced_digitizer"
        val ID = ResourceLocation.fromNamespaceAndPath(DigitalItemsCore.MOD_ID, TYPE)
        val STRATEGIES: Map<String, DigitizedSomethingStrategy<*, *>> = mapOf(
            "item" to DigitizedItemStrategy(),
            "fluid" to DigitizedFluidStrategy(),
            "energy" to DigitizedEnergyStrategy(),
        )
    }

    init {
        if (peripheralOwner is BlockEntityPeripheralOwner<*>) {
            addPlugin(InventoryPlugin(peripheralOwner.level!!, peripheralOwner.storage!!, ModConfig.inventoryTransferLimit))
        } else {
            addPlugin(SuppliedRudimentInventoryPlugin({ peripheralOwner.level!! }, { peripheralOwner.storage!! }))
        }
    }

    override val isEnabled: Boolean
        get() = true

    override fun getType(): String = TYPE

    override val peripheralConfiguration: MutableMap<String, Any>
        get() {
            val base = super.peripheralConfiguration
            base["decayEnabled"] = ModConfig.enableDecay
            base["decayTicks"] = ModConfig.decayTicks
            base["itemStackLimit"] = ModConfig.itemStackLimit
            base["fluidStackLimit"] = ModConfig.fluidStackLimit
            base["energyStackLimit"] = ModConfig.energyStackLimit
            return base
        }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun digitize(access: IComputerAccess, arguments: IArguments): MethodResult {
        // Signature digitize(mode, source, filter?, limit?, destination?)
        val mode = arguments.getString(0)
        val source = arguments.optString(1).getOrDefault("self")
        val filter = arguments.get(2)
        val limit = arguments.optLong(3).getOrNull()
        val destination = arguments.optBytes(4).getOrNull()

        val strategy = STRATEGIES[mode] ?: throw LuaException("There is no such mode")

        if (limit != null) {
            assertBetween(limit, 1, strategy.limitLimit, "limit")
        }
        return strategy.digitize(access, source, filter, limit, destination?.toSafeArray()?.wrap(), peripheralOwner)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun rematerialize(access: IComputerAccess, arguments: IArguments): MethodResult {
        // Signature rematerialize(mode, id, limit?, destination?)
        val mode = arguments.getString(0)
        val id = arguments.getBytes(1)
        val limit = arguments.optLong(2).getOrNull()
        val destination = arguments.optString(3).getOrDefault("self")
        val strategy = STRATEGIES[mode] ?: throw LuaException("There is no such mode")

        if (limit != null) {
            assertBetween(limit, 1, strategy.limitLimit, "limit")
        }
        return strategy.rematerialize(access, id.toSafeArray().wrap(), limit, destination, peripheralOwner)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun refresh(mode: String, id: ByteBuffer): Boolean {
        val strategy = STRATEGIES[mode] ?: throw LuaException("There is no such mode")
        return strategy.refresh(id.toSafeArray().wrap(), peripheralOwner.level!!)
    }

    @Suppress("UNCHECKED_CAST")
    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun get(mode: String, id: ByteBuffer): Map<String, Any>? {
        val strategy = STRATEGIES[mode] as? DigitizedSomethingStrategy<Any, DigitizedSomething<Any>> ?: throw LuaException("There is no such mode")
        val level = peripheralOwner.level!!
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(level)
        val something = strategy.getById(id.toSafeArray().wrap(), sd, level) ?: return null
        return strategy.represent(something, level)
    }
}
