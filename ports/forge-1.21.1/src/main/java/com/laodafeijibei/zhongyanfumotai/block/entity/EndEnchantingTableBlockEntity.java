package com.laodafeijibei.zhongyanfumotai.block.entity;

import com.laodafeijibei.zhongyanfumotai.menu.EndEnchantingMenu;
import com.laodafeijibei.zhongyanfumotai.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.network.chat.Component;
import net.minecraft.util.Mth;
import net.minecraft.util.RandomSource;
import net.minecraft.world.MenuProvider;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraftforge.common.capabilities.Capability;
import net.minecraftforge.common.capabilities.ForgeCapabilities;
import net.minecraftforge.common.util.LazyOptional;
import net.minecraftforge.items.ItemStackHandler;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public final class EndEnchantingTableBlockEntity extends BlockEntity implements MenuProvider {
    private static final RandomSource RANDOM = RandomSource.create();
    private static final String INVENTORY_TAG = "Inventory";
    private static final String CUSTOM_NAME_TAG = "CustomName";

    public int time;
    public float flip;
    public float oFlip;
    public float flipT;
    public float flipA;
    public float open;
    public float oOpen;
    public float rot;
    public float oRot;
    public float tRot;

    private Component customName;
    private final ItemStackHandler inventory = new ItemStackHandler(1) {
        @Override
        public int getSlotLimit(int slot) {
            return 1;
        }

        @Override
        protected void onContentsChanged(int slot) {
            setChanged();
        }
    };
    private LazyOptional<ItemStackHandler> inventoryCapability = LazyOptional.of(() -> inventory);

    public EndEnchantingTableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.END_ENCHANTING_TABLE.get(), pos, state);
    }

    public ItemStackHandler getInventory() {
        return inventory;
    }

    @Override
    protected void saveAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.saveAdditional(tag, provider);
        tag.put(INVENTORY_TAG, inventory.serializeNBT(provider));
        if (customName != null) {
            tag.putString(CUSTOM_NAME_TAG, Component.Serializer.toJson(customName, provider));
        }
    }

    @Override
    protected void loadAdditional(CompoundTag tag, net.minecraft.core.HolderLookup.Provider provider) {
        super.loadAdditional(tag, provider);
        if (tag.contains(INVENTORY_TAG, CompoundTag.TAG_COMPOUND)) {
            inventory.deserializeNBT(provider, tag.getCompound(INVENTORY_TAG));
        }
        if (tag.contains(CUSTOM_NAME_TAG, CompoundTag.TAG_STRING)) {
            customName = Component.Serializer.fromJson(tag.getString(CUSTOM_NAME_TAG), provider);
        }
    }

    @NotNull
    @Override
    public <T> LazyOptional<T> getCapability(@NotNull Capability<T> capability, @Nullable Direction side) {
        if (capability == ForgeCapabilities.ITEM_HANDLER) {
            return inventoryCapability.cast();
        }
        return super.getCapability(capability, side);
    }

    @Override
    public void invalidateCaps() {
        super.invalidateCaps();
        inventoryCapability.invalidate();
    }

    @Override
    public void reviveCaps() {
        super.reviveCaps();
        inventoryCapability = LazyOptional.of(() -> inventory);
    }

    public void setCustomName(Component customName) {
        this.customName = customName;
    }

    @Override
    public Component getDisplayName() {
        return customName != null ? customName : Component.translatable("container.end_enchanting_table.end_enchanting_table");
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int containerId, Inventory playerInventory, Player player) {
        return new EndEnchantingMenu(containerId, playerInventory, this);
    }

    public static void bookAnimationTick(Level level, BlockPos pos, BlockState state,
                                         EndEnchantingTableBlockEntity table) {
        table.oOpen = table.open;
        table.oRot = table.rot;
        Player player = level.getNearestPlayer(
                pos.getX() + 0.5D, pos.getY() + 0.5D, pos.getZ() + 0.5D, 3.0D, false);

        if (player != null) {
            double dx = player.getX() - (pos.getX() + 0.5D);
            double dz = player.getZ() - (pos.getZ() + 0.5D);
            table.tRot = (float) Mth.atan2(dz, dx);
            table.open += 0.1F;
            if (table.open < 0.5F || RANDOM.nextInt(40) == 0) {
                float oldTarget = table.flipT;
                do {
                    table.flipT += RANDOM.nextInt(4) - RANDOM.nextInt(4);
                } while (oldTarget == table.flipT);
            }
        } else {
            table.tRot += 0.02F;
            table.open -= 0.1F;
        }

        table.rot = wrapRadians(table.rot);
        table.tRot = wrapRadians(table.tRot);
        float rotationDelta = wrapRadians(table.tRot - table.rot);
        table.rot += rotationDelta * 0.4F;
        table.open = Mth.clamp(table.open, 0.0F, 1.0F);
        table.time++;
        table.oFlip = table.flip;
        float pageDelta = Mth.clamp((table.flipT - table.flip) * 0.4F, -0.2F, 0.2F);
        table.flipA += (pageDelta - table.flipA) * 0.9F;
        table.flip += table.flipA;
    }

    private static float wrapRadians(float radians) {
        while (radians >= Math.PI) {
            radians -= Mth.TWO_PI;
        }
        while (radians < -Math.PI) {
            radians += Mth.TWO_PI;
        }
        return radians;
    }
}
