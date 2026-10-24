package com.laodafeijibei.zhongyanfumotai.mixin;

import net.minecraft.util.RandomSource;
import net.minecraft.world.item.ArmorItem;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.item.enchantment.DigDurabilityEnchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(DigDurabilityEnchantment.class)
public abstract class DigDurabilityEnchantmentMixin {
    @Inject(method = "shouldIgnoreDurabilityDrop", at = @At("HEAD"), cancellable = true)
    private static void zhongyanfumotai$avoidMaxLevelOverflow(ItemStack stack, int level,
                                                              RandomSource random,
                                                              CallbackInfoReturnable<Boolean> callback) {
        if (level != Integer.MAX_VALUE) {
            return;
        }
        if (stack.getItem() instanceof ArmorItem && random.nextFloat() < 0.6F) {
            callback.setReturnValue(false);
            return;
        }
        double durabilityLossChance = 1.0D / ((double) level + 1.0D);
        callback.setReturnValue(random.nextDouble() >= durabilityLossChance);
    }
}
