package site.siredvin.template

import dan200.computercraft.api.client.ComputerCraftAPIClient
import dan200.computercraft.api.client.turtle.TurtleUpgradeModeller
import net.minecraft.resources.ResourceLocation
import java.util.function.Consumer

object TemplateClientCore {
    private val EXTRA_MODELS = emptyArray<String>()

    fun registerExtraModels(register: Consumer<ResourceLocation>) {
        EXTRA_MODELS.forEach { register.accept(ResourceLocation(TemplateCore.MOD_ID, it)) }
    }

    fun onInit() {
    }
}
