package me.itstake.chviaversion;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.*;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.*;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import me.itstake.chviaversion.bossbar.BossBarFactory;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossStyle;

/**
 * Created by bexco on 2016-03-12.
 */
public class Functions {
    private static ViaAPI api = Via.getAPI();

    private static void checkPlugin(Target t) {
        if (!Bukkit.getPluginManager().isPluginEnabled("ViaVersion")) {
            throw new CREInvalidPluginException("Can't find ViVersion", t);
        }
    }

    public String docs() {
        return "Functions about ViaVersion API";
    }

    @api
    public static class via_get_player_version extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREInvalidPluginException.class,
                    CREPlayerOfflineException.class,
                    CRENotFoundException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return false;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target t, Environment environment, Construct... args) throws ConfigRuntimeException {
            checkPlugin(t);
            Player p = null;
            if(args.length == 0) {
                MCCommandSender m = environment.getEnv(CommandHelperEnvironment.class).GetCommandSender();
                if (m instanceof BukkitMCPlayer) {
                    p = ((BukkitMCPlayer) m)._Player();
                } else {
                    throw new CREPlayerOfflineException("Player is Offline or Not exist", t);
                }
            } else if(args.length == 1) {
                p = ((BukkitMCPlayer) Static.GetPlayer(args[0], t))._Player();
            }
            return new CInt(api.getPlayerVersion(p), t);
        }

        @Override
        public String getName() {
            return "via_get_player_version";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[]{0, 1};
        }

        @Override
        public String docs() {
            return "{[player]} version Returns a Protocol Version. Note that version is not minecraft version. It's a protocol version. You can find infomation of protocol version at http://wiki.vg/Protocol_version_numbers";
        }

        @Override
        public Version since() {
            return new SimpleVersion(1, 0, 0);
        }
    }

    @api
    public static class via_add_bossbar extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREFormatException.class,
                    CRECastException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target t, Environment e, Construct... c) throws ConfigRuntimeException {
            CString id = c[0].asString();
            if (!c[1].getCType().equals(Construct.ConstructType.ARRAY))
                throw new CREFormatException("Argument 2 is must be ViaBossBarArray", t);
            CArray barArray = (CArray) c[1];
            if (!barArray.isAssociative())
                throw new CREFormatException("Argument 2 is must be ViaBossBarArray", t);
            String title = "";
            BossColor color = BossColor.PURPLE;
            BossStyle style = BossStyle.SOLID;
            float health = 0;
            if (barArray.containsKey("title"))
                title = barArray.get("title", t).val();
            if (barArray.containsKey("color")) {
                try {
                    color = BossColor.valueOf(barArray.get("color", t).val());
                } catch (IllegalArgumentException ex) {
                    throw new CREFormatException("Argument color is must be one of color names.", t);
                }
            }
            if (barArray.containsKey("style")) {
                try {
                    style = BossStyle.valueOf(barArray.get("style", t).val());
                } catch (IllegalArgumentException ex) {
                    throw new CREFormatException("Argument style is must be one of style names.", t);
                }
            }
            if (barArray.containsKey("health")) {
                try {
                    health = Float.valueOf(barArray.get("health", t).val());
                } catch (NumberFormatException ex) {
                    throw new CRECastException("Argument health is must be integer", t);
                }
            }
            BossBarFactory.addBossBar(id.val(), api.createBossBar(title, health, color, style));
            if (barArray.containsKey("flags") && barArray.get("flags", t).getCType().equals(Construct.ConstructType.ARRAY) && ((CArray) barArray.get("flags", t)).isAssociative())
                try {
                    BossBarFactory.setFlags(id.val(), ((CArray) barArray.get("flags", t)), t);
                } catch (IllegalArgumentException ex) {
                    throw new CRECastException("Key of flags is must be exists.", t);
                } catch (CREIllegalArgumentException ex) {
                    throw new CREIllegalArgumentException("Bossbar is not exists", t);
                }
            return CBoolean.GenerateCBoolean(true, t);
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_add_bossbar";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[2];
        }

        @Override
        public String docs() {
            return "{id, ViaBossBarArray} Creates a ViaVersion API BossBar and register to an id, ViaBossBarArray contains title(string), color(string), style(string), health(int), flags(ViaBossBarFlagArray). For more information about ViaBossBarFlagArray, see via_set_bossbar_flags() function.";
        }
    }

    @api
    public static class via_remove_bossbar extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[0];
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target target, Environment environment, Construct... constructs) throws ConfigRuntimeException {
            CString id = constructs[0].asString();
            if (BossBarFactory.removeBossBar(id.val()))
                return CBoolean.TRUE;
            return CBoolean.FALSE;
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_remove_bossbar";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[1];
        }

        @Override
        public String docs() {
            return "{id} Removes a bossbar.";
        }
    }

    @api
    public static class via_add_bossbar_player extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREIllegalArgumentException.class,
                    CREPlayerOfflineException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target target, Environment environment, Construct... constructs) throws ConfigRuntimeException {
            CString id = constructs[0].asString();
            MCPlayer player = null;
            if (constructs.length == 1 && environment.getEnv(CommandHelperEnvironment.class).GetCommandSender() instanceof MCPlayer)
                player = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
            player = Static.GetPlayer(constructs[1], target);
            BossBarFactory.addPlayer(id.val(), player, target);
            return CVoid.VOID;
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_add_bossbar_player";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[2];
        }

        @Override
        public String docs() {
            return "{id, player} Add player to bossbar.";
        }
    }

    @api
    public static class via_remove_bossbar_player extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREIllegalArgumentException.class,
                    CREPlayerOfflineException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target target, Environment environment, Construct... constructs) throws ConfigRuntimeException {
            CString id = constructs[0].asString();
            MCPlayer player = null;
            if (constructs.length == 1 && environment.getEnv(CommandHelperEnvironment.class).GetCommandSender() instanceof MCPlayer)
                player = environment.getEnv(CommandHelperEnvironment.class).GetPlayer();
            player = Static.GetPlayer(constructs[1], target);
            BossBarFactory.removePlayer(id.val(), player, target);
            return CVoid.VOID;
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_remove_bossbar_player";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[2];
        }

        @Override
        public String docs() {
            return "{id, player} Removes player to bossbar.";
        }
    }

    @api
    public static class via_get_bossbar_players extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREIllegalArgumentException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target target, Environment environment, Construct... constructs) throws ConfigRuntimeException {
            CString id = constructs[0].asString();
            return BossBarFactory.getPlayers(id.val(), target);
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_get_bossbar_players";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[1];
        }

        @Override
        public String docs() {
            return "{id} Get registered players in bossbar.";
        }
    }

    @api
    public static class via_set_bossbar_visible extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREIllegalArgumentException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target target, Environment environment, Construct... constructs) throws ConfigRuntimeException {
            CString id = constructs[0].asString();
            CBoolean visible = (CBoolean) constructs[1];
            BossBarFactory.setVisible(id.val(), visible.getBoolean(), target);
            return CVoid.VOID;
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_set_bossbar_visible";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[2];
        }

        @Override
        public String docs() {
            return "{id, boolean} Toggle Bossbar visibility";
        }
    }

    @api
    public static class via_set_bossbar_options extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREFormatException.class,
                    CRECastException.class,
                    CREIllegalArgumentException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target target, Environment environment, Construct... constructs) throws ConfigRuntimeException {
            CString id = constructs[0].asString();
            CArray barArray = (CArray) constructs[1];
            try {
                BossBarFactory.setOptions(id.val(), barArray, target);
            } catch (IllegalArgumentException ex) {
                throw new CREFormatException("One of values is incorrect.", target);
            } catch (CREIllegalArgumentException ex) {
                throw new CREIllegalArgumentException("Bossbar is not exists", target);
            }
            return CVoid.VOID;
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_set_bossbar_options";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[2];
        }

        @Override
        public String docs() {
            return "{id, ViaBossBarArray} Set Bossbar options.";
        }
    }

    @api
    public static class via_get_bossbar_options extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREIllegalArgumentException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target target, Environment environment, Construct... constructs) throws ConfigRuntimeException {
            CString id = constructs[0].asString();
            return BossBarFactory.getOptions(id.val(), target);
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_get_bossbar_options";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[1];
        }

        @Override
        public String docs() {
            return "{id} Get selected Bossbar's options.";
        }
    }

    @api
    public static class via_set_bossbar_flags extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREFormatException.class,
                    CRECastException.class,
                    CREIllegalArgumentException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target target, Environment environment, Construct... constructs) throws ConfigRuntimeException {
            CString id = constructs[0].asString();
            CArray flagArray = (CArray) constructs[1];
            try {
                BossBarFactory.setFlags(id.val(), flagArray, target);
            } catch (IllegalArgumentException ex) {
                throw new CREFormatException("One of values is incorrect.", target);
            } catch (CREIllegalArgumentException ex) {
                throw new CREIllegalArgumentException("Bossbar is not exists", target);
            }
            return CVoid.VOID;
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_set_bossbar_flags";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[2];
        }

        @Override
        public String docs() {
            return "{id, ViaBossBarFlagArray} Set Bossbar flags. ViaBossBarFlagArray has ViaBossBarFlag as key, boolean as value. ViaBossBarFlag is must be one of DARKEN_SKY or PLAY_BOSS_MUSIC.";
        }
    }

    @api
    public static class via_has_bossbar_flag extends AbstractFunction {

        @Override
        public Class<? extends CREThrowable>[] thrown() {
            return new Class[]{
                    CREIllegalArgumentException.class
            };
        }

        @Override
        public boolean isRestricted() {
            return true;
        }

        @Override
        public Boolean runAsync() {
            return false;
        }

        @Override
        public Construct exec(Target target, Environment environment, Construct... constructs) throws ConfigRuntimeException {
            CString id = constructs[0].asString();
            CString flag = constructs[1].asString();
            if (BossBarFactory.hasFlag(id.val(), flag.val(), target))
                return CBoolean.TRUE;
            return CBoolean.FALSE;
        }

        @Override
        public Version since() {
            return new SimpleVersion(3, 3, 2);
        }

        @Override
        public String getName() {
            return "via_has_bossbar_flag";
        }

        @Override
        public Integer[] numArgs() {
            return new Integer[2];
        }

        @Override
        public String docs() {
            return "{id, ViaBossBarFlag} If Bossbar has a selected flag, return true.";
        }
    }
}
