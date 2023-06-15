package site.siredvin.digitalitems.data

import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import site.siredvin.digitalitems.common.setup.Blocks
import site.siredvin.digitalitems.xplat.ModRecipeIngredients
import site.siredvin.peripheralium.data.blocks.TweakedShapedRecipeBuilder
import java.util.function.Consumer

class ModRecipeProvider(output: PackOutput) : RecipeProvider(output) {
    override fun buildRecipes(consumer: Consumer<FinishedRecipe>) {
        TweakedShapedRecipeBuilder.shaped(Blocks.DIGITIZER.get().asItem())
            .define('D', ModRecipeIngredients.get().diamondBlock)
            .define('I', ModRecipeIngredients.get().ironBlock)
            .pattern("III")
            .pattern("DID")
            .pattern("III")
            .save(consumer)
    }
}
