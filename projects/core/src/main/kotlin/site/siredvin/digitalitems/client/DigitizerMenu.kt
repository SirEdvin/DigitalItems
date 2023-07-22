package site.siredvin.digitalitems.client

import net.minecraft.network.FriendlyByteBuf
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.*
import net.minecraft.world.item.ItemStack
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.digitalitems.common.blockentity.DigitizerBlockEntity
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.common.setup.ModMenus

class DigitizerMenu(id: Int, inv: Inventory, entity: BlockEntity, data: ContainerData) :
    AbstractContainerMenu(ModMenus.DIGITIZER.get(), id) {
    val blockEntity: DigitizerBlockEntity
    private val level: Level

    constructor(id: Int, inv: Inventory, extraData: FriendlyByteBuf) : this(
        id,
        inv,
        inv.player.level().getBlockEntity(extraData.readBlockPos())!!,
        SimpleContainerData(8),
    )

    @JvmField
    val data: ContainerData

    init {
        checkContainerSize(inv, 3)
        checkContainerDataCount(data, 8)
        blockEntity = entity as DigitizerBlockEntity
        level = inv.player.level()
        this.data = data
        addPlayerInventory(inv)
        addPlayerHotbar(inv)
        addSlot(blockEntity.buildSlot(80, 35))
        addDataSlots(data)
    }

    override fun quickMoveStack(playerIn: Player, index: Int): ItemStack {
        val source = slots[index]
        val sourceStack = source.item
        val sourceStackCopy = source.item.copy()
        if (!source.hasItem()) {
            return ItemStack.EMPTY // no item do move; nothing to do...
        }
        if (index == digitizerIndex) {
            if (!moveItemStackTo(
                    sourceStack,
                    playerHotBarIndex,
                    playerHotBarIndex + playerHotBarLength,
                    true,
                )
            ) { // try the hotbar; last to first
                if (!moveItemStackTo(
                        sourceStack,
                        playerInvIndex,
                        playerInvIndex + playerInvLength,
                        true,
                    )
                ) { // try the inv; last to first
                    return ItemStack.EMPTY // neither hotbar nor inv was empty
                }
            }
        } else {
            if (!moveItemStackTo(
                    sourceStack,
                    digitizerIndex,
                    digitizerIndex + digitizerLength,
                    true,
                )
            ) { // try to move the item into the digitizer; order doesn't matter since there's only one slot in the digitizer
                return ItemStack.EMPTY
            }
        }
        if (sourceStack.count == 0) {
            source.set(ItemStack.EMPTY) // we moved the entire item stack; set the original one to be empty
        } else {
            source.setChanged()
        }
        source.onTake(playerIn, sourceStack)
        return sourceStackCopy
    }

    // https://github.com/Tutorials-By-Kaupenjoe/Forge-Tutorial-1.19/blob/10137e0ad48cd32477891e79d14a0ed43da67459/src/main/java/net/kaupenjoe/tutorialmod/screen/GemInfusingStationMenu.java
    override fun stillValid(player: Player): Boolean {
        return stillValid(
            ContainerLevelAccess.create(level, blockEntity.blockPos),
            player,
            ModBlocks.DIGITIZER.get(),
        )
    }

    private fun addPlayerInventory(playerInventory: Inventory) {
        for (i in 0..2) {
            for (l in 0..8) {
                addSlot(Slot(playerInventory, l + i * 9 + 9, 8 + l * 18, 86 + i * 18 - 2))
            }
        }
    }

    private fun addPlayerHotbar(playerInventory: Inventory) {
        for (i in 0..8) {
            addSlot(Slot(playerInventory, i, 8 + i * 18, 142))
        }
    }

    companion object {
        // 0 - 8 player inventory top row
        // 9 - 17 player inventory middle row
        // 18 - 26 player inventory bottom row
        // 27 - 35 player inventory hot bar
        // 36 digitizer slot
        private const val playerInvIndex = 0
        private const val playerInvLength = 27
        private const val playerHotBarIndex = 27
        private const val playerHotBarLength = 9
        private const val digitizerIndex = 36
        private const val digitizerLength = 1
    }
}
