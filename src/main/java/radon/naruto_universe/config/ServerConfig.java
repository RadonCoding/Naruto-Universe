package radon.naruto_universe.config;

import net.minecraftforge.common.ForgeConfigSpec;

public class ServerConfig {
    public final ForgeConfigSpec.ConfigValue<Integer> powerResetTime;
    public final ForgeConfigSpec.ConfigValue<Float> chakraRegenAmount;
    public final ForgeConfigSpec.ConfigValue<Float> powerChargeAmount;
    public final ForgeConfigSpec.ConfigValue<Float> chakraAmount;
    public final ForgeConfigSpec.ConfigValue<Float> chakraMultiplier;
    public final ForgeConfigSpec.ConfigValue<Float> minimumPower;
    public final ForgeConfigSpec.ConfigValue<Float> maximumPower;
    public final ForgeConfigSpec.ConfigValue<Float> ninjaSpeed;

    public ServerConfig(final ForgeConfigSpec.Builder builder) {
        builder.push("General");

        this.powerResetTime = builder.comment("Number of ticks the power the player has charged stays before it goes back to zero.")
                .translation("Power Reset Time")
                .define("Power Reset Time", 15);

        this.chakraRegenAmount = builder.comment("Amount of chakra the player regenerates every tick (multiplied by rank).")
                .translation("Chakra Regeneration Amount")
                .define("Chakra Regeneration Amount", 0.05F);

        this.powerChargeAmount = builder.comment("Amount of power the player generates every tick charging (multiplied by rank).")
                .translation("Power Charge Amount")
                .define("Power Charge Amount", 0.01F);

        this.chakraAmount = builder.comment("The amount of chakra a ninja has (multiplied by rank).")
                .translation("Chakra Amount")
                .define("Chakra Amount", 100.0F);

        this.chakraMultiplier = builder.comment("The value which multiplies ninja experience to calculate max chakra.")
                .translation("Chakra Multiplier")
                .define("Chakra Multiplier", 1.5F);

        this.minimumPower = builder.comment("Minimum amount of power required for a ability.")
                .translation("Minimum Power")
                .define("Minimum Power", 10.0F);

        this.maximumPower = builder.comment("Maximum amount of power.")
                .translation("Maximum Power")
                .define("Maximum Power", 30.0F);

        this.ninjaSpeed = builder.comment("The amount of speed a ninja initially gets (multiplied by rank).")
                .translation("Ninja Speed")
                .define("Ninja Speed", 0.25F);

        builder.pop();
    }
}
