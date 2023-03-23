package radon.naruto_universe.command;

import net.minecraftforge.event.RegisterCommandsEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID)
public class CommandRegistry {
    @SubscribeEvent
    public static void onRegisterCommands(RegisterCommandsEvent event) {
        SetRankCommand.register(event.getDispatcher());
    }
}
