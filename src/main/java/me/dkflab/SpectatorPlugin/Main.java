package me.dkflab.SpectatorPlugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.plugin.java.JavaPlugin;

import java.util.HashMap;

public final class Main extends JavaPlugin implements Listener {

    public static HashMap<Player, Player> team = new HashMap<>();
    public static HashMap<Player, Player> requests = new HashMap<>();

    @Override
    public void onEnable() {
        // Plugin startup logic
        getServer().getPluginManager().registerEvents(this, this);
        getCommand("teammate").setExecutor(new Commands());
    }

    // player death event
    @EventHandler
    public void onDeath(PlayerDeathEvent e) {
        Player p = e.getEntity();
        if (isInTeam(p)) {
            p.setGameMode(GameMode.SPECTATOR);
            p.setSpectatorTarget(getTeammate(p));
        }
    }

    // team methods

    public static boolean areInTeam(Player one, Player two) {
        if (team == null) {
            return false;
        }
        if (team.get(one).equals(two)) {
            return true;
        }
        return team.get(two).equals(one);
    }

    public static boolean isInTeam(Player p) {
        if (team.get(p) != null) {
            return true;
        }
        for (Player two:Bukkit.getOnlinePlayers()) {
            if (team.get(two) == p) {
                return true;
            }
        }
        return false;
    }

    public static boolean checkRequest(Player one) {
        if (requests == null) {
            return false;
        }
        for (Player two: Bukkit.getOnlinePlayers()) {
            if (requests.get(two) != null) {
                if (requests.get(two).equals(one)) {
                    Utils.success(two, one.getName() + " has accepted your team request!");
                    team.put(one, two);
                    return true;
                }
            }
        }
        return false;
    }

    public static boolean leaveTeam(Player p) {
        if (!isInTeam(p)) {
            return false;
        }
        for (Player all : Bukkit.getOnlinePlayers()) {
            if (team.get(all) != null) {
                if (team.get(all).equals(p)) {
                    team.remove(all);
                }
            }
        }
        team.remove(p);
        return true;
    }

    public static Player getTeammate(Player p) {
        for (Player two : Bukkit.getOnlinePlayers()) {
            if (team.get(two).equals(p)) {
                return two;
            }
        }
        if (team.get(p) != null) {
            return team.get(p);
        }
        return null;
    }
}
