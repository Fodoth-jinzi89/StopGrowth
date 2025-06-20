package net.fodoth.skina.stopgrowth.mixinutil;

import net.minecraft.world.item.ItemStack;

public interface HasStopFood {
    public boolean stopgrowth$isStopFood(ItemStack stack);
}
