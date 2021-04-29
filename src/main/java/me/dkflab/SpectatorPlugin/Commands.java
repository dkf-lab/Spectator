package me.dkflab.SpectatorPlugin;

import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

public class Commands implements CommandExecutor {

    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (cmd.getName().equalsIgnoreCase("teammate")) {
            if (!(sender instanceof Player)) {
                Utils.notAPlayer(sender);
                return true;
            }
            if (args.length == 0 && Main.isInTeam((Player) sender)) {
                Utils.info(sender, "Your teammate is " + Main.getTeammate((Player) sender).getName());
                return true;
            }
            if (args.length != 1||args[0].equalsIgnoreCase("help")) {
                help(sender);
                return true;
            }
            if (args[0].equalsIgnoreCase("accept")) {
                if (Main.isInTeam((Player)sender)) {
                    Utils.warn(sender, "Leave your team before accepting a request.");
                    return true;
                }
                if (Main.checkRequest((Player)sender)) {
                    Utils.success(sender, "Accepted team request!");
                } else {
                    Utils.error(sender, "No incoming requests found.");
                }
                return true;
            }
            if (args[0].equalsIgnoreCase("leave")) {
                if (Main.leaveTeam((Player)sender)) {
                    Utils.success(sender, "Left team.");
                } else {
                    Utils.error(sender, "You are not in a team!");
                }
                return true;
            }
            //
            // /teammate <player>
            //
            // check if sender is trying to teammate themselves
            if (args[0].equals(sender.getName())) {
                Utils.error(sender, "You can't team with yourself!");
                return true;
            }
            for (Player all : Bukkit.getOnlinePlayers()) {
                if (all.getName().equals(args[0])) {
                    // check if already on a team
                    if (Main.isInTeam((Player) sender)) {
                        Utils.error(sender, "Leave your team before sending a new request!");
                        return true;
                    }
                    // check if already on same team
                    if (Main.areInTeam((Player) sender, all)) {
                        Utils.error(sender, "You are already in a team!");
                        return true;
                    }
                    // check if they already have a request from player
                    if (Main.requests != null && Main.requests.get(all) != null && Main.requests.get(all).equals(sender)) {
                        // pair as team
                        Main.team.put((Player) sender, all);
                        Utils.success(sender, "Teamed with " + args[0] + "!");
                        Utils.success(all, "Teamed with " + all.getName() + "!");
                        // remove request
                        Main.requests.remove(all);
                        return true;
                    }
                    // check if player has outgoing request
                    if (Main.requests.get(sender) != null) {
                        if (!Main.requests.get(sender).getName().equals(args[0])) {
                            Utils.warn(sender, "Removing request to "+ Main.requests.get((Player) sender) + " and sending one to " + args[0] + " instead.");
                            // no need to remove request from map as it will be overwritten
                        }
                    }
                    // send request
                    Main.requests.put((Player)sender, all);
                    Utils.success(sender, "Sent request to " + args[0] + "!");
                    Utils.info(all, "You have got a teammate request from " + sender.getName() + "!");
                    Utils.info(all, "Run &6/teammate accept &bto accept.");
                    Utils.info(all, "To deny, just ignore this message.");
                    return true;
                }
            }
            Utils.error(sender, "Please ensure that " + args[0] + " is a valid player name.");
        }
        return true;
    }

    private void help(CommandSender sender) {
        Utils.info(sender, "&6-=- Help Menu -=-");
        Utils.info(sender, "/teammate - Can be shortened to /team");
        Utils.info(sender, "/teammate <player> - Requests player to be your teammate.");
        Utils.info(sender, "/teammate accept - Accepts teammate request.");
        Utils.info(sender, "/teammate leave - Leave team.");
    }
}
