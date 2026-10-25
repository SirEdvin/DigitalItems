package site.siredvin.digitalitems.testmod

import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.upgrades.UpgradeData
import dan200.computercraft.core.computer.ComputerSide
import dan200.computercraft.shared.computer.core.ServerContext
import dan200.computercraft.shared.pocket.core.PocketServerComputer
import dan200.computercraft.shared.pocket.core.PocketSide
import dan200.computercraft.shared.pocket.items.PocketComputerItem
import dan200.computercraft.shared.turtle.blocks.TurtleBlockEntity
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.world.Container
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.digitalitems.common.blockentity.DigitizerBlockEntity
import site.siredvin.digitalitems.common.data.DigitalItemsSavedData
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral
import site.siredvin.digitalitems.wrap
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.owner.BlockEntityPeripheralOwner
import site.siredvin.tweakium.modules.peripheral.owner.TurtlePeripheralOwner
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit
import java.nio.ByteBuffer
import java.util.Optional

object RematerializeFixture {
    fun pocket(helper: GameTestHelper) {
        val (player, channel) = RematerializePlayer.create(helper.level)
        val item = BuiltInRegistries.ITEM.first { it is PocketComputerItem } as PocketComputerItem
        val stack = ItemStack(item)
        player.inventory.setItem(0, stack)
        try {
            item.inventoryTick(stack, helper.level, player, null)
            val computer = ServerContext.get(helper.level.server).registry().computers.single {
                PocketComputerItem.isServerComputer(it, stack)
            } as PocketServerComputer
            try {
                computer.brain.setUpgrade(PocketSide.BACK, UpgradeData.ofDefault(ComputerPlatformToolkit.get().getPocketUpgrade("digitalitems:digitizer").orElseThrow()))
                val peripheral = computer.getPeripheral(ComputerSide.BACK) as DigitizerPeripheral<*>
                checkTransfers(helper, peripheral, player.inventory, 1)
            } finally {
                computer.close()
            }
        } finally {
            channel.finishAndReleaseAll()
            player.discard()
        }
    }

    fun turtle(helper: GameTestHelper) {
        val block = BuiltInRegistries.BLOCK.first { BuiltInRegistries.BLOCK.getKey(it).toString() == "computercraft:turtle_advanced" }
        val pos = BlockPos(1, 1, 1)
        helper.setBlock(pos, block)
        val turtle = helper.level.getBlockEntity(helper.absolutePos(pos)) as TurtleBlockEntity
        checkTransfers(helper, DigitizerPeripheral(TurtlePeripheralOwner(turtle.access, TurtleSide.LEFT)), turtle, 0)
    }

    fun block(helper: GameTestHelper) {
        val pos = BlockPos(1, 1, 1)
        helper.setBlock(pos, ModBlocks.DIGITIZER.get())
        val block = helper.level.getBlockEntity(helper.absolutePos(pos)) as DigitizerBlockEntity
        checkTransfers(helper, DigitizerPeripheral(BlockEntityPeripheralOwner(block)), block, 0)
    }

    private fun checkTransfers(helper: GameTestHelper, peripheral: DigitizerPeripheral<out IPeripheralOwner>, inventory: Container, slot: Int) {
        val data = DigitalItemsSavedData.getFrom(helper.level)
        inventory.setItem(slot, ItemStack(Items.STONE, 16))
        val id = peripheral.digitizeAmount(7, Optional.of(slot + 1))
        try {
            helper.assertTrue(inventory.getItem(slot).count == 9, "Digitization must remove seven physical items")
            helper.assertTrue(peripheral.rematerialize(ByteBuffer.wrap(id)) == 7, "Occupied compatible stack must report seven restored items")
            helper.assertTrue(inventory.getItem(slot).count == 16, "Physical plus digital items must remain sixteen")
            helper.assertTrue(data.get(id.wrap()) == null, "Exhausted ID must be removed")
            var invalid = false
            try {
                peripheral.getIDInfo(ByteBuffer.wrap(id))
            } catch (e: dan200.computercraft.api.lua.LuaException) {
                invalid = e.message == "Invalid item ID"
            }
            helper.assertTrue(invalid, "Exhausted ID must be rejected by the public API")
        } finally {
            data.pop(id.wrap())
        }

        val partial = peripheral.digitizeAmount(7, Optional.of(slot + 1))
        try {
            // Leave exactly three compatible spaces and no empty slots, including offhand/armor.
            for (i in 0 until inventory.containerSize) {
                if (inventory.getItem(i).item !is PocketComputerItem) inventory.setItem(i, ItemStack(Items.DIRT, 64))
            }
            inventory.setItem(slot, ItemStack(Items.STONE, 61))
            helper.assertTrue(peripheral.rematerializeAmount(ByteBuffer.wrap(partial), 7) == 3, "Partial insertion must report three")
            helper.assertTrue(inventory.getItem(slot).count == 64 && data.get(partial.wrap())?.something?.count == 4, "Partial insertion must conserve 68 physical plus digital stone")
            helper.assertTrue(peripheral.rematerialize(ByteBuffer.wrap(partial)) == 0, "Full destination must reject insertion")
            helper.assertTrue(inventory.getItem(slot).count == 64 && data.get(partial.wrap())?.something?.count == 4, "Rejected insertion must preserve both quantities")
            inventory.setItem(slot, ItemStack.EMPTY)
            helper.assertTrue(peripheral.rematerialize(ByteBuffer.wrap(partial)) == 4, "Empty slot must accept remainder")
            helper.assertTrue(inventory.getItem(slot).count == 4 && data.get(partial.wrap()) == null, "Remainder ID must be exhausted")
        } finally {
            data.pop(partial.wrap())
            inventory.clearContent()
        }
    }
}
