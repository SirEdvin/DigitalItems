package site.siredvin.digitalitems.testmod

import dan200.computercraft.api.peripheral.PeripheralLookup
import net.fabricmc.api.ModInitializer
import net.fabricmc.fabric.api.event.lifecycle.v1.ServerLifecycleEvents
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.fabric.FabricModPlatform
import site.siredvin.digitalitems.fabric.FabricModRecipeIngredients
import site.siredvin.testiarium.cct.CctComputers
import site.siredvin.testiarium.cct.CctFixtureCommands
import site.siredvin.tweakium.modules.FabricTweakium

object FabricDigitalItemsTestMod : ModInitializer {
    override fun onInitialize() {
        FabricTweakium.sayHi()
        DigitalItemsCore.configure(FabricModPlatform, FabricModRecipeIngredients)
        DigitalItemsTestContent.register()
        PeripheralLookup.get().registerForBlockEntities(
            { entity, direction -> (entity as? TestStorageBlockEntity)?.getPeripheral(direction) },
            DigitalItemsTestContent.BLOCK_ENTITY.get(),
        )
        CctComputers.initialize()
        ServerLifecycleEvents.SERVER_STARTING.register {
            CctComputers.reset()
            CctFixtureCommands.importFiles(it)
        }
    }
}
