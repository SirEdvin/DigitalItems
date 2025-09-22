package site.siredvin.digitalitems.data

import dan200.computercraft.api.pocket.PocketUpgradeDataProvider
import dan200.computercraft.api.pocket.PocketUpgradeSerialiser
import net.minecraft.data.PackOutput
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.common.setup.PocketUpgradeSerializers
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.tweakium.modules.data.LibPocketUpgradeDataProvider
import java.util.function.Consumer
import java.util.function.Function

class ModPocketUpgradeDataProvider(output: PackOutput) : LibPocketUpgradeDataProvider(output, ModPlatform.holder.pocketSerializers) {
    companion object {
        private val REGISTERED_BUILDERS: MutableList<Function<PocketUpgradeDataProvider, Upgrade<PocketUpgradeSerialiser<*>>>> = mutableListOf()

        fun hookUpgrade(builder: Function<PocketUpgradeDataProvider, Upgrade<PocketUpgradeSerialiser<*>>>) {
            REGISTERED_BUILDERS.add(builder)
        }
    }

    override fun registerUpgrades(addUpgrade: Consumer<Upgrade<PocketUpgradeSerialiser<*>>>) {
        REGISTERED_BUILDERS.forEach {
            it.apply(this).add(addUpgrade)
        }
        addUpgrade.accept(simpleWithCustomItem(PocketUpgradeSerializers.DIGITIZER, ModBlocks.DIGITIZER))
        addUpgrade.accept(simpleWithCustomItem(PocketUpgradeSerializers.ADVANCED_DIGITIZER, ModBlocks.ADVANCED_DIGITIZER))
    }
}
