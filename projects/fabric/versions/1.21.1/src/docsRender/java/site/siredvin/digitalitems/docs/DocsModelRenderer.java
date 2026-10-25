package site.siredvin.digitalitems.docs;

import com.mojang.blaze3d.platform.NativeImage;
import net.fabricmc.api.ClientModInitializer;
import net.fabricmc.fabric.api.client.event.lifecycle.v1.ClientTickEvents;
import net.minecraft.client.Minecraft;
import net.minecraft.client.Screenshot;
import net.minecraft.client.gui.GuiGraphics;
import net.minecraft.client.gui.screens.Screen;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.network.chat.Component;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.ItemStack;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public final class DocsModelRenderer implements ClientModInitializer {
    private static final int SIZE = 128;
    private static final int BACKGROUND = 0xfffeffff;
    private static final int BACKGROUND_ABGR = 0xfffffffe;
    private boolean started;

    @Override
    public void onInitializeClient() {
        // A fresh client may show accessibility onboarding instead of the title screen.
        // Either is safe after the resource-loading overlay has closed and models are baked.
        ClientTickEvents.END_CLIENT_TICK.register(client -> {
            if (!started && client.screen != null && client.getOverlay() == null) {
                started = true;
                client.setScreen(new ExportScreen());
            }
        });
    }

    private static final class ExportScreen extends Screen {
        private final List<String> items = List.of("digitizer", "advanced_digitizer");
        private int next;

        private ExportScreen() {
            super(Component.literal("Exporting documentation models"));
        }

        @Override
        public void render(GuiGraphics graphics, int mouseX, int mouseY, float partialTick) {
            Minecraft client = Minecraft.getInstance();
            if (next == items.size()) {
                client.stop();
                return;
            }
            String name = items.get(next++);
            ResourceLocation id = ResourceLocation.fromNamespaceAndPath("digitalitems", name);
            if (!BuiltInRegistries.ITEM.containsKey(id)) {
                throw new IllegalStateException("Missing documentation item: " + id);
            }
            ItemStack stack = new ItemStack(BuiltInRegistries.ITEM.get(id));
            if (client.getItemRenderer().getModel(stack, null, null, 0) == client.getModelManager().getMissingModel()) {
                throw new IllegalStateException("Missing documentation model: " + id);
            }
            float guiSize = (float) (SIZE / client.getWindow().getGuiScale());
            graphics.fill(0, 0, (int) Math.ceil(guiSize), (int) Math.ceil(guiSize), BACKGROUND);
            graphics.flush();
            graphics.pose().pushPose();
            graphics.pose().scale(guiSize / 16, guiSize / 16, 1);
            graphics.renderItem(stack, 0, 0);
            graphics.pose().popPose();
            graphics.flush();

            // Match Icon Exporter's chroma-key capture while using Minecraft's own item render path.
            try (NativeImage screenshot = Screenshot.takeScreenshot(client.getMainRenderTarget());
                 NativeImage image = new NativeImage(SIZE, SIZE, false)) {
                int visible = 0;
                for (int y = 0; y < SIZE; y++) {
                    for (int x = 0; x < SIZE; x++) {
                        int pixel = screenshot.getPixelRGBA(x, y);
                        if (pixel == BACKGROUND_ABGR) pixel = 0;
                        else visible++;
                        image.setPixelRGBA(x, y, pixel);
                    }
                }
                if (visible == 0 || visible == SIZE * SIZE) {
                    throw new IllegalStateException("Empty or opaque documentation render: " + id);
                }
                Path output = Path.of(System.getProperty("digitalitems.docsOutput"));
                Files.createDirectories(output);
                image.writeToFile(output.resolve(name + ".png"));
                System.out.println("Exported native documentation model: " + id);
            } catch (IOException error) {
                throw new IllegalStateException("Cannot export documentation model: " + id, error);
            }
        }
    }
}
