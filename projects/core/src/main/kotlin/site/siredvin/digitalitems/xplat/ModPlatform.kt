package site.siredvin.digitalitems.xplat

import net.minecraft.advancements.CriterionTrigger
import net.minecraft.resources.ResourceLocation
import site.siredvin.tweakium.modules.platform.ComputerBasePlatform
import site.siredvin.tweakium.modules.platform.ComputerModInformationTracker
import site.siredvin.tweakium.modules.platform.api.InnerComputerBasePlatform
import java.util.function.Supplier

object ModPlatform : ComputerBasePlatform() {
    private var impl: InnerComputerBasePlatform? = null
    private var criterionRegistrar: ((ResourceLocation, CriterionTrigger<*>) -> Supplier<out CriterionTrigger<*>>)? = null
    private val tracker = ComputerModInformationTracker()
    fun configure(impl: InnerComputerBasePlatform) {
        this.impl = impl
    }

    fun configureCriterionRegistrar(registrar: (ResourceLocation, CriterionTrigger<*>) -> Supplier<out CriterionTrigger<*>>) {
        criterionRegistrar = registrar
    }

    fun <T : CriterionTrigger<*>> registerCriterion(id: ResourceLocation, trigger: T): Supplier<T> {
        @Suppress("UNCHECKED_CAST")
        return (criterionRegistrar ?: error("Criterion registrar is not configured"))(id, trigger) as Supplier<T>
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
