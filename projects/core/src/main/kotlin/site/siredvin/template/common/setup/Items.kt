package site.siredvin.template.common.setup

import net.minecraft.world.item.Item
import site.siredvin.peripheralium.common.items.DescriptiveItem
import site.siredvin.template.xplat.ModPlatform

object Items {
    val TEMPLATE_ITEM = ModPlatform.registerItem("template_item") {
        DescriptiveItem(
            Item.Properties(),
        )
    }

    fun doSomething() {
    }
}
