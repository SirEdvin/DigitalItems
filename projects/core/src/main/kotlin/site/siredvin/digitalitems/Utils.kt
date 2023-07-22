package site.siredvin.digitalitems

import net.minecraft.advancements.Advancement
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.digitalitems.common.setup.ModCriterias
import site.siredvin.digitalitems.common.setup.ModStats
import site.siredvin.peripheralium.xplat.XplatTags
import java.util.function.Consumer

fun modId(something: String): ResourceLocation = ResourceLocation(DigitalItemsCore.MOD_ID, something)

fun Advancement.Builder.saveWithID(consumer: Consumer<Advancement>, id: ResourceLocation): Advancement {
    val advancement = this.build(id)
    consumer.accept(advancement)
    return advancement
}

fun ServerPlayer.awardDigitalization(stack: ItemStack) {
    this.awardStat(ModStats.DIGITALIZED_ITEMS.get(), stack.count)
    ModCriterias.DIGITALIZE_ITEMS.trigger(this)
    if (XplatTags.isOre(stack)) {
        this.awardStat(ModStats.DIGITALIZED_ORES.get(), stack.count)
        ModCriterias.DIGITALIZE_ORES.trigger(this)
    }
    if (stack.`is`(Items.NETHER_STAR)) {
        this.awardStat(ModStats.DIGITALIZED_STARS.get(), stack.count)
        ModCriterias.DIGITALIZE_STARS.trigger(this)
    }
}
