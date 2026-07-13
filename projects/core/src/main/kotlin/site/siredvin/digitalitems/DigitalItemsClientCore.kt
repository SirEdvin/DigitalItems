package site.siredvin.digitalitems

import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.resources.ResourceLocation
import site.siredvin.digitalitems.client.DigitizerScreen
import site.siredvin.digitalitems.common.setup.ModMenus
import site.siredvin.digitalitems.common.setup.TurtleUpgradeSerializers
import site.siredvin.digitalitems.computercraft.AdvancedDigitizerPeripheral
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral

object DigitalItemsClientCore {
    @Suppress("DEPRECATION")
    fun onInit() {
        MenuScreens.register(ModMenus.DIGITIZER.get(), ::DigitizerScreen)
        MenuScreens.register(ModMenus.ADVANCED_DIGITIZER.get(), ::DigitizerScreen)
    }

    fun onModelRegister(consumer: (UpgradeType<*>, TurtleUpgradeModeller<*>) -> Unit) {
        consumer(
            TurtleUpgradeSerializers.DIGITIZER.get(),
            TurtleUpgradeModeller.sided<ITurtleUpgrade>(
                ResourceLocation.fromNamespaceAndPath(DigitalItemsCore.MOD_ID, "turtle/${DigitizerPeripheral.ID.path}_left"),
                ResourceLocation.fromNamespaceAndPath(DigitalItemsCore.MOD_ID, "turtle/${DigitizerPeripheral.ID.path}_right"),
            ),
        )
        consumer(
            TurtleUpgradeSerializers.ADVANCED_DIGITIZER.get(),
            TurtleUpgradeModeller.sided<ITurtleUpgrade>(
                ResourceLocation.fromNamespaceAndPath(DigitalItemsCore.MOD_ID, "turtle/${AdvancedDigitizerPeripheral.ID.path}_left"),
                ResourceLocation.fromNamespaceAndPath(DigitalItemsCore.MOD_ID, "turtle/${AdvancedDigitizerPeripheral.ID.path}_right"),
            ),
        )
    }
}
