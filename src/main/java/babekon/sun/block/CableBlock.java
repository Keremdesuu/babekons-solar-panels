package babekon.sun.block;

import babekon.sun.ModTags;
import babekon.sun.block.entity.CableBlockEntity;
import babekon.sun.energy.KeApi;
import net.minecraft.block.Block;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.BlockState;
import net.minecraft.block.ShapeContext;
import net.minecraft.block.Waterloggable;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.fluid.FluidState;
import net.minecraft.fluid.Fluids;
import net.minecraft.registry.tag.FluidTags;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class CableBlock extends Block implements BlockEntityProvider, Waterloggable {
    public static final BooleanProperty UP = Properties.UP;
    public static final BooleanProperty DOWN = Properties.DOWN;
    public static final BooleanProperty NORTH = Properties.NORTH;
    public static final BooleanProperty SOUTH = Properties.SOUTH;
    public static final BooleanProperty EAST = Properties.EAST;
    public static final BooleanProperty WEST = Properties.WEST;
    public static final BooleanProperty WATERLOGGED = Properties.WATERLOGGED;

    private static final VoxelShape CENTER = Block.createCuboidShape(6, 6, 6, 10, 10, 10);
    private static final VoxelShape ARM_UP = Block.createCuboidShape(6, 10, 6, 10, 16, 10);
    private static final VoxelShape ARM_DOWN = Block.createCuboidShape(6, 0, 6, 10, 6, 10);
    private static final VoxelShape ARM_NORTH = Block.createCuboidShape(6, 6, 0, 10, 10, 6);
    private static final VoxelShape ARM_SOUTH = Block.createCuboidShape(6, 6, 10, 10, 10, 16);
    private static final VoxelShape ARM_EAST = Block.createCuboidShape(10, 6, 6, 16, 10, 10);
    private static final VoxelShape ARM_WEST = Block.createCuboidShape(0, 6, 6, 6, 10, 10);

    public CableBlock(Settings settings) {
        super(settings.nonOpaque());
        this.setDefaultState(this.getStateManager().getDefaultState()
            .with(UP, false).with(DOWN, false)
            .with(NORTH, false).with(SOUTH, false)
            .with(EAST, false).with(WEST, false)
            .with(WATERLOGGED, false));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(UP, DOWN, NORTH, SOUTH, EAST, WEST, WATERLOGGED);
    }

    @Override
    public BlockState getPlacementState(net.minecraft.item.ItemPlacementContext ctx) {
        World world = ctx.getWorld();
        BlockPos pos = ctx.getBlockPos();
        boolean water = world.getFluidState(pos).isIn(FluidTags.WATER);
        return this.getDefaultState()
            .with(UP, canConnect(world, pos.up()))
            .with(DOWN, canConnect(world, pos.down()))
            .with(NORTH, canConnect(world, pos.north()))
            .with(SOUTH, canConnect(world, pos.south()))
            .with(EAST, canConnect(world, pos.east()))
            .with(WEST, canConnect(world, pos.west()))
            .with(WATERLOGGED, water);
    }

    private boolean canConnect(net.minecraft.world.WorldAccess world, BlockPos neighborPos) {
        if (world == null) return false;
        if (KeApi.LOOKUP.find((World) world, neighborPos, null) != null) return true;
        return world.getBlockState(neighborPos).isIn(ModTags.KE_CONNECTABLE);
    }

    @Override
    public FluidState getFluidState(BlockState state) {
        return state.get(WATERLOGGED) ? Fluids.WATER.getStill(false) : super.getFluidState(state);
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
