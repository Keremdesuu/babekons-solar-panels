package babekon.sun.block.entity;

import babekon.sun.ModBlockEntities;
import babekon.sun.energy.KeStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
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
    public void set(int index, int value) { }

        @Override
        public int size() { return 2; }
    };

    public BatteryBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.BATTERY, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, BatteryBlockEntity be) {
        if (world == null || world.isClient()) return;
    }

        private static final String NBT_KE = "Ke";
        protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            nbt.putInt(NBT_KE, Math.max(0, Math.min(CAPACITY, ke)));
        }

        public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
            int saved = nbt.getInt(NBT_KE).orElse(0);
            this.ke = Math.max(0, Math.min(CAPACITY, saved));
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

}
