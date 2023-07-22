package site.siredvin.digitalitems.common.critereon

import com.google.gson.JsonObject
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.DeserializationContext
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.digitalitems.modId

class DigitalizeStarCriteria : SimpleCriterionTrigger<CustomStatCountCondition>() {

    companion object {
        val ID = modId("digitilized_stars")

        fun digitilizeSome(count: Int): CustomStatCountCondition {
            return CustomStatCountCondition(
                ID,
                ModStats.DIGITALIZED_STARS,
                count,
            )
        }
    }
    override fun getId(): ResourceLocation {
        return ID
    }

    fun trigger(player: ServerPlayer) {
        trigger(player) {
            it.test(player)
        }
    }

    override fun createInstance(
        p0: JsonObject,
        p1: ContextAwarePredicate,
        p2: DeserializationContext,
    ): CustomStatCountCondition {
        return CustomStatCountCondition(
            ID,
            ModStats.DIGITALIZED_STARS,
            p0.get("count").asInt,
        )
    }
}
