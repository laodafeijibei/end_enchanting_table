package com.laodafeijibei.zhongyanfumotai.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.network.chat.Component;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
    private void zhongyanfumotai$renderNumericHighLevel(int level,
                                                       CallbackInfoReturnable<Component> callback) {
        if (level <= 10) {
            return;
        }
        Enchantment enchantment = (Enchantment) (Object) this;
        ChatFormatting color = enchantment.isCurse() ? ChatFormatting.RED : ChatFormatting.GRAY;
        callback.setReturnValue(Component.translatable(enchantment.getDescriptionId())
                .withStyle(color)
                .append(Component.literal(" " + level).withStyle(color)));
    }
}
