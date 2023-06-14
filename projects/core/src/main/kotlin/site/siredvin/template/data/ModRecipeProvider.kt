package site.siredvin.template.data

import net.minecraft.data.PackOutput
import net.minecraft.data.recipes.FinishedRecipe
import net.minecraft.data.recipes.RecipeProvider
import java.util.function.Consumer

class ModRecipeProvider(output: PackOutput) : RecipeProvider(output) {
    override fun buildRecipes(consumer: Consumer<FinishedRecipe>) {
    }
}
