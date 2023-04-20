package radon.naruto_universe.capability.ninja;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.network.chat.Style;

public enum NinjaTrait {
    NONE(Component.empty()),
    FIRE_RELEASE(Component.translatable("trait.fire_release").withStyle(ChatFormatting.RED)),
    WATER_RELEASE(Component.translatable("trait.water_release").withStyle(ChatFormatting.BLUE)),
    EARTH_RELEASE(Component.translatable("trait.earth_release").withStyle(Style.EMPTY.withColor(9849600))),
    WIND_RELEASE(Component.translatable("trait.wind_release").withStyle(ChatFormatting.GREEN)),
    LIGHTNING_RELEASE(Component.translatable("trait.lightning_release").withStyle(ChatFormatting.YELLOW)),
    BYAKUGAN(Component.translatable("trait.byakugan").withStyle(ChatFormatting.WHITE)),
    SHARINGAN(Component.translatable("trait.sharingan").withStyle(ChatFormatting.DARK_RED)),
    MANGEKYO(Component.translatable("trait.mangekyo").withStyle(ChatFormatting.DARK_RED)),
    RINNEGAN(Component.translatable("trait.rinnegan").withStyle(ChatFormatting.DARK_PURPLE)),
    UNLOCKED_SHARINGAN(Component.translatable("trait.unlocked_sharingan").withStyle(ChatFormatting.DARK_RED)),
    UNLOCKED_MANGEKYO(Component.translatable("trait.unlocked_mangekyo").withStyle(ChatFormatting.DARK_RED)),
    UNLOCKED_RINNEGAN(Component.translatable("trait.unlocked_rinnegan").withStyle(ChatFormatting.DARK_PURPLE));

    private final Component identifier;

    NinjaTrait(Component identifier) {
        this.identifier = identifier;
    }

    public Component getIdentifier() {
        return this.identifier;
    }
}
