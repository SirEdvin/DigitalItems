package site.siredvin.digitalitems.data

import net.minecraft.client.data.models.BlockModelGenerators
import net.minecraft.client.data.models.MultiVariant
import net.minecraft.client.data.models.blockstates.MultiVariantGenerator
import net.minecraft.client.data.models.blockstates.PropertyDispatch
import net.minecraft.client.data.models.model.ItemModelUtils
import net.minecraft.client.data.models.model.ModelTemplates
import net.minecraft.client.data.models.model.TextureMapping
import net.minecraft.client.data.models.model.TextureSlot
import net.minecraft.client.renderer.block.dispatch.Variant
import net.minecraft.util.random.WeightedList
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import site.siredvin.broccolium.modules.data.model.createHorizontalFacingDispatch
import site.siredvin.digitalitems.common.setup.ModBlocks

object ModBlockModelProvider {
    private fun digitizer(generators: BlockModelGenerators, block: Block) {
        fun model(on: Boolean) = ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(
            block,
            if (on) "_on" else "_off",
            TextureMapping.orientableCube(block).put(TextureSlot.FRONT, TextureMapping.getBlockTexture(block, if (on) "_front_on" else "_front_off")),
            generators.modelOutput,
        )
        val off = model(false)
        val on = model(true)
        val dispatch = PropertyDispatch.initial(BlockStateProperties.POWERED)
            .select(false, MultiVariant(WeightedList.of(Variant(off))))
            .select(true, MultiVariant(WeightedList.of(Variant(on))))
        generators.blockStateOutput.accept(MultiVariantGenerator.dispatch(block).with(dispatch).with(createHorizontalFacingDispatch()))
        generators.itemModelOutput.accept(block.asItem(), ItemModelUtils.plainModel(on))
    }

    fun addModels(generators: BlockModelGenerators) {
        digitizer(generators, ModBlocks.DIGITIZER.get())
        digitizer(generators, ModBlocks.ADVANCED_DIGITIZER.get())
    }
}
