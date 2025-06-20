package net.fodoth.skina.stopgrowth.mixin;

import net.fodoth.skina.stopgrowth.init.ModTag;
import net.fodoth.skina.stopgrowth.mixinutil.HasStopFood;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.animal.HappyGhast;
import net.minecraft.world.item.ItemStack;
import org.spongepowered.asm.mixin.Mixin;

import java.util.List;
import java.util.stream.Collectors;

@Mixin(HappyGhast.class)
public class HappyGhastMixin implements HasStopFood {
    @Override
    public boolean stopgrowth$isStopFood(ItemStack stack){
        return stack.is(ModTag.GROWTH_STOP_FOOD_HAPPY_GHAST);
    }
}
