package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.sounds.SoundEvent;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.animal.camel.Camel;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseRemainder;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.Unique;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

@Mixin(Camel.class)
public abstract class CamelMixin {

    @Shadow
    public abstract boolean isFood(ItemStack itemStack);

    @Shadow
    protected abstract SoundEvent getEatingSound();

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void stopgrowth$onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Camel self = (Camel) (Object) this;
        ItemStack stack = player.getItemInHand(hand);
        boolean isFood = self.isFood(stack);
        boolean isBaby = self.isBaby();
        boolean shouldExtras = FoodUtil.processFeed(self, player, hand, cir, isFood, isBaby);
        if (shouldExtras) {
            SoundEvent soundEvent = this.getEatingSound();
            if (soundEvent != null) {
                self.level().playSound(null, self.getX(), self.getY(), self.getZ(), soundEvent, self.getSoundSource(), 1.0F, 1.0F + (self.getRandom().nextFloat() - self.getRandom().nextFloat()) * 0.2F);
            }
        }
    }

}
