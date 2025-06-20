package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.AgeGrowthControl;
import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class AnimalMixin {

    @Shadow
    protected abstract void usePlayerItem(Player player, InteractionHand hand, ItemStack stack);

    @Shadow
    protected abstract void playEatingSound();

    @Shadow
    public abstract boolean isFood(ItemStack itemStack);

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Animal self = (Animal) (Object) this;
        ItemStack stack = player.getItemInHand(hand);

        if (!(self instanceof AgeGrowthControl control)) return;


        boolean isFood = self.isFood(stack);
        boolean isStopFood = FoodUtil.isStopFood(self, stack);
        boolean isRestartFood = FoodUtil.isRestartFood(self, stack);

        if (self.isBaby() && player instanceof ServerPlayer) {
            if (isStopFood && !control.stopgrowth$isGrowthStopped()) {
                control.stopgrowth$setGrowthStopped(true);
                this.stopgrowth$consumeItemAndFeedback(player, hand, stack, cir, ParticleTypes.SMOKE, !isFood);
            } else if (isRestartFood && control.stopgrowth$isGrowthStopped()) {
                control.stopgrowth$setGrowthStopped(false);
                this.stopgrowth$consumeItemAndFeedback(player, hand, stack, cir, ParticleTypes.GLOW, !isFood);
            }
        }

        if (self.level().isClientSide) {
            if (!isFood && (isStopFood || isRestartFood)) {
                cir.setReturnValue(InteractionResult.CONSUME);
            }
        }
    }

    @Unique
    private void stopgrowth$consumeItemAndFeedback(Player player, InteractionHand hand, ItemStack stack, CallbackInfoReturnable<InteractionResult> cir, ParticleOptions particle, boolean consumeItem) {
        if (consumeItem) {
            usePlayerItem(player, hand, stack);
        }
        playEatingSound();
        stopgrowth$spawnParticles((Animal)(Object)this, particle);
        player.swing(hand, true);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    @Unique
    private void stopgrowth$spawnParticles(Animal entity, ParticleOptions particle) {
        if (entity.level().isClientSide) return;
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
