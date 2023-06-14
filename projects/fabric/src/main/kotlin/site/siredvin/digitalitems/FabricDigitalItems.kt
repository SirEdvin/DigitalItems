package site.siredvin.digitalitems
import fuzs.forgeconfigapiport.api.config.v2.ForgeConfigRegistry
import net.fabricmc.api.ModInitializer
import net.minecraftforge.fml.config.ModConfig
import site.siredvin.peripheralium.FabricPeripheralium
import site.siredvin.digitalitems.common.configuration.ConfigHolder
import site.siredvin.digitalitems.fabric.FabricModPlatform
import site.siredvin.digitalitems.fabric.FabricModRecipeIngredients
import site.siredvin.digitalitems.xplat.ModCommonHooks

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
    }
}
