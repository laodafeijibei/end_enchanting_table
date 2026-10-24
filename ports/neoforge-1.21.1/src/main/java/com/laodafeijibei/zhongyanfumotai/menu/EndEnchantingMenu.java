package com.laodafeijibei.zhongyanfumotai.menu;

import com.laodafeijibei.zhongyanfumotai.block.entity.EndEnchantingTableBlockEntity;
import com.laodafeijibei.zhongyanfumotai.registry.ModBlocks;
import com.laodafeijibei.zhongyanfumotai.registry.ModMenus;
import net.minecraft.core.BlockPos;
import net.minecraft.network.FriendlyByteBuf;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.inventory.ContainerLevelAccess;
import net.minecraft.world.inventory.Slot;
import net.minecraft.world.item.ItemStack;
import net.neoforged.neoforge.items.SlotItemHandler;

public final class EndEnchantingMenu extends AbstractContainerMenu {
    public static final int TARGET_SLOT = 0;
    private static final int PLAYER_INVENTORY_START = 1;
    private static final int PLAYER_INVENTORY_END = 28;
    private static final int HOTBAR_END = 37;

    private final EndEnchantingTableBlockEntity table;
    private final ContainerLevelAccess access;

    public EndEnchantingMenu(int containerId, Inventory playerInventory, FriendlyByteBuf data) {
        this(containerId, playerInventory, findTable(playerInventory, data.readBlockPos()));
    }

    public EndEnchantingMenu(int containerId, Inventory playerInventory, EndEnchantingTableBlockEntity table) {
        super(ModMenus.END_ENCHANTING_TABLE.get(), containerId);
        this.table = table;
        this.access = ContainerLevelAccess.create(playerInventory.player.level(), table.getBlockPos());

        addSlot(new SlotItemHandler(table.getInventory(), 0, 155, 50));

        for (int row = 0; row < 3; row++) {
            for (int column = 0; column < 9; column++) {
                addSlot(new Slot(playerInventory, column + row * 9 + 9,
                        151 + column * 18, 142 + row * 18));
            }
        }
        for (int column = 0; column < 9; column++) {
            addSlot(new Slot(playerInventory, column, 151 + column * 18, 200));
        }
    }

    private static EndEnchantingTableBlockEntity findTable(Inventory inventory, BlockPos pos) {
        if (inventory.player.level().getBlockEntity(pos) instanceof EndEnchantingTableBlockEntity table) {
            return table;
        }
        throw new IllegalStateException("Missing end enchanting table at " + pos);
    }

    public ItemStack getTargetStack() {
        return table.getInventory().getStackInSlot(0);
    }

    public void setTargetStack(ItemStack stack) {
        table.getInventory().setStackInSlot(0, stack);
        table.setChanged();
        broadcastChanges();
    }

    @Override
    public boolean stillValid(Player player) {
        return stillValid(access, player, ModBlocks.END_ENCHANTING_TABLE.get());
    }

    @Override
    public ItemStack quickMoveStack(Player player, int index) {
        ItemStack result = ItemStack.EMPTY;
        Slot slot = slots.get(index);
        if (slot.hasItem()) {
            ItemStack source = slot.getItem();
            result = source.copy();
            if (index == TARGET_SLOT) {
                if (!moveItemStackTo(source, PLAYER_INVENTORY_START, HOTBAR_END, true)) {
                    return ItemStack.EMPTY;
                }
            } else if (!moveItemStackTo(source, TARGET_SLOT, TARGET_SLOT + 1, false)) {
                if (index < PLAYER_INVENTORY_END) {
                    if (!moveItemStackTo(source, PLAYER_INVENTORY_END, HOTBAR_END, false)) {
                        return ItemStack.EMPTY;
                    }
                } else if (!moveItemStackTo(source, PLAYER_INVENTORY_START, PLAYER_INVENTORY_END, false)) {
                    return ItemStack.EMPTY;
                }
            }

            if (source.isEmpty()) {
                slot.set(ItemStack.EMPTY);
            } else {
                slot.setChanged();
            }
            if (source.getCount() == result.getCount()) {
                return ItemStack.EMPTY;
            }
            slot.onTake(player, source);
        }
        return result;
    }
}
