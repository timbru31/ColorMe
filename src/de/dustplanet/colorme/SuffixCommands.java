package de.dustplanet.colorme;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class SuffixCommands implements CommandExecutor {

	public ColorMe plugin;
	public SuffixCommands(ColorMe instance) {
		plugin = instance;
	}

	// Commands for suffixing
	public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		String pluginPart = "suffix", message, target, suffix = "", senderName, world = "default", globalSuffix;
		// Reloads the configs
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("suffixer.reload")) {
				Actions.reload(sender);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		// Stop here if suffixer is unwanted
		if (!ColorMe.Suffixer) {
			message = ColorMe.localization.getString("part_disabled");
			ColorMe.message(sender, null, message, null, null, null, null);
			return true;
		}
		// Displays the help
		if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
			Actions.help(sender, "suffix");
			return true;
		}
		// Sets the global suffix
		else if (args.length > 1 && args[0].equalsIgnoreCase("global")) {
			globalSuffix = args[1].replaceAll("_", " ");
			if (sender.hasPermission("suffixer.global")) {
				// If the prefixes are the same
				if (Actions.replaceThings(globalSuffix).equalsIgnoreCase(Actions.getGlobal("suffix"))) {
					message = ColorMe.localization.getString("same_suffix_global");
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
				// If sender hasn't got the noFilter permission look if there are bad words in!
				if (ColorMe.blacklist && !sender.hasPermission("suffixer.nofilter")) {
					for (String s : ColorMe.bannedWords) {
						if (globalSuffix.contains(s)) {
							// Message, bad words in etc.
							message = ColorMe.localization.getString("bad_words");
							ColorMe.message(sender, null, message, s, null, null, null);
							return true;
						}
					}
				}
				// Check if the message is too long
				if (ChatColor.stripColor(Actions.replaceThings(globalSuffix)).length() > ColorMe.suffixLength) {
					message = ColorMe.localization.getString("too_long");
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
				ColorMe.config.set("global_default.suffix", globalSuffix);
				ColorMe.globalSuffix = true;
				plugin.saveConfig();
				message = ColorMe.localization.getString("changed_suffix_global");
				ColorMe.message(sender, null, message, Actions.replaceThings(globalSuffix), null, null, null);
				return true;
			}
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		// Removes a suffix
		else if (args.length > 1 && args[0].equalsIgnoreCase("remove")) {
			world = "default";
			// Removes the global suffix
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("suffixer.global")) {
					// Trying to remove an empty global suffix
					if (!Actions.hasGlobal("suffix")) {
						message = ColorMe.localization.getString("no_suffix_global");
						ColorMe.message(sender, null, message, null, null, null, null);
						return true;
					}
					// Remove global suffix
					Actions.removeGlobal("suffix");
					ColorMe.globalSuffix = false;
					message = ColorMe.localization.getString("removed_suffix_global");
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
			if (sender.hasPermission("suffixer.remove") || Actions.self(sender, target)) {
				// Trying to remove a suffix from a suffix-less player
				if (((!Actions.has(target, world, pluginPart) && ColorMe.players.contains(target + "." + pluginPart + "." + world)))
						|| !ColorMe.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = ColorMe.localization.getString("no_suffix_self");
						ColorMe.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = ColorMe.localization.getString("no_suffix_other");
					ColorMe.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Remove suffix
				Actions.remove(target, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Notify player is online
					Player player = plugin.getServer().getPlayerExact(target);
					message = ColorMe.localization.getString("removed_suffix_self");
					ColorMe.message(null, player, message, null, world, null, null);
				}
				// If player is offline just notify the sender
				if (!target.equalsIgnoreCase(senderName)) {
					message = ColorMe.localization.getString("removed_suffix_other");
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
		// Gets a suffix
		else if (args.length > 1 && args[0].equalsIgnoreCase("get")) {
			world = "default";
			// Get the global suffix if set
			if (args[1].equalsIgnoreCase("global")) {
				if (sender.hasPermission("suffixer.global")) {
					// Trying to get an empty global suffix
					if (!Actions.hasGlobal("suffix")) {
						message = ColorMe.localization.getString("no_suffix_global");
						ColorMe.message(sender, null, message, null, null, null, null);
						return true;
					}
					suffix = Actions.getGlobal("suffix");
					message = ColorMe.localization.getString("get_suffix_global");
					ColorMe.message(sender, null, message, suffix, null, null, null);
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
			suffix = Actions.get(target, world, pluginPart);
			// Check for permission or self
			if (sender.hasPermission("suffixer.get") || Actions.self(sender, target)) {
				// Trying to get a suffix from a suffix-less player
				if (((!Actions.has(target, world, pluginPart) && ColorMe.players.contains(target))) || !ColorMe.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = ColorMe.localization.getString("no_suffix_self");
						ColorMe.message(sender, null, message, null, world, null, null);
						return true;
					}
					// Other
					message = ColorMe.localization.getString("no_suffix_other");
					ColorMe.message(sender, null, message, null, world, target, null);
					return true;
				}
				// Gets suffix
				if (target.equalsIgnoreCase(senderName)) {
					message = ColorMe.localization.getString("get_suffix_self");
					ColorMe.message(sender, null, message, Actions.replaceThings(suffix), world, null, null);
					return true;
				}
				message = ColorMe.localization.getString("get_suffix_other");
				ColorMe.message(sender, null, message, Actions.replaceThings(suffix), world, target, null);
				return true;
			}
			// Deny access
			else {
				message = ColorMe.localization.getString("permission_denied");
				ColorMe.message(sender, null, message, null, null, null, null);
				return true;
			}
		}
		// Suffixing
		else if (args.length > 1) {
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
			suffix = args[1].replaceAll("_", " ");
			senderName = sender.getName().toLowerCase();
			if (args.length > 2) {
				world = args[2].toLowerCase();
			}

			// If the suffixes are the same
			if (Actions.replaceThings(suffix).equalsIgnoreCase(Actions.get(target, world, pluginPart))) {
				if (senderName.equalsIgnoreCase(target)) {
					message = ColorMe.localization.getString("same_suffix_self");
					ColorMe.message(sender, null, message, null, world, null, null);
					return true;
				}	
				message = ColorMe.localization.getString("same_suffix_other");
				ColorMe.message(sender, null, message, null, world, target, null);
				return true;
			}

			// Self suffixing
			if (sender.hasPermission("suffixer.self") && Actions.self(sender, target)) {
				// If sender hasn't got the noFilter permission look if there are bad words in!
				if (ColorMe.blacklist && !sender.hasPermission("suffixer.nofilter")) {
					for (String s : ColorMe.bannedWords) {
						if (suffix.contains(s)) {
							// Message, bad words in etc.
							message = ColorMe.localization.getString("bad_words");
							ColorMe.message(sender, null, message, s, null, null, null);
							return true;
						}
					}
				}
				// Check if the message is too long
				if (ChatColor.stripColor(Actions.replaceThings(suffix)).length() > ColorMe.suffixLength) {
					message = ColorMe.localization.getString("too_long");
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Without economy or costs are null
				Double cost = ColorMe.config.getDouble("costs.suffix");
				if (plugin.economy == null || cost == 0) {
					Actions.set(senderName, suffix, world, pluginPart);
					message = ColorMe.localization.getString("changed_suffix_self");
					ColorMe.message(sender, null, message, Actions.replaceThings(suffix), world, null, null);
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
						// Set suffix and notify sender
						Actions.set(senderName, suffix, world, pluginPart);
						message = ColorMe.localization.getString("changed_suffix_self");
						ColorMe.message(sender, null, message, Actions.replaceThings(suffix), world, null, null);
						return true;
					}
				}
			}
			// Suffixing other
			else if (sender.hasPermission("suffixer.other") && !Actions.self(sender, target)) {
				// If sender hasn't got the noFilter permission look if there are bad words in!
				if (ColorMe.blacklist && !sender.hasPermission("suffixer.nofilter")) {
					for (String s : ColorMe.bannedWords) {
						if (suffix.contains(s)) {
							// Message, bad words in etc.
							message = ColorMe.localization.getString("bad_words");
							ColorMe.message(sender, null, message, s, null, null, null);
							return true;
						}
					}
				}
				// Check if the message is too long
				if (ChatColor.stripColor(Actions.replaceThings(suffix)).length() > ColorMe.suffixLength) {
					message = ColorMe.localization.getString("too_long");
					ColorMe.message(sender, null, message, null, null, null, null);
					return true;
				}
				// Set the new suffix
				Actions.set(target, suffix, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Tell the affected player
					Player player = plugin.getServer().getPlayerExact(target);
					message = ColorMe.localization.getString("changed_suffix_self");
					ColorMe.message(null, player, message, Actions.replaceThings(suffix), world, null, null);
				}
				message = ColorMe.localization.getString("changed_suffix_other");
				ColorMe.message(sender, null, message, Actions.replaceThings(suffix), world, target, null);
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