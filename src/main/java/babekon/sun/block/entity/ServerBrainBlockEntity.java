package babekon.sun.block.entity;

import babekon.sun.ModBlockEntities;
import babekon.sun.brain.ServerBrain;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.Direction;
import net.minecraft.world.World;

import java.util.ArrayDeque;
import java.util.HashSet;
import java.util.Set;

public class ServerBrainBlockEntity extends BlockEntity implements ServerBrain {
    private int serverCount = 0;

    public ServerBrainBlockEntity(BlockPos pos, BlockState state) { super(ModBlockEntities.SERVER_BRAIN, pos, state); }

    public void tickServer() {
        if (world == null || world.isClient()) return;
        int c = discoverServers(world, pos, 256);
        if (c != serverCount) { serverCount = c; markDirty(); }
    }

    private int discoverServers(World w, BlockPos start, int maxVisited) {
        var q = new ArrayDeque<BlockPos>();
        Set<BlockPos> visited = new HashSet<>();
        q.add(start); visited.add(start);
        int cnt = 0;
        while (!q.isEmpty() && visited.size() < maxVisited) {
            BlockPos p = q.poll();
            for (Direction d : Direction.values()) {
                BlockPos np = p.offset(d);
                if (!visited.add(np)) continue;
                BlockEntity be = w.getBlockEntity(np);
                if (be instanceof ServerBlockEntity) { cnt++; continue; }
                if (be instanceof CableBlockEntity || be instanceof ServerBrainBlockEntity) { q.add(np); }
            }
        }
        return cnt;
    }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) { nbt.putInt("count", serverCount); }
    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) { serverCount = nbt.getInt("count").orElse(0); }
    @Override public int getServerCount() { return serverCount; }
}
