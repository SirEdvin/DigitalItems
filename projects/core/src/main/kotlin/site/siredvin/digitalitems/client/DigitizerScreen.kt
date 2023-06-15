package site.siredvin.digitalitems.client

import com.mojang.blaze3d.systems.RenderSystem
import net.minecraft.client.gui.GuiGraphics
import net.minecraft.client.gui.screens.inventory.AbstractContainerScreen
import net.minecraft.client.renderer.GameRenderer
import net.minecraft.client.renderer.Rect2i
import net.minecraft.network.chat.Component
import net.minecraft.resources.ResourceLocation
import net.minecraft.world.entity.player.Inventory
import net.minecraft.world.inventory.ContainerData
import site.siredvin.digitalitems.DigitalItemsCore
import java.util.*

class DigitizerScreen(menu: DigitizerMenu, inv: Inventory, component: Component) :
    AbstractContainerScreen<DigitizerMenu>(menu, inv, component) {
    private val data: ContainerData

    init {
        data = menu.data
    }

    val currentEnergy: Int
        get() = (data[0] shl 16) + (data[1] shl 12) + (data[2] shl 8) + data[3]
    val maxEnergy: Int
        get() = (data[4] shl 16) + (data[5] shl 12) + (data[6] shl 8) + data[7]

    private fun getEnergyArea(x: Int, y: Int): Rect2i {
        return Rect2i(x + 152, y + 9, 16, 69)
    }

    override fun renderBg(graphics: GuiGraphics, partialTick: Float, mouseX: Int, mouseY: Int) {
        RenderSystem.setShader { GameRenderer.getPositionShader() }
        RenderSystem.setShaderColor(1.0f, 1.0f, 1.0f, 1.0f)
        RenderSystem.setShaderTexture(0, TEXTURE)
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        graphics.blit(TEXTURE, x, y, 0, 0, imageWidth, imageHeight)
        val energyArea = getEnergyArea(x, y)
        val entireHeight = energyArea.height.toFloat()
        val filled = currentEnergy.toFloat() / maxEnergy.toFloat()
        val missingHeight = (entireHeight * (1.0f - filled)).toInt()
        graphics.fill(
            energyArea.x,
            energyArea.y,
            energyArea.x + energyArea.width,
            energyArea.y + missingHeight,
            -0x10000,
        )
        graphics.fill(
            energyArea.x,
            energyArea.y + missingHeight,
            energyArea.x + energyArea.width,
            energyArea.y + energyArea.height,
            -0xff0100,
        )
        renderTooltip(graphics, mouseX, mouseY)
    }

    override fun renderLabels(graphics: GuiGraphics, mouseX: Int, mouseY: Int) {
        val x = (width - imageWidth) / 2
        val y = (height - imageHeight) / 2
        val energyArea = getEnergyArea(x, y)
        if (energyArea.contains(mouseX, mouseY)) {
            graphics.renderTooltip(
                font,
                Component.literal("$currentEnergy/$maxEnergy"),
                mouseX - x,
                mouseY - y,
            )
        }
    }

    override fun render(graphics: GuiGraphics, mouseX: Int, mouseY: Int, delta: Float) {
        renderBackground(graphics)
        super.render(graphics, mouseX, mouseY, delta)
        renderTooltip(graphics, mouseX, mouseY)
    }

    companion object {
        private val TEXTURE = ResourceLocation(DigitalItemsCore.MOD_ID, "textures/gui/digitizer_container.png")
    }
}
