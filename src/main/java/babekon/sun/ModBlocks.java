package babekon.sun;

import babekon.sun.block.BatteryBlock;
import babekon.sun.block.CableBlock;
import babekon.sun.block.SolarPanelSlabBlock;
import babekon.sun.block.LightReceiverBlock;
import net.minecraft.block.AbstractBlock;
import net.minecraft.block.Block;
import net.minecraft.block.Blocks;
import net.minecraft.item.BlockItem;
import net.minecraft.item.Item;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.util.Identifier;

public class ModBlocks {
    public static final Block EXAMPLE_BLOCK = registerSolarPanel("example_block");
    public static final Block BATTERY = registerBattery("battery");
    public static final Block CABLE = registerCable("cable");
    public static final Block LIGHT_RECEIVER = registerLightReceiver("light_receiver");

    private static Block registerSolarPanel(String name) {
        Identifier id = Identifier.of(BabekonsSunPanels.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.STONE).registryKey(key);
        Block block = new SolarPanelSlabBlock(settings);
        Block registered = Registry.register(Registries.BLOCK, id, block);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings itemSettings = new Item.Settings().registryKey(itemKey);
        Registry.register(Registries.ITEM, id, new BlockItem(registered, itemSettings));
        return registered;
    }

    private static Block registerBattery(String name) {
        Identifier id = Identifier.of(BabekonsSunPanels.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.IRON_BLOCK).strength(3.0f, 6.0f).registryKey(key);
        Block block = new BatteryBlock(settings);
        Block registered = Registry.register(Registries.BLOCK, id, block);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings itemSettings = new Item.Settings().registryKey(itemKey);
        Registry.register(Registries.ITEM, id, new BlockItem(registered, itemSettings));
        return registered;
    }

    private static Block registerCable(String name) {
        Identifier id = Identifier.of(BabekonsSunPanels.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.IRON_BARS).nonOpaque().strength(0.5f).registryKey(key);
        Block block = new CableBlock(settings);
        Block registered = Registry.register(Registries.BLOCK, id, block);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings itemSettings = new Item.Settings().registryKey(itemKey);
        Registry.register(Registries.ITEM, id, new BlockItem(registered, itemSettings));
        return registered;
    }

    private static Block registerLightReceiver(String name) {
        Identifier id = Identifier.of(BabekonsSunPanels.MOD_ID, name);
        RegistryKey<Block> key = RegistryKey.of(RegistryKeys.BLOCK, id);
        AbstractBlock.Settings settings = AbstractBlock.Settings.copy(Blocks.REDSTONE_LAMP).nonOpaque().strength(0.3f).registryKey(key);
        Block block = new LightReceiverBlock(settings);
        Block registered = Registry.register(Registries.BLOCK, id, block);
        RegistryKey<Item> itemKey = RegistryKey.of(RegistryKeys.ITEM, id);
        Item.Settings itemSettings = new Item.Settings().registryKey(itemKey);
        Registry.register(Registries.ITEM, id, new BlockItem(registered, itemSettings));
        return registered;
    }

    public static void registerModBlocks() {
        BabekonsSunPanels.LOGGER.info("Registered mod blocks for {}", BabekonsSunPanels.MOD_ID);
    }
}
