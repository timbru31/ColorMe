package de.dustplanet.colorme.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import de.dustplanet.colorme.Actions;
import de.dustplanet.colorme.ColorMe;

public class PrefixCommands implements CommandExecutor {
	private ColorMe plugin;
	private Actions actions;
	public PrefixCommands(ColorMe instance, Actions actionsInstance) {
		plugin = instance;
		actions = actionsInstance;
	}

	// Commands for prefixing
	public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		String message, globalPrefix, target, senderName, prefix = "", pluginPart = "prefix", world = "default";
		// Reloads the configs
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("prefixer.reload")) {
				actions.reload(sender);
			}
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Stop here if Prefixer is unwanted
		if (!plugin.Prefixer) {
			message = plugin.localization.getString("part_disabled");
			plugin.message(sender, null, message, null, null, null, null);
			return true;
		}
		// Displays the help
		if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
			actions.help(sender, "prefix");
			return true;
		}
		// Sets the global prefix
		if (args.length > 1 && args[0].equalsIgnoreCase("global")) {
			globalPrefix = args[1].replaceAll("_", " ");
			if (sender.hasPermission("prefixer.global")) {
				// If the prefixes are the same
				if (actions.replaceThings(globalPrefix).equalsIgnoreCase(actions.getGlobal("prefix"))) {
					message = plugin.localization.getString("same_prefix_global");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// If sender hasn't got the noFilter permission look if there are bad words in!
				if (plugin.blacklist && !sender.hasPermission("prefixer.nofilter")) {
					for (String s : plugin.bannedWords) {
						if (globalPrefix.contains(s)) {
							// Message, bad words in etc.
							message = plugin.localization.getString("bad_words");
							plugin.message(sender, null, message, s, null, null, null);
							return true;
						}
					}
				}
				// Check if the message is too long
				if (ChatColor.stripColor(actions.replaceThings(globalPrefix)).length() > plugin.prefixLength) {
					message = plugin.localization.getString("too_long");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				plugin.config.set("global_default.prefix", globalPrefix);
				plugin.globalPrefix = true;
				plugin.saveConfig();
				message = plugin.localization.getString("changed_prefix_global");
				plugin.message(sender, null, message, actions.replaceThings(globalPrefix), null, null, null);
			}
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Removes a prefix
		else if (args.length > 1 && args[0].equalsIgnoreCase("remove")) {
			// Removes the global prefix
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("prefixer.global")) {
					// Trying to remove an empty global prefix
					if (!actions.hasGlobal("prefix")) {
						message = plugin.localization.getString("no_prefix_global");
						plugin.message(sender, null, message, null, null, null, null);
						return true;
					}
					// Remove global prefix
					actions.removeGlobal("prefix");
					plugin.globalPrefix = false;
					message = plugin.localization.getString("removed_prefix_global");
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
			if (sender.hasPermission("prefixer.remove") || actions.self(sender, target)) {
				// Trying to remove a prefix from a prefix-less player
				if (((!actions.has(target, world, pluginPart) && plugin.players.contains(target + "." + pluginPart + "." + world))) || !plugin.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = plugin.localization.getString("no_prefix_self");
						plugin.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = plugin.localization.getString("no_prefix_other");
					plugin.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Remove prefix
				actions.remove(target, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Notify player is online
					Player player = plugin.getServer().getPlayerExact(target);
					message = plugin.localization.getString("removed_prefix_self");
					plugin.message(null, player, message, null, world, null, null);
				}
				// If player is offline just notify the sender
				if (!target.equalsIgnoreCase(senderName)) {
					message = plugin.localization.getString("removed_prefix_other");
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
		// Gets a prefix
		else if (args.length > 1 && args[0].equalsIgnoreCase("get")) {
			// Get the global prefix if set
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("prefixer.global")) {
					// Trying to get an empty global prefix
					if (!actions.hasGlobal("prefix")) {
						message = plugin.localization.getString("no_prefix_global");
						plugin.message(sender, null, message, null, null, null, null);
						return true;
					}
					prefix = actions.getGlobal("prefix");
					message = plugin.localization.getString("get_prefix_global");
					plugin.message(sender, null, message, actions.replaceThings(prefix), null, null, null);
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
			prefix = actions.get(target, world, pluginPart);
			// Check for permission or self
			if (sender.hasPermission("prefixer.get") || actions.self(sender, target)) {
				// Trying to get a prefix from a prefix-less player
				if (((!actions.has(target, world, pluginPart) && plugin.players.contains(target))) || !plugin.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = plugin.localization.getString("no_prefix_self");
						plugin.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = plugin.localization.getString("no_prefix_other");
					plugin.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Gets prefix
				if (target.equalsIgnoreCase(senderName)) {
					message = plugin.localization.getString("get_prefix_self");
					plugin.message(sender, null, message, actions.replaceThings(prefix), world, null, null);
					return true;
				}
				message = plugin.localization.getString("get_prefix_other");
				plugin.message(sender, null, message, actions.replaceThings(prefix), world, target, null);
			}
			// Deny access
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Prefixing
		else if (args.length > 1) {
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
			prefix = args[1].replaceAll("_", " ");
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) world = args[2].toLowerCase();
			// If the prefixes are the same
			if (actions.replaceThings(prefix).equalsIgnoreCase(actions.get(target, world, pluginPart))) {
				if (senderName.equalsIgnoreCase(target)) {
					message = plugin.localization.getString("same_prefix_self");
					plugin.message(sender, null, message, null, world, null, null);
					return true;
				}	
				message = plugin.localization.getString("same_prefix_other");
				plugin.message(sender, null, message, null, world, target, null);
				return true;
			}
			// Self prefixing
			if (sender.hasPermission("prefixer.self") && actions.self(sender, target)) {
				// If sender hasn't got the noFilter permission look if there are bad words in!
				if (plugin.blacklist && !sender.hasPermission("prefixer.nofilter")) {
					for (String s : plugin.bannedWords) {
						if (prefix.contains(s)) {
							// Message, bad words in etc.
							message = plugin.localization.getString("bad_words");
							plugin.message(sender, null, message, s, null, null, null);
							return true;
						}
					}
				}
				// Check if the message is too long
				if (ChatColor.stripColor(actions.replaceThings(prefix)).length() > plugin.prefixLength) {
					message = plugin.localization.getString("too_long");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Without economy or costs are null
				Double cost = plugin.config.getDouble("costs.prefix");
				if (plugin.economy == null || cost == 0) {
					actions.set(senderName, prefix, world, pluginPart);
					message = plugin.localization.getString("changed_prefix_self");
					plugin.message(sender, null, message, actions.replaceThings(prefix), world, null, null);
					return true;
				}
				// With economy
				else if (plugin.economy != null) {
					// Charge costs :)
					if (cost > 0) {
						// Charge player unless he has the free permissions
						if (!sender.hasPermission("prefixer.free")) {
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
						// Set prefix and notify sender
						actions.set(senderName, prefix, world, pluginPart);
						message = plugin.localization.getString("changed_prefix_self");
						plugin.message(sender, null, message, actions.replaceThings(prefix), world, null, null);
						return true;
					}
				}
			}
			// Prefixing other
			else if (sender.hasPermission("prefixer.other") && !actions.self(sender, target)) {
				// If sender hasn't got the noFilter permission look if there are bad words in!
				if (plugin.blacklist && !sender.hasPermission("prefixer.nofilter")) {
					for (String s : plugin.bannedWords) {
						if (prefix.contains(s)) {
							// Message, bad words in etc.
							message = plugin.localization.getString("bad_words");
							plugin.message(sender, null, message, s, null, null, null);
							return true;
						}
					}
				}
				// Check if the message is too long
				if (ChatColor.stripColor(actions.replaceThings(prefix)).length() > plugin.prefixLength) {
					message = plugin.localization.getString("too_long");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Set the new prefix
				actions.set(target, prefix, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Tell the affected player
					Player player = plugin.getServer().getPlayerExact(target);
					message = plugin.localization.getString("changed_prefix_self");
					plugin.message(null, player, message, actions.replaceThings(prefix), world, null, null);
				}
				message = plugin.localization.getString("changed_prefix_other");
				plugin.message(sender, null, message, actions.replaceThings(prefix), world, target, null);
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