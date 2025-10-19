package babekon.sun.inventory;

import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.inventory.Inventory;
import net.minecraft.item.ItemStack;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

import java.util.Optional;

public class ItemStorageInventory implements Inventory {
    private final World world;
    private final Optional<BlockPos> serverPos;

    public ItemStorageInventory(World world, Optional<BlockPos> serverPos) {
        this.world = world;
        this.serverPos = serverPos;
    }

    private ItemStorage storage() {
        if (world == null || serverPos.isEmpty()) return null;
        return ItemApi.LOOKUP.find(world, serverPos.get(), null);
    }

    @Override
    public int size() { return 54; }

    @Override
    public boolean isEmpty() {
        ItemStorage s = storage();
        if (s == null) return true;
        for (int i = 0; i < 54; i++) if (!s.getStack(i).isEmpty()) return false;
        return true;
    }

    @Override
    public ItemStack getStack(int slot) {
        ItemStorage s = storage();
        return s == null ? ItemStack.EMPTY : s.getStack(slot);
    }

    @Override
    public ItemStack removeStack(int slot, int amount) {
        ItemStorage s = storage();
        return s == null ? ItemStack.EMPTY : s.extract(slot, amount);
    }

    @Override
    public ItemStack removeStack(int slot) {
        ItemStorage s = storage();
        if (s == null) return ItemStack.EMPTY;
        ItemStack cur = s.getStack(slot);
        if (cur.isEmpty()) return ItemStack.EMPTY;
        return s.extract(slot, cur.getCount());
    }

    @Override
    public void setStack(int slot, ItemStack stack) {
        ItemStorage s = storage();
        if (s == null) return;
        ItemStack cur = s.getStack(slot);
        if (!cur.isEmpty()) s.extract(slot, cur.getCount());
        ItemStack rem = s.insert(stack);
    }

    @Override
    public void markDirty() { }

    @Override
    public boolean canPlayerUse(PlayerEntity player) { return true; }

    @Override
    public void clear() {
        ItemStorage s = storage();
        if (s == null) return;
        for (int i = 0; i < 54; i++) removeStack(i);
    }
}
