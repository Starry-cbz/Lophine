package fun.bm.lophine.config.modules.trading;

import me.earthme.luminol.config.LuminolConfig;
import org.bukkit.configuration.file.FileConfiguration;
public class VillagerTradeConfig extends LuminolConfig {
    public static boolean disableVillagerTradeUses = false;

    public VillagerTradeConfig(String name, int version) {
        super(name, version);
    }

    public void load() {
        FileConfiguration config = getConfig();
        if (config != null) {
            disableVillagerTradeUses = config.getBoolean("disable-villager-trade-uses", false);
        }
    }
}