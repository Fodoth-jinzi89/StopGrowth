package net.fodoth.skina.stopgrowth.mixinutil;

import net.minecraft.core.component.DataComponents;
import net.minecraft.core.particles.ParticleOptions;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.core.registries.BuiltInRegistries;
import net.minecraft.core.registries.Registries;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.tags.TagKey;
import net.minecraft.world.InteractionHand;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.Entity;
import net.minecraft.world.entity.EntityType;
import net.minecraft.world.entity.animal.Animal;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.Item;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.component.UseRemainder;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

import java.util.Objects;

public class FoodUtil {
    public static boolean isStopFood(Entity entity, ItemStack stack){
        TagKey<Item> stopFoodTag = getTag(entity.getType(), "growth_stop_food");
        return stack.is(stopFoodTag);
    }
    public static boolean isRestartFood(Entity entity, ItemStack stack){
        TagKey<Item> restartFoodTag = getTag(entity.getType(), "growth_restart_food");
        return stack.is(restartFoodTag);
    }

    public static boolean processFeed(Object entity, Player player, InteractionHand hand, CallbackInfoReturnable<InteractionResult> cir, boolean isFood, boolean isBaby){
        if (!(entity instanceof AgeGrowthControl control)) return false;
        ItemStack stack = player.getItemInHand(hand);
        boolean isStopFood = FoodUtil.isStopFood((Entity) control, stack);
        boolean isRestartFood = FoodUtil.isRestartFood((Entity) control, stack);
        boolean shouldExtras = false;


        if (isBaby && player instanceof ServerPlayer) {
            if (isStopFood && !control.stopgrowth$isGrowthStopped()) {
                control.stopgrowth$setGrowthStopped(true);
                consumeItemAndFeedback(control, player, hand, stack, cir, ParticleTypes.SMOKE, !isFood);
                shouldExtras = true;
            } else if (isRestartFood && control.stopgrowth$isGrowthStopped()) {
                control.stopgrowth$setGrowthStopped(false);
                consumeItemAndFeedback(control, player, hand, stack, cir, ParticleTypes.GLOW, !isFood);
                shouldExtras = true;
            }

        }

        if (((Entity) control).level().isClientSide) {
            if (!isFood && (isStopFood || isRestartFood)) {
                cir.setReturnValue(InteractionResult.CONSUME);
            }
        }
        return shouldExtras;


    }

    private static void consumeItemAndFeedback(AgeGrowthControl control,Player player, InteractionHand hand, ItemStack stack, CallbackInfoReturnable<InteractionResult> cir, ParticleOptions particle, boolean consumeItem) {
        if (consumeItem) {
            int i = stack.getCount();
            UseRemainder useRemainder = stack.get(DataComponents.USE_REMAINDER);
            stack.consume(1, player);
            if (useRemainder != null) {
                boolean var10003 = player.hasInfiniteMaterials();
                Objects.requireNonNull(player);
                ItemStack itemStack2 = useRemainder.convertIntoRemainder(stack, i, var10003, player::handleExtraItemsCreatedOnUse);
                player.setItemInHand(hand, itemStack2);
            }
        }
        spawnParticles((Entity)control, particle);
        player.swing(hand, true);
        cir.setReturnValue(InteractionResult.SUCCESS);
    }

    private static void spawnParticles(Entity entity, ParticleOptions particle) {
        if (entity.level().isClientSide) return;
        if (!(entity.level() instanceof ServerLevel serverLevel)) return;

        serverLevel.sendParticles(
                particle,
                entity.getX(),
                entity.getY() + 0.5 * entity.getBbHeight(),
                entity.getZ(),
                20, 0.1, 0.1, 0.1, 0.1
        );
    }

    private static TagKey<Item> getTag(EntityType<?> type, String prefix) {
        ResourceLocation typeId = BuiltInRegistries.ENTITY_TYPE.getKey(type);
        ResourceLocation tagId = ResourceLocation.fromNamespaceAndPath("stopgrowth", prefix + "/" + typeId.getPath());
        return TagKey.create(Registries.ITEM, tagId);
    }
}
