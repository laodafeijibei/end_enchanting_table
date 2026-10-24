package com.laodafeijibei.zhongyanfumotai.mixin;

import net.minecraft.ChatFormatting;
import net.minecraft.core.Holder;
import net.minecraft.network.chat.Component;
import net.minecraft.tags.EnchantmentTags;
import net.minecraft.world.item.enchantment.Enchantment;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.At;
import org.spongepowered.asm.mixin.injection.Inject;
import org.spongepowered.asm.mixin.injection.callback.CallbackInfoReturnable;

@Mixin(Enchantment.class)
public abstract class EnchantmentMixin {
    @Inject(method = "getFullname", at = @At("HEAD"), cancellable = true)
    private static void endEnchantingTable$renderNumericHighLevel(Holder<Enchantment> holder, int level,
                                                                    CallbackInfoReturnable<Component> callback) {
        if (level <= 10) {
            return;
        }
        ChatFormatting color = holder.is(EnchantmentTags.CURSE) ? ChatFormatting.RED : ChatFormatting.GRAY;
        callback.setReturnValue(holder.value().description().copy()
                .withStyle(color)
                .append(Component.literal(" " + level).withStyle(color)));
    }
}
