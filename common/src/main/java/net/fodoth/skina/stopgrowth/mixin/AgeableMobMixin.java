package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.AgeGrowthControl;
import net.minecraft.world.entity.AgeableMob;
import net.minecraft.world.level.storage.ValueInput;
import net.minecraft.world.level.storage.ValueOutput;

import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.Redirect;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;

@Mixin(AgeableMob.class)
public abstract class AgeableMobMixin implements AgeGrowthControl {

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

    @Redirect(
            method = "aiStep",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/AgeableMob;setAge(I)V"
            )
    )
    private void redirectSetAge(AgeableMob instance, int age) {
        if (!stopgrowth$growthStopped) {
            instance.setAge(age);
        }
    }

    @Inject(method = "readAdditionalSaveData", at = @At("TAIL"))
    private void readGrowthStopState(ValueInput nbt, CallbackInfo ci) {
        this.stopgrowth$growthStopped = nbt.getBooleanOr("GrowthStopped", false);
    }

    @Inject(method = "addAdditionalSaveData", at = @At("TAIL"))
    private void writeGrowthStopState(ValueOutput nbt, CallbackInfo ci) {
        nbt.putBoolean("GrowthStopped", this.stopgrowth$growthStopped);
    }
}
