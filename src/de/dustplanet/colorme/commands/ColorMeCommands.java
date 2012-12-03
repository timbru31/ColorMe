package de.dustplanet.colorme.commands;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.dustplanet.colorme.Actions;
import de.dustplanet.colorme.ColorMe;

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
	private ColorMe plugin;
	private Actions actions;
	public ColorMeCommands(ColorMe instance, Actions actionsInstance) {
		plugin = instance;
		actions = actionsInstance;
	}

	// Commands for coloring
	public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		String pluginPart = "colors", message, target, color, senderName, world = "default", globalColor;
		// Returns the color list
		if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
			if (sender.hasPermission("colorme.list") || sender.hasPermission("prefixer.list") || sender.hasPermission("suffixer.list") || sender.hasPermission("groups.list")) {
				actions.listColors(sender);
			}
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Reloads the configs
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("colorme.reload")) {
				actions.reload(sender);
			}
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Stop here if ColorMe is unwanted
		if (!plugin.displayName && !plugin.tabList && !plugin.playerTitle && !plugin.playerTitleWithoutSpout) {
			message = plugin.localization.getString("part_disabled");
			plugin.message(sender, null, message, null, null, null, null);
			return true;
		}
		// Displays the help
		if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
			actions.help(sender, "color");
			return true;
		}
		// Sets the global color
		if (args.length > 1 && args[0].equalsIgnoreCase("global")) {
			globalColor = args[1];
			if (sender.hasPermission("colorme.global")) {
				// If the colors are the same
				if (globalColor.equalsIgnoreCase(actions.getGlobal("color"))) {
					message = plugin.localization.getString("same_color_global");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Else set the global color
				plugin.config.set("global_default.color", globalColor);
				plugin.globalColor = true;
				plugin.saveConfig();
				message = plugin.localization.getString("changed_color_global");
				plugin.message(sender, null, message, globalColor, null, null, null);
			}
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Removes a color
		if (args.length > 1 && args[0].equalsIgnoreCase("remove")) {
			// Removes the global color
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("colorme.global")) {
					// Trying to remove an empty global color
					if (!actions.hasGlobal("color")) {
						message = plugin.localization.getString("no_color_global");
						plugin.message(sender, null, message, null, null, null, null);
						return true;
					}
					// Remove global color
					actions.removeGlobal("color");
					plugin.globalColor = false;
					message = plugin.localization.getString("removed_color_global");
					plugin.message(sender, null, message, null, null, null, null);
				}
				// Deny access
				else {
					message = plugin.localization.getString("permission_denied");
					plugin.message(sender, null, message, null, null, null, null);
				}
				return true;
			}
			target = args[1].toLowerCase();
			// Support for "me" -> this is the senderName!
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName().toLowerCase();
				// Tell console only ingame command
				if (sender instanceof ConsoleCommandSender) {
					message = plugin.localization.getString("only_ingame");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
			}
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) world = args[2].toLowerCase();
			// Check for permission or self
			if (sender.hasPermission("colorme.remove") || actions.self(sender, target)) {
				// Trying to remove a color from a color-less player
				if (((!actions.has(target, world, pluginPart) && plugin.players.contains(target + "." + pluginPart + "." + world))) || !plugin.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = plugin.localization.getString("no_color_self");
						plugin.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = plugin.localization.getString("no_color_other");
					plugin.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Remove color
				actions.remove(target, world, pluginPart);
				actions.checkNames(target, world);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Notify player is online
					Player player = plugin.getServer().getPlayerExact(target);
					message = plugin.localization.getString("removed_color_self");
					plugin.message(null, player, message, null, world, null, null);
				}
				// If player is offline just notify the sender
				if (!target.equalsIgnoreCase(senderName)) {
					message = plugin.localization.getString("removed_color_other");
					plugin.message(sender, null, message, null, world, target, null);
				}
			}
			// Deny access
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Gets a color
		if (args.length > 1 && args[0].equalsIgnoreCase("get")) {
			// Get the global color if set
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("colorme.global")) {
					// Trying to get an empty global color
					if (!actions.hasGlobal("color")) {
						message = plugin.localization.getString("no_color_global");
						plugin.message(sender, null, message, null, null, null, null);
						return true;
					}
					color = actions.getGlobal("color");
					message = plugin.localization.getString("get_color_global");
					plugin.message(sender, null, message, color, null, null, null);

				}
				// Deny access
				else {
					message = plugin.localization.getString("permission_denied");
					plugin.message(sender, null, message, null, null, null, null);
				}
				return true;
			}
			// If a player name is there, too			
			target = args[1].toLowerCase();
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName().toLowerCase();
				// Tell console only ingame command
				if (sender instanceof ConsoleCommandSender) {
					message = plugin.localization.getString("only_ingame");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
			}
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) world = args[2].toLowerCase();
			// Check for permission or self
			if (sender.hasPermission("colorme.get") || actions.self(sender, target)) {
				// Trying to get a color from a color-less player
				if (((!actions.has(target, world, pluginPart) && plugin.players.contains(target))) || !plugin.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = plugin.localization.getString("no_color_self");
						plugin.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = plugin.localization.getString("no_color_other");
					plugin.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Gets color
				color = actions.get(target, world, pluginPart).toLowerCase();
				if (target.equalsIgnoreCase(senderName)) {
					message = plugin.localization.getString("get_color_self");
					plugin.message(sender, null, message, color, world, null, null);
					return true;
				}
				message = plugin.localization.getString("get_color_other");
				plugin.message(sender, null, message, color, world, target, null);
			}
			// Deny access
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Coloring
		if (args.length > 1) {
			target = args[0].toLowerCase();
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName().toLowerCase();
				// Tell console only ingame command
				if (sender instanceof ConsoleCommandSender) {
					message = plugin.localization.getString("only_ingame");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
			}
			// Is a world explicit named? Also lower case
			color = args[1].toLowerCase();
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) world = args[2].toLowerCase();
			String[] colors = color.split("-");
			// If the colors are the same
			if (color.equalsIgnoreCase(actions.get(target, world, pluginPart))) {
				if (senderName.equalsIgnoreCase(target)) {
					message = plugin.localization.getString("same_color_self");
					plugin.message(sender, null, message, null, world, null, null);
					return true;
				}
				message = plugin.localization.getString("same_color_other");
				plugin.message(sender, null, message, null, world, target, null);
				return true;
			}
			// Iterate through the different colors
			for (String colorPart : colors) {
				// Unsupported colors
				if (!actions.validColor(colorPart)) {
					message = plugin.localization.getString("invalid_color");
					plugin.message(sender, null, message, colorPart, null, null, null);
					return true;
				}

				// If color is disabled
				if (actions.isDisabled(colorPart)) {
					message = plugin.localization.getString("disabled_color");
					plugin.message(sender, null, message, colorPart, null, null, null);
					return true;
				}
			}
			// Check for name of the color
			String colorName = color.toLowerCase();
			if (plugin.colors.contains(color) && (plugin.colors.getString(color).trim().length() > 1 ? true : false) == true) colorName = "custom";
			if (color.contains("-")) {
				colorName = "mixed";
				// If color is disabled
				if (actions.isDisabled(colorName)) {
					message = plugin.localization.getString("disabled_color");
					plugin.message(sender, null, message, colorName, null, null, null);
					return true;
				}
			}

			// Self coloring
			if (sender.hasPermission("colorme.self." + colorName) && actions.self(sender, target)) {
				// Without economy or costs are null
				Double cost = plugin.config.getDouble("costs.color");
				if (plugin.economy == null || cost == 0) {
					actions.set(senderName, color, world, pluginPart);
					actions.checkNames(senderName, world);
					message = plugin.localization.getString("changed_color_self");
					plugin.message(sender, null, message, color, world, null, null);
					return true;
				}
				// With economy
				else if (plugin.economy != null) {
					if (color.equalsIgnoreCase("white")) {
						actions.set(senderName, color, world, pluginPart);
						actions.checkNames(senderName, world);
						message = plugin.localization.getString("changed_color_self");
						plugin.message(sender, null, message, color, world, null, null);
						return true;
					}
					// Charge costs :)
					if (cost > 0) {
						// Charge player unless he has the free permissions
						if (!sender.hasPermission("colorme.free")) {
							// Enough money?
							if (plugin.economy.getBalance(senderName) < cost) {
								// Tell and return
								message = plugin.localization.getString("not_enough_money_1");
								plugin.message(sender, null, message, null, null, null, null);
								message = plugin.localization.getString("not_enough_money_2");
								plugin.message(sender, null, message, null, null, null, cost);
								return true;
							}
							else {
								plugin.economy.withdrawPlayer(senderName, cost);
								message = plugin.localization.getString("charged");
								plugin.message(sender, null, message, null, null, null, cost);
							}
						}
						// Set color and notify sender
						actions.set(senderName, color, world, pluginPart);
						actions.checkNames(senderName, world);
						message = plugin.localization.getString("changed_color_self");
						plugin.message(sender, null, message, color, world, null, null);
						return true;
					}
				}
			}
			// Coloring other
			else if (sender.hasPermission("colorme.other") && !actions.self(sender, target)) {
				// Set the new color
				actions.set(target, color, world, pluginPart);
				actions.checkNames(target, world);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Tell the affected player
					Player player = plugin.getServer().getPlayerExact(target);
					message = plugin.localization.getString("changed_color_self");
					plugin.message(null, player, message, color, world, null, null);
				}
				message = plugin.localization.getString("changed_color_other");
				plugin.message(sender, null, message, color, world, target, null);
			}
			// Permission check failed
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		return false;
	}
}