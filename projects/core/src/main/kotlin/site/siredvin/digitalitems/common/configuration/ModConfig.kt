package site.siredvin.digitalitems.common.configuration

import net.minecraftforge.common.ForgeConfigSpec

object ModConfig {

    val enableDecay: Boolean
        get() = ConfigHolder.COMMON_CONFIG.ENABLE_DECAY.get()

    val decayTicks: Long
        get() = ConfigHolder.COMMON_CONFIG.DECAY_TICKS.get()

    class CommonConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        // Generic plugins
        var ENABLE_DECAY: ForgeConfigSpec.BooleanValue
        val DECAY_TICKS: ForgeConfigSpec.LongValue

        init {
            builder.comment("Item decay options")
            builder.push("decay")
            ENABLE_DECAY = builder.comment("Is item decay enabled")
                .define("enableDecay", true)
            builder.comment("After how many ticks do digital items decay? Default: 120000 (5 in game days)")
            DECAY_TICKS = builder.defineInRange("decay_ticks", 20L * 60 * 20 * 5, 0, Long.MAX_VALUE)
            builder.pop()
        }
    }
}
