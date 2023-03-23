package radon.naruto_universe.client;

import com.mojang.blaze3d.platform.InputConstants;
import net.minecraftforge.client.event.RegisterKeyMappingsEvent;
import radon.naruto_universe.NarutoUniverse;
import net.minecraft.client.KeyMapping;

public class KeyRegistry {
    public static final String KEY_CATEGORY_NARUTO_UNIVERSE = String.format("key.category.%s", NarutoUniverse.MOD_ID);
    public static final KeyMapping KEY_HAND_SIGN_ONE = createKeyMapping("hand_sign_one",
            InputConstants.KEY_C);
    public static final KeyMapping KEY_HAND_SIGN_TWO = createKeyMapping("hand_sign_two",
            InputConstants.KEY_V);
    public static final KeyMapping KEY_HAND_SIGN_THREE = createKeyMapping("hand_sign_three",
            InputConstants.KEY_B);
    public static final KeyMapping KEY_CHAKRA_JUMP = createKeyMapping("chakra_jump",
            InputConstants.KEY_X);
    public static final KeyMapping OPEN_NINJA_SCREEN = createKeyMapping("open_ninja_screen",
            InputConstants.KEY_J);
    public static final KeyMapping SHOW_DOJUTSU_MENU = createKeyMapping("show_dojutsu_menu",
            InputConstants.KEY_Z);

    private static KeyMapping createKeyMapping(String name, int keyCode) {
        return new KeyMapping(String.format("key.%s.%s", NarutoUniverse.MOD_ID, name), keyCode, KeyRegistry.KEY_CATEGORY_NARUTO_UNIVERSE);
    }

    public static void register(final RegisterKeyMappingsEvent event) {
        event.register(OPEN_NINJA_SCREEN);
        event.register(SHOW_DOJUTSU_MENU);
    }
}
