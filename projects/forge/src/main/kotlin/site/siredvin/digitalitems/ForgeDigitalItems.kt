package site.siredvin.digitalitems

import dan200.computercraft.api.ForgeComputerCraftAPI
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.common.util.LazyOptional
import net.minecraftforge.fml.ModLoadingContext
import net.minecraftforge.fml.common.Mod
import net.minecraftforge.fml.config.ModConfig
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent
import net.minecraftforge.registries.DeferredRegister
import net.minecraftforge.registries.ForgeRegistries
import site.siredvin.digitalitems.common.configuration.ConfigHolder
import site.siredvin.digitalitems.forge.ForgeModPlatform
import site.siredvin.digitalitems.forge.ForgeModRecipeIngredients
import site.siredvin.digitalitems.xplat.ModCommonHooks
import site.siredvin.peripheralium.ForgePeripheralium
import site.siredvin.peripheralium.api.peripheral.IPeripheralProvider
import thedarkcolour.kotlinforforge.forge.MOD_CONTEXT

@Mod(DigitalItemsCore.MOD_ID)
@Mod.EventBusSubscriber(modid = DigitalItemsCore.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD)
object ForgeDigitalItems {

    val blocksRegistry: DeferredRegister<Block> =
        DeferredRegister.create(ForgeRegistries.BLOCKS, DigitalItemsCore.MOD_ID)
    val itemsRegistry: DeferredRegister<Item> =
        DeferredRegister.create(ForgeRegistries.ITEMS, DigitalItemsCore.MOD_ID)
    val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>> =
        DeferredRegister.create(ForgeRegistries.BLOCK_ENTITY_TYPES, DigitalItemsCore.MOD_ID)
    val creativeTabRegistry: DeferredRegister<CreativeModeTab> =
        DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), DigitalItemsCore.MOD_ID)
    val turtleSerializers = DeferredRegister.create(
        TurtleUpgradeSerialiser.registryId(),
        DigitalItemsCore.MOD_ID,
    )
    val pocketSerializers = DeferredRegister.create(
        PocketUpgradeSerialiser.registryId(),
        DigitalItemsCore.MOD_ID,
    )
    val menuTypes = DeferredRegister.create(ForgeRegistries.MENU_TYPES, DigitalItemsCore.MOD_ID)

    init {
        ForgePeripheralium.sayHi()
        // Configure configuration
        val context = ModLoadingContext.get()
        context.registerConfig(ModConfig.Type.COMMON, ConfigHolder.COMMON_SPEC, "${DigitalItemsCore.MOD_ID}.toml")
        DigitalItemsCore.configure(ForgeModPlatform, ForgeModRecipeIngredients)
        val eventBus = MOD_CONTEXT.getKEventBus()
        eventBus.addListener(this::commonSetup)
        // Register items and blocks
        ModCommonHooks.onRegister()
        blocksRegistry.register(eventBus)
        itemsRegistry.register(eventBus)
        blockEntityTypesRegistry.register(eventBus)
        creativeTabRegistry.register(eventBus)
        turtleSerializers.register(eventBus)
        pocketSerializers.register(eventBus)
        menuTypes.register(eventBus)
    }

    fun commonSetup(event: FMLCommonSetupEvent) {
        // Register peripheral provider
        ForgeComputerCraftAPI.registerPeripheralProvider { world, pos, side ->
            val entity = world.getBlockEntity(pos)
            if (entity is IPeripheralProvider<*>) {
                val foundPeripheral = entity.getPeripheral(side)
                if (foundPeripheral != null) {
                    return@registerPeripheralProvider LazyOptional.of { foundPeripheral }
                }
            }
            return@registerPeripheralProvider LazyOptional.empty()
        }
    }
}
