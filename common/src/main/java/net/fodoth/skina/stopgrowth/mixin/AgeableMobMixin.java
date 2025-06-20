package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.AgeGrowthControl;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin implements AgeGrowthControl {

    @Shadow
    public abstract int getAge();

    @Shadow
    public abstract void setAge(int age);

    @Unique
    private boolean stopgrowth$growthStopped = false;

    // === AgeGrowthControl 接口实现 ===

    @Override
    public boolean stopgrowth$isGrowthStopped() {
        return stopgrowth$growthStopped;
    }

    @Override
    public void stopgrowth$setGrowthStopped(boolean stopped) {
        this.stopgrowth$growthStopped = stopped;
    }

    // === 阻止 age 每 tick 增减 ===

    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/AgeableMob;setAge(I)V"
            )
    )
    private void redirectSetAge(AgeableMob instance, int age) {
        if (!stopgrowth$growthStopped) {
            // 若未停止生长，则照常调用 setAge 以调整 age
            instance.setAge(age);
        }
    }

    // === 数据持久化支持 ===

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readGrowthStopState(ValueInput nbt, CallbackInfo ci) {
        this.stopgrowth$growthStopped = nbt.getBooleanOr("GrowthStopped", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeGrowthStopState(ValueOutput nbt, CallbackInfo ci) {
        nbt.putBoolean("GrowthStopped", this.stopgrowth$growthStopped);
    }
}