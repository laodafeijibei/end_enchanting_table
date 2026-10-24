package com.laodafeijibei.zhongyanfumotai.mixin;

import net.minecraft.world.item.enchantment.ItemEnchantments;
import org.spongepowered.asm.mixin.Mixin;
import org.spongepowered.asm.mixin.injection.Constant;
import org.spongepowered.asm.mixin.injection.ModifyConstant;

@Mixin(ItemEnchantments.Mutable.class)
public abstract class ItemEnchantmentsMutableMixin {
    @ModifyConstant(method = {"set", "upgrade"}, constant = @Constant(intValue = 255))
    private static int endEnchantingTable$unboundedMutableLevel(int ignored) {
        return Integer.MAX_VALUE;
    }
}
