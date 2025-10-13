package babekon.sun.inventory;

import babekon.sun.BabekonsSunPanels;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public class ItemApi {
    public static final BlockApiLookup<ItemStorage, Direction> LOOKUP = BlockApiLookup.get(
            Identifier.of(BabekonsSunPanels.MOD_ID, "item_storage"), ItemStorage.class, Direction.class);
}
