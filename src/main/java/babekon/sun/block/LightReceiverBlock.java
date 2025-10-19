package babekon.sun.block;

import babekon.sun.block.entity.LightReceiverBlockEntity;
import net.minecraft.block.*;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.state.StateManager;
import net.minecraft.state.property.BooleanProperty;
import net.minecraft.state.property.IntProperty;
import net.minecraft.state.property.Properties;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.shape.VoxelShape;
import net.minecraft.util.shape.VoxelShapes;
import net.minecraft.world.BlockView;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class LightReceiverBlock extends Block implements BlockEntityProvider {
    public static final BooleanProperty LIT = Properties.LIT;
    public static final IntProperty BRIGHTNESS = Properties.LEVEL_15;

    public LightReceiverBlock(Settings settings) {
        super(settings.luminance(state -> state.contains(BRIGHTNESS) ? state.get(BRIGHTNESS) : 0));
        this.setDefaultState(this.stateManager.getDefaultState().with(LIT, false).with(BRIGHTNESS, 0));
    }

    @Override
    protected void appendProperties(StateManager.Builder<Block, BlockState> builder) {
        builder.add(LIT, BRIGHTNESS);
    }

    @Override
    public BlockRenderType getRenderType(BlockState state) {
        return BlockRenderType.MODEL;
    }

    @Override
    public VoxelShape getOutlineShape(BlockState state, BlockView world, BlockPos pos, net.minecraft.block.ShapeContext context) {
        return VoxelShapes.fullCube();
    }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) {
        return new LightReceiverBlockEntity(pos, state);
    }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : (w, p, s, be) -> {
            if (be instanceof LightReceiverBlockEntity lr) {
                LightReceiverBlockEntity.tick(w, p, s, lr);
            }
        };
    }
}
