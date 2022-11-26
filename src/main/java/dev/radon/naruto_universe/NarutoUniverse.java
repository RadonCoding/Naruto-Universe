package dev.radon.naruto_universe;

import dev.radon.naruto_universe.ability.AbilityRegistry;
import dev.radon.naruto_universe.block.BlockRegistry;
import dev.radon.naruto_universe.entity.EntityRegistry;
import dev.radon.naruto_universe.item.ItemRegistry;
import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.sound.SoundRegistry;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.eventbus.api.IEventBus;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.event.lifecycle.FMLCommonSetupEvent;
import net.minecraftforge.fml.javafmlmod.FMLJavaModLoadingContext;
import net.minecraftforge.network.NetworkRegistry;

// TODO: Finish great fireball jutsu and make the gui also make chakra control

@Mod(NarutoUniverse.MOD_ID)
public class NarutoUniverse
{
    public static final String MOD_ID = "naruto_universe";

    public NarutoUniverse()
    {
        IEventBus bus = FMLJavaModLoadingContext.get().getModEventBus();

        BlockRegistry.BLOCKS.register(bus);
        ItemRegistry.ITEMS.register(bus);
        EntityRegistry.ENTITIES.register(bus);
        SoundRegistry.SOUNDS.register(bus);

        AbilityRegistry.ABILITIES.register(bus);
        bus.addListener(this::onCommonSetup);

        MinecraftForge.EVENT_BUS.register(this);
    }

    private void onCommonSetup(final FMLCommonSetupEvent event) {
        PacketHandler.register();
        AbilityRegistry.registerCombos();
    }
}
