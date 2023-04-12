package radon.naruto_universe.config;

import net.minecraftforge.common.ForgeConfigSpec;
import org.apache.commons.lang3.tuple.Pair;

public class ConfigHolder {

    public static ForgeConfigSpec CLIENT_SPEC;
    public static ForgeConfigSpec SERVER_SPEC;
    public static ClientConfig CLIENT;
    public static ServerConfig SERVER;

    static {
        {
            Pair<ClientConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ClientConfig::new);
            CLIENT = specPair.getLeft();
            CLIENT_SPEC = specPair.getRight();
        }
        {
            Pair<ServerConfig, ForgeConfigSpec> specPair = new ForgeConfigSpec.Builder().configure(ServerConfig::new);
            SERVER = specPair.getLeft();
            SERVER_SPEC = specPair.getRight();
        }
    }
}
