package site.siredvin.digitalitems

import net.minecraft.resources.ResourceLocation
import java.util.function.Consumer

object DigitalItemsClientCore {
    private val EXTRA_MODELS = emptyArray<String>()

    fun registerExtraModels(register: Consumer<ResourceLocation>) {
        EXTRA_MODELS.forEach { register.accept(ResourceLocation(DigitalItemsCore.MOD_ID, it)) }
    }

    fun onInit() {
    }
}
