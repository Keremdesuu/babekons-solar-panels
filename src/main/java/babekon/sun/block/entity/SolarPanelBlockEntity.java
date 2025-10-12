package babekon.sun.block.entity;

import babekon.sun.ModBlockEntities;
import babekon.sun.energy.KeStorage;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;
import net.minecraft.block.entity.BlockEntity;


public class SolarPanelBlockEntity extends BlockEntity implements KeStorage {
    private static final int CAPACITY = 10000;
    private static final int MAX_GEN_PER_TICK = 2; // tune later

    private int keStored = 0;
    private boolean generating = false;

    public SolarPanelBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.SOLAR_PANEL, pos, state);
    }

    public int getKeStored() {
        return keStored;
    }

    public int getCapacity() {
        return CAPACITY;
    }

    // KeStorage implementation: panels only allow extract, not insert
    @Override
    public int insertKe(int amount, boolean simulate) { return 0; }

    @Override
    public int extractKe(int amount, boolean simulate) {
        int extracted = Math.min(amount, keStored);
        if (!simulate && extracted > 0) {
            keStored -= extracted;
            markDirty();
        }
        return extracted;
    }

    @Override
    public int getKeCapacity() { return getCapacity(); }

    public boolean isGenerating() { return generating; }

    public static void tick(World world, BlockPos pos, BlockState state, SolarPanelBlockEntity be) {
    if (world == null || world.isClient()) return;

        // Only generate if daytime, sky visible, and not thundering (reduced or zero in rain)
        boolean hasSky = world.isSkyVisible(pos.up());
        boolean isDay = world.isDay();
        int skyLight = world.getLightLevel(LightType.SKY, pos.up());
        boolean raining = world.isRaining();
        boolean thundering = world.isThundering();

        int gen = 0;
        if (isDay && hasSky && skyLight > 10) {
            gen = MAX_GEN_PER_TICK;
            if (thundering) {
                gen = 0;
            } else if (raining) {
                gen = Math.max(1, gen - 1);
            }
        }

        if (gen > 0 && be.keStored < CAPACITY) {
            be.keStored = Math.min(CAPACITY, be.keStored + gen);
            be.markDirty();
        }
        be.generating = gen > 0;

        // Try to push out a bit of KE to adjacent storages automatically
        if (be.keStored > 0) {
            int toSend = Math.min(be.keStored, 4);
            for (Direction dir : Direction.values()) {
                if (toSend <= 0) break;
                BlockPos np = pos.offset(dir);
                BlockEntity other = world.getBlockEntity(np);
                if (other instanceof KeStorage storage) {
                    int accepted = storage.insertKe(toSend, false);
                    if (accepted > 0) {
                        be.keStored -= accepted;
                        toSend -= accepted;
                        be.markDirty();
                    }
                }
            }
        }
    }

    // No GUI

    // Persistence is temporarily omitted to avoid mapping signature issues; KE is runtime-only for now.
}
