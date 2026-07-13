package site.siredvin.digitalitems.data

import dan200.computercraft.api.turtle.ITurtleUpgrade
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.world.item.ItemStack
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.common.setup.TurtleUpgradeSerializers

object ModTurtleUpgradeDataProvider : RegistrySetBuilder.RegistryBootstrap<ITurtleUpgrade> {
    override fun run(context: BootstrapContext<ITurtleUpgrade>) {
        context.register(
            ITurtleUpgrade.createKey(TurtleUpgradeSerializers.DIGITIZER.id),
            TurtleUpgradeSerializers.DIGITIZER.createUpgrade(ItemStack(ModBlocks.DIGITIZER.get())),
        )
        context.register(
            ITurtleUpgrade.createKey(TurtleUpgradeSerializers.ADVANCED_DIGITIZER.id),
            TurtleUpgradeSerializers.ADVANCED_DIGITIZER.createUpgrade(ItemStack(ModBlocks.ADVANCED_DIGITIZER.get())),
        )
    }
}
