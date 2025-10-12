package babekon.sun.block.entity;

import babekon.sun.ModBlockEntities;
import babekon.sun.energy.KeStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.screen.PropertyDelegate;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class BatteryBlockEntity extends BlockEntity implements KeStorage {
    private static final int CAPACITY = 100_000;
    private int ke;
    private final PropertyDelegate properties = new PropertyDelegate() {
        @Override
        public int get(int index) {
            return switch (index) {
                case 0 -> CAPACITY;
                case 1 -> ke;
                default -> 0;
            };
        }

        @Override
        public void set(int index, int value) { /* read-only from client */ }

        @Override
        public int size() { return 2; }
    };

    public BatteryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BATTERY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BatteryBlockEntity be) {
        if (world == null || world.isClient()) return;
        // For now, passive storage; transfer handled by cables.
    }

    @Override
    public int insertKe(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        int accepted = Math.min(amount, CAPACITY - ke);
        if (!simulate && accepted > 0) {
            ke += accepted;
            markDirty();
        }
        return accepted;
    }

    @Override
    public int extractKe(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        int extracted = Math.min(amount, ke);
        if (!simulate && extracted > 0) {
            ke -= extracted;
            markDirty();
        }
        return extracted;
    }

    @Override
    public int getKeStored() {
        return ke;
    }

    @Override
    public int getKeCapacity() {
        return CAPACITY;
    }

    public PropertyDelegate getPropertyDelegate() { return properties; }

    // No GUI
}
