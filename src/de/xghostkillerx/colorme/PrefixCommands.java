package de.xghostkillerx.colorme;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

public class PrefixCommands implements CommandExecutor {
	
	ColorMe plugin;
	public PrefixCommands(ColorMe instance) {
		plugin = instance;
	}
	private String pluginPart = "prefix", message, target, color, senderName, world, actualColor;
	private Integer i;

	// Commands for prefix
	public boolean onCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		// Stop here if Prefixer is unwanted
		if (ColorMe.config.getBoolean("Prefixer") == false) {
			message = plugin.localization.getString("part_disabled");
			Actions.message(sender, message);
			return true;
		}
		
		// Reloads the configs
		if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
			if (sender.hasPermission("prefixer.reload")) {
				PrefixerReload(sender, args);
				return true;
			}
			else {
				message = plugin.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		// Returns the color list
		if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
			if (sender.hasPermission("prefixer.list")) {
				Actions.listColors(sender);
				return true;
			}
			else {
				message = plugin.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		// Displays the help
		if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
			PrefixerHelp(sender, args);
			return true;
		}
		// Removes a color
		if (args.length >= 1 && args[0].equalsIgnoreCase("remove")) {
			target = args[1].toLowerCase();
			// Support for "me" -> this is the senderName!
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName();
			}
			senderName = sender.getName().toLowerCase();
			// Use standard if no world is included
			world = "standard";
			if (args.length > 1) {
				world = args[2].toLowerCase();
			}
			// Check for permission or self
			if (sender.hasPermission("prefixer.remove") || Actions.self(sender, target)) {
				// Trying to remove a color from a color-less player
				if (((!Actions.has(target, world, pluginPart) && ColorMe.players.contains(target))) || !ColorMe.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = plugin.localization.getString("no_color_self");
						Actions.message(sender, message, world);
						return true;
					}
					// Other
					message = plugin.localization.getString("no_color_other");
					Actions.message(sender, message, world, target);
					return true;
				}
				// Remove color
				Actions.remove(target, world, pluginPart);
				if (plugin.getServer().getPlayerExact(target) != null) {
					// Notify player is online
					Player player = plugin.getServer().getPlayerExact(target);
					message = plugin.localization.getString("removed_color_self");
					Actions.messagePlayer(player, message, world);
					// If it's the console or not the same player, tell success
					if ((sender instanceof ConsoleCommandSender) || (!target.equalsIgnoreCase(senderName))) {
						message = plugin.localization.getString("removed_color_other");
						Actions.message(sender, message, world, target);
						return true;
					}
					return true;
				}
				// If player is offline just notify the sender
				message = plugin.localization.getString("removed_color_other");
				Actions.message(sender, message, world, target);
				return true;
			}
			// Deny access
			else {
				message = plugin.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		// Gets a color
		if (args.length >= 1 && args[0].equalsIgnoreCase("get")) {
			// If a player name is there, too
			target = args[1].toLowerCase();
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName();
			}
			senderName = sender.getName().toLowerCase();
			world = "standard";
			if (args.length > 1) {
				world = args[2].toLowerCase();
			}
			actualColor = Actions.get(target, world, pluginPart).toLowerCase();
			// Check for permission or self
			if (sender.hasPermission("prefixer.get") || Actions.self(sender, target)) {
				// Trying to get a color from a color-less player
				if (((!Actions.has(target, world, pluginPart) && ColorMe.players.contains(target))) || !ColorMe.players.contains(target)) {
					// Self
					if (target.equalsIgnoreCase(senderName)) {
						message = plugin.localization.getString("no_color_self");
						Actions.message(sender, message, world);
						return true;
					}
					// Other
					message = plugin.localization.getString("no_color_other");
					Actions.message(sender, message, world, target);
					return true;
				}
				// Gets color
				if (target.equalsIgnoreCase(senderName)) {
					// Normal
					if (!actualColor.equalsIgnoreCase("random") && !actualColor.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + ChatColor.valueOf(actualColor.toUpperCase()) + actualColor);
						return true;
					}
					// Random
					else if (actualColor.equalsIgnoreCase("random")) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + Actions.randomColor(actualColor));
						return true;
					}
					// Rainbow
					else if (actualColor.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + Actions.rainbowColor(actualColor));
						return true;
					}
				}
				// Normal, only if sender isn't itself
				if (!actualColor.equalsIgnoreCase("random") && !actualColor.equalsIgnoreCase("rainbow")) {
					sender.sendMessage(ChatColor.YELLOW + target + ChatColor.GREEN + " have got the color " + ChatColor.valueOf(actualColor.toUpperCase()) + actualColor);
					return true;
				}
				// Random, only if sender isn't itself
				else if (actualColor.equalsIgnoreCase("random")) {
					sender.sendMessage(ChatColor.YELLOW + target + ChatColor.GREEN + " have got the color " + Actions.randomColor(actualColor));
					return true;
				}
				// Rainbow, only if sender isn't itself
				else if (actualColor.equalsIgnoreCase("rainbow")) {
					sender.sendMessage(ChatColor.YELLOW + target + ChatColor.GREEN + " have got the color " + Actions.rainbowColor(actualColor));
					return true;
				}
			}
			// Deny access
			else {
				message = plugin.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		// Self coloring
		if (args.length >= 1) {	
			target = args[0].toLowerCase();
			if (target.equalsIgnoreCase("me")) {
				target = sender.getName();
			}
			color = args[1].toLowerCase();
			senderName = sender.getName().toLowerCase();
			world = "standard";
			if (args.length > 1) {
				world = args[2].toLowerCase();
			}

			// Unsupported colors
			if (Actions.validColor(color) == false) {
				message = plugin.localization.getString("invalid_color");
				Actions.message(sender, message, color);
				return true;
			}

			// If color is disabled
			if (Actions.isDisabled(color) == true) {
				message = plugin.localization.getString("disabled_color");
				Actions.message(sender, message, color);
				return true;
			}

			// If the colors are the same
			if (color.equalsIgnoreCase(Actions.get(target, world, pluginPart))) {
				if (senderName.equalsIgnoreCase(target)) {
					message = plugin.localization.getString("same_color_self");
					Actions.message(sender, message, world);
					return true;
				}	
				message = plugin.localization.getString("same_color_other");
				Actions.message(sender, message, world, target);
				return true;
			}

			if (sender.hasPermission("prefixer.self") && Actions.self(sender, target)) {
				// Tell console only ingame command
				if (sender instanceof ConsoleCommandSender) {
					message = plugin.localization.getString("only_ingame");
					Actions.message(sender, message);
					return true;
				}
				// With economy
				if (plugin.economy != null) {
					double cost = ColorMe.config.getDouble("costs.color");
					if (color.equalsIgnoreCase("white")) {
						Actions.set(senderName, color, world, pluginPart);
						sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.valueOf(color.toUpperCase()) + color);
						return true;
					}
					// Charge costs :)
					if (cost > 0 && plugin.economy.has(senderName, cost)) {
						plugin.economy.withdrawPlayer(senderName, cost);
						// Set color an notify sender
						Actions.set(senderName, color, world, pluginPart);
						sender.sendMessage(ChatColor.GREEN + "You have been charged " + ChatColor.RED + plugin.economy.format(cost) + ChatColor.GREEN + '.');
						// Normal
						if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.valueOf(color.toUpperCase()) + color);
							return true;
						}
						// Random
						else if (color.equalsIgnoreCase("random")) {
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + Actions.randomColor(color));
							return true;
						}
						// Rainbow
						else if (color.equalsIgnoreCase("rainbow")) {
							return true;
						}
					}
					// If player hasn't got enough money
					else if (cost > 0 && plugin.economy.getBalance(senderName) < cost) {
						sender.sendMessage(ChatColor.RED + "Sorry, you don't have enough money to color your name.");
						sender.sendMessage(ChatColor.RED + "It costs " + ChatColor.YELLOW + plugin.economy.format(cost) + ChatColor.RED + " to color your name.");
						return true;
					}
					// If it's for free
					else if (cost == 0) {
						Actions.set(senderName, color, world, pluginPart);
						// Normal
						if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.valueOf(color.toUpperCase()) + color);
							return true;
						}
						// Random
						else if (color.equalsIgnoreCase("random")) {
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + Actions.randomColor(color));
							return true;
						}
						// Rainbow
						else if (color.equalsIgnoreCase("rainbow")) {
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + Actions.rainbowColor(color));
							return true;
						}
					}
				}
				// No economy
				else {
					Actions.set(senderName, color, world, pluginPart);
					// Normal
					if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.valueOf(color.toUpperCase()) + color);
						return true;
					}
					// Random
					else if (color.equalsIgnoreCase("random")) {
						sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + Actions.randomColor(color));
						return true;
					}
					// Rainbow
					else if (color.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + Actions.rainbowColor(color));
						return true;
					}
				}
			}
			else if (sender.hasPermission("prefixer.other")) {
				// Set the new color
				if (Actions.set(target, color, world, pluginPart)) {
					if (plugin.getServer().getPlayerExact(target) != null) {
						// Tell the affected player
						Player player = plugin.getServer().getPlayerExact(target);
						// Normal
						if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
							player.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.valueOf(color.toUpperCase()) + color);
						}
						// Random
						else if (color.equalsIgnoreCase("random")) {
							player.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + Actions.randomColor(color));
						}
						// Rainbow
						else if (color.equalsIgnoreCase("rainbow")) {
							player.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + Actions.rainbowColor(color));
						}
						// No double message
						if (!senderName.equalsIgnoreCase(target)) {
							// Normal
							if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
								sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + ChatColor.valueOf(color.toUpperCase()) + color);
								return true;
							}
							// Random
							else if (color.equalsIgnoreCase("random")) {
								sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + Actions.randomColor(color));
								return true;
							}
							// Rainbow
							else if (color.equalsIgnoreCase("rainbow")) {
								sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + Actions.rainbowColor(color));
								return true;
							}
						}
						return true;
					}
					// If he/she/it is offline
					// Normal
					if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + ChatColor.valueOf(color.toUpperCase()) + color);
						return true;
					}
					// Random
					else if (color.equalsIgnoreCase("random")) {
						sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + Actions.randomColor(color));
						return true;
					}
					// Rainbow
					else if (color.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + Actions.rainbowColor(color));
						return true;
					}
				}
			}
			// Permission check failed
			else {
				message = plugin.localization.getString("permission_denied");
				Actions.message(sender, message);
				return true;
			}
		}
		return false;
	}

	// Reloads the config with /prefixer reload
	private boolean PrefixerReload(CommandSender sender, String[] args) {
		plugin.loadConfigsAgain();		
		message = plugin.localization.getString("reload");
		Actions.message(sender, message);
		return true;
	}

	// Displays the help with /prefixer help
	private boolean PrefixerHelp(CommandSender sender, String[] args) {
		for (i = 1; i <= 8; i++) {
			message = plugin.localization.getString("help_color" + Integer.toString(i));
			Actions.message(sender, message);
		}
		return true;
	}
}