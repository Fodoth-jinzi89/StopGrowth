package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.Shadow;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Animal.class)
public abstract class AnimalMixin {

    @Shadow
    protected abstract void usePlayerItem(Player player, InteractionHand hand, ItemStack stack);

    @Shadow
    protected abstract void playEatingSound();

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Animal self = (Animal) (Object) this;
        ItemStack stack = player.getItemInHand(hand);
        boolean isFood = self.isFood(stack);
        boolean isBaby = self.isBaby();
        boolean shouldExtras = FoodUtil.processFeed(self, player, hand, cir, isFood, isBaby);
        if (shouldExtras) {
            playEatingSound();
        }
    }
}
