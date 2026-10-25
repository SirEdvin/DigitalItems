package site.siredvin.digitalitems.client

import net.minecraft.client.gui.GuiGraphicsExtractor
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.RenderPipelines
import net.minecraft.network.chat.Component
import net.minecraft.resources.Identifier
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.AbstractContainerMenu
import site.siredvin.digitalitems.DigitalItemsCore

class DigitizerScreen<T : AbstractContainerMenu>(menu: BasicMenu<T>, inv: Inventory, component: Component) : AbstractContainerScreen<BasicMenu<T>>(menu, inv, component) {
    val currentEnergy: Int get() = (menu.data[0] shl 16) + (menu.data[1] shl 12) + (menu.data[2] shl 8) + menu.data[3]
    val maxEnergy: Int get() = (menu.data[4] shl 16) + (menu.data[5] shl 12) + (menu.data[6] shl 8) + menu.data[7]
    val texture: Identifier = Identifier.fromNamespaceAndPath(DigitalItemsCore.MOD_ID, "textures/gui/${if (menu is DigitizerMenu) "digitizer" else "advanced_digitizer"}_container.png")

    override fun extractBackground(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int, delta: Float) {
        super.extractBackground(graphics, mouseX, mouseY, delta)
        graphics.blit(RenderPipelines.GUI_TEXTURED, texture, leftPos, topPos, 0f, 0f, imageWidth, imageHeight, 256, 256)
        val missing = (69 * (1f - currentEnergy.toFloat() / maxEnergy.coerceAtLeast(1))).toInt().coerceIn(0, 69)
        graphics.fill(leftPos + 152, topPos + 9, leftPos + 168, topPos + 9 + missing, -0x10000)
        graphics.fill(leftPos + 152, topPos + 9 + missing, leftPos + 168, topPos + 78, -0xff0100)
    }

    override fun extractLabels(graphics: GuiGraphicsExtractor, mouseX: Int, mouseY: Int) {
        if (mouseX in leftPos + 152 until leftPos + 168 && mouseY in topPos + 9 until topPos + 78) {
            graphics.setTooltipForNextFrame(font, Component.literal("$currentEnergy/$maxEnergy"), mouseX - leftPos, mouseY - topPos)
        }
    }
}
