package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.tags.ItemTags;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.Dolphin;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Dolphin.class)
public abstract class DolphinMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void stopgrowth$onMobInteract(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Dolphin self = (Dolphin) (Object) this;
        ItemStack stack = player.getItemInHand(hand);
        boolean isFood = stack.is(ItemTags.FISHES);
        boolean isBaby = self.isBaby();
        FoodUtil.processFeed(self, player, hand, cir, isFood, isBaby);
    }
}
