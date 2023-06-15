package site.siredvin.digitalitems.forge

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import net.minecraftforge.common.extensions.IForgeMenuType
import net.minecraftforge.network.NetworkHooks
import site.siredvin.digitalitems.ForgeDigitalItems
import site.siredvin.digitalitems.xplat.ModPlatform
import java.util.function.Supplier

object ForgeModPlatform : ModPlatform {
    override fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
        return ForgeDigitalItems.itemsRegistry.register(key.path, item)
    }

    override fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> Item): Supplier<T> {
        val blockRegister = ForgeDigitalItems.blocksRegistry.register(key.path, block)
        ForgeDigitalItems.itemsRegistry.register(key.path) { itemFactory(blockRegister.get()) }
        return blockRegister
    }

    override fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T> {
        return ForgeDigitalItems.blockEntityTypesRegistry.register(key.path, blockEntityTypeSup)
    }

    override fun <M : AbstractContainerMenu> registerMenu(
        key: ResourceLocation,
        builder: ModPlatform.MenuBuilder<M>,
    ): Supplier<MenuType<M>> {
        val result = ForgeDigitalItems.menuTypes.register(key.path) {
            IForgeMenuType.create(builder::build)
        }
        return result
    }

    override fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab> {
        return ForgeDigitalItems.creativeTabRegistry.register(key.path) { tab }
    }

    override fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>> {
        return ForgeDigitalItems.pocketSerializers.register(key.path) { serializer }
    }

    override fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>> {
        return ForgeDigitalItems.turtleSerializers.register(key.path) { serializer }
    }

    override fun openMenu(player: Player, owner: MenuProvider, savingFunction: ModPlatform.SavingFunction) {
        NetworkHooks.openScreen(player as ServerPlayer, owner, savingFunction::toBytes)
    }
}
