package site.siredvin.digitalitems.common.criteria

import com.mojang.serialization.Codec
import net.minecraft.advancements.Criterion
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.server.level.ServerPlayer
import site.siredvin.digitalitems.common.setup.ModCriterias
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.digitalitems.modId

class DigitalizeLavaCriteria : SimpleCriterionTrigger<CustomStatCountCondition>() {

    companion object {
        val ID = modId("digitilized_lava")
        private val CODEC = CustomStatCountCondition.codec(ModStats.DIGITALIZED_LAVA)

        fun digitilizeSome(count: Int): Criterion<CustomStatCountCondition> = ModCriterias.DIGITALIZE_LAVA.createCriterion(
            CustomStatCountCondition.create(ModStats.DIGITALIZED_LAVA, count),
        )
    }

    override fun codec(): Codec<CustomStatCountCondition> = CODEC

    fun trigger(player: ServerPlayer) {
        trigger(player) {
            it.test(player)
        }
    }
}
