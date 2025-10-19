package babekon.sun.block.entity;

import babekon.sun.ModBlockEntities;
import babekon.sun.brain.BrainApi;
import babekon.sun.inventory.ItemApi;
import babekon.sun.inventory.ItemStorage;
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

public class ComputerBlockEntity extends BlockEntity implements ItemStorage {
    private int serverCount = 0;
    private int currentPage = 0;
    private java.util.List<BlockPos> serverPositions = new java.util.ArrayList<>();

    public ComputerBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.COMPUTER, pos, state);
    }

    public void tickServer() {
    if (world == null || world.isClient()) return;
        
        int brainCount = findBrainCount(world, pos, 128);
    java.util.List<BlockPos> found = discoverServers(world, pos, 128);
        serverPositions = found;
        int cnt = (brainCount >= 0) ? brainCount : found.size();
        if (cnt != serverCount) { serverCount = cnt; if (currentPage >= serverCount) currentPage = Math.max(0, serverCount - 1); markDirty(); }
    }

    private int findBrainCount(World w, BlockPos start, int maxVisited) {
        var queue = new ArrayDeque<BlockPos>();
        java.util.Set<BlockPos> visited = new java.util.HashSet<>();
        queue.add(start); visited.add(start);
        while (!queue.isEmpty() && visited.size() < maxVisited) {
            BlockPos p = queue.poll();
            for (Direction d : Direction.values()) {
                BlockPos np = p.offset(d);
                if (!visited.add(np)) continue;
                var brain = BrainApi.LOOKUP.find(w, np, d.getOpposite());
                if (brain != null) return brain.getServerCount();
                BlockEntity be = w.getBlockEntity(np);
                if (be instanceof CableBlockEntity || be instanceof ComputerBlockEntity || be instanceof ServerBrainBlockEntity) queue.add(np);
            }
        }
        return -1;
    }

    private java.util.List<BlockPos> discoverServers(World w, BlockPos start, int maxVisited) {
        var queue = new ArrayDeque<BlockPos>();
        Set<BlockPos> visited = new HashSet<>();
        queue.add(start);
        visited.add(start);
        java.util.List<BlockPos> servers = new java.util.ArrayList<>();
        while (!queue.isEmpty() && visited.size() < maxVisited) {
            BlockPos p = queue.poll();
            for (Direction dir : Direction.values()) {
                BlockPos np = p.offset(dir);
                if (!visited.add(np)) continue;
                
                BlockEntity be = w.getBlockEntity(np);
                if (be instanceof ServerBlockEntity) {
                    servers.add(np);
                    continue;
                }
                if (be instanceof CableBlockEntity || be instanceof ComputerBlockEntity) {
                    queue.add(np);
                }
            }
        }
        return servers;
    }

    
    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt("serverCount", serverCount);
        nbt.putInt("page", currentPage);
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        serverCount = nbt.getInt("serverCount").orElse(0);
        currentPage = Math.max(0, Math.min(serverCount - 1, nbt.getInt("page").orElse(0)));
    }

    
    @Override
    public int size() {
        return serverCount * 54;
    }

    @Override
    public net.minecraft.item.ItemStack getStack(int slot) {
    if (slot < 0 || slot >= size() || serverPositions.isEmpty()) return net.minecraft.item.ItemStack.EMPTY;
        int serverIdx = slot / 54;
        int inner = slot % 54;
        if (serverIdx >= serverPositions.size() || world == null) return net.minecraft.item.ItemStack.EMPTY;
        BlockPos sp = serverPositions.get(serverIdx);
        ItemStorage s = ItemApi.LOOKUP.find(world, sp, null);
        return (s != null) ? s.getStack(inner) : net.minecraft.item.ItemStack.EMPTY;
    }
    @Override
    public net.minecraft.item.ItemStack insert(net.minecraft.item.ItemStack stack) {
    if (stack.isEmpty() || world == null || serverPositions.isEmpty()) return stack;
        net.minecraft.item.ItemStack remaining = stack.copy();
        for (BlockPos sp : serverPositions) {
            ItemStorage s = ItemApi.LOOKUP.find(world, sp, null);
            if (s == null) continue;
            remaining = s.insert(remaining);
            if (remaining.isEmpty()) break;
        }
        return remaining;
    }
    @Override
    public net.minecraft.item.ItemStack extract(int slot, int amount) {
    if (slot < 0 || slot >= size() || world == null || amount <= 0 || serverPositions.isEmpty()) return net.minecraft.item.ItemStack.EMPTY;
        int serverIdx = slot / 54;
        int inner = slot % 54;
        if (serverIdx >= serverPositions.size()) return net.minecraft.item.ItemStack.EMPTY;
        BlockPos sp = serverPositions.get(serverIdx);
        ItemStorage s = ItemApi.LOOKUP.find(world, sp, null);
        return (s != null) ? s.extract(inner, amount) : net.minecraft.item.ItemStack.EMPTY;
    }

    public int getCurrentPage() { return currentPage; }
    public void setCurrentPage(int page) { this.currentPage = page; markDirty(); }
    public int getPageCount() { return serverPositions.size(); }
    public java.util.Optional<net.minecraft.util.math.BlockPos> getPageServerPos() {
        if (currentPage < 0 || currentPage >= serverPositions.size()) return java.util.Optional.empty();
        return java.util.Optional.of(serverPositions.get(currentPage));
    }
}
