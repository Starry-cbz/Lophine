package fun.bm.lophine.config.modules.experiment;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;

public class RayTrackingEntityTrackerConfig implements IConfigModule {
    @ConfigInfo(baseName = "enabled")
    public static boolean enabled = false;
    @ConfigInfo(baseName = "skip_marker_armor_stands")
    public static boolean skipMarkerArmorStands = true;
    @ConfigInfo(baseName = "check_interval_ms")
    public static int checkIntervalMs = 10;
    @ConfigInfo(baseName = "tracing_distance")
    public static int tracingDistance = 48;
    @ConfigInfo(baseName = "hitbox_limit")
    public static int hitboxLimit = 50;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.EXPERIMENT;
    }

    @Override
    public String getBaseName() {
        return "ray_tracking_entity_tracker";
    }
}