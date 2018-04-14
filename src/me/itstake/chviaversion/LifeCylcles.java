package me.itstake.chviaversion;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;
import org.bukkit.Bukkit;

/**
 * Created by bexco on 2016-03-12.
 */
@MSExtension("CHViaVersion")
public class LifeCylcles extends AbstractExtension {

    @Override
    public Version getVersion() {
        return new SimpleVersion(1, 0, 0);
    }

    @Override
    public void onStartup() {
        Bukkit.getConsoleSender().sendMessage("[CommandHelper] CHViaVersion v" + getVersion().toString() + " Enabled.");
    }

    @Override
    public void onShutdown() {
        Bukkit.getConsoleSender().sendMessage("[CommandHelper] CHViaVersion v" + getVersion().toString() + " Disabled.");
    }
}
