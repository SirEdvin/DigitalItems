package site.siredvin.digitalitems.common.setup

import net.minecraft.advancements.CriteriaTriggers
import site.siredvin.digitalitems.common.criteria.DigitalizeEnergyCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeFluidCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeItemCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeLavaCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeOreCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeStarCriteria

object ModCriterias {
    val DIGITALIZE_ITEMS = CriteriaTriggers.register(
        DigitalizeItemCriteria(),
    )
    val DIGITALIZE_ORES = CriteriaTriggers.register(
        DigitalizeOreCriteria(),
    )
    val DIGITALIZE_STARS = CriteriaTriggers.register(
        DigitalizeStarCriteria(),
    )
    val DIGITALIZE_FLUIDS = CriteriaTriggers.register(
        DigitalizeFluidCriteria(),
    )
    val DIGITALIZE_ENERGY = CriteriaTriggers.register(
        DigitalizeEnergyCriteria(),
    )
    val DIGITALIZE_LAVA = CriteriaTriggers.register(
        DigitalizeLavaCriteria(),
    )

    fun doSomething() {}
}
