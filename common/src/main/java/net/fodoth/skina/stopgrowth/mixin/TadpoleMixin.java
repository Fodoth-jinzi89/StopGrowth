package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.AgeGrowthControl;
import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.frog.Tadpole;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Tadpole.class)
public abstract class TadpoleMixin implements AgeGrowthControl {

    // ======== 状态字段与接口实现 ========

    @Unique
    private boolean stopgrowth$growthStopped = false;

    @Override
    public boolean stopgrowth$isGrowthStopped() {
        return stopgrowth$growthStopped;
    }

    @Override
    public void stopgrowth$setGrowthStopped(boolean stopped) {
        this.stopgrowth$growthStopped = stopped;
    }

    // ======== 阻止 age 自然增长 ========

    @Shadow
    protected abstract void setAge(int age);

    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/frog/Tadpole;setAge(I)V"
            )
    )
    private void stopgrowth$redirectSetAge(Tadpole instance, int age) {
        if (!stopgrowth$growthStopped) {
            this.setAge(age);
        }
    }

    // ======== 存档持久化支持 ========

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void stopgrowth$readState(ValueInput nbt, CallbackInfo ci) {
        this.stopgrowth$growthStopped = nbt.getBooleanOr("GrowthStopped", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void stopgrowth$writeState(ValueOutput nbt, CallbackInfo ci) {
        nbt.putBoolean("GrowthStopped", this.stopgrowth$growthStopped);
    }

    // ======== 互动逻辑扩展（阻止成长食物） ========

    @Shadow
    protected abstract void usePlayerItem(Player player, ItemStack stack);

    @Shadow
    protected abstract boolean isFood(ItemStack itemStack);

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void stopgrowth$onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Tadpole self = (Tadpole)(Object)this;
        if (!(self instanceof AgeGrowthControl control)) return;

        ItemStack stack = player.getItemInHand(hand);
        boolean isFood = this.isFood(stack);
        boolean isStopFood = FoodUtil.isStopFood(self, stack);
        boolean isRestartFood = FoodUtil.isRestartFood(self, stack);





        if (player instanceof ServerPlayer) {
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

    // ======== 辅助：食物消耗与特效 ========

    @Unique
    private void stopgrowth$consumeItemAndFeedback(Player player, InteractionHand hand, ItemStack stack, CallbackInfoReturnable<InteractionResult> cir, ParticleOptions particle, boolean shouldConsume) {
        if (shouldConsume) {
            usePlayerItem(player, stack);
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
