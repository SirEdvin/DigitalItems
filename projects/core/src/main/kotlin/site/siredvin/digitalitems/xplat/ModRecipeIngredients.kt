package site.siredvin.digitalitems.xplat

import net.minecraft.world.item.crafting.Ingredient

interface ModRecipeIngredients {

    companion object {
        private var impl: ModRecipeIngredients? = null

        fun configure(impl: ModRecipeIngredients) {
            this.impl = impl
        }

        fun get(): ModRecipeIngredients {
            if (impl == null) {
                throw IllegalStateException("You should init PeripheralWorks Platform first")
            }
            return impl!!
        }
    }

    val ironBlock: Ingredient
    val diamondBlock: Ingredient
}
