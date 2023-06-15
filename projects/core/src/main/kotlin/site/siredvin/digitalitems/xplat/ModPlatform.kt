package site.siredvin.digitalitems.xplat

import dan200.computercraft.api.pocket.IPocketUpgrade
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import dan200.computercraft.api.turtle.ITurtleUpgrade
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.item.CreativeModeTab
import net.minecraft.world.item.Item
import net.minecraft.world.level.block.Block
import net.minecraft.world.level.block.entity.BlockEntity
import net.minecraft.world.level.block.entity.BlockEntityType
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.peripheralium.common.items.DescriptiveBlockItem
import site.siredvin.peripheralium.data.language.ModInformationHolder
import java.util.function.Supplier

interface ModPlatform : ModInformationHolder {

    fun interface MenuBuilder<M : AbstractContainerMenu> {
        fun build(id: Int, player: Inventory, data: FriendlyByteBuf): M
    }

    fun interface SavingFunction {
        fun toBytes(buf: FriendlyByteBuf)
    }

    companion object {
        private var _IMPL: ModPlatform? = null
        private val ITEMS: MutableList<Supplier<out Item>> = mutableListOf()
        private val BLOCKS: MutableList<Supplier<out Block>> = mutableListOf()
        private val POCKET_UPGRADES: MutableList<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>> = mutableListOf()
        private val TURTLE_UPGRADES: MutableList<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>> = mutableListOf()

        val holder: ModInformationHolder
            get() = get()

        fun configure(impl: ModPlatform) {
            _IMPL = impl
        }

        private fun get(): ModPlatform {
            if (_IMPL == null) {
                throw IllegalStateException("You should init PeripheralWorks Platform first")
            }
            return _IMPL!!
        }

        fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T> {
            val registeredItem = get().registerItem(key, item)
            ITEMS.add(registeredItem)
            return registeredItem
        }

        fun <T : Item> registerItem(name: String, item: Supplier<T>): Supplier<T> {
            return registerItem(ResourceLocation(DigitalItemsCore.MOD_ID, name), item)
        }

        fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): Supplier<T> {
            return get().registerBlock(key, block, itemFactory)
        }

        fun <T : Block> registerBlock(name: String, block: Supplier<T>, itemFactory: (T) -> (Item) = { block -> DescriptiveBlockItem(block, Item.Properties()) }): Supplier<T> {
            val registeredBlock = get()
                .registerBlock(ResourceLocation(DigitalItemsCore.MOD_ID, name), block, itemFactory)
            BLOCKS.add(registeredBlock)
            return registeredBlock
        }

        fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
            name: String,
            blockEntityTypeSup: Supplier<T>,
        ): Supplier<T> {
            return registerBlockEntity(ResourceLocation(DigitalItemsCore.MOD_ID, name), blockEntityTypeSup)
        }

        fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
            key: ResourceLocation,
            blockEntityTypeSup: Supplier<T>,
        ): Supplier<T> {
            return get().registerBlockEntity(key, blockEntityTypeSup)
        }

        fun <M : AbstractContainerMenu> registerMenu(
            name: String,
            builder: MenuBuilder<M>,
        ): Supplier<MenuType<M>> {
            return get().registerMenu(ResourceLocation(DigitalItemsCore.MOD_ID, name), builder)
        }

        fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab> {
            return get().registerCreativeTab(key, tab)
        }

        fun <V : ITurtleUpgrade> registerTurtleUpgrade(
            name: String,
            serializer: TurtleUpgradeSerialiser<V>,
        ): Supplier<TurtleUpgradeSerialiser<V>> {
            return registerTurtleUpgrade(ResourceLocation(DigitalItemsCore.MOD_ID, name), serializer)
        }

        fun <V : ITurtleUpgrade> registerTurtleUpgrade(
            key: ResourceLocation,
            serializer: TurtleUpgradeSerialiser<V>,
        ): Supplier<TurtleUpgradeSerialiser<V>> {
            val registered = get().registerTurtleUpgrade(key, serializer)
            TURTLE_UPGRADES.add(registered as Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>)
            return registered
        }

        fun <V : IPocketUpgrade> registerPocketUpgrade(
            name: String,
            serializer: PocketUpgradeSerialiser<V>,
        ): Supplier<PocketUpgradeSerialiser<V>> {
            return registerPocketUpgrade(ResourceLocation(DigitalItemsCore.MOD_ID, name), serializer)
        }

        fun <V : IPocketUpgrade> registerPocketUpgrade(
            key: ResourceLocation,
            serializer: PocketUpgradeSerialiser<V>,
        ): Supplier<PocketUpgradeSerialiser<V>> {
            val registered = get().registerPocketUpgrade(key, serializer)
            POCKET_UPGRADES.add(registered as Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>)
            return registered
        }

        fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction) {
            get().openMenu(player, owner, savingFunction)
        }
    }

    override val blocks: List<Supplier<out Block>>
        get() = BLOCKS

    override val items: List<Supplier<out Item>>
        get() = ITEMS

    override val turtleSerializers: List<Supplier<TurtleUpgradeSerialiser<out ITurtleUpgrade>>>
        get() = TURTLE_UPGRADES

    override val pocketSerializers: List<Supplier<PocketUpgradeSerialiser<out IPocketUpgrade>>>
        get() = POCKET_UPGRADES

    fun <T : Item> registerItem(key: ResourceLocation, item: Supplier<T>): Supplier<T>

    fun <T : Block> registerBlock(key: ResourceLocation, block: Supplier<T>, itemFactory: (T) -> (Item)): Supplier<T>

    fun <V : BlockEntity, T : BlockEntityType<V>> registerBlockEntity(
        key: ResourceLocation,
        blockEntityTypeSup: Supplier<T>,
    ): Supplier<T>

    fun <M : AbstractContainerMenu> registerMenu(
        key: ResourceLocation,
        builder: MenuBuilder<M>,
    ): Supplier<MenuType<M>>

    fun registerCreativeTab(key: ResourceLocation, tab: CreativeModeTab): Supplier<CreativeModeTab>

    fun <V : ITurtleUpgrade> registerTurtleUpgrade(
        key: ResourceLocation,
        serializer: TurtleUpgradeSerialiser<V>,
    ): Supplier<TurtleUpgradeSerialiser<V>>
    fun <V : IPocketUpgrade> registerPocketUpgrade(
        key: ResourceLocation,
        serializer: PocketUpgradeSerialiser<V>,
    ): Supplier<PocketUpgradeSerialiser<V>>

    fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction)
}
