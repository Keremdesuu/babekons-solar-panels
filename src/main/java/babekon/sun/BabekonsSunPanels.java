package babekon.sun;

import net.fabricmc.api.ModInitializer;
import babekon.sun.energy.KeApi;
import babekon.sun.inventory.ItemApi;
import babekon.sun.brain.BrainApi;

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
		KeApi.register();
	// Expose ItemStorage providers
	ItemApi.LOOKUP.registerForBlockEntities((be, side) -> (be instanceof babekon.sun.inventory.ItemStorage s) ? s : null,
		ModBlockEntities.SERVER,
		ModBlockEntities.COMPUTER
	);
	BrainApi.LOOKUP.registerForBlockEntities((be, side) -> (be instanceof babekon.sun.brain.ServerBrain b) ? b : null,
		ModBlockEntities.SERVER_BRAIN
	);
		LOGGER.info("{} initialized. Registered blocks.", MOD_ID);
	}
}