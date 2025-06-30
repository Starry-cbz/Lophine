package fun.bm.lophine.config.modules.trading;

import fun.bm.lophine.config.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class VillagerTradeConfig extends Config {
    public static boolean disableVillagerTradeUses = false;

    public VillagerTradeConfig(@NotNull File file, @NotNull String name, @NotNull String version) {
        super(file, name, version);
    }

    @Override
    public void load() {
        FileConfiguration config = getConfig("config");
        if (config != null) {
            disableVillagerTradeUses = config.getBoolean("disable-villager-trade-uses", false);
        }
    }
}