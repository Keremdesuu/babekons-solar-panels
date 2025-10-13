package babekon.sun.brain;

import babekon.sun.BabekonsSunPanels;
import net.fabricmc.fabric.api.lookup.v1.block.BlockApiLookup;
import net.minecraft.util.Identifier;
import net.minecraft.util.math.Direction;

public final class BrainApi {
    private BrainApi() {}
    public static final BlockApiLookup<ServerBrain, Direction> LOOKUP = BlockApiLookup.get(
            Identifier.of(BabekonsSunPanels.MOD_ID, "server_brain"), ServerBrain.class, Direction.class);
}
