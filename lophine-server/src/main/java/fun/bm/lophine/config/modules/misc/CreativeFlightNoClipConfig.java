package fun.bm.lophine.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;

public class CreativeFlightNoClipConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled", comments = "Allow players in creative mode to fly through blocks.")
    public static boolean enabled = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "creative_flight_noclip";
    }
} 