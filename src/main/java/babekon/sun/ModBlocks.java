package babekon.sun;

import net.fabricmc.fabric.api.itemgroup.v1.ItemGroupEvents;
import net.minecraft.block.Block;
import net.minecraft.block.SlabBlock;
import net.minecraft.block.Blocks;
import net.minecraft.block.AbstractBlock;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block EXAMPLE_BLOCK = register("example_block");

    private static Block register(String name) {
        Identifier id = Identifier.of(BabekonsSunPanels.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);

    AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.STONE).registryKey(key);
    Block block = new SlabBlock(settings);

    Block registered = Registry.register(Registries.BLOCK, id, block);
    // Register BlockItem so it appears in inventories (item requires registry key too)
    RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
    Item.Settings itemSettings = new Item.Settings().registryKey(itemKey);
    Registry.register(Registries.ITEM, id, new BlockItem(registered, itemSettings));
        return registered;
    }

    public static void registerModBlocks() {
        // Items are added via ModItemGroups now.

        BabekonsSunPanels.LOGGER.info("Registered mod blocks for {}", BabekonsSunPanels.MOD_ID);
    }
}
