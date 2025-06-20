package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.mixinutil.FoodUtil;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.animal.horse.Horse;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Redirect;

@Mixin(Horse.class)
public abstract class HorseMixin {

    /**
     * 拦截 if (!this.isTamed()) 判断，用以阻止 makeMad 和 return SUCCESS 的分支执行。
     */
    @Redirect(
            method = "mobInteract",
            at = @At(
                    value = "INVOKE",
                    target = "Lnet/minecraft/world/entity/animal/horse/Horse;isTamed()Z"
            )
    )
    private boolean stopgrowth$redirectIsTamed(Horse self, Player player, InteractionHand hand) {
        ItemStack stack = player.getItemInHand(hand);
        if (!self.isTamed()) {
            if (FoodUtil.isStopFood(self, stack) || FoodUtil.isRestartFood(self, stack)) {
                // 伪装成“已驯服”，让 if (!isTamed()) 失败，从而跳过 makeMad 和 return
                return true;
            }
        }

        // 原逻辑
        return self.isTamed();
    }
}
