package site.siredvin.digitalitems.common.blocks

import com.mojang.serialization.MapCodec
import net.minecraft.core.BlockPos
import net.minecraft.core.Direction
import net.minecraft.core.registries.Registries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceKey
import net.minecraft.server.level.ServerPlayer
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
import site.siredvin.digitalitems.common.blockentity.DigitizerBlockEntity
import site.siredvin.digitalitems.common.setup.ModBlockEntityTypes
import site.siredvin.digitalitems.modId
import java.util.*

class Digitizer(properties: Properties = BlockUtil.defaultProperties().setId(ResourceKey.create(Registries.BLOCK, modId("digitizer")))) : BaseBlockEntityBlock<DigitizerBlockEntity>(false, properties) {

    override fun codec(): MapCodec<out Digitizer> = simpleCodec(::Digitizer)

    companion object {
        val FACING = BlockStateProperties.HORIZONTAL_FACING
        val POWERED = BlockStateProperties.POWERED
    }

    override fun newBlockEntity(p0: BlockPos, p1: BlockState): BlockEntity? = ModBlockEntityTypes.DIGITIZER.get().create(p0, p1)

    @Deprecated("Deprecated in Java")
    override fun hasAnalogOutputSignal(state: BlockState): Boolean = true

    @Deprecated("Deprecated in Java")
    override fun getAnalogOutputSignal(state: BlockState, l: Level, pos: BlockPos, direction: Direction): Int {
        val i: ItemStack =
            (Objects.requireNonNull(l.getBlockEntity(pos)) as DigitizerBlockEntity).storage.get(0)
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
    override fun getRenderShape(blockState: BlockState): RenderShape = RenderShape.MODEL

    override fun createBlockStateDefinition(blockState: StateDefinition.Builder<Block, BlockState>) {
        blockState.add(FACING, POWERED)
    }

    @Deprecated("Deprecated in Java")
    override fun useWithoutItem(
        state: BlockState,
        level: Level,
        pos: BlockPos,
        player: Player,
        blockHitResult: BlockHitResult,
    ): InteractionResult {
        if (player !is ServerPlayer) {
            return InteractionResult.SUCCESS
        }
        val blockEntity = level.getBlockEntity(pos) as? DigitizerBlockEntity ?: return InteractionResult.CONSUME
        PlatformToolkit.get().openMenu(player, blockEntity) { buf: FriendlyByteBuf ->
            buf.writeBlockPos(
                pos,
            )
        }
        return InteractionResult.CONSUME
    }
}
