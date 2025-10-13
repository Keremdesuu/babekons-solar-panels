package babekon.sun.inventory;

import net.minecraft.item.ItemStack;

public interface ItemStorage {
    int size();
    ItemStack getStack(int slot);
    ItemStack insert(ItemStack stack);
    ItemStack extract(int slot, int amount);
}
