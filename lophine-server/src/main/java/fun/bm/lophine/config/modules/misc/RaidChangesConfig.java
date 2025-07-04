package fun.bm.lophine.config.modules.misc;

import me.earthme.luminol.config.EnumConfigCategory;
import me.earthme.luminol.config.IConfigModule;
import me.earthme.luminol.config.flags.ConfigInfo;

public class RaidChangesConfig implements IConfigModule {
    @ConfigInfo(baseName = "allow_skip_cooldown", comments =
            """
                    Allow players with bad omen to skip a
                    30-second cooldown and trigger attacks directly""")
    public static boolean trigger = false;

    @ConfigInfo(baseName = "give_bad_omen_when_kill_raid_captain", comments =
            """
                    Enable players to obtain a bad omen
                    effect when killing the raid captain""")
    public static boolean effect = false;

    @ConfigInfo(baseName = "bad_omen_infinite", comments =
            """
                    Enable bad omen effect infinite time
                        --- this config is not old version's function""")
    public static boolean infinite = false;

    @ConfigInfo(baseName = "skip_height_check", comments =
            """
                    Disable y <= 96 check.
                    If you enabled use_old_position_find, this config
                    will useless and always behavior of enabled""")
    public static boolean heightCheck = false;

    @ConfigInfo(baseName = "skip_self_raid_check", comments =
            """
                    Disable raid self check
                        --- this config is not old version's function""")
    public static boolean selfCheck = false;

    @ConfigInfo(baseName = "use_old_position_find", comments =
            """
                    Revert Old raid's find spawn position logic
                        --- This revert MC-274911""")
    public static boolean posRevert = false;

    @Override
    public EnumConfigCategory getCategory() {
        return EnumConfigCategory.MISC;
    }

    @Override
    public String getBaseName() {
        return "revert_raid_changes";
    }
}