package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.AgeGrowthControl;
import net.fodoth.skina.stopgrowth.mixinutil.HasStopFood;
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

    /**
     * 动物交互逻辑注入：支持通过特定食物冻结/解冻成长状态。
     */
    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Animal self = (Animal) (Object) this;
        ItemStack stack = player.getItemInHand(hand);

        // 若不实现成长控制接口，跳过处理
        if (!(self instanceof AgeGrowthControl control)) return;

        boolean isStopFood = this instanceof HasStopFood stopSelf && stopSelf.stopgrowth$isStopFood(stack);
        boolean isRegularFood = isFood(stack);

        // 客户端：提前消费停止生长食物的交互，避免手感延迟
        if (self.level().isClientSide && isStopFood) {
            cir.setReturnValue(InteractionResult.CONSUME);
            return;
        }

        // 服务端：冻结逻辑——幼年体 + 未冻结 + 停止食物
        if (isStopFood && self.isBaby() && player instanceof ServerPlayer && !control.stopgrowth$isGrowthStopped()) {
            control.stopgrowth$setGrowthStopped(true);
            usePlayerItem(player, hand, stack);
            playEatingSound();
            player.swing(hand, true);
            stopgrowth$spawnParticles(self, ParticleTypes.SMOKE); // 冻结粒子效果
            cir.setReturnValue(InteractionResult.SUCCESS);
            return;
        }

        // 服务端：解冻逻辑——已冻结 + 普通食物
        if (isRegularFood && player instanceof ServerPlayer && control.stopgrowth$isGrowthStopped()) {
            control.stopgrowth$setGrowthStopped(false);
            stopgrowth$spawnParticles(self, ParticleTypes.GLOW); // 解冻粒子效果
            // 可添加音效/提示等反馈
        }
    }

    /**
     * 生成指定粒子效果（仅服务端）。
     */
    @Unique
    private void stopgrowth$spawnParticles(Animal entity, net.minecraft.core.particles.ParticleOptions particle) {
        if (!entity.level().isClientSide && entity.level() instanceof ServerLevel serverLevel) {
            serverLevel.sendParticles(
                    particle,
                    entity.getX(),
                    entity.getY() + 0.5 * entity.getBbHeight(),
                    entity.getZ(),
                    20,    // 数量
                    0.1,   // x 偏移
                    0.1,   // y 偏移
                    0.1,   // z 偏移
                    0.1    // 粒子速度
            );
        }
    }
}
