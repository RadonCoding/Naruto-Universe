package radon.naruto_universe.capability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

public enum NinjaTrait {
    NONE(Component.empty()),
    FIRE_RELEASE(Component.translatable("trait.fire_release").withStyle(ChatFormatting.RED)),
    SHARINGAN(Component.translatable("trait.sharingan").withStyle(ChatFormatting.DARK_RED)),
    UNLOCKED_SHARINGAN(Component.translatable("trait.unlocked_shraringan").withStyle(ChatFormatting.DARK_RED)),
    UNLOCKED_RINNEGAN(Component.translatable("trait.unlocked_rinnegan").withStyle(ChatFormatting.DARK_PURPLE));

    private final Component identifier;

    NinjaTrait(Component identifier) {
        this.identifier = identifier;
    }

    public Component getIdentifier() {
        return this.identifier;
    }
}
