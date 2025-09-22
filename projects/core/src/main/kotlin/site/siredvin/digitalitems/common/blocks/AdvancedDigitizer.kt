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
import net.minecraft.world.phys.BlockHitResult
import site.siredvin.broccolium.modules.base.block.BaseBlockEntityBlock
import site.siredvin.broccolium.modules.base.util.BlockUtil
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.digitalitems.common.blockentity.AdvancedDigitizerBlockEntity
import site.siredvin.digitalitems.common.setup.ModBlockEntityTypes
import java.util.*

class AdvancedDigitizer : BaseBlockEntityBlock<AdvancedDigitizerBlockEntity>(false, BlockUtil.defaultProperties()) {

    companion object {
        val FACING = BlockStateProperties.HORIZONTAL_FACING
        val POWERED = BlockStateProperties.POWERED
    }

    override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity? = ModBlockEntityTypes.ADVANCED_DIGITIZER.get().create(p0, p1)

    @Deprecated("Deprecated in Java")
    override fun hasAnalogOutputSignal(state: BlockState): Boolean = true

    @Deprecated("Deprecated in Java")
    override fun getAnalogOutputSignal(state: BlockState, l: Level, pos: BlockPos): Int {
        val i: ItemStack =
            (Objects.requireNonNull(l.getBlockEntity(pos)) as AdvancedDigitizerBlockEntity).storage.getItem(0)
        return if (i.isEmpty) {
            0
        } else {
            (1 + i.count.toFloat() / i.maxStackSize * 14).toInt()
        }
    }

    override fun getStateForPlacement(context: BlockPlaceContext): BlockState? = defaultBlockState().setValue(FACING, context.horizontalDirection.opposite)

    @Deprecated("Deprecated in Java")
    override fun rotate(pState: BlockState, pRotation: Rotation): BlockState = pState.setValue(FACING, pRotation.rotate(pState.getValue(FACING)))

    @Deprecated("Deprecated in Java")
    @Suppress("DEPRECATION", "KotlinRedundantDiagnosticSuppress")
    override fun mirror(pState: BlockState, pMirror: Mirror): BlockState = pState.rotate(pMirror.getRotation(pState.getValue(FACING)))

    @Deprecated("Deprecated in Java")
    override fun getRenderShape(state: BlockState): RenderShape = RenderShape.MODEL

    override fun createBlockStateDefinition(builder: StateDefinition.Builder<Block?, BlockState?>) {
        builder.add(FACING, POWERED)
    }

    @Deprecated("Deprecated in Java")
    override fun onRemove(blockState: BlockState, level: Level, blockPos: BlockPos, replace: BlockState, bl: Boolean) {
        if (!blockState.`is`(replace.block)) {
            val blockEntity = level.getBlockEntity(blockPos)
            if (blockEntity is AdvancedDigitizerBlockEntity) {
                blockEntity.storage.getItems().forEach {
                    if (!it.isEmpty) {
                        Containers.dropItemStack(
                            level,
                            blockPos.x.toDouble(),
                            blockPos.y.toDouble(),
                            blockPos.z.toDouble(),
                            it,
                        )
                    }
                }
                level.updateNeighbourForOutputSignal(blockPos, this)
            }
            @Suppress("DEPRECATION")
            super.onRemove(blockState, level, blockPos, replace, bl)
        }
    }

    @Deprecated("Deprecated in Java")
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
        val blockEntity = level.getBlockEntity(pos) as? AdvancedDigitizerBlockEntity ?: return InteractionResult.CONSUME
        PlatformToolkit.get().openMenu(player, blockEntity) { buf: FriendlyByteBuf ->
            buf.writeBlockPos(
                pos,
            )
        }
        return InteractionResult.CONSUME
    }
}
