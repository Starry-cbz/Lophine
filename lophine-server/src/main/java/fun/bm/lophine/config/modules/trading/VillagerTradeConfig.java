package fun.bm.lophine.config.modules.trading;

import fun.bm.lophine.config.Config;

public class VillagerTradeConfig extends Config {
    public static boolean disableVillagerTradeUses = false;

    public VillagerTradeConfig(String name) {
        super(name);
    }

    @Override
    public void init() {
        disableVillagerTradeUses = getBoolean("disable-villager-trade-uses", false);
    }
}