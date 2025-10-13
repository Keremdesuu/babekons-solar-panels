package babekon.sun.block;

import babekon.sun.block.entity.ServerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ServerBlock extends Block implements BlockEntityProvider {
    public ServerBlock(Settings settings) { super(settings); }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new ServerBlockEntity(pos, state); }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : (w, p, s, be) -> {
            if (be instanceof ServerBlockEntity server) server.tickServer();
        };
    }
}
