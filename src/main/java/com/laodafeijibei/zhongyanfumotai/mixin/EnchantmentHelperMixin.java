package com.laodafeijibei.zhongyanfumotai.mixin;

import net.minecraft.nbt.CompoundTag;
import net.minecraft.resources.ResourceLocation;
import net.minecraft.world.item.enchantment.EnchantmentHelper;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfo;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(EnchantmentHelper.class)
public abstract class EnchantmentHelperMixin {
    @Inject(method = "storeEnchantment(Lnet/minecraft/resources/ResourceLocation;I)Lnet/minecraft/nbt/CompoundTag;",
            at = @At("HEAD"), cancellable = true)
    private static void zhongyanfumotai$storeIntLevel(ResourceLocation id, int level,
                                                      CallbackInfoReturnable<CompoundTag> callback) {
        CompoundTag tag = new CompoundTag();
        tag.putString("id", id.toString());
        tag.putInt("lvl", Math.max(0, level));
        callback.setReturnValue(tag);
    }

    @Inject(method = "setEnchantmentLevel(Lnet/minecraft/nbt/CompoundTag;I)V",
            at = @At("HEAD"), cancellable = true)
    private static void zhongyanfumotai$setIntLevel(CompoundTag tag, int level, CallbackInfo callback) {
        tag.putInt("lvl", Math.max(0, level));
        callback.cancel();
    }

    @Inject(method = "getEnchantmentLevel(Lnet/minecraft/nbt/CompoundTag;)I",
            at = @At("HEAD"), cancellable = true)
    private static void zhongyanfumotai$getIntLevel(CompoundTag tag,
                                                    CallbackInfoReturnable<Integer> callback) {
        callback.setReturnValue(Math.max(0, tag.getInt("lvl")));
    }
}
