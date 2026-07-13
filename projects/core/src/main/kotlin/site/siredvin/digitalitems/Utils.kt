package site.siredvin.digitalitems

import dan200.computercraft.api.lua.LuaException
import net.minecraft.advancements.Advancement
import net.minecraft.advancements.AdvancementHolder
import net.minecraft.resources.ResourceLocation
import net.minecraft.server.level.ServerPlayer
import net.minecraft.world.item.ItemStack
import net.minecraft.world.item.Items
import site.siredvin.broccolium.modules.platform.PlatformTags
import site.siredvin.digitalitems.common.data.ByteArrayWrapper
import site.siredvin.digitalitems.common.setup.ModCriterias
import site.siredvin.digitalitems.common.setup.ModStats
import java.nio.ByteBuffer
import java.util.function.Consumer

fun modId(something: String): ResourceLocation = ResourceLocation.fromNamespaceAndPath(DigitalItemsCore.MOD_ID, something)

fun Advancement.Builder.saveWithID(consumer: Consumer<AdvancementHolder>, id: ResourceLocation): AdvancementHolder {
    val advancement = this.build(id)
    consumer.accept(advancement)
    return advancement
}

fun assertBetween(arg: Long, min: Long, max: Long, name: String) {
    if (arg !in min..max) {
        throw LuaException("$name should be between $min and $max")
    }
}

fun ServerPlayer.awardDigitalization(stack: ItemStack) {
    this.awardStat(ModStats.DIGITALIZED_ITEMS.get(), stack.count)
    ModCriterias.DIGITALIZE_ITEMS.trigger(this)
    if (PlatformTags.get().isOre(stack)) {
        this.awardStat(ModStats.DIGITALIZED_ORES.get(), stack.count)
        ModCriterias.DIGITALIZE_ORES.trigger(this)
    }
    if (stack.`is`(Items.NETHER_STAR)) {
        this.awardStat(ModStats.DIGITALIZED_STARS.get(), stack.count)
        ModCriterias.DIGITALIZE_STARS.trigger(this)
    }
}

fun ByteBuffer.toSafeArray(): ByteArray {
    if (this.hasArray()) {
        return this.array()
    }
    val bytes = ByteArray(this.remaining())
    this[bytes]
    return bytes
}

fun ByteArray.wrap(): ByteArrayWrapper = ByteArrayWrapper(this)
