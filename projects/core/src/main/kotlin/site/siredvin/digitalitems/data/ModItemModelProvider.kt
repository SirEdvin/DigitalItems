package site.siredvin.digitalitems.data

import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates
import site.siredvin.digitalitems.common.setup.Items

object ModItemModelProvider {

    fun addModels(generators: ItemModelGenerators) {
        generators.generateFlatItem(Items.TEMPLATE_ITEM.get(), ModelTemplates.FLAT_ITEM)
    }
}
