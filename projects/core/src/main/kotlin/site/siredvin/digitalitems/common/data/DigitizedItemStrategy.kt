package site.siredvin.digitalitems.common.data

import dan200.computercraft.api.lua.LuaException
import dan200.computercraft.api.peripheral.IComputerAccess
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import site.siredvin.broccolium.modules.storage.item.AgnosticItemStorageLookup
import site.siredvin.broccolium.modules.storage.item.ItemStorageUtils
import site.siredvin.broccolium.modules.storage.item.api.AgnosticItemStorage
import site.siredvin.broccolium.modules.storage.item.api.SlottedAgnosticItemStorage
import site.siredvin.digitalitems.awardDigitalization
import site.siredvin.digitalitems.common.configuration.ModConfig
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
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

    private fun extractFromStorage(target: AgnosticItemStorage, filter: Any?, limit: Long?, simulate: Boolean): ItemStack {
        if (simulate) {
            val stack = when (filter) {
                null -> {
                    target.getItems().asSequence().filter { !it.isEmpty }.first()
                }
                is Number -> {
                    if (target !is SlottedAgnosticItemStorage) {
                        throw LuaException("Cannot use slot filter with not-slotted storage")
                    }
                    target.getItem(filter.toInt() - 1)
                }
                else -> {
                    val predicate = PeripheralPluginUtils.itemQueryToPredicate(filter)
                    target.getItems().asSequence().filter { predicate.test(it) }.first()
                }
            }
            return stack.copyWithCount(limit?.toInt() ?: stack.count.coerceAtMost(stack.maxStackSize))
        }
        if (filter == null) {
            return target.takeItems({ true }, limit?.toInt() ?: Int.MAX_VALUE)
        } else if (filter is Number) {
            if (target !is SlottedAgnosticItemStorage) {
                throw LuaException("Cannot use slot filter with not-slotted storage")
            }
            return target.takeItems(limit?.toInt() ?: Int.MAX_VALUE, filter.toInt() - 1, filter.toInt() - 1, { true })
        }
        return target.takeItems(PeripheralPluginUtils.itemQueryToPredicate(filter), limit?.toInt() ?: Int.MAX_VALUE)
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
        val storage = AgnosticItemStorageLookup.extractStorageFromUnknown(level, peripheral.target) ?: throw LuaException("$source is not inventory or item storage")
        return extractFromStorage(storage, filter, limit, simulate)
    }

    private fun storeInStorage(target: AgnosticItemStorage, something: ItemStack, limit: Long): Long {
        val realLimit = limit.toInt().coerceAtMost(something.count)
        val stackToStore = if (something.count != realLimit) {
            something.copyWithCount(realLimit)
        } else {
            something.copy()
        }
        val amountToStore = stackToStore.count
        val reminder = target.storeItem(stackToStore)
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
        val storage = AgnosticItemStorageLookup.extractStorageFromUnknown(level, peripheral.target) ?: throw LuaException("$destination is not inventory or item storage")
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
