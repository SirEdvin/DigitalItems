package site.siredvin.digitalitems.fabric

import site.siredvin.digitalitems.DigitalItemsCore
import site.siredvin.peripheralium.fabric.FabricBaseInnerPlatform

object FabricModPlatform : FabricBaseInnerPlatform() {
    override val modID: String
        get() = DigitalItemsCore.MOD_ID
}
