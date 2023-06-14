package site.siredvin.digitalitems.common.setup

import net.minecraft.world.item.Item
import site.siredvin.peripheralium.common.items.DescriptiveItem
import site.siredvin.digitalitems.xplat.ModPlatform

object Items {
    val TEMPLATE_ITEM = ModPlatform.registerItem("template_item") {
        DescriptiveItem(
            Item.Properties(),
        )
    }

    fun doSomething() {
    }
}
