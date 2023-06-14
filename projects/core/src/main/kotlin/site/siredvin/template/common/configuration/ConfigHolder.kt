package site.siredvin.template.common.configuration

import net.minecraftforge.common.ForgeConfigSpec

object ConfigHolder {
    var COMMON_SPEC: ForgeConfigSpec
    var COMMON_CONFIG: ModConfig.CommonConfig

    init {
        val (key, value) = ForgeConfigSpec.Builder()
            .configure { builder: ForgeConfigSpec.Builder -> ModConfig.CommonConfig(builder) }
        COMMON_CONFIG = key
        COMMON_SPEC = value
    }
}
