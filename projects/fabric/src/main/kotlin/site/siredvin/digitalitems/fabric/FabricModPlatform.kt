package site.siredvin.digitalitems.fabric

import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.tweakium.modules.platform.FabricInnerComputerBasePlatform

object FabricModPlatform : FabricInnerComputerBasePlatform() {
    override val modID: String
        get() = DigitalItemsCore.MOD_ID
}
