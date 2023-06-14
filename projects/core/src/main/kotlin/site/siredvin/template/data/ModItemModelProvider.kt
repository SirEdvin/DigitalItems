package site.siredvin.template.data

import net.minecraft.data.models.ItemModelGenerators
import net.minecraft.data.models.model.ModelTemplates
import site.siredvin.template.common.setup.Items

object ModItemModelProvider {

    fun addModels(generators: ItemModelGenerators) {
        generators.generateFlatItem(Items.TEMPLATE_ITEM.get(), ModelTemplates.FLAT_ITEM)
    }
}
