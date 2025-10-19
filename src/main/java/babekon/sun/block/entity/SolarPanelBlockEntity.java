package babekon.sun.block.entity;

import babekon.sun.ModBlockEntities;
import babekon.sun.energy.KeStorage;
import babekon.sun.energy.KeApi;
import net.minecraft.nbt.NbtCompound;
import net.minecraft.registry.RegistryWrapper;
import net.minecraft.block.BlockState;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.math.MathHelper;
import net.minecraft.world.LightType;
import net.minecraft.world.World;
import net.minecraft.util.math.Direction;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.world.biome.Biome;


public class SolarPanelBlockEntity extends BlockEntity implements KeStorage {
    private static final int CAPACITY = 10000;
    private static final int MAX_GEN_PER_TICK = 8;

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

        int skyLight = world.getLightLevel(LightType.SKY, pos.up());
        boolean raining = world.isRaining();
        boolean thundering = world.isThundering();
        boolean isDay = world.isDay();

        float timeFactor = 0f;
        long tod = world.getTimeOfDay();
        long t = tod % 24000L;
        if (t >= 0 && t < 12000L) {
            float dayProg = (t / 12000f);
            timeFactor = 1f - Math.abs(dayProg - 0.5f) * 2f;
        }

        float shadeFactor = MathHelper.clamp(skyLight / 15f, 0f, 1f);

    Biome biome = world.getBiome(pos).value();
    float temp = biome.getTemperature();
    float biomeFactor = MathHelper.clamp(1.0f + (temp - 0.8f) * 0.1f, 0.8f, 1.1f);

        float altFactor = 1.0f + MathHelper.clamp((pos.getY() - 64) / 128f, -0.2f, 0.3f);

        float weatherFactor = 1.0f;
        if (thundering) weatherFactor = 0f;
        else if (raining) weatherFactor = 0.7f;

        float combined = timeFactor * shadeFactor * biomeFactor * altFactor * weatherFactor;
        int gen = 0;
        if (isDay && combined > 0f) {
            gen = Math.max(0, Math.round(MAX_GEN_PER_TICK * combined));
        }

        if (gen > 0 && be.keStored < CAPACITY) {
            be.keStored = Math.min(CAPACITY, be.keStored + gen);
            be.markDirty();
        }
        be.generating = gen > 0;

        if (be.keStored > 0) {
            int perSide = 4;
            for (Direction dir : Direction.values()) {
                if (be.keStored <= 0) break;
                BlockPos np = pos.offset(dir);
                KeStorage dst = KeApi.LOOKUP.find(world, np, dir.getOpposite());
                if (dst == null) continue;
                int toSend = Math.min(be.keStored, perSide);
                int accepted = dst.insertKe(toSend, false);
                if (accepted > 0) {
                    be.keStored -= accepted;
                    be.markDirty();
                }
            }
        }
    }

    private static final String NBT_KE = "Ke";
    private static final String NBT_GEN = "Generating";

    protected void writeNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        nbt.putInt(NBT_KE, Math.max(0, Math.min(CAPACITY, keStored)));
        nbt.putBoolean(NBT_GEN, generating);
    }

    public void readNbt(NbtCompound nbt, RegistryWrapper.WrapperLookup registryLookup) {
        int savedKe = nbt.getInt(NBT_KE).orElse(0);
        this.keStored = Math.max(0, Math.min(CAPACITY, savedKe));
        this.generating = nbt.getBoolean(NBT_GEN).orElse(false);
    }

}
