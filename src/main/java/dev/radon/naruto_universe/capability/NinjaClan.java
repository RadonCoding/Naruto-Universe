package dev.radon.naruto_universe.capability;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;

import java.util.Arrays;
import java.util.List;

public enum NinjaClan {
    UCHIHA(Component.literal("Uchiha").withStyle(ChatFormatting.RED), new NinjaTrait[] {
            NinjaTrait.FIRE_RELEASE
    }, new NinjaTrait[] {
            NinjaTrait.SHARINGAN
    });

    private final Component identifier;
    private final List<NinjaTrait> releaseTraits;
    private final List<NinjaTrait> bloodlineTraits;

    NinjaClan(Component identifier, NinjaTrait[] releaseTraits, NinjaTrait[] bloodlineTraits) {
        this.identifier = identifier;
        this.releaseTraits = Arrays.asList(releaseTraits);
        this.bloodlineTraits = Arrays.asList(bloodlineTraits);
    }

    public Component getIdentifier() {
        return this.identifier;
    }

    public List<NinjaTrait> getReleaseTraits() {
        return this.releaseTraits;
    }

    public List<NinjaTrait> getBloodlineTraits() {
        return this.bloodlineTraits;
    }
}
