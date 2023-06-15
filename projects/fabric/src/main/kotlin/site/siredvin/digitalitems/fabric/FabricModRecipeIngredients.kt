package site.siredvin.digitalitems.fabric

import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import site.siredvin.digitalitems.xplat.ModRecipeIngredients

object FabricModRecipeIngredients : ModRecipeIngredients {
    override val ironBlock: Ingredient
        get() = Ingredient.of(Items.IRON_BLOCK)
    override val diamondBlock: Ingredient
        get() = Ingredient.of(Items.DIAMOND_BLOCK)
}
