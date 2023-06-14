package site.siredvin.template

import net.minecraft.world.item.CreativeModeTab
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.template.common.setup.Items
import site.siredvin.template.data.ModText
import site.siredvin.template.xplat.ModCommonHooks
import site.siredvin.template.xplat.ModPlatform
import site.siredvin.template.xplat.ModRecipeIngredients

object TemplateCore {
    const val MOD_ID = "template"

    var LOGGER: Logger = LogManager.getLogger(MOD_ID)

    fun configureCreativeTab(builder: CreativeModeTab.Builder): CreativeModeTab.Builder {
        return builder.icon { Items.TEMPLATE_ITEM.get().defaultInstance }
            .title(ModText.CREATIVE_TAB.text)
            .displayItems { _, output ->
                ModPlatform.holder.blocks.forEach { output.accept(it.get()) }
                ModPlatform.holder.items.forEach { output.accept(it.get()) }
                ModCommonHooks.registerUpgradesInCreativeTab(output)
            }
    }

    fun configure(platform: ModPlatform, ingredients: ModRecipeIngredients) {
        ModPlatform.configure(platform)
        ModRecipeIngredients.configure(ingredients)
    }
}