package site.siredvin.digitalitems.xplat

import dan200.computercraft.api.upgrades.UpgradeData
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.common.setup.*
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
            ResourceLocation.fromNamespaceAndPath(DigitalItemsCore.MOD_ID, "tab"),
            DigitalItemsCore.configureCreativeTab(PlatformToolkit.get().createTabBuilder()).build(),
        )
    }

    fun registerUpgradesInCreativeTab(output: CreativeModeTab.Output) {
        ModPlatform.holder.turtleUpgrades.forEach {
            ComputerPlatformToolkit.get().getTurtleUpgrade(it.id.toString()).ifPresent { upgrade ->
                ComputerPlatformToolkit.get().createTurtlesWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
        ModPlatform.holder.pocketUpgrades.forEach {
            ComputerPlatformToolkit.get().getPocketUpgrade(it.id.toString()).ifPresent { upgrade ->
                ComputerPlatformToolkit.get().createPocketsWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
    }
}
