package site.siredvin.digitalitems.data

import net.minecraft.data.models.ItemModelGenerators
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.tweakium.modules.data.turtleUpgrades

object ModItemModelProvider {

    fun addModels(generators: ItemModelGenerators) {
        turtleUpgrades(generators, ModBlocks.DIGITIZER.get(), "_front_on")
        turtleUpgrades(generators, ModBlocks.ADVANCED_DIGITIZER.get(), "_front_on")
    }
}
