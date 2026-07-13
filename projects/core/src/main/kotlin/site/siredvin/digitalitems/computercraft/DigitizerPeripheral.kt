package site.siredvin.digitalitems.computercraft

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.awardDigitalization
import site.siredvin.digitalitems.common.configuration.ModConfig
import site.siredvin.digitalitems.common.data.ByteArrayWrapper
import site.siredvin.digitalitems.common.data.DigitalItemsSavedData
import site.siredvin.digitalitems.common.data.DigitizedItem
import site.siredvin.digitalitems.toSafeArray
import site.siredvin.digitalitems.wrap
import site.siredvin.tweakium.modules.peripheral.OwnedPeripheral
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation
import site.siredvin.tweakium.modules.plugins.InventoryPlugin
import site.siredvin.tweakium.modules.plugins.SuppliedRudimentInventoryPlugin
import java.nio.ByteBuffer
import java.security.SecureRandom
import java.util.Optional
import kotlin.jvm.optionals.getOrDefault

class DigitizerPeripheral<T : IPeripheralOwner>(peripheralOwner: T) :
    OwnedPeripheral<T>(
        TYPE,
        peripheralOwner,
    ) {

    init {
        if (peripheralOwner is BlockEntityPeripheralOwner<*>) {
            addPlugin(InventoryPlugin(peripheralOwner.level!!, peripheralOwner.storage!!, ModConfig.inventoryTransferLimit))
        } else {
            addPlugin(SuppliedRudimentInventoryPlugin({ peripheralOwner.level!! }, { peripheralOwner.storage!! }))
        }
    }

    @Throws(LuaException::class)
    private fun checkID(sd: DigitalItemsSavedData, id: ByteArrayWrapper): DigitizedItem {
        val item = sd.get(id)
        if (item == null || item.decayed(peripheralOwner.level!!)) {
            sd.pop(id)
            throw LuaException("Invalid item ID")
        }
        return item
    }

    override val isEnabled: Boolean
        get() = true // This is only peripheral in mod, of course it enabled!

    override fun getType(): String = TYPE

    @get:LuaFunction(mainThread = true)
    val decayEnabled: Boolean
        get() = ModConfig.enableDecay

    @get:LuaFunction(mainThread = true)
    val decayTicks: Long
        get() = ModConfig.decayTicks

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun digitize(slotOp: Optional<Int>): ByteArray = digitizeAmount(peripheralOwner.storage!!.get(slotOp.map { it - 1 }.getOrDefault(0)).count, slotOp)

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun digitizeAmount(amount: Int, slotOp: Optional<Int>): ByteArray {
        val slot = slotOp.map { it - 1 }.getOrDefault(0)
        val item: ItemStack = peripheralOwner.storage!!.get(slot)
        if (item.`is`(Items.AIR)) {
            throw LuaException("There is nothing to digitize")
        }
        if (amount <= 0) {
            throw LuaException("Invalid amount")
        }
        if (item.count < amount) {
            throw LuaException("Fewer items present than requested for digitization")
        }
        val id = ByteArray(16)
        rand.nextBytes(id)
        val data: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(peripheralOwner.level!!)
        val digitizedItem = DigitizedItem(
            id.wrap(),
            peripheralOwner.storage!!.take(amount, slot, slot, ItemStorageUtils.ALWAYS, false).copy(),
            peripheralOwner.level!!.gameTime,
            (peripheralOwner.owner as? ServerPlayer),
        )
        if (digitizedItem.something.count > 0) {
            data.add(digitizedItem)
            data.setDirty()
        } else {
            throw LuaException("Something strange happened and nothing was digitized")
        }
        (peripheralOwner.owner as? ServerPlayer)?.awardDigitalization(digitizedItem.something)
        return id
    }

    fun innerRematerializeAmount(id: ByteArrayWrapper, amount: Int, sd: DigitalItemsSavedData): Int {
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(peripheralOwner.level!!)
        val item: DigitizedItem = checkID(sd, id)
        if (amount <= 0) {
            throw LuaException("Invalid amount")
        }
        if (item.something.count < amount) {
            throw LuaException("Fewer items present in ID than requested for rematerialization")
        }
        val limitedAmount: ItemStack = item.something.copy()
        limitedAmount.count = amount
        val remaining: ItemStack = peripheralOwner.storage!!.store(limitedAmount, false)
        item.something.count -= (limitedAmount.count - remaining.count)
        if (item.something.count == 0) {
            sd.pop(id)
        } else {
            item.refresh(peripheralOwner.level!!.gameTime)
        }
        sd.setDirty()
        return limitedAmount.count - remaining.count
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun rematerialize(id: ByteBuffer): Int {
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(peripheralOwner.level!!)
        val trueID = id.toSafeArray().wrap()
        val item: DigitizedItem = checkID(sd, trueID)
        return innerRematerializeAmount(trueID, item.something.count, sd)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun rematerializeAmount(id: ByteBuffer, amount: Int): Int {
        val trueID = id.toSafeArray().wrap()
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(peripheralOwner.level!!)
        return innerRematerializeAmount(trueID, amount, sd)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun refresh(id: ByteBuffer) {
        val level = peripheralOwner.level!!
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(level)
        val item: DigitizedItem = checkID(sd, id.toSafeArray().wrap())
        val currentTime: Long = level.gameTime
        item.refresh(currentTime)
        sd.setDirty()
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun getIDInfo(id: ByteBuffer): Map<String, Any> {
        val level = peripheralOwner.level!!
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(level)
        val item: DigitizedItem = checkID(sd, id.toSafeArray().wrap())
        val root = HashMap<String, Any>()
        val currentTime: Long = level.gameTime
        root["currentTime"] = currentTime
        root["digitizedAt"] = item.digitizedAt
        root["decaysAt"] = item.decaysAt
        root["lastRefresh"] = item.lastRefresh
        root["item"] = LuaRepresentation.forItemStack(item.something)
        return root
    }

    companion object {
        val rand = SecureRandom()
        const val TYPE = "digitizer"
        val ID = ResourceLocation.fromNamespaceAndPath(DigitalItemsCore.MOD_ID, TYPE)
    }
}
