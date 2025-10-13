package babekon.sun.block;

import babekon.sun.block.entity.ComputerBlockEntity;
import net.minecraft.block.Block;
import net.minecraft.block.BlockRenderType;
import net.minecraft.block.BlockState;
import net.minecraft.block.BlockEntityProvider;
import net.minecraft.block.entity.BlockEntity;
import net.minecraft.block.entity.BlockEntityTicker;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.util.math.BlockPos;
import net.minecraft.util.hit.BlockHitResult;
import net.minecraft.util.ActionResult;
import net.minecraft.entity.player.PlayerEntity;
import net.minecraft.screen.GenericContainerScreenHandler;
import net.minecraft.screen.SimpleNamedScreenHandlerFactory;
import net.minecraft.text.Text;
import net.minecraft.world.World;
import org.jetbrains.annotations.Nullable;

public class ComputerBlock extends Block implements BlockEntityProvider {
    public ComputerBlock(Settings settings) { super(settings); }

    @Override
    public @Nullable BlockEntity createBlockEntity(BlockPos pos, BlockState state) { return new ComputerBlockEntity(pos, state); }

    @Override
    public BlockRenderType getRenderType(BlockState state) { return BlockRenderType.MODEL; }

    @Override
    public <T extends BlockEntity> @Nullable BlockEntityTicker<T> getTicker(World world, BlockState state, BlockEntityType<T> type) {
        return world.isClient() ? null : (w, p, s, be) -> {
            if (be instanceof ComputerBlockEntity computer) computer.tickServer();
        };
    }

    @Override
    public ActionResult onUse(BlockState state, World world, BlockPos pos, PlayerEntity player, BlockHitResult hit) {
        if (world.isClient()) return ActionResult.SUCCESS;
        BlockEntity be = world.getBlockEntity(pos);
        if (!(be instanceof ComputerBlockEntity comp)) return ActionResult.PASS;
        if (player.isSneaking()) {
            int pages = Math.max(1, comp.getPageCount());
            comp.setCurrentPage((comp.getCurrentPage() + 1) % pages);
            return ActionResult.CONSUME;
        }
        var opt = comp.getPageServerPos();
        var inventory = new babekon.sun.inventory.ItemStorageInventory(world, opt);
        player.openHandledScreen(new SimpleNamedScreenHandlerFactory((syncId, playerInv, p) ->
                GenericContainerScreenHandler.createGeneric9x6(syncId, playerInv, inventory),
                Text.translatable("container.babekons-sun-panels.computer")));
        return ActionResult.CONSUME;
    }
}
