package me.dkflab.SpectatorPlugin;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;

public class Utils {

    public static String prefix = "&f&l[&6&l!&f&l]&r ";

    public static void notAPlayer(CommandSender sender) {
        error(sender, "You must be a player to run this command!");
    }

    public static void error(CommandSender sender, String s) {
        sender.sendMessage(color(prefix + "&4&l[ERROR] &c" + s));
    }

    public static void warn(CommandSender sender, String s) {
        sender.sendMessage(color(prefix + "&c&l[WARN] &r&c" + s));
    }

    public static void info(CommandSender sender, String s) {
        sender.sendMessage(color(prefix + "&b" + s));
    }

    public static void success(CommandSender sender, String s) {
        sender.sendMessage(color(prefix + "&a" + s));
    }

    public static String color(String s) {
        return ChatColor.translateAlternateColorCodes('&',s);
    }
}
