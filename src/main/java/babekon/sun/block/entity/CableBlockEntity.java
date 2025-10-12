package babekon.sun.block.entity;

import babekon.sun.ModBlockEntities;
import babekon.sun.block.CableBlock;
import babekon.sun.energy.KeStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

public class CableBlockEntity extends BlockEntity implements KeStorage {
    private static final int THROUGHPUT = 16; // per tick total
    private static final int BUFFER_CAPACITY = 64; // allows chaining cables
    private int buffer = 0;

    public CableBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.CABLE, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, CableBlockEntity be) {
        if (world == null || world.isClient()) return;
        // Recompute connections each tick so cables react to neighbor changes without relying on Block overrides
        boolean up = canConnect(world, pos.up());
        boolean down = canConnect(world, pos.down());
        boolean north = canConnect(world, pos.north());
        boolean south = canConnect(world, pos.south());
        boolean east = canConnect(world, pos.east());
        boolean west = canConnect(world, pos.west());
        BlockState desired = state
            .with(CableBlock.UP, up)
            .with(CableBlock.DOWN, down)
            .with(CableBlock.NORTH, north)
            .with(CableBlock.SOUTH, south)
            .with(CableBlock.EAST, east)
            .with(CableBlock.WEST, west);
        if (!desired.equals(state)) {
            world.setBlockState(pos, desired, 3);
            state = desired; // use updated state for this tick
        }
        // Phase 1: pull into internal buffer from any connected neighbor with energy
        int budget = THROUGHPUT;
        for (Direction dir : Direction.values()) {
            if (budget <= 0) break;
            if (!isConnected(state, dir)) continue;
            if (be.buffer >= BUFFER_CAPACITY) break;
            BlockPos np = pos.offset(dir);
            BlockEntity beNeighbor = world.getBlockEntity(np);
            if (!(beNeighbor instanceof KeStorage src)) continue;
            if (src.getKeStored() <= 0) continue;
            int space = BUFFER_CAPACITY - be.buffer;
            int want = Math.min(space, budget);
            if (want <= 0) break;
            int pulled = src.extractKe(want, false);
            if (pulled > 0) {
                be.buffer += pulled;
                budget -= pulled;
                be.markDirty();
            }
        }

        // Phase 2: push from internal buffer to any connected neighbor that can accept
        for (Direction dir : Direction.values()) {
            if (be.buffer <= 0) break;
            if (budget <= 0) break;
            if (!isConnected(state, dir)) continue;
            BlockPos np = pos.offset(dir);
            BlockEntity beNeighbor = world.getBlockEntity(np);
            if (!(beNeighbor instanceof KeStorage dst)) continue;
            int want = Math.min(be.buffer, budget);
            if (want <= 0) break;
            int accepted = dst.insertKe(want, false);
            if (accepted > 0) {
                be.buffer -= accepted;
                budget -= accepted;
                be.markDirty();
            }
        }
    }

    private static boolean isConnected(BlockState state, Direction dir) {
        return switch (dir) {
            case UP -> state.get(CableBlock.UP);
            case DOWN -> state.get(CableBlock.DOWN);
            case NORTH -> state.get(CableBlock.NORTH);
            case SOUTH -> state.get(CableBlock.SOUTH);
            case EAST -> state.get(CableBlock.EAST);
            case WEST -> state.get(CableBlock.WEST);
        };
    }

    private static boolean canConnect(World world, BlockPos neighborPos) {
        if (world == null) return false;
        BlockEntity be = world.getBlockEntity(neighborPos);
        return be instanceof KeStorage;
    }

    // Cable has a small buffer to support multi-block chains
    @Override
    public int insertKe(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        int space = BUFFER_CAPACITY - buffer;
        int accepted = Math.min(space, amount);
        if (!simulate && accepted > 0) {
            buffer += accepted;
            markDirty();
        }
        return accepted;
    }

    @Override
    public int extractKe(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        int extracted = Math.min(amount, buffer);
        if (!simulate && extracted > 0) {
            buffer -= extracted;
            markDirty();
        }
        return extracted;
    }

    @Override
    public int getKeStored() { return buffer; }
    @Override
    public int getKeCapacity() { return BUFFER_CAPACITY; }
}
