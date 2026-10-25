package site.siredvin.digitalitems.testmod

import it.unimi.dsi.fastutil.ints.Int2ObjectOpenHashMap
import net.minecraft.core.BlockPos
import net.minecraft.core.registries.Registries
import net.minecraft.gametest.framework.GameTestHelper
import net.minecraft.network.HashedStack
import net.minecraft.network.protocol.game.ServerboundContainerClickPacket
import net.minecraft.util.ProblemReporter
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.item.ItemEntity
import net.minecraft.world.inventory.ContainerInput
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import net.minecraft.world.item.enchantment.Enchantments
import net.minecraft.world.level.block.Blocks
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.storage.TagValueInput
import net.minecraft.world.phys.AABB
import site.siredvin.digitalitems.common.blockentity.DigitizerInventoryBlockEntity
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.testiarium.api.GameTest
import site.siredvin.testiarium.api.TestGroup
import site.siredvin.testiarium.api.immediate

@TestGroup("digitalitems")
class TargetBoundaryGameTests {
    @GameTest(template = "digitalitemsgametests.persistence")
    fun inventoryCallbacks(helper: GameTestHelper) = helper.immediate {
        val registries = helper.level.registryAccess()
        for (block in listOf(ModBlocks.DIGITIZER.get(), ModBlocks.ADVANCED_DIGITIZER.get())) {
            val original = block.newBlockEntity(BlockPos.ZERO, block.defaultBlockState()) as DigitizerInventoryBlockEntity<*>
            val restored = block.newBlockEntity(BlockPos.ZERO, block.defaultBlockState()) as DigitizerInventoryBlockEntity<*>
            val stack = PersistenceVersionFixture.namedStack().copyWithCount(1)
            stack.enchant(registries.lookupOrThrow(Registries.ENCHANTMENT).getOrThrow(Enchantments.UNBREAKING), 2)
            original.setItem(original.containerSize - 1, stack)
            val saved = original.saveWithoutMetadata(registries)
            restored.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, registries, saved))
            check(original.level == null && restored.level == null) { "Native callbacks must work before level assignment" }
            check(saved == restored.saveWithoutMetadata(registries)) { "Sparse inventory and complete components must round-trip" }
            check(ItemStack.matches(stack, restored.getItem(restored.containerSize - 1)))
            original.clearContent()
            restored.loadWithComponents(TagValueInput.create(ProblemReporter.DISCARDING, registries, original.saveWithoutMetadata(registries)))
            check(restored.isEmpty) { "Empty update must clear a previously populated inventory" }
        }
    }

    @GameTest(template = "digitalitemsgametests.persistence")
    fun menuPacketValidation(helper: GameTestHelper) = helper.immediate {
        val (player, channel) = RematerializePlayer.create(helper.level)
        val pos = BlockPos(1, 1, 1)
        val absolute = helper.absolutePos(pos)
        try {
            for (block in listOf(ModBlocks.DIGITIZER.get(), ModBlocks.ADVANCED_DIGITIZER.get())) {
                helper.setBlock(pos, block)
                val entity = helper.level.getBlockEntity(absolute) as DigitizerInventoryBlockEntity<*>
                val menu = (entity as MenuProvider).createMenu(7, player.inventory, player)!!
                player.containerMenu = menu
                player.setPos(absolute.x + 0.5, absolute.y + 0.5, absolute.z + 0.5)
                player.inventory.setItem(9, ItemStack(Items.STONE, 8))
                check(menu.stillValid(player)) { "Positive menu precondition" }
                fun click(container: Int, slot: Int) = player.connection.handleContainerClick(
                    ServerboundContainerClickPacket(container, menu.stateId, slot.toShort(), 0, ContainerInput.QUICK_MOVE, Int2ObjectOpenHashMap(), HashedStack.EMPTY),
                )
                click(8, 0)
                click(7, -2)
                click(7, menu.slots.size)
                check(player.inventory.getItem(9).count == 8 && entity.isEmpty) { "Foreign container and invalid slots must not mutate inventory" }
                player.setPos(absolute.x + 100.0, absolute.y.toDouble(), absolute.z.toDouble())
                check(!menu.stillValid(player))
                click(7, 0)
                check(player.inventory.getItem(9).count == 8 && entity.isEmpty) { "Out-of-range menu packet must not transfer items" }
                player.setPos(absolute.x + 0.5, absolute.y + 0.5, absolute.z + 0.5)
                click(7, 0)
                check(player.inventory.getItem(9).isEmpty && entity.getItem(0).count == 8) { "Native handler must actually execute a valid inbound transfer" }
                click(7, 36)
                check(entity.isEmpty && (0 until player.inventory.containerSize).sumOf { player.inventory.getItem(it).count } == 8) { "Native outbound transfer must conserve all eight items" }
                player.inventory.clearContent()
            }
        } finally {
            player.containerMenu = player.inventoryMenu
            channel.finishAndReleaseAll()
            player.discard()
        }
    }

    @GameTest(template = "digitalitemsgametests.persistence")
    fun removalConservation(helper: GameTestHelper) = helper.immediate {
        val pos = BlockPos(1, 1, 1)
        val absolute = helper.absolutePos(pos)
        val area = AABB(absolute).inflate(1.0)
        for (block in listOf(ModBlocks.DIGITIZER.get(), ModBlocks.ADVANCED_DIGITIZER.get())) {
            helper.setBlock(pos, block)
            val entity = helper.level.getBlockEntity(absolute) as DigitizerInventoryBlockEntity<*>
            entity.setItem(entity.containerSize - 1, ItemStack(Items.STONE, 7))
            helper.setBlock(pos, block.defaultBlockState().setValue(BlockStateProperties.POWERED, true))
            check(helper.level.getEntitiesOfClass(ItemEntity::class.java, area).isEmpty()) { "Same-block state changes must not drop inventory" }
            helper.setBlock(pos, Blocks.AIR)
            val drops = helper.level.getEntitiesOfClass(ItemEntity::class.java, area)
            check(drops.sumOf { it.item.count } == 7) { "Native removal must drop exactly seven stored items once" }
            drops.forEach { it.discard() }
        }
    }
}
