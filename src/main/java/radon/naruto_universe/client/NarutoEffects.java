package radon.naruto_universe.client;

import com.mojang.blaze3d.vertex.DefaultVertexFormat;
import net.minecraft.client.renderer.ShaderInstance;
import net.minecraft.resources.ResourceLocation;
import net.minecraftforge.api.distmarker.Dist;
import net.minecraftforge.client.event.RegisterShadersEvent;
import net.minecraftforge.eventbus.api.SubscribeEvent;
import net.minecraftforge.fml.common.Mod;
import radon.naruto_universe.NarutoUniverse;

import java.io.IOException;

@Mod.EventBusSubscriber(modid = NarutoUniverse.MOD_ID, bus = Mod.EventBusSubscriber.Bus.MOD, value = Dist.CLIENT)
public class NarutoEffects {
    private static ShaderInstance translucentParticleShader;

    @SubscribeEvent
    public static void onRegisterShaders(RegisterShadersEvent event) throws IOException {
        event.registerShader(new ShaderInstance(event.getResourceProvider(),
                new ResourceLocation(NarutoUniverse.MOD_ID, "translucent_particle"), DefaultVertexFormat.PARTICLE),
                shader -> translucentParticleShader = shader);
    }

    public static ShaderInstance getTranslucentParticleShader() {
        return translucentParticleShader;
    }
}
