package radon.naruto_universe.client.render;

import net.minecraft.client.renderer.entity.EntityRenderer;
import net.minecraft.client.renderer.entity.EntityRendererProvider;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.entity.Entity;
import radon.naruto_universe.entity.JutsuProjectile;

public class EmptyRenderer<T extends Entity> extends EntityRenderer<T> {
    public EmptyRenderer(EntityRendererProvider.Context pContext) {
        super(pContext);
    }

    @Override
    public ResourceLocation getTextureLocation(T pEntity) {
        return null;
    }
}
