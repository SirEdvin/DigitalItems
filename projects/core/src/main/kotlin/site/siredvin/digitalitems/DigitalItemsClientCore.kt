package site.siredvin.digitalitems

import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.client.gui.screens.MenuScreens
import net.minecraft.resources.ResourceLocation
import site.siredvin.digitalitems.client.DigitizerScreen
import site.siredvin.digitalitems.common.setup.ModMenus
import site.siredvin.digitalitems.common.setup.TurtleUpgradeSerializers
import site.siredvin.digitalitems.computercraft.AdvancedDigitizerPeripheral
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral
import java.util.function.BiConsumer

object DigitalItemsClientCore {
    fun onInit() {
        MenuScreens.register(ModMenus.DIGITIZER.get(), ::DigitizerScreen)
        MenuScreens.register(ModMenus.ADVANCED_DIGITIZER.get(), ::DigitizerScreen)
    }

    fun onModelRegister(consumer: BiConsumer<TurtleUpgradeSerialiser<*>, TurtleUpgradeModeller<ITurtleUpgrade>>) {
        consumer.accept(
            TurtleUpgradeSerializers.DIGITIZER.get(),
            TurtleUpgradeModeller.sided(
                ResourceLocation(DigitalItemsCore.MOD_ID, "turtle/${DigitizerPeripheral.ID.path}_left"),
                ResourceLocation(DigitalItemsCore.MOD_ID, "turtle/${DigitizerPeripheral.ID.path}_right"),
            ),
        )
        consumer.accept(
            TurtleUpgradeSerializers.ADVANCED_DIGITIZER.get(),
            TurtleUpgradeModeller.sided(
                ResourceLocation(DigitalItemsCore.MOD_ID, "turtle/${AdvancedDigitizerPeripheral.ID.path}_left"),
                ResourceLocation(DigitalItemsCore.MOD_ID, "turtle/${AdvancedDigitizerPeripheral.ID.path}_right"),
            ),
        )
    }
}
