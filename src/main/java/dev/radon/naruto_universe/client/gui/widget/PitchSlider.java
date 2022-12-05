package dev.radon.naruto_universe.client.gui.widget;

import dev.radon.naruto_universe.network.PacketHandler;
import dev.radon.naruto_universe.network.packet.HandlePitchChangeC2SPacket;
import net.minecraft.client.gui.components.AbstractSliderButton;
import net.minecraft.network.chat.CommonComponents;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraftforge.client.gui.widget.ForgeSlider;
import org.lwjgl.glfw.GLFW;

import java.text.DecimalFormat;

public class PitchSlider extends ForgeSlider {
    public static final double MIN_VALUE = 0.8D;
    public static final double MAX_VALUE = 1.0D;
    public static final double STEP_SIZE = 0.01D;

    public PitchSlider(int x, int y, int width, int height, float value) {
        super(x, y, width, height, Component.empty(), Component.empty(), MIN_VALUE, MAX_VALUE, value, STEP_SIZE, 3, true);
    }

    @Override
    public Component getMessage() {
        Component component = Component.literal(this.getValueString());
        return Component.translatable("voice.pitch").append(": ").append(component);
    }

    @Override
    protected void applyValue() {
        PacketHandler.sendToServer(new HandlePitchChangeC2SPacket((float) this.getValue()));
    }
}
