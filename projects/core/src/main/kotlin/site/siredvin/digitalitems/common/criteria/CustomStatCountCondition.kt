package site.siredvin.digitalitems.common.criteria

import com.mojang.serialization.Codec
import com.mojang.serialization.codecs.RecordCodecBuilder
import net.minecraft.advancements.critereon.ContextAwarePredicate
import net.minecraft.advancements.critereon.EntityPredicate
import net.minecraft.advancements.critereon.SimpleCriterionTrigger
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.stats.Stat
import java.util.Optional
import java.util.function.Supplier

class CustomStatCountCondition(
    private val playerPredicate: Optional<ContextAwarePredicate>,
    private val stat: Supplier<Stat<ResourceLocation>>,
    private val count: Int,
) : SimpleCriterionTrigger.SimpleInstance {

    companion object {
        fun codec(stat: Supplier<Stat<ResourceLocation>>): Codec<CustomStatCountCondition> = RecordCodecBuilder.create { instance ->
            instance.group(
                EntityPredicate.ADVANCEMENT_CODEC.optionalFieldOf("player").forGetter { it.playerPredicate },
                Codec.INT.fieldOf("count").forGetter { it.count },
            ).apply(instance) { player, count -> CustomStatCountCondition(player, stat, count) }
        }

        fun create(stat: Supplier<Stat<ResourceLocation>>, count: Int) = CustomStatCountCondition(Optional.empty(), stat, count)
    }

    fun test(player: ServerPlayer): Boolean = player.stats.getValue(stat.get()) >= count

    override fun player(): Optional<ContextAwarePredicate> = playerPredicate
}
