package radon.naruto_universe;

import radon.naruto_universe.ability.AbilityRegistry;
import radon.naruto_universe.client.ModClientEventHandler;
import radon.naruto_universe.client.particle.ParticleRegistry;
import radon.naruto_universe.entity.EntityRegistry;
import radon.naruto_universe.item.ItemRegistry;
import radon.naruto_universe.sound.SoundRegistry;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;

// TODO: Come up with a system for special abilities that don't use hand signs
// Potential idea: Display a wheel type thing in top right where the player can choose a ability using shift + scroll or arrow keys.
// The wheel will display the next ability above and below as transparent so it's easier to see what's next. The abilities are triggered using the B key which will have the combo of -2 instead of -1 for X

@Mod(NarutoUniverse.MOD_ID)
public class NarutoUniverse {
    public static final String MOD_ID = "naruto_universe";

    public NarutoUniverse()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        EntityRegistry.ENTITIES.register(bus);
        SoundRegistry.SOUNDS.register(bus);
        ParticleRegistry.PARTICLES.register(bus);
        ItemRegistry.ITEMS.register(bus);

        AbilityRegistry.ABILITIES.register(bus);
        bus.addListener(ModEventHandler::onCommonSetup);
        bus.addListener(ModClientEventHandler::onClientSetup);
    }
}
