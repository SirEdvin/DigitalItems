package site.siredvin.digitalitems.common.configuration

import net.neoforged.neoforge.common.ModConfigSpec

object ModConfig {

    val enableDecay: Boolean
        get() = ConfigHolder.commonConfig.enableDecay.get()

    val decayTicks: Long
        get() = ConfigHolder.commonConfig.decayTicks.get()

    val itemStackLimit: Int
        get() = ConfigHolder.commonConfig.itemStackLimit.get()

    val fluidStackLimit: Long
        get() = ConfigHolder.commonConfig.fluidStackLimit.get()

    val energyStackLimit: Long
        get() = ConfigHolder.commonConfig.energyStackLimit.get()

    val inventoryTransferLimit: Int
        get() = ConfigHolder.commonConfig.inventoryTransferLimit.get()

    class CommonConfig internal constructor(builder: ModConfigSpec.Builder) {

        // Generic plugins
        var enableDecay: ModConfigSpec.BooleanValue
        val decayTicks: ModConfigSpec.LongValue
        val itemStackLimit: ModConfigSpec.IntValue
        val fluidStackLimit: ModConfigSpec.LongValue
        val energyStackLimit: ModConfigSpec.LongValue
        val inventoryTransferLimit: ModConfigSpec.IntValue

        init {
            builder.comment("Item decay options")
            builder.push("decay")
            enableDecay = builder.comment("Is item decay enabled")
                .define("enableDecay", true)
            builder.comment("After how many ticks do digital items decay? Default: 120000 (5 in game days)")
            decayTicks = builder.defineInRange("decay_ticks", 20L * 60 * 20 * 5, 0, Long.MAX_VALUE)
            itemStackLimit = builder.defineInRange("itemStackLimit", 16384, 64, Int.MAX_VALUE)
            fluidStackLimit = builder.defineInRange("fluidStackLimit", Long.MAX_VALUE / 2, 1024, Long.MAX_VALUE)
            energyStackLimit = builder.defineInRange("energyStackLimit", Long.MAX_VALUE / 2, 1024, Long.MAX_VALUE)
            inventoryTransferLimit = builder.defineInRange("inventoryTransferLimit", 1024, 1, Int.MAX_VALUE)
            builder.pop()
        }
    }
}
