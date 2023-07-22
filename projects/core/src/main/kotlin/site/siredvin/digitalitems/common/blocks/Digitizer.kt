package site.siredvin.digitalitems.common.blocks

import net.minecraft.core.BlockPos
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.Containers
import net.minecraft.world.InteractionHand
import net.minecraft.world.InteractionResult
import net.minecraft.world.entity.player.Player
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.context.BlockPlaceContext
import net.minecraft.world.level.Level
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.Mirror
import net.minecraft.world.level.block.RenderShape
import net.minecraft.world.level.block.Rotation
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.state.BlockState
import net.minecraft.world.level.block.state.StateDefinition
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import net.minecraft.world.level.block.state.properties.BooleanProperty
import net.minecraft.world.phys.BlockHitResult
import site.siredvin.digitalitems.common.blockentity.DigitizerBlockEntity
import site.siredvin.digitalitems.common.setup.ModBlockEntityTypes
import site.siredvin.peripheralium.common.blocks.BaseTileEntityBlock
import site.siredvin.peripheralium.util.BlockUtil
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import java.util.*

class Digitizer :
    BaseTileEntityBlock<DigitizerBlockEntity>(false, BlockUtil.defaultProperties()) {

    companion object {
        val FACING = BlockStateProperties.HORIZONTAL_FACING
        val POWERED = BooleanProperty.create("powered")
    }

    override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity? {
        return ModBlockEntityTypes.DIGITIZER.get().create(p0, p1)
    }

    override fun hasAnalogOutputSignal(state: BlockState): Boolean {
        return true
    }

    override fun getAnalogOutputSignal(state: BlockState, l: Level, pos: BlockPos): Int {
        val i: ItemStack =
            (Objects.requireNonNull(l.getBlockEntity(pos)) as DigitizerBlockEntity).storage.getItem(0)
        return if (i.isEmpty) {
            0
        } else {
            (1 + i.count.toFloat() / i.maxStackSize * 14).toInt()
        }
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? {
        return defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)
    }

    override fun rotate(pState: BlockState, pRotation: Rotation): BlockState {
        return pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)))
    }

    override fun mirror(pState: BlockState, pMirror: Mirror): BlockState {
        return pState.rotate(pMirror.getRotation(pState.getValue(FACING)))
    }

    override fun getRenderShape(state: BlockState): RenderShape {
        return RenderShape.MODEL
    }

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING, POWERED)
    }

    override fun onRemove(blockState: BlockState, level: Level, blockPos: BlockPos, replace: BlockState, bl: Boolean) {
        if (!blockState.`is`(replace.block)) {
            val blockEntity = level.getBlockEntity(blockPos)
            if (blockEntity is DigitizerBlockEntity) {
                Containers.dropItemStack(
                    level,
                    blockPos.x.toDouble(),
                    blockPos.y.toDouble(),
                    blockPos.z.toDouble(),
                    blockEntity.storage.getItem(0),
                )
                level.updateNeighbourForOutputSignal(blockPos, this)
            }
            super.onRemove(blockState, level, blockPos, replace, bl)
        }
    }

    override fun use(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        interactionHand: InteractionHand,
        blockHitResult: BlockHitResult,
    ): InteractionResult {
        if (player !is ServerPlayer) {
            return InteractionResult.SUCCESS
        }
        val blockEntity = level.getBlockEntity(pos) as? DigitizerBlockEntity ?: return InteractionResult.CONSUME
        PeripheraliumPlatform.openMenu(player, blockEntity) { buf: FriendlyByteBuf ->
            buf.writeBlockPos(
                pos,
            )
        }
        return InteractionResult.CONSUME
    }
}
