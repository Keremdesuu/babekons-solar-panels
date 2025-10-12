package babekon.sun;

import babekon.sun.block.entity.BatteryBlockEntity;
import babekon.sun.block.entity.CableBlockEntity;
import babekon.sun.block.entity.SolarPanelBlockEntity;
import babekon.sun.block.entity.LightReceiverBlockEntity;
import net.fabricmc.fabric.api.object.builder.v1.block.entity.FabricBlockEntityTypeBuilder;
import net.minecraft.block.entity.BlockEntityType;
import net.minecraft.registry.Registries;
import net.minecraft.registry.Registry;
import net.minecraft.util.Identifier;

public class ModBlockEntities {
    public static BlockEntityType<SolarPanelBlockEntity> SOLAR_PANEL;
    public static BlockEntityType<BatteryBlockEntity> BATTERY;
    public static BlockEntityType<CableBlockEntity> CABLE;
        public static BlockEntityType<LightReceiverBlockEntity> LIGHT_RECEIVER;

    public static void register() {
        SOLAR_PANEL = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(BabekonsSunPanels.MOD_ID, "solar_panel"),
                FabricBlockEntityTypeBuilder.create(SolarPanelBlockEntity::new, ModBlocks.EXAMPLE_BLOCK).build()
        );

        BATTERY = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(BabekonsSunPanels.MOD_ID, "battery"),
                FabricBlockEntityTypeBuilder.create(BatteryBlockEntity::new, ModBlocks.BATTERY).build()
        );

        CABLE = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(BabekonsSunPanels.MOD_ID, "cable"),
                FabricBlockEntityTypeBuilder.create(CableBlockEntity::new, ModBlocks.CABLE).build()
        );

        LIGHT_RECEIVER = Registry.register(
                Registries.BLOCK_ENTITY_TYPE,
                Identifier.of(BabekonsSunPanels.MOD_ID, "light_receiver"),
                FabricBlockEntityTypeBuilder.create(LightReceiverBlockEntity::new, ModBlocks.LIGHT_RECEIVER).build()
        );

        BabekonsSunPanels.LOGGER.info("Registered block entities for {}", BabekonsSunPanels.MOD_ID);
    }
}
