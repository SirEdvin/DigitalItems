package site.siredvin.digitalitems.computercraft

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.lua.LuaFunction
import net.minecraft.world.item.ItemStack
import site.siredvin.digitalitems.common.DigitalItemsSavedData
import site.siredvin.digitalitems.common.DigitizedItem
import site.siredvin.digitalitems.common.blockentity.DigitizerBlockEntity
import site.siredvin.digitalitems.common.configuration.ModConfig
import site.siredvin.peripheralium.api.storage.StorageUtils
import site.siredvin.peripheralium.computercraft.peripheral.OwnedPeripheral
import site.siredvin.peripheralium.computercraft.peripheral.owner.BlockEntityPeripheralOwner
import site.siredvin.peripheralium.extra.plugins.InventoryPlugin
import site.siredvin.peripheralium.util.representation.LuaRepresentation
import java.nio.ByteBuffer
import java.security.SecureRandom

class DigitizerPeripheral(private val blockEntity: DigitizerBlockEntity) : OwnedPeripheral<BlockEntityPeripheralOwner<DigitizerBlockEntity>>(
    TYPE,
    BlockEntityPeripheralOwner(blockEntity),
) {

    init {
        addPlugin(InventoryPlugin(peripheralOwner.level!!, blockEntity.storage))
    }

    @Throws(LuaException::class)
    private fun checkID(sd: DigitalItemsSavedData, id: ByteBuffer): DigitizedItem {
        val item = sd.get(id)
        if (item == null || item.decayed(peripheralOwner.level!!)) {
            sd.pop(id)
            throw LuaException("Invalid item ID")
        }
        return item
    }

    override val isEnabled: Boolean
        get() = true // This is only peripheral in mod, of course it enabled!

    override fun getType(): String {
        return "digitizer"
    }

    @get:LuaFunction(mainThread = true)
    val decayEnabled: Boolean
        get() = ModConfig.enableDecay

    @get:LuaFunction(mainThread = true)
    val decayTicks: Long
        get() = ModConfig.decayTicks

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun digitize(): ByteArray {
        return digitizeAmount(blockEntity.storage.getItem(0).count)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun digitizeAmount(amount: Int): ByteArray {
        val item: ItemStack = blockEntity.storage.getItem(0)
        if (amount <= 0) {
            throw LuaException("Invalid amount")
        }
        if (item.count < amount) {
            throw LuaException("Fewer items present than requested for digitization")
        }
        val id = ByteArray(16)
        rand.nextBytes(id)
        val data: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(blockEntity.level!!)
        val digitizedItem = DigitizedItem(
            id,
            blockEntity.storage.takeItems(amount, 0, 0, StorageUtils.ALWAYS).copy(),
            blockEntity.level!!.gameTime,
        )
        data.add(digitizedItem)
        data.setDirty()
        return id
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun rematerialize(id: ByteBuffer): Int {
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(blockEntity.level!!)
        val item: DigitizedItem = checkID(sd, id)
        return rematerializeAmount(id, item.item.count)
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun rematerializeAmount(id: ByteBuffer, amount: Int): Int {
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(blockEntity.level!!)
        val item: DigitizedItem = checkID(sd, id)
        if (amount <= 0) {
            throw LuaException("Invalid amount")
        }
        if (item.item.count < amount) {
            throw LuaException("Fewer items present in ID than requested for rematerialization")
        }
        val limitedAmount: ItemStack = item.item.copy()
        limitedAmount.count = amount
        val remaining: ItemStack = blockEntity.storage.storeItem(limitedAmount)
        item.item.count = item.item.count - (limitedAmount.count - remaining.count)
        if (item.item.count == 0) {
            sd.pop(id)
        } else {
            item.refresh(blockEntity.level!!.gameTime)
            sd.setDirty()
        }
        sd.setDirty()
        return limitedAmount.count - remaining.count
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun refresh(id: ByteBuffer) {
        val level = peripheralOwner.level!!
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(level)
        val item: DigitizedItem = checkID(sd, id)
        val currentTime: Long = level.gameTime
        item.refresh(currentTime)
        sd.setDirty()
    }

    @LuaFunction(mainThread = true)
    @Throws(LuaException::class)
    fun getIDInfo(id: ByteBuffer): Map<String, Any> {
        val level = peripheralOwner.level!!
        val sd: DigitalItemsSavedData = DigitalItemsSavedData.getFrom(level)
        val item: DigitizedItem = checkID(sd, id)
        val root = HashMap<String, Any>()
        val currentTime: Long = level.gameTime
        root["currentTime"] = currentTime
        root["digitizedAt"] = item.digitizedAt
        root["decaysAt"] = item.decaysAt
        root["lastRefresh"] = item.lastRefresh
        root["item"] = LuaRepresentation.forItemStack(item.item)
        return root
    }

    companion object {
        val rand = SecureRandom()
        const val TYPE = "digitizer"
    }
}
