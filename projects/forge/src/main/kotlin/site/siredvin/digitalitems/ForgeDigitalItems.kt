package site.siredvin.digitalitems

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.advancements.CriterionTrigger
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.bus.api.IEventBus
import net.neoforged.fml.ModContainer
import net.neoforged.fml.common.Mod
import net.neoforged.fml.config.ModConfig
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.digitalitems.common.configuration.ConfigHolder
import site.siredvin.digitalitems.forge.ForgeModPlatform
import site.siredvin.digitalitems.forge.ForgeModRecipeIngredients
import site.siredvin.digitalitems.xplat.ModCommonHooks
import site.siredvin.tweakium.ForgeTweakium

@Mod(DigitalItemsCore.MOD_ID)
class ForgeDigitalItems(modEventBus: IEventBus, modContainer: ModContainer) {

    companion object {
        val blocksRegistry: DeferredRegister<Block> =
            DeferredRegister.create(BuiltInRegistries.BLOCK, DigitalItemsCore.MOD_ID)
        val itemsRegistry: DeferredRegister<Item> =
            DeferredRegister.create(BuiltInRegistries.ITEM, DigitalItemsCore.MOD_ID)
        val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>> =
            DeferredRegister.create(BuiltInRegistries.BLOCK_ENTITY_TYPE, DigitalItemsCore.MOD_ID)
        val creativeTabRegistry: DeferredRegister<CreativeModeTab> =
            DeferredRegister.create(BuiltInRegistries.CREATIVE_MODE_TAB.key(), DigitalItemsCore.MOD_ID)
        val menuTypes = DeferredRegister.create(BuiltInRegistries.MENU, DigitalItemsCore.MOD_ID)
        val customStats = DeferredRegister.create(BuiltInRegistries.CUSTOM_STAT, DigitalItemsCore.MOD_ID)
        val criterionTriggers = DeferredRegister.create(BuiltInRegistries.TRIGGER_TYPES, DigitalItemsCore.MOD_ID)
        val turtleUpgradeTypes: DeferredRegister<UpgradeType<out ITurtleUpgrade>> = DeferredRegister.create(ITurtleUpgrade.typeRegistry(), DigitalItemsCore.MOD_ID)
        val pocketUpgradeTypes: DeferredRegister<UpgradeType<out IPocketUpgrade>> = DeferredRegister.create(IPocketUpgrade.typeRegistry(), DigitalItemsCore.MOD_ID)
    }

    init {
        ForgeTweakium.sayHi()
        modContainer.registerConfig(ModConfig.Type.COMMON, ConfigHolder.commonSpec, "${DigitalItemsCore.MOD_ID}.toml")
        DigitalItemsCore.configure(ForgeModPlatform, ForgeModRecipeIngredients)
        ModCommonHooks.onRegister()
        blocksRegistry.register(modEventBus)
        itemsRegistry.register(modEventBus)
        blockEntityTypesRegistry.register(modEventBus)
        creativeTabRegistry.register(modEventBus)
        menuTypes.register(modEventBus)
        customStats.register(modEventBus)
        criterionTriggers.register(modEventBus)
        turtleUpgradeTypes.register(modEventBus)
        pocketUpgradeTypes.register(modEventBus)
    }
}
