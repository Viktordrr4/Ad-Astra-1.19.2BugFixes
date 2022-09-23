package com.github.alexnijjar.ad_astra.blocks.pipes;

import java.util.ArrayList;
import java.util.List;

import com.github.alexnijjar.ad_astra.registry.ModBlockEntities;

import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class FluidPipeBlockEntity extends BlockEntity implements InteractablePipe<FluidHolder> {
    private List<Node<FluidHolder>> consumers = new ArrayList<>();
    private Node<FluidHolder> source;

    public FluidPipeBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.FLUID_PIPE.get(), pos, state);
    }

    @Override
    public boolean supportsAutoExtract() {
        return true;
    }

    @Override
    public boolean canTakeFrom(FluidHolder source) {
        return source.supportsExtraction();
    }

    @Override
    public boolean canInsertInto(FluidHolder consumer) {
        return consumer.supportsInsertion();
    }

    @Override
    public boolean canConnectTo(BlockEntity next, Direction direction, BlockPos pos) {
        return true;
    }

    @Override
    @SuppressWarnings("UnstableApiUsage")
    public void insertInto(FluidHolder consumer, Direction direction, BlockPos pos) {

        BlockState state = this.getCachedState();
        BlockState state2 = world.getBlockState(pos);

        if (!(state.getBlock() instanceof FluidPipeBlock) || !(state2.getBlock() instanceof FluidPipeBlock)) {
            return;
        }

        PipeState pipeState = state.get(FluidPipeBlock.DIRECTIONS.get(this.getSource().direction()));
        PipeState pipeState2 = state2.get(FluidPipeBlock.DIRECTIONS.get(direction));

        FluidHolder input;
        FluidHolder output;

        if (pipeState.equals(PipeState.INSERT) && pipeState2.equals(PipeState.INSERT)) {
            return;
        } else if (pipeState.equals(PipeState.EXTRACT) && pipeState2.equals(PipeState.EXTRACT)) {
            return;
        } else if (pipeState.equals(PipeState.NONE) || pipeState2.equals(PipeState.NONE)) {
            return;
        } else if (pipeState2.equals(PipeState.INSERT) && !pipeState2.equals(PipeState.NONE)) {
            input = source.storage();
            output = consumer;
        } else if (pipeState2.equals(PipeState.EXTRACT) && !pipeState2.equals(PipeState.NONE)) {
            input = consumer;
            output = source.storage();
        } else if (pipeState.equals(PipeState.INSERT) && !pipeState.equals(PipeState.NONE)) {
            input = consumer;
            output = source.storage();
        } else if (pipeState.equals(PipeState.EXTRACT) && !pipeState.equals(PipeState.NONE)) {
            input = source.storage();
            output = consumer;
        } else {
            return;
        }

        if (getSource() != null && getConsumers().size() > 0) {
            StorageUtil.move(input, output, f -> true, Math.max(0, this.getTransferAmount() / getConsumers().size()), null);
        }
    }

    @Override
    public FluidHolder getInteraction(World world, BlockPos pos, Direction direction) {
        return FluidStorage.SIDED.find(world, pos, direction);
    }

    @Override
    public Node<FluidHolder> getSource() {
        return source;
    }

    @Override
    public void setSource(Node<FluidHolder> source) {
        this.source = source;
    }

    @Override
    public void clearSource() {
        this.source = null;
    }

    @Override
    public List<Node<FluidHolder>> getConsumers() {
        return this.consumers;
    }

    @Override
    public int getWorkTime() {
        return 5;
    }

    @Override
    public World getPipeWorld() {
        return this.world;
    }

    @Override
    public long getTransferAmount() {
        if (this.getCachedState().getBlock() instanceof FluidPipeBlock fluidPipe) {
            return fluidPipe.getTransferRate();
        }
        return 0;
    }

    @Override
    public BlockPos getPipePos() {
        return this.getPos();
    }
}