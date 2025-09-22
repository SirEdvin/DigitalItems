package site.siredvin.digitalitems.data

import net.minecraft.data.models.BlockModelGenerators
import net.minecraft.data.models.blockstates.MultiVariantGenerator
import net.minecraft.data.models.blockstates.PropertyDispatch
import net.minecraft.data.models.blockstates.Variant
import net.minecraft.data.models.blockstates.VariantProperties
import net.minecraft.data.models.model.ModelTemplates
import net.minecraft.data.models.model.TextureMapping
import net.minecraft.data.models.model.TextureSlot
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.state.properties.BlockStateProperties
import site.siredvin.broccolium.modules.data.model.createHorizontalFacingDispatch
import site.siredvin.digitalitems.common.setup.ModBlocks

object ModBlockModelProvider {

    fun horizontalOrientedModelWithSuffix(
        generators: BlockModelGenerators,
        block: Block,
        suffix: String,
        overwriteSide: ResourceLocation? = null,
        overwriteTop: ResourceLocation? = null,
        overwriteBottom: ResourceLocation? = null,
        overwriteFront: ResourceLocation? = null,
    ): ResourceLocation {
        val textureMapping = TextureMapping.orientableCube(block)
        if (overwriteSide != null) {
            textureMapping.put(TextureSlot.SIDE, overwriteSide)
        }
        if (overwriteBottom != null) {
            textureMapping.put(TextureSlot.BOTTOM, overwriteBottom)
        }
        if (overwriteTop != null) {
            textureMapping.put(TextureSlot.TOP, overwriteTop)
        }
        if (overwriteFront != null) {
            textureMapping.put(TextureSlot.FRONT, overwriteFront)
        }
        return ModelTemplates.CUBE_ORIENTABLE.createWithSuffix(
            block,
            suffix,
            textureMapping,
            generators.modelOutput,
        )
    }

    fun digitizer(generators: BlockModelGenerators, block: Block) {
        val offModel = horizontalOrientedModelWithSuffix(
            generators,
            block,
            "_off",
            overwriteFront = TextureMapping.getBlockTexture(block, "_front_off"),
        )

        val onModel = horizontalOrientedModelWithSuffix(
            generators,
            block,
            "_on",
            overwriteFront = TextureMapping.getBlockTexture(block, "_front_on"),
        )

        val modelDispatch = PropertyDispatch.property(BlockStateProperties.POWERED)
        modelDispatch.select(
            false,
            Variant.variant().with(
                VariantProperties.MODEL,
                offModel,
            ),
        )

        modelDispatch.select(
            true,
            Variant.variant().with(
                VariantProperties.MODEL,
                onModel,
            ),
        )

        generators.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(
                block,
                Variant.variant(),
            ).with(
                createHorizontalFacingDispatch(),
            ).with(
                modelDispatch,
            ),
        )
        generators.delegateItemModel(block, onModel)
    }

    fun addModels(generators: BlockModelGenerators) {
        digitizer(generators, ModBlocks.DIGITIZER.get())
        digitizer(generators, ModBlocks.ADVANCED_DIGITIZER.get())
    }
}
