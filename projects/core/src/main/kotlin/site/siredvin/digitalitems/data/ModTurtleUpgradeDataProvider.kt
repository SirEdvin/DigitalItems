package site.siredvin.digitalitems.data

import dan200.computercraft.api.turtle.TurtleUpgradeDataProvider
import dan200.computercraft.api.turtle.TurtleUpgradeSerialiser
import net.minecraft.data.PackOutput
import site.siredvin.digitalitems.common.setup.ModBlocks
import site.siredvin.digitalitems.common.setup.TurtleUpgradeSerializers
import site.siredvin.digitalitems.xplat.ModPlatform
import site.siredvin.tweakium.modules.data.LibTurtleUpgradeDataProvider
import java.util.function.Consumer
import java.util.function.Function

class ModTurtleUpgradeDataProvider(output: PackOutput) : LibTurtleUpgradeDataProvider(output, ModPlatform.holder.turtleSerializers) {
    companion object {
        private val REGISTERED_BUILDERS: MutableList<Function<TurtleUpgradeDataProvider, Upgrade<TurtleUpgradeSerialiser<*>>>> =
            mutableListOf()

        fun hookUpgrade(builder: Function<TurtleUpgradeDataProvider, Upgrade<TurtleUpgradeSerialiser<*>>>) {
            REGISTERED_BUILDERS.add(builder)
        }
    }

    override fun registerUpgrades(addUpgrade: Consumer<Upgrade<TurtleUpgradeSerialiser<*>>>) {
        REGISTERED_BUILDERS.forEach {
            it.apply(this).add(addUpgrade)
        }

        addUpgrade.accept(simpleWithCustomItem(TurtleUpgradeSerializers.DIGITIZER, ModBlocks.DIGITIZER))
        addUpgrade.accept(simpleWithCustomItem(TurtleUpgradeSerializers.ADVANCED_DIGITIZER, ModBlocks.ADVANCED_DIGITIZER))
    }
}
