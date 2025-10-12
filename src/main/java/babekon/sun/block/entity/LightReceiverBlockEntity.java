package babekon.sun.block.entity;

import babekon.sun.ModBlockEntities;
import babekon.sun.block.LightReceiverBlock;
import babekon.sun.energy.KeStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightReceiverBlockEntity extends BlockEntity implements KeStorage {
    private static final int DECAY_TICKS = 50; // stay lit longer (5x) after last energy
    private int ticksSinceEnergy = DECAY_TICKS;

    public LightReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LIGHT_RECEIVER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, LightReceiverBlockEntity be) {
        if (world == null || world.isClient()) return;
        boolean shouldBeLit = be.ticksSinceEnergy < DECAY_TICKS;
        boolean isLit = state.get(LightReceiverBlock.LIT);
        if (shouldBeLit != isLit) {
            world.setBlockState(pos, state.with(LightReceiverBlock.LIT, shouldBeLit), 3);
        }
        if (be.ticksSinceEnergy < DECAY_TICKS) be.ticksSinceEnergy++;
    }

    // KeStorage implementation: we don't store, we just detect incoming KE
    @Override
    public int insertKe(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        if (!simulate) {
            ticksSinceEnergy = 0; // mark as recently energized
            markDirty();
        }
        return amount; // accept all, but don't store
    }

    @Override
    public int extractKe(int amount, boolean simulate) { return 0; }
    @Override
    public int getKeStored() { return 0; }
    @Override
    public int getKeCapacity() { return 0; }
}
