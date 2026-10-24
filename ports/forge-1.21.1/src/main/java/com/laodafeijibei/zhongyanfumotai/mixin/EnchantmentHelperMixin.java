package com.laodafeijibei.zhongyanfumotai.mixin;

import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;
import org.spongepowered.asm.mixin.injection.Redirect;

import com.mojang.serialization.Codec;

@Mixin(ItemEnchantments.class)
public abstract class EnchantmentHelperMixin {
    @Redirect(method = "<clinit>", at = @At(value = "INVOKE",
            target = "Lcom/mojang/serialization/Codec;intRange(II)Lcom/mojang/serialization/Codec;"))
    private static Codec<Integer> endEnchantingTable$unboundedLevelCodec(int min, int max) {
        return Codec.intRange(min, Integer.MAX_VALUE);
    }

    @ModifyConstant(method = "<init>", constant = @Constant(intValue = 255))
    private static int endEnchantingTable$unboundedStoredLevel(int ignored) {
        return Integer.MAX_VALUE;
    }
}
