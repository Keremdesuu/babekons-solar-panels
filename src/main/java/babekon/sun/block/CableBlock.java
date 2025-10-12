package babekon.sun.block;

import babekon.sun.block.entity.CableBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CableBlock extends Block implements BlockEntityProvider {
    // Six-way connection properties
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty WEST = Properties.WEST;

    // Predefined arm and center shapes (6x6 thickness)
    private static final VoxelShape CENTER = Block.createCuboidShape(5, 5, 5, 11, 11, 11);
    private static final VoxelShape ARM_UP = Block.createCuboidShape(5, 11, 5, 11, 16, 11);
    private static final VoxelShape ARM_DOWN = Block.createCuboidShape(5, 0, 5, 11, 5, 11);
    private static final VoxelShape ARM_NORTH = Block.createCuboidShape(5, 5, 0, 11, 11, 5);
    private static final VoxelShape ARM_SOUTH = Block.createCuboidShape(5, 5, 11, 11, 11, 16);
    private static final VoxelShape ARM_EAST = Block.createCuboidShape(11, 5, 5, 16, 11, 11);
    private static final VoxelShape ARM_WEST = Block.createCuboidShape(0, 5, 5, 5, 11, 11);

    public CableBlock(Settings settings) {
        super(settings.nonOpaque());
        this.setDefaultState(this.getStateManager().getDefaultState()
            .with(UP, false).with(DOWN, false)
            .with(NORTH, false).with(SOUTH, false)
            .with(EAST, false).with(WEST, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST);
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        return this.getDefaultState()
            .with(UP, canConnect(world, pos.up()))
            .with(DOWN, canConnect(world, pos.down()))
            .with(NORTH, canConnect(world, pos.north()))
            .with(SOUTH, canConnect(world, pos.south()))
            .with(EAST, canConnect(world, pos.east()))
            .with(WEST, canConnect(world, pos.west()));
    }

    private boolean canConnect(net.minecraft.world.WorldAccess world, BlockPos neighborPos) {
        if (world == null) return false;
        BlockEntity be = world.getBlockEntity(neighborPos);
        // Connect if the neighbor has a KE storage (including other cables, batteries, panels, lamps)
        return be instanceof babekon.sun.energy.KeStorage;
    }

    

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, ShapeContext context) {
        VoxelShape shape = CENTER;
        if (state.get(UP)) shape = VoxelShapes.union(shape, ARM_UP);
        if (state.get(DOWN)) shape = VoxelShapes.union(shape, ARM_DOWN);
        if (state.get(NORTH)) shape = VoxelShapes.union(shape, ARM_NORTH);
        if (state.get(SOUTH)) shape = VoxelShapes.union(shape, ARM_SOUTH);
        if (state.get(EAST)) shape = VoxelShapes.union(shape, ARM_EAST);
        if (state.get(WEST)) shape = VoxelShapes.union(shape, ARM_WEST);
        return shape;
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new CableBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : (w, p, s, be) -> {
            if (be instanceof CableBlockEntity cable) {
                CableBlockEntity.tick(w, p, s, cable);
            }
        };
    }
}
