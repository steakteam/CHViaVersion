package me.itstake.chviaversion;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.abstraction.MCCommandSender;
import com.laytonsmith.abstraction.bukkit.entities.BukkitMCPlayer;
import com.laytonsmith.annotations.api;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CInt;
import com.laytonsmith.core.constructs.Construct;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.environments.CommandHelperEnvironment;
import com.laytonsmith.core.environments.Environment;
import com.laytonsmith.core.exceptions.CRE.CREInvalidPluginException;
import com.laytonsmith.core.exceptions.CRE.CRENotFoundException;
import com.laytonsmith.core.exceptions.CRE.CREPlayerOfflineException;
import com.laytonsmith.core.exceptions.CRE.CREThrowable;
import com.laytonsmith.core.exceptions.ConfigRuntimeException;
import com.laytonsmith.core.functions.AbstractFunction;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import us.myles.ViaVersion.api.Via;
import us.myles.ViaVersion.api.ViaAPI;

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
}
