package site.siredvin.digitalitems.common.setup

import site.siredvin.digitalitems.common.criteria.DigitalizeEnergyCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeFluidCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeItemCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeLavaCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeOreCriteria
import site.siredvin.digitalitems.common.criteria.DigitalizeStarCriteria
import site.siredvin.digitalitems.xplat.ModPlatform

object ModCriterias {
    private val digitalizeItems = ModPlatform.registerCriterionTrigger(DigitalizeItemCriteria.ID, DigitalizeItemCriteria())
    private val digitalizeOres = ModPlatform.registerCriterionTrigger(DigitalizeOreCriteria.ID, DigitalizeOreCriteria())
    private val digitalizeStars = ModPlatform.registerCriterionTrigger(DigitalizeStarCriteria.ID, DigitalizeStarCriteria())
    private val digitalizeFluids = ModPlatform.registerCriterionTrigger(DigitalizeFluidCriteria.ID, DigitalizeFluidCriteria())
    private val digitalizeEnergy = ModPlatform.registerCriterionTrigger(DigitalizeEnergyCriteria.ID, DigitalizeEnergyCriteria())
    private val digitalizeLava = ModPlatform.registerCriterionTrigger(DigitalizeLavaCriteria.ID, DigitalizeLavaCriteria())

    val DIGITALIZE_ITEMS: DigitalizeItemCriteria get() = digitalizeItems.get()
    val DIGITALIZE_ORES: DigitalizeOreCriteria get() = digitalizeOres.get()
    val DIGITALIZE_STARS: DigitalizeStarCriteria get() = digitalizeStars.get()
    val DIGITALIZE_FLUIDS: DigitalizeFluidCriteria get() = digitalizeFluids.get()
    val DIGITALIZE_ENERGY: DigitalizeEnergyCriteria get() = digitalizeEnergy.get()
    val DIGITALIZE_LAVA: DigitalizeLavaCriteria get() = digitalizeLava.get()

    fun doSomething() {}
}
