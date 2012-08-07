package de.dustplanet.colorme;

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

	// Commands for coloring
	public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		String pluginPart = "colors", message, target, color, senderName, world = "default", globalColor, colorArray;
		// Returns the color list
		if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
			if (sender.hasPermission("colorme.list") || sender.hasPermission("prefixer.list") || sender.hasPermission("suffixer.list")) {
				Actions.listColors(sender);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		// Reloads the configs
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("colorme.reload")) {
				Actions.reload(sender);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		// Stop here if ColorMe is unwanted
		if (ColorMe.config.getBoolean("ColorMe.displayName") == false && ColorMe.config.getBoolean("ColorMe.tabList") == false && ColorMe.config.getBoolean("ColorMe.playerTitle") == false) {
			message = ColorMe.localization.getString("part_disabled");
			ColorMe.message(sender, null, message, null, null, null, null);
			return true;
		}
		// Displays the help
		if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
			Actions.help(sender, "color");
			return true;
		}
		// Sets the global color
		if (args.length > 1 && args[0].equalsIgnoreCase("global")) {
			globalColor = args[1];
			if (sender.hasPermission("colorme.global")) {
				// If the colors are the same
				if (globalColor.equalsIgnoreCase(Actions.getGlobal("color"))) {
					message = ColorMe.localization.getString("same_color_global");
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Else set the global color
				ColorMe.config.set("global_default.color", globalColor);
				ColorMe.globalColor = true;
				plugin.saveConfig();
				message = ColorMe.localization.getString("changed_color_global");
				ColorMe.message(sender, null, message, globalColor, null, null, null);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		// Removes a color
		if (args.length > 1 && args[0].equalsIgnoreCase("remove")) {
			world = "default";
			// Removes the global color
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("colorme.global")) {
					// Trying to remove an empty global color
					if (!Actions.hasGlobal("color")) {
						message = ColorMe.localization.getString("no_color_global");
						ColorMe.message(sender, null, message, null, null, null, null);
						return true;
					}
					// Remove global color
					Actions.removeGlobal("color");
					ColorMe.globalColor = false;
					message = ColorMe.localization.getString("removed_color_global");
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Deny access
				else {
					message = ColorMe.localization.getString("permission_denied");
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
			}
			target = args[1].toLowerCase();
			// Support for "me" -> this is the senderName!
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName().toLowerCase();
				// Tell console only ingame command
				if (sender instanceof ConsoleCommandSender) {
					message = ColorMe.localization.getString("only_ingame");
					ColorMe.message(sender, null, message, null, null, null, null);
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
						ColorMe.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = ColorMe.localization.getString("no_color_other");
					ColorMe.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Remove color
				Actions.remove(target, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Notify player is online
					Player player = plugin.getServer().getPlayerExact(target);
					message = ColorMe.localization.getString("removed_color_self");
					ColorMe.message(null, player, message, null, world, null, null);
				}
				// If player is offline just notify the sender
				if (!target.equalsIgnoreCase(senderName)) {
					message = ColorMe.localization.getString("removed_color_other");
					ColorMe.message(sender, null, message, null, world, target, null);
					return true;
				}
				return true;
			}
			// Deny access
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		// Gets a color
		if (args.length > 1 && args[0].equalsIgnoreCase("get")) {
			world = "default";
			// Get the global color if set
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("colorme.global")) {
					// Trying to get an empty global color
					if (!Actions.hasGlobal("color")) {
						message = ColorMe.localization.getString("no_color_global");
						ColorMe.message(sender, null, message, null, null, null, null);
						return true;
					}
					color = Actions.getGlobal("color");
					message = ColorMe.localization.getString("get_color_global");
					ColorMe.message(sender, null, message, color, null, null, null);
					return true;
				}
				// Deny access
				else {
					message = ColorMe.localization.getString("permission_denied");
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
			}
			// If a player name is there, too			
			target = args[1].toLowerCase();
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName().toLowerCase();
				// Tell console only ingame command
				if (sender instanceof ConsoleCommandSender) {
					message = ColorMe.localization.getString("only_ingame");
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
			}
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) {
				world = args[2].toLowerCase();
			}
			// Check for permission or self
			if (sender.hasPermission("colorme.get") || Actions.self(sender, target)) {
				// Trying to get a color from a color-less player
				if (((!Actions.has(target, world, pluginPart) && ColorMe.players.contains(target))) || !ColorMe.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = ColorMe.localization.getString("no_color_self");
						ColorMe.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = ColorMe.localization.getString("no_color_other");
					ColorMe.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Gets color
				color = Actions.get(target, world, pluginPart).toLowerCase();
				if (target.equalsIgnoreCase(senderName)) {
					message = ColorMe.localization.getString("get_color_self");
					ColorMe.message(sender, null, message, color, world, null, null);
					return true;
				}
				message = ColorMe.localization.getString("get_color_other");
				ColorMe.message(sender, null, message, color, world, target, null);
				return true;
			}
			// Deny access
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
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
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
			}
			// Is a world explicit named? Also lower case
			color = args[1].toLowerCase();
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) {
				world = args[2].toLowerCase();
			}
			String[] colors = color.split("-");
			// If the colors are the same
			if (color.equalsIgnoreCase(Actions.get(target, world, pluginPart))) {
				if (senderName.equalsIgnoreCase(target)) {
					message = ColorMe.localization.getString("same_color_self");
					ColorMe.message(sender, null, message, null, world, null, null);
					return true;
				}
				message = ColorMe.localization.getString("same_color_other");
				ColorMe.message(sender, null, message, null, world, target, null);
				return true;
			}
			// Iterate through the different colors
			for (String colorPart : colors) {
				// Unsupported colors
				if (Actions.validColor(colorPart) == false) {
					message = ColorMe.localization.getString("invalid_color");
					ColorMe.message(sender, null, message, colorPart, null, null, null);
					return true;
				}

				// If color is disabled
				if (Actions.isDisabled(colorPart) == true) {
					message = ColorMe.localization.getString("disabled_color");
					ColorMe.message(sender, null, message, colorPart, null, null, null);
					return true;
				}
			}

			// Check for name of the color
			String colorName = color.toLowerCase();
			if (ColorMe.colors.contains(color) && (ColorMe.colors.getString(color).trim().length() > 1 ? true : false) == true) colorName = "custom";
			if (color.contains("-")) {
				colorName = "mixed";
				// If color is disabled
				if (Actions.isDisabled(colorName) == true) {
					message = ColorMe.localization.getString("disabled_color");
					ColorMe.message(sender, null, message, colorName, null, null, null);
					return true;
				}
			}

			// Self coloring
			if (sender.hasPermission("colorme.self." + colorName) && Actions.self(sender, target)) {
				// Without economy or costs are null
				Double cost = ColorMe.config.getDouble("costs.color");
				if (plugin.economy == null || cost == 0) {
					Actions.set(senderName, color, world, pluginPart);
					message = ColorMe.localization.getString("changed_color_self");
					ColorMe.message(sender, null, message, color, world, null, null);
					return true;
				}
				// With economy
				else if (plugin.economy != null) {
					if (color.equalsIgnoreCase("white")) {
						Actions.set(senderName, color, world, pluginPart);
						message = ColorMe.localization.getString("changed_color_self");
						ColorMe.message(sender, null, message, color, world, null, null);
						return true;
					}
					// Charge costs :)
					if (cost > 0) {
						// Charge player unless he has the free permissions
						if (!sender.hasPermission("colorme.free")) {
							// Enough money?
							if (plugin.economy.getBalance(senderName) < cost) {
								// Tell and return
								message = ColorMe.localization.getString("not_enough_money_1");
								ColorMe.message(sender, null, message, null, null, null, null);
								message = ColorMe.localization.getString("not_enough_money_2");
								ColorMe.message(sender, null, message, null, null, null, cost);
								return true;
							}
							else {
								plugin.economy.withdrawPlayer(senderName, cost);
								message = ColorMe.localization.getString("charged");
								ColorMe.message(sender, null, message, null, null, null, cost);
							}
						}
						// Set color and notify sender
						Actions.set(senderName, color, world, pluginPart);
						message = ColorMe.localization.getString("changed_color_self");
						ColorMe.message(sender, null, message, color, world, null, null);
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
					ColorMe.message(null, player, message, color, world, null, null);
				}
				message = ColorMe.localization.getString("changed_color_other");
				ColorMe.message(sender, null, message, color, world, target, null);
				return true;
			}
			// Permission check failed
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		return false;
	}
}