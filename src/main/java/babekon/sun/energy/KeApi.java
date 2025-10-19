package babekon.sun.energy;

import babekon.sun.BabekonsSunPanels;
import babekon.sun.ModBlockEntities;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public final class KeApi {
    private KeApi() {}

    public static final BlockApiLookup<KeStorage, Direction> LOOKUP =
            BlockApiLookup.get(Identifier.of(BabekonsSunPanels.MOD_ID, "ke"), KeStorage.class, Direction.class);

    public static void register() {
        LOOKUP.registerForBlockEntities((be, side) -> (be instanceof KeStorage ks) ? ks : null,
                ModBlockEntities.BATTERY,
                ModBlockEntities.SOLAR_PANEL,
                ModBlockEntities.CABLE,
                ModBlockEntities.LIGHT_RECEIVER
        );
    }
}
