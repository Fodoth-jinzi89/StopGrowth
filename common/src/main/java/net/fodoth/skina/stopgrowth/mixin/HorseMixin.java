package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Horse.class)
public abstract class HorseMixin {

    @Redirect(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/horse/Horse;isTamed()Z"
            )
    )
    private boolean stopgrowth$redirectIsTamed(Horse self, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!self.isTamed() && (FoodUtil.isStopFood(self, stack) || FoodUtil.isRestartFood(self, stack))) {
            return true;
        }
        return self.isTamed();
    }
}
