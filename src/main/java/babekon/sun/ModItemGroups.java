package babekon.sun;

import net.fabricmc.fabric.api.itemgroup.v1.FabricItemGroup;
import net.minecraft.item.ItemGroup;
import net.minecraft.item.ItemStack;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.registry.RegistryKey;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.text.Text;
import net.minecraft.util.Identifier;

public class ModItemGroups {
    public static ItemGroup BABEKONS_ENGINEERING;

    public static void registerItemGroups() {
        Identifier id = Identifier.of(BabekonsSunPanels.MOD_ID, "babekons_engineering");
        RegistryKey<ItemGroup> key = RegistryKey.of(RegistryKeys.ITEM_GROUP, id);

        BABEKONS_ENGINEERING = Registry.register(Registries.ITEM_GROUP, key,
                FabricItemGroup.builder()
                        .icon(() -> new ItemStack(ModBlocks.EXAMPLE_BLOCK))
                        .displayName(Text.translatable("itemGroup." + BabekonsSunPanels.MOD_ID + ".babekons_engineering"))
                        .entries((context, entries) -> {
                            entries.add(ModBlocks.EXAMPLE_BLOCK);
                            entries.add(ModBlocks.BATTERY);
                            entries.add(ModBlocks.CABLE);
                            entries.add(ModBlocks.LIGHT_RECEIVER);
                            entries.add(ModBlocks.SERVER);
                            entries.add(ModBlocks.COMPUTER);
                            entries.add(ModBlocks.SERVER_BRAIN);
                        })
                        .build());

        BabekonsSunPanels.LOGGER.info("Registered item groups for {}", BabekonsSunPanels.MOD_ID);
    }
}
