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
import site.siredvin.digitalitems.common.blocks.Digitizer
import site.siredvin.digitalitems.common.setup.Blocks
import site.siredvin.peripheralium.data.blocks.createHorizontalFacingDispatch

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

    fun addModels(generators: BlockModelGenerators) {
        val off_model = horizontalOrientedModelWithSuffix(
            generators,
            Blocks.DIGITIZER.get(),
            "_off",
            overwriteFront = TextureMapping.getBlockTexture(Blocks.DIGITIZER.get(), "_front_off"),
        )

        val on_model = horizontalOrientedModelWithSuffix(
            generators,
            Blocks.DIGITIZER.get(),
            "_on",
            overwriteFront = TextureMapping.getBlockTexture(Blocks.DIGITIZER.get(), "_front_on"),
        )

        val modelDispatch = PropertyDispatch.property(Digitizer.POWERED)
        modelDispatch.select(
            false,
            Variant.variant().with(
                VariantProperties.MODEL,
                off_model,
            ),
        )

        modelDispatch.select(
            true,
            Variant.variant().with(
                VariantProperties.MODEL,
                on_model,
            ),
        )

        generators.blockStateOutput.accept(
            MultiVariantGenerator.multiVariant(
                Blocks.DIGITIZER.get(),
                Variant.variant(),
            ).with(
                createHorizontalFacingDispatch(),
            ).with(
                modelDispatch,
            ),
        )
        generators.delegateItemModel(Blocks.DIGITIZER.get(), on_model)
    }
}
