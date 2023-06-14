package site.siredvin.template.forge

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.template.ForgeTemplate
import site.siredvin.template.xplat.ModPlatform
import java.util.function.Supplier

object ForgeModPlatform : ModPlatform {
    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        return ForgeTemplate.itemsRegistry.register(key.path, item)
    }

    override fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> Item): Supplier<T> {
        val blockRegister = ForgeTemplate.blocksRegistry.register(key.path, block)
        ForgeTemplate.itemsRegistry.register(key.path) { itemFactory(blockRegister.get()) }
        return blockRegister
    }

    override fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab> {
        return ForgeTemplate.creativeTabRegistry.register(key.path) { tab }
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> {
        return ForgeTemplate.pocketSerializers.register(key.path) { serializer }
    }

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> {
        return ForgeTemplate.turtleSerializers.register(key.path) { serializer }
    }
}
