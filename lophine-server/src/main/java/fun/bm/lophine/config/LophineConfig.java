package fun.bm.lophine.config;

import me.earthme.luminol.config.LuminolConfig;

import java.io.File;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class LophineConfig {
    public static final Map<String, LuminolConfig> configfiles = new HashMap<>();
    private static final File luminolConfigFolder = new File("luminol_config");
    private static final File lophineConfigFolder = new File("lophine_config");

    public static void initConfigs() throws IOException {
        configfiles.put("luminol", new LuminolConfig(luminolConfigFolder, "luminol", "me.earthme.luminol.config.modules"));
        configfiles.put("lophine", new LuminolConfig(lophineConfigFolder, "lophine", "fun.bm.lophine.config.modules"));
        preLoad();
    }

    public static void preLoad() throws IOException {
        for (LuminolConfig config : configfiles.values()) {
            config.preLoadConfig();
        }
    }

    public static void loadConfigFiles() {
        for (LuminolConfig config : configfiles.values()) {
            config.finalizeLoadConfig();
            config.setupLatch();
        }
    }
}