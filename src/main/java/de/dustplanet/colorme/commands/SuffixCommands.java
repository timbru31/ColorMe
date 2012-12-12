package de.dustplanet.colorme.commands;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import de.dustplanet.colorme.Actions;
import de.dustplanet.colorme.ColorMe;

public class SuffixCommands implements CommandExecutor {
	private Actions actions;
	private ColorMe plugin;

	public SuffixCommands(ColorMe instance, Actions actionsInstance) {
		plugin = instance;
		actions = actionsInstance;
	}

	// Commands for suffixing
	public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		String pluginPart = "suffix", message, target, suffix = "", senderName, world = "default", globalSuffix;
		// Reloads the configs
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("suffixer.reload")) {
				actions.reload(sender);
			}
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Stop here if suffixer is unwanted
		if (!plugin.Suffixer) {
			message = plugin.localization.getString("part_disabled");
			plugin.message(sender, null, message, null, null, null, null);
			return true;
		}
		// Displays the help
		if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
			actions.help(sender, "suffix");
			return true;
		}
		// Sets the global suffix
		else if (args.length > 1 && args[0].equalsIgnoreCase("global")) {
			globalSuffix = args[1].replaceAll("_", " ");
			if (sender.hasPermission("suffixer.global")) {
				// If the prefixes are the same
				if (actions.replaceThings(globalSuffix).equalsIgnoreCase(actions.getGlobal("suffix"))) {
					message = plugin.localization.getString("same_suffix_global");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// If sender hasn't got the noFilter permission look if there are bad words in!
				if (plugin.blacklist && !sender.hasPermission("suffixer.nofilter")) {
					String s = actions.containsBlackListedWord(globalSuffix);
					if (s != null) {
						// Message, bad words in etc.
						message = plugin.localization.getString("bad_words");
						plugin.message(sender, null, message, s, null, null, null);
						return true;
					}
				}
				// Check if the message is too long or too short
				if (ChatColor.stripColor(actions.replaceThings(globalSuffix)).length() < plugin.suffixLengthMin) {
					message = plugin.localization.getString("too_short");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Check if the message is too long
				if (ChatColor.stripColor(actions.replaceThings(globalSuffix)).length() > plugin.suffixLengthMax) {
					message = plugin.localization.getString("too_long");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				plugin.config.set("global_default.suffix", globalSuffix);
				plugin.globalSuffix = true;
				plugin.saveConfig();
				message = plugin.localization.getString("changed_suffix_global");
				plugin.message(sender, null, message, actions.replaceThings(globalSuffix), null, null, null);
			}
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Removes a suffix
		else if (args.length > 1 && args[0].equalsIgnoreCase("remove")) {
			// Removes the global suffix
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("suffixer.global")) {
					// Trying to remove an empty global suffix
					if (!actions.hasGlobal("suffix")) {
						message = plugin.localization.getString("no_suffix_global");
						plugin.message(sender, null, message, null, null, null, null);
						return true;
					}
					// Remove global suffix
					actions.removeGlobal("suffix");
					plugin.globalSuffix = false;
					message = plugin.localization.getString("removed_suffix_global");
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
			if (sender.hasPermission("suffixer.remove") || actions.self(sender, target)) {
				// Trying to remove a suffix from a suffix-less player
				if (((!actions.has(target, world, pluginPart) && plugin.players.contains(target + "." + pluginPart + "." + world)))
						|| !plugin.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = plugin.localization.getString("no_suffix_self");
						plugin.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = plugin.localization.getString("no_suffix_other");
					plugin.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Remove suffix
				actions.remove(target, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Notify player is online
					Player player = plugin.getServer().getPlayerExact(target);
					message = plugin.localization.getString("removed_suffix_self");
					plugin.message(null, player, message, null, world, null, null);
				}
				// If player is offline just notify the sender
				if (!target.equalsIgnoreCase(senderName)) {
					message = plugin.localization.getString("removed_suffix_other");
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
		// Gets a suffix
		else if (args.length > 1 && args[0].equalsIgnoreCase("get")) {
			// Get the global suffix if set
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("suffixer.global")) {
					// Trying to get an empty global suffix
					if (!actions.hasGlobal("suffix")) {
						message = plugin.localization.getString("no_suffix_global");
						plugin.message(sender, null, message, null, null, null, null);
						return true;
					}
					suffix = actions.getGlobal("suffix");
					message = plugin.localization.getString("get_suffix_global");
					plugin.message(sender, null, message, suffix, null, null, null);
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
			suffix = actions.get(target, world, pluginPart);
			// Check for permission or self
			if (sender.hasPermission("suffixer.get") || actions.self(sender, target)) {
				// Trying to get a suffix from a suffix-less player
				if (((!actions.has(target, world, pluginPart) && plugin.players.contains(target))) || !plugin.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = plugin.localization.getString("no_suffix_self");
						plugin.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = plugin.localization.getString("no_suffix_other");
					plugin.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Gets suffix
				if (target.equalsIgnoreCase(senderName)) {
					message = plugin.localization.getString("get_suffix_self");
					plugin.message(sender, null, message, actions.replaceThings(suffix), world, null, null);
					return true;
				}
				message = plugin.localization.getString("get_suffix_other");
				plugin.message(sender, null, message, actions.replaceThings(suffix), world, target, null);
			}
			// Deny access
			else {
				message = plugin.localization.getString("permission_denied");
				plugin.message(sender, null, message, null, null, null, null);
			}
			return true;
		}
		// Suffixing
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
			suffix = args[1].replaceAll("_", " ");
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) world = args[2].toLowerCase();
			// If the suffixes are the same
			if (actions.replaceThings(suffix).equalsIgnoreCase(actions.get(target, world, pluginPart))) {
				if (senderName.equalsIgnoreCase(target)) {
					message = plugin.localization.getString("same_suffix_self");
					plugin.message(sender, null, message, null, world, null, null);
					return true;
				}	
				message = plugin.localization.getString("same_suffix_other");
				plugin.message(sender, null, message, null, world, target, null);
				return true;
			}
			// Self suffixing
			if (sender.hasPermission("suffixer.self") && actions.self(sender, target)) {
				// If sender hasn't got the noFilter permission look if there are bad words in!
				if (plugin.blacklist && !sender.hasPermission("suffixer.nofilter")) {
					String s = actions.containsBlackListedWord(suffix);
					if (s != null) {
						// Message, bad words in etc.
						message = plugin.localization.getString("bad_words");
						plugin.message(sender, null, message, s, null, null, null);
						return true;
					}
				}
				// Check if the message is too long or too short
				if (ChatColor.stripColor(actions.replaceThings(suffix)).length() < plugin.suffixLengthMin) {
					message = plugin.localization.getString("too_short");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Check if the message is too long
				if (ChatColor.stripColor(actions.replaceThings(suffix)).length() > plugin.suffixLengthMax) {
					message = plugin.localization.getString("too_long");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Without economy or costs are null
				Double cost = plugin.config.getDouble("costs.suffix");
				if (plugin.economy == null || cost == 0) {
					actions.set(senderName, suffix, world, pluginPart);
					message = plugin.localization.getString("changed_suffix_self");
					plugin.message(sender, null, message, actions.replaceThings(suffix), world, null, null);
					return true;
				}
				// With economy
				else if (plugin.economy != null) {
					// Charge costs :)
					if (cost > 0) {
						// Charge player unless he has the free permissions
						if (!sender.hasPermission("suffixer.free")) {
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
						// Set suffix and notify sender
						actions.set(senderName, suffix, world, pluginPart);
						message = plugin.localization.getString("changed_suffix_self");
						plugin.message(sender, null, message, actions.replaceThings(suffix), world, null, null);
						return true;
					}
				}
			}
			// Suffixing other
			else if (sender.hasPermission("suffixer.other") && !actions.self(sender, target)) {
				// If sender hasn't got the noFilter permission look if there are bad words in!
				if (plugin.blacklist && !sender.hasPermission("suffixer.nofilter")) {
					String s = actions.containsBlackListedWord(suffix);
					if (s != null) {
						// Message, bad words in etc.
						message = plugin.localization.getString("bad_words");
						plugin.message(sender, null, message, s, null, null, null);
						return true;
					}
				}
				// Check if the message is too long or too short
				if (ChatColor.stripColor(actions.replaceThings(suffix)).length() < plugin.suffixLengthMin) {
					message = plugin.localization.getString("too_short");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Check if the message is too long
				if (ChatColor.stripColor(actions.replaceThings(suffix)).length() > plugin.suffixLengthMax) {
					message = plugin.localization.getString("too_long");
					plugin.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Set the new suffix
				actions.set(target, suffix, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Tell the affected player
					Player player = plugin.getServer().getPlayerExact(target);
					message = plugin.localization.getString("changed_suffix_self");
					plugin.message(null, player, message, actions.replaceThings(suffix), world, null, null);
				}
				message = plugin.localization.getString("changed_suffix_other");
				plugin.message(sender, null, message, actions.replaceThings(suffix), world, target, null);
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