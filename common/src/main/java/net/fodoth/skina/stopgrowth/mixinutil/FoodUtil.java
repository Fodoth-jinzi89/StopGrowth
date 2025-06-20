package net.fodoth.skina.stopgrowth.mixinutil;

import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.tags.TagKey;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;

public class FoodUtil {
    public static boolean isStopFood(Entity entity, ItemStack stack){
        TagKey<Item> stopFoodTag = getTag(entity.getType(), "growth_stop_food");
        return stack.is(stopFoodTag);
    }
    public static boolean isRestartFood(Entity entity, ItemStack stack){
        TagKey<Item> restartFoodTag = getTag(entity.getType(), "growth_restart_food");
        return stack.is(restartFoodTag);
    }

    private static TagKey<Item> getTag(EntityType<?> type, String prefix) {
        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        ResourceLocation tagId = ResourceLocation.fromNamespaceAndPath("stopgrowth", prefix + "/" + typeId.getPath());
        return TagKey.create(Registries.ITEM, tagId);
    }
}
