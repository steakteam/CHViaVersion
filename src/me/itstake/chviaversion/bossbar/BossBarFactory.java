package me.itstake.chviaversion.bossbar;

import com.laytonsmith.abstraction.MCPlayer;
import com.laytonsmith.core.Static;
import com.laytonsmith.core.constructs.CArray;
import com.laytonsmith.core.constructs.CBoolean;
import com.laytonsmith.core.constructs.CString;
import com.laytonsmith.core.constructs.Target;
import com.laytonsmith.core.exceptions.CRE.CREIllegalArgumentException;
import us.myles.ViaVersion.api.boss.BossBar;
import us.myles.ViaVersion.api.boss.BossColor;
import us.myles.ViaVersion.api.boss.BossFlag;
import us.myles.ViaVersion.api.boss.BossStyle;

import java.util.HashMap;

public class BossBarFactory {
    private static HashMap<String, BossBar> bossMap = new HashMap<>();

    public static boolean addBossBar(String id, BossBar bar) {
        bossMap.put(id, bar);
        return true;
    }

    public static boolean removeBossBar(String id) {
        if (!bossMap.containsKey(id))
            return false;
        bossMap.get(id).hide();
        bossMap.remove(id);
        return true;
    }

    public static boolean addPlayer(String id, MCPlayer p, Target t) throws CREIllegalArgumentException {
        if (!bossMap.containsKey(id))
            throw new CREIllegalArgumentException("Bossbar is not exists", t);
        bossMap.get(id).addPlayer(p.getUniqueId());
        return true;
    }

    public static boolean removePlayer(String id, MCPlayer p, Target t) throws CREIllegalArgumentException {
        if (!bossMap.containsKey(id))
            throw new CREIllegalArgumentException("Bossbar is not exists", t);
        if (!bossMap.get(id).getPlayers().contains(p.getUniqueId()))
            return false;
        bossMap.get(id).removePlayer(p.getUniqueId());
        return true;
    }

    public static CArray getPlayers(String id, Target t) throws CREIllegalArgumentException {
        if (!bossMap.containsKey(id))
            throw new CREIllegalArgumentException("Bossbar is not exists", t);
        CArray players = new CArray(t);
        bossMap.get(id).getPlayers().forEach(uuid -> {
            players.push(new CString(Static.GetPlayer(uuid.toString(), t).getName(), t), t);
        });
        return players;
    }

    public static boolean setVisible(String id, boolean isVisible, Target t) throws CREIllegalArgumentException {
        if (!bossMap.containsKey(id))
            throw new CREIllegalArgumentException("Bossbar is not exists", t);
        if (isVisible) {
            bossMap.get(id).show();
        } else {
            bossMap.get(id).hide();
        }
        return true;
    }

    public static boolean setOptions(String id, CArray options, Target t) throws CREIllegalArgumentException {
        if (!bossMap.containsKey(id))
            throw new CREIllegalArgumentException("Bossbar is not exists", t);
        BossBar bar = bossMap.get(id);
        if (options.containsKey("title"))
            bar.setTitle(options.get("title", t).val());
        if (options.containsKey("color"))
            bar.setColor(BossColor.valueOf(options.get("color", t).val().toUpperCase()));
        if (options.containsKey("style"))
            bar.setStyle(BossStyle.valueOf(options.get("style", t).val().toUpperCase()));
        if (options.containsKey("health"))
            bar.setHealth(Float.valueOf(options.get("health", t).val()));
        return true;
    }

    public static CArray getOptions(String id, Target t) throws CREIllegalArgumentException {
        if (!bossMap.containsKey(id))
            throw new CREIllegalArgumentException("Bossbar is not exists", t);
        BossBar bar = bossMap.get(id);
        CArray ret = new CArray(t);
        ret.set("title", bar.getTitle());
        ret.set("color", bar.getColor().name());
        ret.set("style", bar.getStyle().name());
        ret.set("health", bar.getHealth() + "");
        return ret;
    }

    public static boolean setFlags(String id, CArray flags, Target t) throws CREIllegalArgumentException, IllegalArgumentException {
        if (!bossMap.containsKey(id))
            throw new CREIllegalArgumentException("Bossbar is not exists", t);
        BossBar bar = bossMap.get(id);
        flags.keySet().forEach(flag -> {
            if (((CBoolean) flags.get(flag, t)).getBoolean()) {
                bar.addFlag(BossFlag.valueOf(flag.val().toUpperCase()));
            } else {
                bar.removeFlag(BossFlag.valueOf(flag.val().toUpperCase()));
            }
        });
        return true;
    }

    public static boolean hasFlag(String id, String flag, Target t) throws CREIllegalArgumentException {
        if (!bossMap.containsKey(id))
            throw new CREIllegalArgumentException("Bossbar is not exists", t);
        BossBar bar = bossMap.get(id);
        return bar.hasFlag(BossFlag.valueOf(flag.toUpperCase()));
    }


}
