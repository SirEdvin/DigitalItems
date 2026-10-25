package site.siredvin.digitalitems.xplat

import net.minecraft.core.HolderLookup
import net.minecraft.world.item.crafting.Ingredient

interface ModRecipeIngredients {
    fun ironBlock(registries: HolderLookup.Provider): Ingredient
    fun diamondBlock(registries: HolderLookup.Provider): Ingredient

    companion object {
        private var impl: ModRecipeIngredients? = null
        fun configure(impl: ModRecipeIngredients) {
            this.impl = impl
        }
        fun get(): ModRecipeIngredients = checkNotNull(impl) { "DigitalItems ingredients must be initialized first" }
    }
}
