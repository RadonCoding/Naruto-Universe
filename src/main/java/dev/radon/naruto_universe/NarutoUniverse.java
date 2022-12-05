package dev.radon.naruto_universe;

import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.client.particle.ParticleRegistry;
import dev.radon.naruto_universe.client.render.GreatFireballRenderer;
import dev.radon.naruto_universe.entity.EntityRegistry;
import dev.radon.naruto_universe.item.ItemRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.client.renderer.entity.EntityRenderers;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLClientSetupEvent;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;
import software.bernie.geckolib3.util.GeckoLibUtil;

// TODO: Finish great fireball jutsu and make the gui also make chakra control

@Mod(NarutoUniverse.MOD_ID)
public class NarutoUniverse
{
    public static final String MOD_ID = "naruto_universe";

    public NarutoUniverse()
    {
        GeckoLib.initialize();

        System.setProperty(GeckoLibMod.DISABLE_EXAMPLES_PROPERTY_KEY, "true");

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        EntityRegistry.ENTITIES.register(bus);
        SoundRegistry.SOUNDS.register(bus);
        ParticleRegistry.PARTICLES.register(bus);
        ItemRegistry.ITEMS.register(bus);

        AbilityRegistry.ABILITIES.register(bus);
        bus.addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
        AbilityRegistry.registerCombos();
    }
}
