package site.siredvin.digitalitems.common.configuration

import net.minecraftforge.common.ForgeConfigSpec

object ConfigHolder {
    var commonSpec: ForgeConfigSpec
    var commonConfig: ModConfig.CommonConfig

    init {
        val (key, value) = ForgeConfigSpec.Builder()
            .configure { builder: ForgeConfigSpec.Builder -> ModConfig.CommonConfig(builder) }
        commonConfig = key
        commonSpec = value
    }
}
