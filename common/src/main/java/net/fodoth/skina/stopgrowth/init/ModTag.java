package net.fodoth.skina.stopgrowth.init;

import net.fodoth.skina.stopgrowth.Stopgrowth;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.core.registries.Registries;
import net.minecraft.world.item.Item;

public class ModTag {
    public static final TagKey<Item> GROWTH_STOP_FOOD = TagKey.create(Registries.ITEM, ResourceLocation.fromNamespaceAndPath(Stopgrowth.MOD_ID, "growth_stop_food"));

    public static final TagKey<Item> GROWTH_STOP_FOOD_HAPPY_GHAST = TagKey.create(
            Registries.ITEM,
            ResourceLocation.fromNamespaceAndPath(Stopgrowth.MOD_ID, "growth_stop_food/happy_ghast")
    );

}
