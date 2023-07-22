package site.siredvin.digitalitems.forge

import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.registries.DeferredRegister
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.digitalitems.ForgeDigitalItems
import site.siredvin.peripheralium.forge.ForgeBaseInnerPlatform

object ForgeModPlatform : ForgeBaseInnerPlatform() {
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
    override val pocketSerializers: DeferredRegister<PocketUpgradeSerialiser<*>>
        get() = ForgeDigitalItems.pocketSerializers
    override val turtleSerializers: DeferredRegister<TurtleUpgradeSerialiser<*>>
        get() = ForgeDigitalItems.turtleSerializers
}
