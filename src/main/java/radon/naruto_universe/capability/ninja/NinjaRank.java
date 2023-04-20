package radon.naruto_universe.capability.ninja;

import net.minecraft.network.chat.Component;

public enum NinjaRank {
    UNRANKED(0.0F, Component.literal("Unranked")),
    ACADEMY_STUDENT(10.0F, Component.literal("Academy Student")),
    GENIN(100.0F, Component.literal("Genin")),
    CHUNIN(300.0F, Component.literal("Chunin")),
    JONIN(1000.0F, Component.literal("Jonin")),
    KAGE(3000.0F, Component.literal("Kage"));

    private final float experience;
    private final Component identifier;

    NinjaRank(float experience, Component identifier) {
        this.experience = experience;
        this.identifier = identifier;
    }

    public Component getIdentifier() {
        return this.identifier;
    }

    public float getExperience() {
        return this.experience;
    }

    public static NinjaRank getRank(float experience) {
        NinjaRank result = null;

        for (NinjaRank rank : NinjaRank.values()) {
            if (rank.getExperience() > experience) {
                break;
            }
            result = rank;
        }
        return result;
    }
}
