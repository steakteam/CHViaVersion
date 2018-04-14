package me.itstake.chprotocolsupport;

import com.laytonsmith.PureUtilities.SimpleVersion;
import com.laytonsmith.PureUtilities.Version;
import com.laytonsmith.core.extensions.AbstractExtension;
import com.laytonsmith.core.extensions.MSExtension;
import org.bukkit.Bukkit;

/**
 * Created by bexco on 2016-03-12.
 */
@MSExtension("CHProtocolSupport")
public class LifeCylcles extends AbstractExtension {

    @Override
    public Version getVersion() {
        return new SimpleVersion(1, 0, 0);
    }

    @Override
    public void onStartup() {
        Bukkit.getConsoleSender().sendMessage("[CommandHelper] CHProtocolSupport v" + getVersion().toString() + " Enabled.");
    }

    @Override
    public void onShutdown() {
        Bukkit.getConsoleSender().sendMessage("[CommandHelper] CHProtocolSupport v" + getVersion().toString() + " Disabled.");
    }
}
