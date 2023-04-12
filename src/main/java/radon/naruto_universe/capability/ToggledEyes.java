package radon.naruto_universe.capability;

import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.registries.RegistryObject;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;

public class ToggledEyes {
    private final ResourceLocation identifier;
    private final int sharinganLevel;
    private final MangekyoType mangekyoType;

    public ToggledEyes(ResourceLocation identifier, int sharinganLevel, MangekyoType mangekyoType) {
        this.identifier = identifier;
        this.sharinganLevel = sharinganLevel;
        this.mangekyoType = mangekyoType;
    }

    public ResourceLocation getIdentifier() {
        return this.identifier;
    }

    public int getSharinganLevel() {
        return this.sharinganLevel;
    }

    public MangekyoType getMangekyoType() {
        return this.mangekyoType;
    }

    public boolean is(Ability obj) {
        return NarutoAbilities.getValue(this.identifier) == obj;
    }
}
