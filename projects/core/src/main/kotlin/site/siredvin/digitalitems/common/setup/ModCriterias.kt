package site.siredvin.digitalitems.common.setup

import net.minecraft.advancements.CriteriaTriggers
import site.siredvin.digitalitems.common.critereon.DigitalizeItemCriteria
import site.siredvin.digitalitems.common.critereon.DigitalizeOreCriteria
import site.siredvin.digitalitems.common.critereon.DigitalizeStarCriteria

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

    fun doSomething() {}
}
