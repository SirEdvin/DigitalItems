package site.siredvin.digitalitems.forge

import net.minecraft.world.item.crafting.Ingredient
import net.minecraftforge.common.Tags
import site.siredvin.digitalitems.xplat.ModRecipeIngredients

object ForgeModRecipeIngredients : ModRecipeIngredients {
    override val ironBlock: Ingredient
        get() = Ingredient.of(Tags.Items.STORAGE_BLOCKS_IRON)
    override val diamondBlock: Ingredient
        get() = Ingredient.of(Tags.Items.STORAGE_BLOCKS_DIAMOND)
}
