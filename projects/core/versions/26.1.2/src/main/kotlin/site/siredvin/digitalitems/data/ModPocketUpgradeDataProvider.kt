package site.siredvin.digitalitems.data

import dan200.computercraft.api.pocket.IPocketUpgrade
import net.minecraft.core.RegistrySetBuilder
import net.minecraft.data.worldgen.BootstrapContext
import net.minecraft.resources.ResourceKey
import net.minecraft.world.item.ItemStackTemplate
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.common.setup.PocketUpgradeSerializers

object ModPocketUpgradeDataProvider : RegistrySetBuilder.RegistryBootstrap<IPocketUpgrade> {
    override fun run(context: BootstrapContext<IPocketUpgrade>) {
        context.register(
            ResourceKey.create(IPocketUpgrade.REGISTRY, PocketUpgradeSerializers.DIGITIZER.id),
            PocketUpgradeSerializers.DIGITIZER.createUpgrade(ItemStackTemplate(ModBlocks.DIGITIZER.get().asItem())),
        )
        context.register(
            ResourceKey.create(IPocketUpgrade.REGISTRY, PocketUpgradeSerializers.ADVANCED_DIGITIZER.id),
            PocketUpgradeSerializers.ADVANCED_DIGITIZER.createUpgrade(ItemStackTemplate(ModBlocks.ADVANCED_DIGITIZER.get().asItem())),
        )
    }
}
