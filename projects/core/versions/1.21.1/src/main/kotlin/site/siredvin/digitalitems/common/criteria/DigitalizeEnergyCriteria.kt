package site.siredvin.digitalitems.common.criteria

import com.mojang.serialization.Codec
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import site.siredvin.digitalitems.common.setup.ModCriterias
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.digitalitems.modId

class DigitalizeEnergyCriteria : SimpleCriterionTrigger<CustomStatCountCondition>() {

    companion object {
        val ID = modId("digitilized_energy")
        private val CODEC = CustomStatCountCondition.codec(ModStats.DIGITALIZED_ENERGY)

        fun digitilizeSome(count: Int): Criterion<CustomStatCountCondition> = ModCriterias.DIGITALIZE_ENERGY.createCriterion(
            CustomStatCountCondition.create(ModStats.DIGITALIZED_ENERGY, count),
        )
    }

    override fun codec(): Codec<CustomStatCountCondition> = CODEC

    fun trigger(player: ServerPlayer) {
        trigger(player) {
            it.test(player)
        }
    }
}
