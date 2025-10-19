package babekon.sun.block.entity;

import babekon.sun.inventory.ItemApi;
import babekon.sun.inventory.ItemStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.item.ItemStack;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.util.collection.DefaultedList;
import net.minecraft.util.math.BlockPos;

public class ServerBlockEntity extends BlockEntity implements ItemStorage {
    private static final int SIZE = 54;
    private final DefaultedList<ItemStack> stacks = DefaultedList.ofSize(SIZE, ItemStack.EMPTY);

    public ServerBlockEntity(BlockPos pos, BlockState state) {
        super(babekon.sun.ModBlockEntities.SERVER, pos, state);
    }

    public void tickServer() { }

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) { }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) { }

    public int size() { return SIZE; }
    public ItemStack getStack(int slot) { return stacks.get(slot); }
    public ItemStack insert(ItemStack stack) {
        if (stack == null || stack.isEmpty()) return ItemStack.EMPTY;
        ItemStack remaining = stack.copy();
        for (int i = 0; i < SIZE && !remaining.isEmpty(); i++) {
            ItemStack cur = stacks.get(i);
            if (cur.isEmpty()) {
                stacks.set(i, remaining);
                markDirty();
                return ItemStack.EMPTY;
            } else if (ItemStack.areItemsAndComponentsEqual(cur, remaining) && cur.getCount() < cur.getMaxCount()) {
                int can = Math.min(remaining.getCount(), cur.getMaxCount() - cur.getCount());
                if (can > 0) {
                    cur.increment(can);
                    remaining.decrement(can);
                    markDirty();
                }
            }
        }
        return remaining;
    }
    public ItemStack extract(int slot, int amount) {
        if (slot < 0 || slot >= SIZE || amount <= 0) return ItemStack.EMPTY;
        ItemStack cur = stacks.get(slot);
        if (cur.isEmpty()) return ItemStack.EMPTY;
        int take = Math.min(amount, cur.getCount());
        ItemStack out = cur.copy();
        out.setCount(take);
        cur.decrement(take);
        if (cur.getCount() <= 0) stacks.set(slot, ItemStack.EMPTY);
        markDirty();
        return out;
    }
}
