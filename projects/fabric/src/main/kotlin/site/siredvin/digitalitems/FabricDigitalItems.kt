package site.siredvin.digitalitems
import dan200.computercraft.api.peripheral.PeripheralLookup
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import net.fabricmc.api.ModInitializer
import net.minecraftforge.fml.config.ModConfig
import site.siredvin.digitalitems.common.configuration.ConfigHolder
import site.siredvin.digitalitems.common.setup.ModBlockEntityTypes
import site.siredvin.digitalitems.fabric.FabricModPlatform
import site.siredvin.digitalitems.fabric.FabricModRecipeIngredients
import site.siredvin.digitalitems.xplat.ModCommonHooks
import site.siredvin.peripheralium.FabricPeripheralium
import site.siredvin.peripheralium.api.peripheral.IPeripheralProvider

@Suppress("UNUSED")
object FabricDigitalItems : ModInitializer {

    override fun onInitialize() {
        // Register configuration
        FabricPeripheralium.sayHi()
        DigitalItemsCore.configure(FabricModPlatform, FabricModRecipeIngredients)
        // Register items and blocks
        ModCommonHooks.onRegister()
        // Pretty important to setup configuration after integration loading!
        ForgeConfigRegistry.INSTANCE.register(DigitalItemsCore.MOD_ID, ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC)

        PeripheralLookup.get().registerForBlockEntities({ entity, direction ->
            if (entity is IPeripheralProvider<*>) {
                return@registerForBlockEntities entity.getPeripheral(direction)
            }
            return@registerForBlockEntities null
        }, ModBlockEntityTypes.DIGITIZER.get())
    }
}
