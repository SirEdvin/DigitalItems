package site.siredvin.digitalitems.xplat

import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.common.setup.BlockEntityTypes
import site.siredvin.digitalitems.common.setup.Blocks
import site.siredvin.digitalitems.common.setup.Items
import site.siredvin.digitalitems.common.setup.Menus
import site.siredvin.peripheralium.xplat.PeripheraliumPlatform
import site.siredvin.peripheralium.xplat.XplatRegistries

object ModCommonHooks {

    fun onRegister() {
        Items.doSomething()
        Blocks.doSomething()
        BlockEntityTypes.doSomething()
        Menus.doSomething()
        ModPlatform.registerCreativeTab(
            ResourceLocation(DigitalItemsCore.MOD_ID, "tab"),
            DigitalItemsCore.configureCreativeTab(PeripheraliumPlatform.createTabBuilder()).build(),
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
