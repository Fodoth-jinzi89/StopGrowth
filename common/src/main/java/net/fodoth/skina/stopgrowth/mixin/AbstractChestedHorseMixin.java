package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.entity.animal.horse.AbstractChestedHorse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(AbstractChestedHorse.class)
public abstract class AbstractChestedHorseMixin extends AbstractHorseMixin {

    @Redirect(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/horse/AbstractChestedHorse;isTamed()Z"
            )
    )
    private boolean stopgrowth$redirectIsTamed(AbstractChestedHorse self, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);

        if (!self.isTamed()) {
            if (FoodUtil.isStopFood(self, stack) || FoodUtil.isRestartFood(self, stack)) {
                if (!self.isFood(stack)) {
                    this.handleEating(player, stack);
                }
                return true;
            }
        }

        return self.isTamed();
    }

    @Redirect(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/horse/AbstractChestedHorse;isFood(Lnet/minecraft/world/item/ItemStack;)Z"
            )
    )
    private boolean stopgrowth$redirectIsFood(AbstractChestedHorse self, ItemStack stack) {
        if (FoodUtil.isStopFood(self, stack) || FoodUtil.isRestartFood(self, stack)) {
            return false;
        }
        return self.isFood(stack);
    }
}
