package me.dkflab.SpectatorPlugin;

import org.bukkit.Bukkit;
import org.bukkit.GameMode;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.PlayerDeathEvent;
import org.bukkit.event.player.PlayerMoveEvent;
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
            Player teammate = getTeammate(p);
            if (teammate.getGameMode().equals(GameMode.SPECTATOR)) {
                //both dead
                teammate.showPlayer(this, p);
                p.showPlayer(this, teammate);
                // send message to both
                p.sendTitle(Utils.color("&c&lYOU DIED!"),Utils.color("&7Better luck next time."),20,20,20);
                teammate.sendTitle(Utils.color("&c&lYOUR TEAMMATE DIED!"),Utils.color("&7You can freely spectate now."),20,20,20);
                p.teleport(teammate);
                return;
            }
            p.sendTitle(Utils.color("&c&lYOU DIED!"),Utils.color("&7Better luck next time."),20,20,20);
            p.setSpectatorTarget(getTeammate(p));
        }
    }

    @EventHandler
    public void onMove(PlayerMoveEvent e) {
        Player p = e.getPlayer();
        if (isInTeam(p)) {
            // spectator
            if (p.getGameMode().equals(GameMode.SPECTATOR)) {
                if (getTeammate(p).getGameMode().equals(GameMode.SPECTATOR)) {
                    return;
                }
                p.teleport(getTeammate(p));
                p.hidePlayer(this, getTeammate(p));
            }
            // spectating player moving
            if (getTeammate(p).getGameMode().equals(GameMode.SPECTATOR)) {
                getTeammate(p).teleport(p);
                getTeammate(p).hidePlayer(this, p);
            }
        }
    }
    // team methods

    public static boolean areInTeam(Player one, Player two) {
        if (team == null) {
            return false;
        }
        if (team.get(one)!=null&&team.get(one).equals(two)) {
            return true;
        }
        if (team.get(two)!=null&&team.get(two).equals(one)) {
            return true;
        }
        return false;
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
            if (team.get(two)!=null&&team.get(two).equals(p)) {
                return two;
            }
        }
        if (team.get(p) != null) {
            return team.get(p);
        }
        return null;
    }
}
