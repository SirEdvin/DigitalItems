package site.siredvin.digitalitems.common.setup

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import site.siredvin.digitalitems.computercraft.AdvancedDigitizerPeripheral
import site.siredvin.digitalitems.computercraft.DigitizerPeripheral
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.tweakium.modules.peripheral.owner.PocketPeripheralOwner
import site.siredvin.tweakium.modules.pocket.PeripheralPocketUpgrade

object PocketUpgradeSerializers {

    val DIGITIZER = ModPlatform.registerPocketUpgrade(
        DigitizerPeripheral.ID,
        PocketUpgradeSerialiser.simpleWithCustomItem { id, stack ->
            PeripheralPocketUpgrade(
                id,
                stack,
                { DigitizerPeripheral(PocketPeripheralOwner(it)) },
            )
        },
    )

    val ADVANCED_DIGITIZER = ModPlatform.registerPocketUpgrade(
        AdvancedDigitizerPeripheral.ID,
        PocketUpgradeSerialiser.simpleWithCustomItem { id, stack ->
            PeripheralPocketUpgrade(
                id,
                stack,
                { AdvancedDigitizerPeripheral(PocketPeripheralOwner(it)) },
            )
        },
    )

    fun doSomething() {}
}
