package site.siredvin.digitalitems

import net.minecraft.world.item.CreativeModeTab
import org.apache.logging.log4j.LogManager
import org.apache.logging.log4j.Logger
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.data.ModText
import site.siredvin.digitalitems.xplat.ModCommonHooks
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.digitalitems.xplat.ModRecipeIngredients
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform

object DigitalItemsCore {
    const val MOD_ID = "digitalitems"

    var logger: Logger = LogManager.getLogger(MOD_ID)

    fun configureCreativeTab(builder: CreativeModeTab.Builder): CreativeModeTab.Builder = builder.icon { ModBlocks.DIGITIZER.get().asItem().defaultInstance }
        .title(ModText.CREATIVE_TAB.text)
        .displayItems { _, output ->
            ModPlatform.holder.blocks.forEach { output.accept(it.get()) }
            ModPlatform.holder.items.forEach { output.accept(it.get()) }
            ModCommonHooks.registerUpgradesInCreativeTab(output)
        }

    fun configure(platform: InnerComputerBasePlatform, ingredients: ModRecipeIngredients) {
        ModPlatform.configure(platform)
        ModRecipeIngredients.configure(ingredients)
    }
}
