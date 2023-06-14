package site.siredvin.template.common.configuration

import net.minecraftforge.common.ForgeConfigSpec
import site.siredvin.peripheralium.api.config.IConfigHandler

object ModConfig {

    val enableSomething: Boolean
        get() = ConfigHolder.COMMON_CONFIG.ENABLE_SOMETHING.get()

    class CommonConfig internal constructor(builder: ForgeConfigSpec.Builder) {

        // Generic plugins
        var ENABLE_SOMETHING: ForgeConfigSpec.BooleanValue

        init {
            builder.push("base")
            ENABLE_SOMETHING = builder.comment("Something enabled")
                .define("enableSomething", true)
            builder.pop()
        }

        private fun register(data: Array<out IConfigHandler>, builder: ForgeConfigSpec.Builder) {
            for (handler in data) {
                handler.addToConfig(builder)
            }
        }
    }
}
