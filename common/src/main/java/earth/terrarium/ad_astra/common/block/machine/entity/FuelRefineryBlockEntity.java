package earth.terrarium.ad_astra.common.block.machine.entity;

import earth.terrarium.ad_astra.common.config.FuelRefineryConfig;
import earth.terrarium.ad_astra.common.recipe.FuelConversionRecipe;
import earth.terrarium.ad_astra.common.registry.ModBlockEntityTypes;
import earth.terrarium.ad_astra.common.screen.menu.ConversionMenu;
import earth.terrarium.ad_astra.common.util.FluidUtils;
import earth.terrarium.botarium.api.energy.EnergyBlock;
import earth.terrarium.botarium.api.energy.InsertOnlyEnergyContainer;
import earth.terrarium.botarium.api.fluid.FluidHolder;
import earth.terrarium.botarium.api.fluid.FluidHooks;
import net.minecraft.core.BlockPos;
import net.minecraft.core.Direction;
import net.minecraft.world.entity.player.Inventory;
import net.minecraft.world.entity.player.Player;
import net.minecraft.world.inventory.AbstractContainerMenu;
import net.minecraft.world.item.ItemStack;
import net.minecraft.world.level.block.state.BlockState;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;

import java.util.List;
import java.util.function.Predicate;

public class FuelRefineryBlockEntity extends FluidMachineBlockEntity implements EnergyBlock {

    public FuelRefineryBlockEntity(BlockPos blockPos, BlockState blockState) {
        super(ModBlockEntityTypes.FUEL_REFINERY.get(), blockPos, blockState);
    }

    @Override
    public long getInputTankCapacity() {
        return FuelRefineryConfig.tankSize;
    }

    @Override
    public long getOutputTankCapacity() {
        return FuelRefineryConfig.tankSize;
    }

    @Override
    public Predicate<FluidHolder> getInputFilter() {
        return f -> FuelConversionRecipe.getRecipes(this.getLevel()).stream().anyMatch(r -> r.matches(f.getFluid()));
    }

    @Override
    public int getInventorySize() {
        return 4;
    }

    @Override
    public boolean canPlaceItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 0;
    }

    @Override
    public boolean canTakeItemThroughFace(int slot, ItemStack stack, Direction dir) {
        return slot == 1 || slot == 3;
    }

    @Nullable
    @Override
    public AbstractContainerMenu createMenu(int syncId, @NotNull Inventory inv, @NotNull Player player) {
        return new ConversionMenu(syncId, inv, this);
    }

    @Override
    public void tick() {
        if (!this.level.isClientSide()) {
            ItemStack insertSlot = this.getItems().get(0);
            ItemStack extractSlot = this.getItems().get(1);
            ItemStack outputInsertSlot = this.getItems().get(2);
            ItemStack outputExtractSlot = this.getItems().get(3);

            if (!insertSlot.isEmpty() && extractSlot.getCount() < extractSlot.getMaxStackSize() && FluidHooks.isFluidContainingItem(insertSlot)) {
                FluidUtils.insertItemFluidToTank(this.getFluidContainer().getInput(), this, 0, 1, 0, f -> FuelConversionRecipe.getRecipes(this.level).stream().anyMatch(r -> r.matches(f)));
                FluidUtils.extractTankFluidToItem(this.getFluidContainer().getInput(), this, 0, 1, 0, f -> true);
            }

            if (!outputInsertSlot.isEmpty() && outputExtractSlot.getCount() < outputExtractSlot.getMaxStackSize()) {
                FluidUtils.extractTankFluidToItem(this.getFluidContainer().getOutput(), this, 2, 3, 0, f -> true);
            }

            if (this.getEnergyStorage().internalExtract(this.getEnergyPerTick(), true) > 0) {
                List<FuelConversionRecipe> recipes = FuelConversionRecipe.getRecipes(this.level);
                if (this.getEnergyStorage().internalExtract(this.getEnergyPerTick(), true) >= this.getEnergyPerTick() && FluidUtils.convertFluid(this.getFluidContainer(), recipes, FluidHooks.buckets(1) / 50)) {
                    this.getEnergyStorage().internalExtract(this.getEnergyPerTick(), false);
                    this.setActive(true);
                } else {
                    this.setActive(false);
                }
            } else {
                this.setActive(false);
            }
        }
    }

    @Override
    public long getEnergyPerTick() {
        return FuelRefineryConfig.energyPerTick;
    }

    @Override
    public InsertOnlyEnergyContainer getEnergyStorage() {
        return this.energyContainer == null ? this.energyContainer = new InsertOnlyEnergyContainer(this, (int) FuelRefineryConfig.maxEnergy) : this.energyContainer;
    }
}