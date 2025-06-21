package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.AgeGrowthControl;
import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
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

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void stopgrowth$readState(ValueInput nbt, CallbackInfo ci) {
        this.stopgrowth$growthStopped = nbt.getBooleanOr("GrowthStopped", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void stopgrowth$writeState(ValueOutput nbt, CallbackInfo ci) {
        nbt.putBoolean("GrowthStopped", this.stopgrowth$growthStopped);
    }

    @Shadow
    protected abstract void usePlayerItem(Player player, ItemStack stack);

    @Shadow
    protected abstract boolean isFood(ItemStack itemStack);

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void stopgrowth$onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        ItemStack stack = player.getItemInHand(hand);
        boolean isFood = this.isFood(stack);
        FoodUtil.processFeed((Tadpole)(Object)this, player, hand, cir, isFood, true);
    }
}
