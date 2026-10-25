package site.siredvin.digitalitems.xplat

import dan200.computercraft.api.upgrades.UpgradeData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.common.setup.*
import site.siredvin.tweakium.modules.platform.ComputerPlatformRegistries
import site.siredvin.tweakium.modules.platform.ComputerPlatformToolkit

object ModCommonHooks {

    fun onRegister() {
        ModItems.doSomething()
        ModBlocks.doSomething()
        ModBlockEntityTypes.doSomething()
        ModMenus.doSomething()
        ModStats.doSomething()
        ModCriterias.doSomething()
        PocketUpgradeSerializers.doSomething()
        TurtleUpgradeSerializers.doSomething()
        ModPlatform.registerCreativeTab(
            ResourceLocation(DigitalItemsCore.MOD_ID, "tab"),
            DigitalItemsCore.configureCreativeTab(PlatformToolkit.get().createTabBuilder()).build(),
        )
    }

    fun registerUpgradesInCreativeTab(output: CreativeModeTab.Output) {
        ModPlatform.holder.turtleSerializers.forEach {
            val upgrade = ComputerPlatformToolkit.get().getTurtleUpgrade(ComputerPlatformRegistries.TURTLE_SERIALIZERS.getKey(it.get()).toString())
            if (upgrade != null) {
                ComputerPlatformToolkit.get().createTurtlesWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
        ModPlatform.holder.pocketSerializers.forEach {
            val upgrade = ComputerPlatformToolkit.get().getPocketUpgrade(ComputerPlatformRegistries.POCKET_SERIALIZERS.getKey(it.get()).toString())
            if (upgrade != null) {
                ComputerPlatformToolkit.get().createPocketsWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
    }
}
