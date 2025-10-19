package babekon.sun.block.entity;

import babekon.sun.ModBlockEntities;
import babekon.sun.block.LightReceiverBlock;
import babekon.sun.energy.KeStorage;
import net.minecraft.block.BlockState;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class LightReceiverBlockEntity extends BlockEntity implements KeStorage {
    private static final int MAX_BRIGHTNESS = 15;
    private static final int DECAY_STEPS = 50;
    private int brightness = 0;
    private int decayCounter = 0;

    public LightReceiverBlockEntity(BlockPos pos, BlockState state) {
        super(ModBlockEntities.LIGHT_RECEIVER, pos, state);
    }

    public static void tick(World world, BlockPos pos, BlockState state, LightReceiverBlockEntity be) {
        if (world == null) return;
        if (!world.isClient()) {
            int current = state.get(LightReceiverBlock.BRIGHTNESS);
            int target = be.brightness;
            if (current != target || state.get(LightReceiverBlock.LIT) != (target > 0)) {
                world.setBlockState(pos, state.with(LightReceiverBlock.BRIGHTNESS, target).with(LightReceiverBlock.LIT, target > 0), 3);
            }

            if (be.decayCounter < DECAY_STEPS) {
                be.decayCounter++;
                if (be.decayCounter % Math.max(1, DECAY_STEPS / MAX_BRIGHTNESS) == 0 && be.brightness > 0) {
                    be.brightness = Math.max(0, be.brightness - 1);
                    be.markDirty();
                }
            }
        }
    }

    @Override
    public int insertKe(int amount, boolean simulate) {
        if (amount <= 0) return 0;
        if (!simulate) {
            brightness = MAX_BRIGHTNESS;
            decayCounter = 0;
            markDirty();
        }
        return amount; 
    }

    @Override
    public int extractKe(int amount, boolean simulate) { return 0; }
    @Override
    public int getKeStored() { return 0; }
    @Override
    public int getKeCapacity() { return 0; }
}
