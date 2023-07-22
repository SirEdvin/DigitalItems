package site.siredvin.digitalitems.common.critereon

import com.google.gson.JsonObject
import net.minecraft.advancements.critereon.AbstractCriterionTriggerInstance
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.SerializationContext
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stat
import java.util.function.Supplier

class CustomStatCountCondition(id: ResourceLocation, private val stat: Supplier<Stat<ResourceLocation>>, private val count: Int) :
    AbstractCriterionTriggerInstance(id, ContextAwarePredicate.ANY) {

    fun test(player: ServerPlayer): Boolean {
        return player.stats.getValue(stat.get()) >= count
    }

    override fun serializeToJson(context: SerializationContext): JsonObject {
        val base = super.serializeToJson(context)
        base.addProperty("count", count)
        return base
    }
}
