package radon.naruto_universe;

import net.minecraftforge.fml.ModLoadingContext;
import net.minecraftforge.fml.config.ModConfig;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.client.ModClientEventHandler;
import radon.naruto_universe.client.particle.NarutoParticles;
import radon.naruto_universe.config.ConfigHolder;
import radon.naruto_universe.entity.NarutoEntities;
import radon.naruto_universe.item.NarutoItems;
import radon.naruto_universe.sound.NarutoSounds;
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
        final ModLoadingContext ctx = ModLoadingContext.get();
        ctx.registerConfig(ModConfig.Type.CLIENT, ConfigHolder.CLIENT_SPEC);
        ctx.registerConfig(ModConfig.Type.COMMON, ConfigHolder.SERVER_SPEC);

        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        NarutoEntities.ENTITIES.register(bus);
        NarutoSounds.SOUNDS.register(bus);
        NarutoParticles.PARTICLES.register(bus);
        NarutoItems.ITEMS.register(bus);

        NarutoAbilities.ABILITIES.register(bus);
        bus.addListener(ModEventHandler::onCommonSetup);
        bus.addListener(ModClientEventHandler::onClientSetup);
    }
}
