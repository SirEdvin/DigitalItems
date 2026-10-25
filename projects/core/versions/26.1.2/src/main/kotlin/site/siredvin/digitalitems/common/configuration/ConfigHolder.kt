package site.siredvin.digitalitems.common.configuration

import net.neoforged.neoforge.common.ModConfigSpec

object ConfigHolder {
    var commonSpec: ModConfigSpec
    var commonConfig: ModConfig.CommonConfig

    init {
        val (key, value) = ModConfigSpec.Builder()
            .configure { builder: ModConfigSpec.Builder -> ModConfig.CommonConfig(builder) }
        commonConfig = key
        commonSpec = value
    }
}
