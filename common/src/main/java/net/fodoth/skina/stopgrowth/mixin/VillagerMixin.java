package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.AgeGrowthControl;
import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseRemainder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Villager.class)
public abstract class VillagerMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void stopgrowth$mobInteractPatch(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Villager self = (Villager)(Object)this;
        if (!(self instanceof AgeGrowthControl control)) return;
        ItemStack stack = player.getItemInHand(hand);

        boolean isFood = false;
        boolean isStopFood = FoodUtil.isStopFood(self, stack);
        boolean isRestartFood = FoodUtil.isRestartFood(self, stack);

        if (self.isBaby() && self.isAlive() && player instanceof ServerPlayer) {
            if (isStopFood && !control.stopgrowth$isGrowthStopped()) {
                control.stopgrowth$setGrowthStopped(true);
                stopgrowth$consumeItemAndFeedback(player, hand, stack, cir, ParticleTypes.SMOKE, !isFood);
            } else if (isRestartFood && control.stopgrowth$isGrowthStopped()) {
                control.stopgrowth$setGrowthStopped(false);
                stopgrowth$consumeItemAndFeedback(player, hand, stack, cir, ParticleTypes.GLOW, !isFood);
            }
        }

        if (self.level().isClientSide && !isFood && (isStopFood || isRestartFood)) {
            cir.setReturnValue(InteractionResult.CONSUME);
        }
}

    @Unique
    private void stopgrowth$consumeItemAndFeedback(Player player, InteractionHand hand, ItemStack stack, CallbackInfoReturnable<InteractionResult> cir, ParticleOptions particle, boolean shouldConsume) {
        if (shouldConsume) {
            int i = stack.getCount();
            UseRemainder useRemainder = stack.get(DataComponents.USE_REMAINDER);
            stack.consume(1, player);
            if (useRemainder != null) {
                boolean var10003 = player.hasInfiniteMaterials();
                Objects.requireNonNull(player);
                ItemStack itemStack2 = useRemainder.convertIntoRemainder(stack, i, var10003, player::handleExtraItemsCreatedOnUse);
                player.setItemInHand(hand, itemStack2);
            }
        }
        stopgrowth$spawnParticles((Entity)(Object)this, particle);
        player.swing(hand, true);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Unique
    private void stopgrowth$spawnParticles(Entity entity, ParticleOptions particle) {
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;
        serverLevel.sendParticles(
                particle,
                entity.getX(),
                entity.getY() + 0.5 * entity.getBbHeight(),
                entity.getZ(),
                20, 0.1, 0.1, 0.1, 0.1
        );
    }

}

