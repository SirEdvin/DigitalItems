package site.siredvin.digitalitems.common.data

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.base.api.AgnosticStorage
import site.siredvin.broccolium.modules.storage.base.api.SlottedAgnosticStorage
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import site.siredvin.digitalitems.awardDigitalization
import site.siredvin.digitalitems.common.configuration.ModConfig
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.api.ISidedPeripheral
import site.siredvin.tweakium.modules.peripheral.representation.LuaRepresentation
import site.siredvin.tweakium.modules.plugins.PeripheralPluginUtils

class DigitizedItemStrategy : DigitizedSomethingStrategy<ItemStack, DigitizedItem>() {
    override val mode: String
        get() = "item"
    override val stackLimit: Long
        get() = ModConfig.itemStackLimit.toLong()

    override fun getByIdRaw(id: ByteArrayWrapper, sd: DigitalItemsSavedData): DigitizedItem? = sd.get(id)

    override fun pop(digitizedSomething: DigitizedItem, sd: DigitalItemsSavedData) {
        sd.pop(digitizedSomething.id)
    }

    override fun singleRepresent(something: ItemStack): Any = LuaRepresentation.forItemStack(something)

    override fun isSame(
        something: ItemStack,
        another: ItemStack,
    ): Boolean = ItemStorageUtils.canStack(something, another)

    override fun isEmpty(something: ItemStack): Boolean = something.isEmpty

    override fun amount(something: ItemStack): Long = something.count.toLong()

    private fun extractFromStorage(target: AgnosticStorage<ItemStack, Int>, filter: Any?, limit: Long?, simulate: Boolean): ItemStack {
        if (filter == null) {
            return target.take({ true }, limit?.toInt() ?: Int.MAX_VALUE, simulate)
        } else if (filter is Number) {
            if (target !is SlottedAgnosticStorage<ItemStack, Int>) {
                throw LuaException("Cannot use slot filter with not-slotted storage")
            }
            return target.take(limit?.toInt() ?: Int.MAX_VALUE, filter.toInt() - 1, filter.toInt() - 1, { true }, simulate)
        }
        return target.take(PeripheralPluginUtils.itemQueryToPredicate(filter), limit?.toInt() ?: Int.MAX_VALUE, simulate)
    }

    override fun extractFromSelf(
        owner: IPeripheralOwner,
        filter: Any?,
        limit: Long?,
        simulate: Boolean,
    ): ItemStack {
        val inventory = owner.storage ?: return ItemStack.EMPTY
        return extractFromStorage(inventory, filter, limit, simulate)
    }

    override fun extract(
        access: IComputerAccess,
        level: Level,
        source: String,
        filter: Any?,
        limit: Long?,
        simulate: Boolean,
    ): ItemStack {
        val peripheral = access.getAvailablePeripheral(source) ?: throw LuaException("Cannot find $source")
        val direction = if (peripheral is ISidedPeripheral) peripheral.side else null
        val storage = AgnosticItemStorageLookup.extractFromUnknown(level, peripheral.target, direction) ?: throw LuaException("$source is not inventory or item storage")
        return extractFromStorage(storage, filter, limit, simulate)
    }

    private fun storeInStorage(target: AgnosticStorage<ItemStack, Int>, something: ItemStack, limit: Long): Long {
        val realLimit = limit.toInt().coerceAtMost(something.count)
        val stackToStore = if (something.count != realLimit) {
            something.copyWithCount(realLimit)
        } else {
            something.copy()
        }
        val amountToStore = stackToStore.count
        val reminder = target.store(stackToStore, false)
        return (reminder.count + (something.count - amountToStore)).toLong()
    }

    override fun storeInSelf(
        owner: IPeripheralOwner,
        something: ItemStack,
        limit: Long,
    ): Long {
        val inventory = owner.storage ?: return something.count.toLong()
        return storeInStorage(inventory, something, limit)
    }

    override fun store(
        access: IComputerAccess,
        level: Level,
        destination: String,
        something: ItemStack,
        limit: Long,
    ): Long {
        val peripheral = access.getAvailablePeripheral(destination) ?: throw LuaException("Cannot find $destination")
        val direction = if (peripheral is ISidedPeripheral) peripheral.side else null
        val storage = AgnosticItemStorageLookup.extractFromUnknown(level, peripheral.target, direction) ?: throw LuaException("$destination is not inventory or item storage")
        return storeInStorage(storage, something, limit)
    }

    override fun put(
        id: ByteArrayWrapper,
        something: ItemStack,
        peripheralOwner: IPeripheralOwner,
        sd: DigitalItemsSavedData,
    ) {
        val item = DigitizedItem(
            id,
            something,
            peripheralOwner.level!!.gameTime,
            peripheralOwner.owner as? ServerPlayer,
        )
        sd.add(item)
        sd.setDirty()
    }

    override fun awardDigitization(
        something: ItemStack,
        player: ServerPlayer?,
    ) {
        player?.awardDigitalization(something)
    }
}
