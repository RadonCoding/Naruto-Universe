package radon.naruto_universe.item;

import com.google.common.collect.ImmutableMultimap;
import com.google.common.collect.Multimap;
import radon.naruto_universe.entity.ThrownKunaiEntity;
import radon.naruto_universe.sound.SoundRegistry;
import net.minecraft.core.BlockPos;
import net.minecraft.sounds.SoundSource;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResultHolder;
import net.minecraft.world.entity.EquipmentSlot;
import net.minecraft.world.entity.LivingEntity;
import net.minecraft.world.entity.ai.attributes.Attribute;
import net.minecraft.world.entity.ai.attributes.AttributeModifier;
import net.minecraft.world.entity.ai.attributes.Attributes;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.entity.projectile.AbstractArrow;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.UseAnim;
import net.minecraft.world.item.Vanishable;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.state.BlockState;

public class KunaiItem extends Item implements Vanishable {
    public static final int THROW_THRESHOLD_TIME = 10;
    public static final float BASE_DAMAGE = 8.0F;
    public static final float SHOOT_POWER = 2.5F;
    private final Multimap<Attribute, AttributeModifier> defaultModifiers;

    public KunaiItem(Item.Properties pProperties) {
        super(pProperties);
        ImmutableMultimap.Builder<Attribute, AttributeModifier> builder = ImmutableMultimap.builder();
        builder.put(Attributes.ATTACK_DAMAGE, new AttributeModifier(BASE_ATTACK_DAMAGE_UUID, "Tool modifier", BASE_DAMAGE, AttributeModifier.Operation.ADDITION));
        builder.put(Attributes.ATTACK_SPEED, new AttributeModifier(BASE_ATTACK_SPEED_UUID, "Tool modifier", -2.9D, AttributeModifier.Operation.ADDITION));
        this.defaultModifiers = builder.build();
    }

    @Override
    public boolean canAttackBlock(BlockState pState, Level pLevel, BlockPos pPos, Player pPlayer) {
        return !pPlayer.isCreative();
    }

    @Override
    public UseAnim getUseAnimation(ItemStack pStack) {
        return UseAnim.SPEAR;
    }

    @Override
    public InteractionResultHolder<ItemStack> use(Level pLevel, Player pPlayer, InteractionHand pHand) {
        ItemStack pStack = pPlayer.getItemInHand(pHand);

            if (!pLevel.isClientSide) {
                pStack.hurtAndBreak(1, pPlayer, (entity) -> {
                    entity.broadcastBreakEvent(pPlayer.getUsedItemHand());
                });

                ThrownKunaiEntity thrownKunai = new ThrownKunaiEntity(pLevel, pPlayer, pStack);
                thrownKunai.shootFromRotation(pPlayer, pPlayer.getXRot(), pPlayer.getYRot(), 0.0F, SHOOT_POWER, 1.0F);

                if (pPlayer.getAbilities().instabuild) {
                    thrownKunai.pickup = AbstractArrow.Pickup.CREATIVE_ONLY;
                }

                pLevel.addFreshEntity(thrownKunai);
                pLevel.playSound(null, thrownKunai, SoundRegistry.KUNAI_THROW.get(), SoundSource.PLAYERS, 1.0F, 1.0F);

                if (!pPlayer.getAbilities().instabuild) {
                    pPlayer.getInventory().removeItem(pStack);
                }
            }
        return InteractionResultHolder.consume(pStack);
    }

    @Override
    public boolean hurtEnemy(ItemStack pStack, LivingEntity pTarget, LivingEntity pAttacker) {
        pStack.hurtAndBreak(1, pAttacker, (entity) -> {
            entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
        });
        return true;
    }

    @Override
    public boolean mineBlock(ItemStack pStack, Level pLevel, BlockState pState, BlockPos pPos, LivingEntity pEntityLiving) {
        if ((double) pState.getDestroySpeed(pLevel, pPos) != 0.0D) {
            pStack.hurtAndBreak(2, pEntityLiving, (entity) -> {
                entity.broadcastBreakEvent(EquipmentSlot.MAINHAND);
            });
        }
        return true;
    }

    @Override
    public Multimap<Attribute, AttributeModifier> getDefaultAttributeModifiers(EquipmentSlot pEquipmentSlot) {
        return pEquipmentSlot == EquipmentSlot.MAINHAND ? this.defaultModifiers : super.getDefaultAttributeModifiers(pEquipmentSlot);
    }

    @Override
    public int getEnchantmentValue() {
        return 1;
    }
}