package babekon.sun;

import net.fabricmc.api.ModInitializer;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public class BabekonsSunPanels implements ModInitializer {
	public static final String MOD_ID = "babekons-sun-panels";

	public static final Logger LOGGER = LoggerFactory.getLogger(MOD_ID);

	@Override
	public void onInitialize() {

		ModBlocks.registerModBlocks();
		ModBlockEntities.register();
		ModItemGroups.registerItemGroups();
		LOGGER.info("{} initialized. Registered blocks.", MOD_ID);
	}
}