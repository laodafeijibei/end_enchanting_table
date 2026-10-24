package com.laodafeijibei.zhongyanfumotai.block;

import com.laodafeijibei.zhongyanfumotai.block.entity.EndEnchantingTableBlockEntity;
import com.laodafeijibei.zhongyanfumotai.registry.ModBlockEntities;
import net.minecraft.core.BlockPos;
import net.minecraft.core.particles.ParticleTypes;
import net.minecraft.server.level.ServerPlayer;
import net.minecraft.util.RandomSource;
import net.minecraft.world.Containers;
import net.minecraft.world.InteractionResult;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.item.ItemStack;
import net.minecraft.core.component.DataComponents;
import net.minecraft.world.level.Level;
import net.minecraft.world.level.block.EnchantingTableBlock;
import net.minecraft.world.level.block.entity.BlockEntity;
import net.minecraft.world.level.block.entity.BlockEntityTicker;
import net.minecraft.world.level.block.entity.BlockEntityType;
import net.minecraft.world.level.block.state.BlockState;
import net.minecraft.world.level.pathfinder.PathComputationType;
import net.minecraft.world.phys.BlockHitResult;
import org.jetbrains.annotations.Nullable;

public final class EndEnchantingTableBlock extends EnchantingTableBlock {
    public EndEnchantingTableBlock(Properties properties) {
        super(properties);
    }

    @Override
    public EndEnchantingTableBlockEntity newBlockEntity(BlockPos pos, BlockState state) {
        return new EndEnchantingTableBlockEntity(pos, state);
    }

    @Nullable
    @Override
    public <T extends BlockEntity> BlockEntityTicker<T> getTicker(
            Level level, BlockState state, BlockEntityType<T> type) {
        return level.isClientSide
                ? createTickerHelper(type, ModBlockEntities.END_ENCHANTING_TABLE.get(),
                EndEnchantingTableBlockEntity::bookAnimationTick)
                : null;
    }

    @Override
    protected InteractionResult useWithoutItem(BlockState state, Level level, BlockPos pos,
                                               Player player, BlockHitResult hit) {
        if (!level.isClientSide && player instanceof ServerPlayer serverPlayer
                && level.getBlockEntity(pos) instanceof EndEnchantingTableBlockEntity table) {
            serverPlayer.openMenu(table, pos);
        }
        return InteractionResult.sidedSuccess(level.isClientSide);
    }

    @Override
    public void animateTick(BlockState state, Level level, BlockPos pos, RandomSource random) {
        for (BlockPos offset : BOOKSHELF_OFFSETS) {
            if (random.nextInt(16) == 0 && isValidBookShelf(level, pos, offset)) {
                level.addParticle(ParticleTypes.ENCHANT,
                        pos.getX() + 0.5D, pos.getY() + 1.5D, pos.getZ() + 0.5D,
                        offset.getX() + random.nextFloat() - 0.5D,
                        offset.getY() - random.nextFloat() - 1.0D,
                        offset.getZ() + random.nextFloat() - 0.5D);
            }
        }
    }

    @Override
    public void setPlacedBy(Level level, BlockPos pos, BlockState state,
                            @Nullable net.minecraft.world.entity.LivingEntity placer, ItemStack stack) {
        super.setPlacedBy(level, pos, state, placer, stack);
        if (stack.has(DataComponents.CUSTOM_NAME)
                && level.getBlockEntity(pos) instanceof EndEnchantingTableBlockEntity table) {
            table.setCustomName(stack.getHoverName());
        }
    }

    @Override
    public void onRemove(BlockState state, Level level, BlockPos pos, BlockState newState, boolean moving) {
        if (!state.is(newState.getBlock())) {
            if (level.getBlockEntity(pos) instanceof EndEnchantingTableBlockEntity table) {
                ItemStack stack = table.getInventory().extractItem(0, 1, false);
                if (!stack.isEmpty()) {
                    Containers.dropItemStack(level, pos.getX(), pos.getY(), pos.getZ(), stack);
                }
                level.updateNeighbourForOutputSignal(pos, this);
            }
            super.onRemove(state, level, pos, newState, moving);
        }
    }

    @Override
    protected boolean isPathfindable(BlockState state, PathComputationType type) {
        return false;
    }
}
