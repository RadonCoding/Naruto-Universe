package radon.naruto_universe.command;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.FORGE)
public class NarutoCommands {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SetRankCommand.register(event.getDispatcher());
        SetMangekyoTypeCommand.register(event.getDispatcher());
        UnlockEverythingCommand.register(event.getDispatcher());
    }
}
