package site.siredvin.digitalitems.testmod;

import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.event.server.ServerStartingEvent;
import net.minecraftforge.fml.common.Mod;
import site.siredvin.digitalitems.DigitalItemsCore;
import site.siredvin.digitalitems.forge.ForgeModPlatform;
import site.siredvin.digitalitems.forge.ForgeModRecipeIngredients;
import site.siredvin.testiarium.ForgeTestiarium;
import site.siredvin.testiarium.Testiarium;
import site.siredvin.testiarium.cct.CctComputers;
import site.siredvin.testiarium.cct.CctFixtureCommands;
import site.siredvin.tweakium.ForgeTweakium;

@Mod("digitalitems_testmod")
public final class ForgeDigitalItemsTestMod {
    public ForgeDigitalItemsTestMod() {
        ForgeTweakium.INSTANCE.sayHi();
        DigitalItemsCore.INSTANCE.configure(ForgeModPlatform.INSTANCE, ForgeModRecipeIngredients.INSTANCE);
        DigitalItemsTestContent.INSTANCE.register();
        CctComputers.INSTANCE.initialize();
        MinecraftForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            CctComputers.INSTANCE.reset();
            CctFixtureCommands.INSTANCE.importFiles(event.getServer());
        });
        Testiarium.register(DigitalItemsGameTests.class);
        ForgeTestiarium.registerTests();
    }
}
