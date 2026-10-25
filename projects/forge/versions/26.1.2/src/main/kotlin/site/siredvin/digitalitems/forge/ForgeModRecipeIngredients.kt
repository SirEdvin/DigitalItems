package site.siredvin.digitalitems.forge

import net.minecraft.core.HolderLookup
import net.minecraft.core.registries.Registries
import net.minecraft.world.item.crafting.Ingredient
import net.neoforged.neoforge.common.Tags
import site.siredvin.digitalitems.xplat.ModRecipeIngredients

object ForgeModRecipeIngredients : ModRecipeIngredients {
    override fun ironBlock(registries: HolderLookup.Provider): Ingredient = Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(Tags.Items.STORAGE_BLOCKS_IRON))
    override fun diamondBlock(registries: HolderLookup.Provider): Ingredient = Ingredient.of(registries.lookupOrThrow(Registries.ITEM).getOrThrow(Tags.Items.STORAGE_BLOCKS_DIAMOND))
}
