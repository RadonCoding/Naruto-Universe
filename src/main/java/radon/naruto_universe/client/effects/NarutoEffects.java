package radon.naruto_universe.client.effects;

import radon.naruto_universe.client.NarutoPostEffect;

import java.util.ArrayList;
import java.util.List;

public class NarutoEffects {
    public static final List<NarutoPostEffect> EFFECTS = new ArrayList<>();

    static {
        EFFECTS.add(new BlindnessEffect());
        EFFECTS.add(new SharinganEffect());
    }
}
