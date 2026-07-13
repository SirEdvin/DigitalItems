package site.siredvin.digitalitems.xplat

import site.siredvin.tweakium.modules.platform.ComputerBasePlatform
import site.siredvin.tweakium.modules.platform.ComputerModInformationTracker
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform

object ModPlatform : ComputerBasePlatform() {
    private var impl: InnerComputerBasePlatform? = null
    private val tracker = ComputerModInformationTracker()
    fun configure(impl: InnerComputerBasePlatform) {
        this.impl = impl
    }

    override val baseInnerPlatform: InnerComputerBasePlatform
        get() {
            if (impl == null) {
                throw IllegalStateException("You should init PeripheralWorks Platform first")
            }
            return impl!!
        }

    override val modInformationTracker: ComputerModInformationTracker
        get() = tracker
}
