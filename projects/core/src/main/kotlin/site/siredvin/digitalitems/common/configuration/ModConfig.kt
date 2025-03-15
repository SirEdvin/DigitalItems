package site.siredvin.digitalitems.common.configuration

import net.minecraftforge.common.ForgeConfigSpec

object ModConfig {

    val enableDecay: Boolean
        get() = ConfigHolder.commonConfig.enableDecay.get()

    val decayTicks: Long
        get() = ConfigHolder.commonConfig.decayTicks.get()

    class CommonConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        // Generic plugins
        var enableDecay: ForgeConfigSpec.BooleanValue
        val decayTicks: ForgeConfigSpec.LongValue

        init {
            builder.comment("Item decay options")
            builder.push("decay")
            enableDecay = builder.comment("Is item decay enabled")
                .define("enableDecay", true)
            builder.comment("After how many ticks do digital items decay? Default: 120000 (5 in game days)")
            decayTicks = builder.defineInRange("decay_ticks", 20L * 60 * 20 * 5, 0, Long.MAX_VALUE)
            builder.pop()
        }
    }
}
