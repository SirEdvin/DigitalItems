package site.siredvin.digitalitems.data

import dan200.computercraft.api.ComputerCraftTags
import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.RecipeOutput
import net.minecraft.data.recipes.RecipeProvider
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import site.siredvin.broccolium.modules.data.recipe.TweakedShapedRecipeBuilder
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.xplat.ModRecipeIngredients
import java.util.concurrent.CompletableFuture

class ModRecipeProvider(output: PackOutput, registries: CompletableFuture<HolderLookup.Provider>) : RecipeProvider.Runner(output, registries) {
    override fun getName(): String = "DigitalItems recipes"
    override fun createRecipeProvider(registries: HolderLookup.Provider, output: RecipeOutput): RecipeProvider = object : RecipeProvider(registries, output) {
        override fun buildRecipes() {
            TweakedShapedRecipeBuilder(ModBlocks.DIGITIZER.get().asItem())
                .define('D', ModRecipeIngredients.get().diamondBlock(registries))
                .define('I', ModRecipeIngredients.get().ironBlock(registries))
                .pattern("III")
                .pattern("DID")
                .pattern("III")
                .save(output)
            TweakedShapedRecipeBuilder(ModBlocks.ADVANCED_DIGITIZER.get().asItem())
                .define('D', ModBlocks.DIGITIZER.get())
                .define('W', Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(ComputerCraftTags.Items.WIRED_MODEM)))
                .define('E', Items.ENDER_PEARL)
                .pattern(" E ")
                .pattern("WDW")
                .pattern(" E ")
                .save(output)
        }
    }
}
