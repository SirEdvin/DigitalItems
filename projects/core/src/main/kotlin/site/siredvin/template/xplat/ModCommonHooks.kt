package site.siredvin.template.xplat

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.XplatRegistries
import site.siredvin.template.TemplateCore
import site.siredvin.template.common.setup.Blocks
import site.siredvin.template.common.setup.Items

object ModCommonHooks {

    fun onRegister() {
        Items.doSomething()
        Blocks.doSomething()
        ModPlatform.registerCreativeTab(
            ResourceLocation(TemplateCore.MOD_ID, "tab"),
            TemplateCore.configureCreativeTab(PeripheraliumPlatform.createTabBuilder()).build(),
        )
    }

    fun registerUpgradesInCreativeTab(output: CreativeModeTab.Output) {
        ModPlatform.holder.turtleSerializers.forEach {
            val upgrade = PeripheraliumPlatform.getTurtleUpgrade(XplatRegistries.TURTLE_SERIALIZERS.getKey(it.get()).toString())
            if (upgrade != null) {
                PeripheraliumPlatform.createTurtlesWithUpgrade(upgrade).forEach(output::accept)
            }
        }
        ModPlatform.holder.pocketSerializers.forEach {
            val upgrade = PeripheraliumPlatform.getPocketUpgrade(XplatRegistries.POCKET_SERIALIZERS.getKey(it.get()).toString())
            if (upgrade != null) {
                PeripheraliumPlatform.createPocketsWithUpgrade(upgrade).forEach(output::accept)
            }
        }
    }
}
