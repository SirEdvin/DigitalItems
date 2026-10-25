package site.siredvin.digitalitems.fabric

import net.minecraft.core.HolderLookup
import net.minecraft.world.item.Items
import net.minecraft.world.item.crafting.Ingredient
import site.siredvin.digitalitems.xplat.ModRecipeIngredients

object FabricModRecipeIngredients : ModRecipeIngredients {
    override fun ironBlock(registries: HolderLookup.Provider): Ingredient = Ingredient.of(Items.IRON_BLOCK)
    override fun diamondBlock(registries: HolderLookup.Provider): Ingredient = Ingredient.of(Items.DIAMOND_BLOCK)
}
