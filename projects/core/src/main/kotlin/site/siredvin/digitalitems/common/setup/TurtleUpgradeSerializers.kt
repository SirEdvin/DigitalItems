package site.siredvin.digitalitems.common.setup

import dan200.computercraft.api.turtle.ITurtleAccess
import dan200.computercraft.api.turtle.TurtleSide
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import site.siredvin.digitalitems.computercraft.AdvancedDigitizerPeripheral
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.tweakium.modules.peripheral.owner.TurtlePeripheralOwner
import site.siredvin.tweakium.modules.turtle.PeripheralTurtleUpgrade

object TurtleUpgradeSerializers {

    val DIGITIZER = ModPlatform.registerTurtleUpgrade(
        DigitizerPeripheral.ID,
        TurtleUpgradeSerialiser.simpleWithCustomItem { id, stack ->
            PeripheralTurtleUpgrade.dynamic(
                stack.item,
                { turtle: ITurtleAccess, side: TurtleSide -> DigitizerPeripheral(TurtlePeripheralOwner(turtle, side)) },
                { DigitizerPeripheral.ID },
            )
        },
    )
    val ADVANCED_DIGITIZER = ModPlatform.registerTurtleUpgrade(
        AdvancedDigitizerPeripheral.ID,
        TurtleUpgradeSerialiser.simpleWithCustomItem { id, stack ->
            PeripheralTurtleUpgrade.dynamic(
                stack.item,
                { turtle: ITurtleAccess, side: TurtleSide -> AdvancedDigitizerPeripheral(TurtlePeripheralOwner(turtle, side)) },
                { AdvancedDigitizerPeripheral.ID },
            )
        },
    )

    fun doSomething() {}
}
