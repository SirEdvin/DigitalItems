package site.siredvin.digitalitems.testmod;

import net.neoforged.fml.common.Mod;
import net.neoforged.neoforge.common.NeoForge;
import net.neoforged.neoforge.event.server.ServerStartingEvent;
import site.siredvin.digitalitems.DigitalItemsCore;
import site.siredvin.digitalitems.forge.ForgeModPlatform;
import site.siredvin.digitalitems.forge.ForgeModRecipeIngredients;
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
        NeoForge.EVENT_BUS.addListener((ServerStartingEvent event) -> {
            CctComputers.INSTANCE.reset();
            CctFixtureCommands.INSTANCE.importFiles(event.getServer());
        });
        Testiarium.register(DigitalItemsGameTests.class);
    }
}
