package earth.terrarium.adastra.common.blockentities.base;

import earth.terrarium.botarium.common.energy.impl.WrappedBlockEnergyContainer;
import net.minecraft.core.BlockPos;
import net.minecraft.nbt.CompoundTag;
import net.minecraft.server.level.ServerLevel;
import net.minecraft.world.item.crafting.Recipe;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

public abstract class RecipeMachineBlockEntity<T extends Recipe<?>> extends ContainerMachineBlockEntity {
    @Nullable
    protected T recipe;
    protected int cookTime;
    protected int cookTimeTotal;

    public RecipeMachineBlockEntity(BlockPos pos, BlockState state, int containerSize) {
        super(pos, state, containerSize);
    }

    @Override
    public void internalServerTick(ServerLevel level, long time, BlockState state, BlockPos pos) {
        super.internalServerTick(level, time, state, pos);
        if (recipe != null) recipeTick(level, getEnergyStorage());
        if (time % 20 == 0 && recipe == null && shouldSync()) update();
    }

    public abstract void recipeTick(ServerLevel level, WrappedBlockEnergyContainer energyStorage);

    public abstract void craft();

    public void updateSlots() {}

    @Override
    public void load(@NotNull CompoundTag tag) {
        super.load(tag);
        cookTime = tag.getInt("CookTime");
        cookTimeTotal = tag.getInt("CookTimeTotal");
    }

    @Override
    protected void saveAdditional(@NotNull CompoundTag tag) {
        super.saveAdditional(tag);
        tag.putInt("CookTime", cookTime);
        tag.putInt("CookTimeTotal", cookTimeTotal);
    }

    public int cookTime() {
        return cookTime;
    }

    public int cookTimeTotal() {
        return cookTimeTotal;
    }
}
