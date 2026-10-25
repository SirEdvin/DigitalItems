package site.siredvin.digitalitems.data

import dan200.computercraft.api.ComputerCraftTags
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import site.siredvin.broccolium.modules.data.recipe.TweakedShapedRecipeBuilder
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.xplat.ModRecipeIngredients
import java.util.function.Consumer

class ModRecipeProvider(output: PackOutput) : RecipeProvider(output) {
    override fun buildRecipes(consumer: Consumer<FinishedRecipe>) {
        TweakedShapedRecipeBuilder.shaped(ModBlocks.DIGITIZER.get().asItem())
            .define('D', ModRecipeIngredients.get().diamondBlock)
            .define('I', ModRecipeIngredients.get().ironBlock)
            .pattern("III")
            .pattern("DID")
            .pattern("III")
            .save(consumer)
        TweakedShapedRecipeBuilder.shaped(ModBlocks.ADVANCED_DIGITIZER.get().asItem())
            .define('D', ModBlocks.DIGITIZER.get())
            .define('W', Ingredient.of(ComputerCraftTags.Items.WIRED_MODEM))
            .define('E', Items.ENDER_PEARL)
            .pattern(" E ")
            .pattern("WDW")
            .pattern(" E ")
            .save(consumer)
    }
}
