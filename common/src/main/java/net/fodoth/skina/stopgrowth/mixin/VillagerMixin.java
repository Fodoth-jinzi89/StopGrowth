package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.npc.Villager;
import net.minecraft.world.entity.player.Player;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Villager.class)
public abstract class VillagerMixin {

    @Inject(method = "mobInteract", at = @At("HEAD"), cancellable = true)
    private void stopgrowth$mobInteractPatch(Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir) {
        Villager self = (Villager)(Object) this;
        boolean isFood = false;
        boolean isBaby = self.isBaby() && self.isAlive();
        FoodUtil.processFeed(self, player, hand, cir, isFood, isBaby);
    }
}
