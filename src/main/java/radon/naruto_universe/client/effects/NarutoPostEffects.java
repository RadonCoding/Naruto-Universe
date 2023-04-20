package radon.naruto_universe.client.effects;

import radon.naruto_universe.client.NarutoPostEffect;

import java.util.ArrayList;
import java.util.List;

public class NarutoPostEffects {
    public static final List<NarutoPostEffect> EFFECTS = new ArrayList<>();

    static {
        EFFECTS.add(new BlindnessPostEffect());
        EFFECTS.add(new SharinganPostEffect());
    }
}
