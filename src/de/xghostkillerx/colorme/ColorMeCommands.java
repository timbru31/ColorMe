package de.xghostkillerx.colorme;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles the commands. Heart of the plugin!
 * 
 * Refer to the forum thread:
 * http://bit.ly/colormebukkit
 * Refer to the dev.bukkit.org page:
 * http://bit.ly/bukkitdevcolorme
 *
 * @author xGhOsTkiLLeRx
 * @thanks to Valrix for the original ColorMe plugin!!
 * 
 */

public class ColorMeCommands implements CommandExecutor {
	ColorMe plugin;
	public ColorMeCommands(ColorMe instance) {
		plugin = instance;
	}
	private String pluginPart = "colors", message, target, color, senderName, world = "default";
	private Integer i;
	private Double cost;

	// Commands for coloring
	public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		// Returns the color list
		if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
			if (sender.hasPermission("colorme.list") || sender.hasPermission("prefixer.list") || sender.hasPermission("suffixer.list")) {
				Actions.listColors(sender);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		// Reloads the configs
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("colorme.reload")) {
				ColorMeReload(sender);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		// Stop here if ColorMe is unwanted
		if (ColorMe.config.getBoolean("ColorMe") == false) {
			message = ColorMe.localization.getString("part_disabled");
			Actions.message(sender, message);
			return true;
		}
		// Displays the help
		if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
			ColorMeHelp(sender);
			return true;
		}
		// Removes a color
		if (args.length > 1 && args[0].equalsIgnoreCase("remove")) {
			world = "default";
			target = args[1].toLowerCase();
			// Support for "me" -> this is the senderName!
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName().toLowerCase();
				// Tell console only ingame command
				if (sender instanceof ConsoleCommandSender) {
					message = ColorMe.localization.getString("only_ingame");
					Actions.message(sender, message);
					return true;
				}
			}
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) {
				world = args[2].toLowerCase();
			}
			// Check for permission or self
			if (sender.hasPermission("colorme.remove") || Actions.self(sender, target)) {
				// Trying to remove a color from a color-less player
				if (((!Actions.has(target, world, pluginPart) && ColorMe.players.contains(target + "." + pluginPart + "." + world)))
						|| !ColorMe.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = ColorMe.localization.getString("no_color_self");
						Actions.message(sender, message, world);
						return true;
					}
					// Other
					message = ColorMe.localization.getString("no_color_other");
					Actions.message(sender, message, world, target);
					return true;
				}
				// Remove color
				Actions.remove(target, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Notify player is online
					Player player = plugin.getServer().getPlayerExact(target);
					message = ColorMe.localization.getString("removed_color_self");
					Actions.messagePlayer(player, message, world);
				}
				// If player is offline just notify the sender
				if (!target.equalsIgnoreCase(senderName)) {
					message = ColorMe.localization.getString("removed_color_other");
					Actions.message(sender, message, world, target);
					return true;
				}
				return true;
			}
			// Deny access
			else {
				message = ColorMe.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		// Gets a color
		if (args.length > 1 && args[0].equalsIgnoreCase("get")) {
			world = "default";
			// If a player name is there, too
			target = args[1].toLowerCase();
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName().toLowerCase();
				// Tell console only ingame command
				if (sender instanceof ConsoleCommandSender) {
					message = ColorMe.localization.getString("only_ingame");
					Actions.message(sender, message);
					return true;
				}
			}
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) {
				world = args[2].toLowerCase();
			}
			Actions.get(target, world, pluginPart).toLowerCase();
			// Check for permission or self
			if (sender.hasPermission("colorme.get") || Actions.self(sender, target)) {
				// Trying to get a color from a color-less player
				if (((!Actions.has(target, world, pluginPart) && ColorMe.players.contains(target)))
						|| !ColorMe.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = ColorMe.localization.getString("no_color_self");
						Actions.message(sender, message, world);
						return true;
					}
					// Other
					message = ColorMe.localization.getString("no_color_other");
					Actions.message(sender, message, world, target);
					return true;
				}
				// Gets color
				if (target.equalsIgnoreCase(senderName)) {
					message = ColorMe.localization.getString("get_color_self");
					Actions.message(sender, message, world, color);
					return true;
				}
				message = ColorMe.localization.getString("get_color_other");
				Actions.message(sender, message, world, color, target);
				return true;
			}
			// Deny access
			else {
				message = ColorMe.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		// Coloring
		if (args.length > 1) {
			world = "default";
			target = args[0].toLowerCase();
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName().toLowerCase();
				// Tell console only ingame command
				if (sender instanceof ConsoleCommandSender) {
					message = ColorMe.localization.getString("only_ingame");
					Actions.message(sender, message);
					return true;
				}
			}
			color = args[1].toLowerCase();
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) {
				world = args[2].toLowerCase();
			}

			// Unsupported colors
			if (Actions.validColor(color) == false) {
				message = ColorMe.localization.getString("invalid_color");
				Actions.message(sender, message, color);
				return true;
			}

			// If color is disabled
			if (Actions.isDisabled(color) == true) {
				message = ColorMe.localization.getString("disabled_color");
				Actions.message(sender, message, color);
				return true;
			}

			// If the colors are the same
			if (color.equalsIgnoreCase(Actions.get(target, world, pluginPart))) {
				if (senderName.equalsIgnoreCase(target)) {
					message = ColorMe.localization.getString("same_color_self");
					Actions.message(sender, message, world);
					return true;
				}	
				message = ColorMe.localization.getString("same_color_other");
				Actions.message(sender, message, world, target);
				return true;
			}

			// Self coloring
			if (sender.hasPermission("colorme.self." + color.toLowerCase()) && Actions.self(sender, target)) {
				// Without economy or costs are null
				cost = ColorMe.config.getDouble("costs.color");
				if (ColorMe.economy == null || cost == 0) {
					Actions.set(senderName, color, world, pluginPart);
					message = ColorMe.localization.getString("changed_color_self");
					Actions.message(sender, message, world, color);
					return true;
				}
				// With economy
				else if (ColorMe.economy != null){
					if (color.equalsIgnoreCase("white")) {
						Actions.set(senderName, color, world, pluginPart);
						message = ColorMe.localization.getString("changed_color_self");
						Actions.message(sender, message, world, color);
						return true;
					}
					// Charge costs :)
					if (cost > 0 && ColorMe.economy.has(senderName, cost)) {
						ColorMe.economy.withdrawPlayer(senderName, cost);
						// Set color an notify sender
						Actions.set(senderName, color, world, pluginPart);
						message = ColorMe.localization.getString("charged");
						Actions.message(sender, message, cost);
						message = ColorMe.localization.getString("changed_color_self");
						Actions.message(sender, message, world, color);
						return true;
					}
					// If player hasn't got enough money
					else if (cost > 0 && ColorMe.economy.getBalance(senderName) < cost) {						
						message = ColorMe.localization.getString("not_enough_money_1");
						Actions.message(sender, message);
						message = ColorMe.localization.getString("not_enough_money_2");
						Actions.message(sender, message, cost);
						return true;
					}
				}
			}
			// Coloring other
			else if (sender.hasPermission("colorme.other") && !Actions.self(sender, target)) {
				// Set the new color
				Actions.set(target, color, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Tell the affected player
					Player player = plugin.getServer().getPlayerExact(target);
					message = ColorMe.localization.getString("changed_color_self");
					Actions.messagePlayer(player, message, world, color);
				}
				message = ColorMe.localization.getString("changed_color_other");
				Actions.message(sender, message, world, color, target);
				return true;
			}
			// Permission check failed
			else {
				message = ColorMe.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		return false;
	}

	// Reloads the config with /colorme reload
	private boolean ColorMeReload(CommandSender sender) {
		plugin.loadConfigsAgain();		
		message = ColorMe.localization.getString("reload");
		Actions.message(sender, message);
		return true;
	}

	// Displays the help with /colorme help
	private boolean ColorMeHelp(CommandSender sender) {
		for (i = 1; i <= 8; i++) {
			message = ColorMe.localization.getString("help_color_" + Integer.toString(i));
			Actions.message(sender, message);
		}
		return true;
	}
}
