package site.siredvin.digitalitems.xplat

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeData
import net.minecraft.core.HolderLookup
import net.minecraft.resources.ResourceKey
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

    // Resolve through the display parameters: on a remote client the server registries do not exist.
    fun registerUpgradesInCreativeTab(holders: HolderLookup.Provider, output: CreativeModeTab.Output) {
        val turtleUpgrades = holders.lookupOrThrow(ITurtleUpgrade.REGISTRY)
        val pocketUpgrades = holders.lookupOrThrow(IPocketUpgrade.REGISTRY)
        ModPlatform.holder.turtleUpgrades.forEach {
            turtleUpgrades.get(ResourceKey.create(ITurtleUpgrade.REGISTRY, it.id)).ifPresent { upgrade ->
                ComputerPlatformToolkit.get().createTurtlesWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
        ModPlatform.holder.pocketUpgrades.forEach {
            pocketUpgrades.get(ResourceKey.create(IPocketUpgrade.REGISTRY, it.id)).ifPresent { upgrade ->
                ComputerPlatformToolkit.get().createPocketsWithUpgrade(UpgradeData.ofDefault(upgrade)).forEach(output::accept)
            }
        }
    }
}
