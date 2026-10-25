package site.siredvin.digitalitems.fabric

import io.netty.buffer.Unpooled
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerFactory
import net.fabricmc.fabric.api.screenhandler.v1.ExtendedScreenHandlerType
import net.minecraft.core.BlockPos
import net.minecraft.core.Registry
import net.minecraft.core.registries.BuiltInRegistries
import net.minecraft.network.FriendlyByteBuf
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.MenuProvider
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.entity.player.Player
import net.minecraft.world.inventory.AbstractContainerMenu
import net.minecraft.world.inventory.MenuType
import net.minecraft.world.level.block.entity.BlockEntity
import site.siredvin.broccolium.modules.platform.FabricPlatformToolkit
import site.siredvin.broccolium.modules.platform.api.InnerPlatformToolkit
import site.siredvin.broccolium.modules.platform.api.MenuBuilder
import site.siredvin.broccolium.modules.platform.api.SavingFunction
import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.tweakium.modules.platform.FabricInnerComputerBasePlatform
import java.util.function.Supplier

object FabricModPlatform : FabricInnerComputerBasePlatform() {
    override val modID: String
        get() = DigitalItemsCore.MOD_ID

    override fun <M : AbstractContainerMenu> registerMenu(key: ResourceLocation, builder: MenuBuilder<M>): Supplier<MenuType<M>> {
        val type = ExtendedScreenHandlerType(
            { id: Int, inventory: Inventory, pos: BlockPos ->
                val buffer = FriendlyByteBuf(Unpooled.buffer()).writeBlockPos(pos)
                builder.build(id, inventory, buffer)
            },
            BlockPos.STREAM_CODEC,
        )
        val registered = Registry.register(BuiltInRegistries.MENU, key, type)
        return Supplier { registered }
    }
}

object FabricDigitalItemsPlatformToolkit : InnerPlatformToolkit by FabricPlatformToolkit {
    override fun openMenu(player: Player, owner: MenuProvider, savingFunction: SavingFunction) {
        player.openMenu(
            object : ExtendedScreenHandlerFactory<BlockPos> {
                override fun getScreenOpeningData(player: ServerPlayer): BlockPos = (owner as BlockEntity).blockPos

                override fun createMenu(id: Int, inventory: Inventory, player: Player): AbstractContainerMenu? = owner.createMenu(id, inventory, player)

                override fun getDisplayName(): Component = owner.displayName
            },
        )
    }
}
