package dev.radon.naruto_universe;

import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.client.ClientEvents;
import dev.radon.naruto_universe.client.particle.ParticleRegistry;
import dev.radon.naruto_universe.entity.EntityRegistry;
import dev.radon.naruto_universe.item.ItemRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.sound.SoundRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import software.bernie.example.GeckoLibMod;
import software.bernie.geckolib3.GeckoLib;

// TODO: Make sound effects for all shit

@Mod("naruto_universe")
public class NarutoUniverse {
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
        bus.addListener(Events::onCommonSetup);
        bus.addListener(ClientEvents::onClientSetup);

        MinecraftForge.EVENT_BUS.register(Events.class);
        MinecraftForge.EVENT_BUS.register(ClientEvents.class);
    }
}
