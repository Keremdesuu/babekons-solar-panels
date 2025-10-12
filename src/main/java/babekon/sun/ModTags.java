package babekon.sun;

import net.minecraft.block.Block;
import net.minecraft.registry.RegistryKeys;
import net.minecraft.registry.tag.TagKey;
import net.minecraft.util.Identifier;

public final class ModTags {
    private ModTags() {}

    public static final TagKey<Block> KE_CONNECTABLE = TagKey.of(RegistryKeys.BLOCK,
            Identifier.of(BabekonsSunPanels.MOD_ID, "ke_connectable"));
}
