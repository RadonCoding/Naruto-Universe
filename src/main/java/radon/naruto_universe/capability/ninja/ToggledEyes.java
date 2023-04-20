package radon.naruto_universe.capability.ninja;

import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.resources.ResourceLocation;
import radon.naruto_universe.ability.Ability;
import radon.naruto_universe.ability.NarutoAbilities;
import radon.naruto_universe.capability.ninja.MangekyoType;

public class ToggledEyes {
    public final ResourceLocation identifier;
    public final int sharinganLevel;
    public final MangekyoType mangekyoType;

    public ToggledEyes(ResourceLocation identifier, int sharinganLevel, MangekyoType mangekyoType) {
        this.identifier = identifier;
        this.sharinganLevel = sharinganLevel;
        this.mangekyoType = mangekyoType;
    }

    public ToggledEyes(FriendlyByteBuf buf) {
        this.identifier = buf.readResourceLocation();
        this.sharinganLevel = buf.readInt();
        this.mangekyoType = MangekyoType.values()[buf.readInt()];
    }

    public void serialize(FriendlyByteBuf buf) {
        buf.writeResourceLocation(this.identifier);
        buf.writeInt(this.sharinganLevel);
        buf.writeInt(this.mangekyoType.ordinal());
    }

    public boolean is(Ability obj) {
        return NarutoAbilities.getValue(this.identifier) == obj;
    }
}
