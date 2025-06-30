package fun.bm.lophine.config.modules.trading;

import fun.bm.lophine.config.Config;
import org.bukkit.configuration.file.FileConfiguration;
import org.jetbrains.annotations.NotNull;
import java.io.File;

public class VillagerTradeConfig extends Config {
    public static boolean disableVillagerTradeUses = false;
    private FileConfiguration configuration;

    public VillagerTradeConfig(@NotNull File file, @NotNull String name, @NotNull String version) {
        super(file, name, version);
        this.configuration = getConfig();
    }

    // 加载配置文件
    public void load() {
        if (configuration != null) {
            disableVillagerTradeUses = configuration.getBoolean("disable-villager-trade-uses", false);
        }
    }
}