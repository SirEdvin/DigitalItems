package site.siredvin.digitalitems.xplat

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.common.setup.*

object ModCommonHooks {

    fun onRegister() {
        ModItems.doSomething()
        ModBlocks.doSomething()
        ModBlockEntityTypes.doSomething()
        ModMenus.doSomething()
        ModStats.doSomething()
        ModCriterias.doSomething()
        ModPlatform.registerCreativeTab(
            ResourceLocation(DigitalItemsCore.MOD_ID, "tab"),
            DigitalItemsCore.configureCreativeTab(PlatformToolkit.get().createTabBuilder()).build(),
        )
    }

    fun registerUpgradesInCreativeTab(output: CreativeModeTab.Output) {
    }
}
