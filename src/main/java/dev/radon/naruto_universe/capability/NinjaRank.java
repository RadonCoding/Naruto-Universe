package dev.radon.naruto_universe.capability;

import net.minecraft.network.chat.Component;

public enum NinjaRank {
    ACADEMY_STUDENT(Component.literal("Academy Student")),
    GENIN(Component.literal("Genin")),
    CHUNIN(Component.literal("Chunin")),
    JONIN(Component.literal("Jonin")),
    KAGE(Component.literal("Kage"));

    private final Component identifier;

    NinjaRank(Component identifier) {
        this.identifier = identifier;
    }

    public Component getIdentifier() {
        return this.identifier;
    }
}
