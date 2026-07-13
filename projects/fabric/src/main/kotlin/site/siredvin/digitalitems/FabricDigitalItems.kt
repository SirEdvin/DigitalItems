package site.siredvin.digitalitems

import dan200.computercraft.api.peripheral.PeripheralLookup
import fuzs.forgeconfigapiport.fabric.api.neoforge.v4.NeoForgeConfigRegistry
import net.fabricmc.api.ModInitializer
import net.neoforged.fml.config.ModConfig
import site.siredvin.broccolium.modules.platform.PlatformToolkit
import site.siredvin.digitalitems.common.configuration.ConfigHolder
import site.siredvin.digitalitems.common.setup.ModBlockEntityTypes
import site.siredvin.digitalitems.fabric.FabricDigitalItemsPlatformToolkit
import site.siredvin.digitalitems.fabric.FabricModPlatform
import site.siredvin.digitalitems.fabric.FabricModRecipeIngredients
import site.siredvin.digitalitems.xplat.ModCommonHooks
import site.siredvin.tweakium.modules.FabricTweakium
import site.siredvin.tweakium.modules.peripheral.api.IPeripheralProvider

@Suppress("UNUSED")
object FabricDigitalItems : ModInitializer {

    override fun onInitialize() {
        // Register configuration
        FabricTweakium.sayHi()
        PlatformToolkit.configure(FabricDigitalItemsPlatformToolkit)
        DigitalItemsCore.configure(FabricModPlatform, FabricModRecipeIngredients)
        // Register items and blocks
        ModCommonHooks.onRegister()
        // Pretty important to setup configuration after integration loading!
        NeoForgeConfigRegistry.INSTANCE.register(DigitalItemsCore.MOD_ID, ModConfig.Type.COMMON, ConfigHolder.commonSpec)

        PeripheralLookup.get().registerForBlockEntities({ entity, direction ->
            if (entity is IPeripheralProvider<*>) {
                return@registerForBlockEntities entity.getPeripheral(direction)
            }
            return@registerForBlockEntities null
        }, ModBlockEntityTypes.DIGITIZER.get(), ModBlockEntityTypes.ADVANCED_DIGITIZER.get())
    }
}
