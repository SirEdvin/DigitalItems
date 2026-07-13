package site.siredvin.digitalitems.forge

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.upgrades.UpgradeType
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.neoforged.neoforge.registries.DeferredRegister
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.ForgeDigitalItems
import site.siredvin.tweakium.modules.platform.ForgeInnerComputerBasePlatform
import java.util.function.Supplier

object ForgeModPlatform : ForgeInnerComputerBasePlatform() {
    override val modID: String
        get() = DigitalItemsCore.MOD_ID

    override val blockEntityTypesRegistry: DeferredRegister<BlockEntityType<*>>
        get() = ForgeDigitalItems.blockEntityTypesRegistry
    override val blocksRegistry: DeferredRegister<Block>
        get() = ForgeDigitalItems.blocksRegistry
    override val creativeTabRegistry: DeferredRegister<CreativeModeTab>
        get() = ForgeDigitalItems.creativeTabRegistry
    override val customStats: DeferredRegister<ResourceLocation>
        get() = ForgeDigitalItems.customStats
    override val itemsRegistry: DeferredRegister<Item>
        get() = ForgeDigitalItems.itemsRegistry
    override val menuTypes: DeferredRegister<MenuType<*>>
        get() = ForgeDigitalItems.menuTypes

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(key: ResourceLocation, upgrade: UpgradeType<V>): Supplier<UpgradeType<V>> {
        @Suppress("UNCHECKED_CAST")
        return ForgeDigitalItems.turtleUpgradeTypes.register(key.path, Supplier { upgrade }) as Supplier<UpgradeType<V>>
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(key: ResourceLocation, upgrade: UpgradeType<V>): Supplier<UpgradeType<V>> {
        @Suppress("UNCHECKED_CAST")
        return ForgeDigitalItems.pocketUpgradeTypes.register(key.path, Supplier { upgrade }) as Supplier<UpgradeType<V>>
    }
}
