package site.siredvin.digitalitems.common.setup

import site.siredvin.digitalitems.computercraft.AdvancedDigitizerPeripheral
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.tweakium.modules.peripheral.owner.PocketPeripheralOwner
import site.siredvin.tweakium.modules.pocket.PeripheralPocketUpgrade

object PocketUpgradeSerializers {

    val DIGITIZER = ModPlatform.registerPocketUpgradeWithSelfCustomItem(
        DigitizerPeripheral.ID,
    ) { id, type, stack ->
        PeripheralPocketUpgrade(
            id,
            stack,
            { DigitizerPeripheral(PocketPeripheralOwner(it)) },
            { type },
        )
    }

    val ADVANCED_DIGITIZER = ModPlatform.registerPocketUpgradeWithSelfCustomItem(
        AdvancedDigitizerPeripheral.ID,
    ) { id, type, stack ->
        PeripheralPocketUpgrade(
            id,
            stack,
            { AdvancedDigitizerPeripheral(PocketPeripheralOwner(it)) },
            { type },
        )
    }

    fun doSomething() {}
}
