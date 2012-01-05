package de.xghostkillerx.colorme;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

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

public class ColorMeCommands {
	ColorMe plugin;
	public ColorMeCommands(ColorMe instance) {
		plugin = instance;
	}
	
	//TODO Einfachere Messages?

	// Commands (primary use /color <args>)
	public boolean ColorMeCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		if (command.getName().equalsIgnoreCase("colorme") || command.getName().equalsIgnoreCase("color") || command.getName().equalsIgnoreCase("colour")) {
			// Reloads the configs
			if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
				if (sender.hasPermission("colorme.reload")) {
					ColorMeReload(sender, args);
					return true;
				}
				else {
					sender.sendMessage(ChatColor.RED + "You don't have the permission to do this!");
					return true;
				}
			}
			// Returns the color list
			if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
				if (sender.hasPermission("colorme.list")) {
					plugin.list(sender);
					return true;
				}
				else {
					sender.sendMessage(ChatColor.RED + "You don't have the permission to view the color list!");
					return true;
				}
			}
			// Displays the help
			if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
				ColorMeHelp(sender, args);
				return true;
			}
			// Removes a color
			if (args.length > 1 && args[0].equalsIgnoreCase("remove")) {
				// If a player name is there, too
				String target = args[1].toLowerCase();
				String senderName = sender.getName().toLowerCase();
				// Check for permission or self
				if (sender.hasPermission("colorme.remove") || plugin.self(sender, target)) {
					// Trying to remove a color from a color-less player
					if (((!plugin.hasColor(target) && plugin.colors.contains(target))) || !plugin.colors.contains(target)) {
						// Self
						if (target.equalsIgnoreCase(senderName)) {
							sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.RED + " don't have a colored name.");
							return true;
						}
						// Other
						sender.sendMessage(ChatColor.YELLOW + target + ChatColor.RED + " doesn't have a colored name.");
						return true;
					}
					// Remove color
					plugin.removeColor(target);
					if (plugin.getServer().getPlayerExact(target) != null) {
						// Notify player is online
						Player player = plugin.getServer().getPlayerExact(target);
						player.sendMessage(ChatColor.YELLOW + "Your" + ChatColor.GREEN + " name color has been removed.");
						// If it's the console or not the same player, tell success
						if ((sender instanceof ConsoleCommandSender) || (!target.equalsIgnoreCase(senderName))) {
							sender.sendMessage(ChatColor.GREEN + "Removed "+ ChatColor.YELLOW + player.getName() + ChatColor.GREEN + "'s color.");
							return true;
						}
						return true;
					}
					// If player is offline just notify the sender
					sender.sendMessage(ChatColor.GREEN + "Removed "+ ChatColor.YELLOW + target + ChatColor.GREEN + "'s color.");
					return true;
				}
				// Deny access
				else {
					sender.sendMessage(ChatColor.RED + "You don't have the permission to remove colors!");
					return true;
				}
			}
			// If it's only the player itself
			if (args.length == 1 && args[0].equalsIgnoreCase("remove")) {
				String senderName = sender.getName().toLowerCase();
				// Tell console to include a name
				if (sender instanceof ConsoleCommandSender) {
					sender.sendMessage(ChatColor.RED + "You have to include a name!");
					return true;
				}
				// Self removal is okay that way
				else if (plugin.self(sender, senderName)) {
					// Check for no color
					if (((!plugin.hasColor(senderName) && plugin.colors.contains(senderName))) || !plugin.colors.contains(senderName)) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.RED + " don't have a colored name.");
						return true;
					}
					plugin.removeColor(senderName);
					if (plugin.getServer().getPlayerExact(senderName) != null) {
						// Tell message the player
						sender.sendMessage(ChatColor.YELLOW + "Your" + ChatColor.GREEN + " name color has been removed.");
						return true;
					}
				}
			}
			// Gets a color
			if (args.length > 1 && args[0].equalsIgnoreCase("get")) {
				// If a player name is there, too
				String target = args[1].toLowerCase();
				String senderName = sender.getName().toLowerCase();
				String actualColor = plugin.getColor(target).toLowerCase();
				// Check for permission or self
				if (sender.hasPermission("colorme.get") || plugin.self(sender, target)) {
					// Trying to get a color from a color-less player
					if (((!plugin.hasColor(target) && plugin.colors.contains(target))) || !plugin.colors.contains(target)) {
						// Self
						if (target.equalsIgnoreCase(senderName)) {
							sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.RED + " don't have a colored name.");
							return true;
						}
						// Other
						sender.sendMessage(ChatColor.YELLOW + target + ChatColor.RED + " doesn't have a colored name.");
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
							sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + plugin.randomColor(actualColor));
							return true;
						}
						// Rainbow
						else if (actualColor.equalsIgnoreCase("rainbow")) {
							sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + plugin.rainbowColor(actualColor));
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
						sender.sendMessage(ChatColor.YELLOW + target + ChatColor.GREEN + " have got the color " + plugin.randomColor(actualColor));
						return true;
					}
					// Rainbow, only if sender isn't itself
					else if (actualColor.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.YELLOW + target + ChatColor.GREEN + " have got the color " + plugin.rainbowColor(actualColor));
						return true;
					}
				}
				// Deny access
				else {
					sender.sendMessage(ChatColor.RED + "You don't have the permission to get colors!");
					return true;
				}
			}
			// If it's only the player itself
			if (args.length == 1 && args[0].equalsIgnoreCase("get")) {
				String senderName = sender.getName().toLowerCase();
				String actualColor = plugin.getColor(senderName).toLowerCase();
				// Tell console to include a name
				if (sender instanceof ConsoleCommandSender) {
					sender.sendMessage(ChatColor.RED + "The conole can't have a color! Include a name!");
					return true;
				}
				else if (plugin.self(sender, senderName)) {
					// Check for no color
					if (((!plugin.hasColor(senderName) && plugin.colors.contains(senderName))) || !plugin.colors.contains(senderName)) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.RED + " don't have a colored name.");
						return true;
					}
					// Normal
					if (!actualColor.equalsIgnoreCase("random") && !actualColor.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + ChatColor.valueOf(actualColor.toUpperCase()) + actualColor);
						return true;
					}
					// Random
					else if (actualColor.equalsIgnoreCase("random")) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + plugin.randomColor(actualColor));
						return true;
					}
					// Rainbow
					else if (actualColor.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + plugin.rainbowColor(actualColor));
						return true;
					}
				}
			}
			// Self coloring
			if (args.length > 1 && args[0].equalsIgnoreCase("me")) {		
				if (sender.hasPermission("colorme.self")) {
					String senderName = sender.getName().toLowerCase();
					String color = args[1];
					// Tell console only ingame command
					if (sender instanceof ConsoleCommandSender) {
						sender.sendMessage(ChatColor.RED + "Sorry, this command can only be run from ingame!");
						return true;
					}
					// Color is invalid
					if (plugin.validColor(color) == false) {
						sender.sendMessage(ChatColor.RED + "'" + ChatColor.YELLOW + color + ChatColor.RED + "' is not a supported color.");
						return true;
					}
					// If the colors are the same
					if (color.equalsIgnoreCase(plugin.getColor(senderName))) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.RED + " already have got this color");
						return true;
					}
					// With economy
					if (plugin.economy != null) {
						double cost = plugin.config.getDouble("costs");
						// Charge costs :)
						if (cost > 0 && plugin.economy.has(senderName, cost)) {
							plugin.economy.withdrawPlayer(senderName, cost);
							// Set color an notify sender
							plugin.setColor(senderName, color);
							sender.sendMessage(ChatColor.GREEN + "You have been charged " + ChatColor.RED + plugin.economy.format(cost) + ChatColor.GREEN + '.');
							// Normal
							if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
								sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.valueOf(color.toUpperCase()) + color);
								return true;
							}
							// Random
							else if (color.equalsIgnoreCase("random")) {
								sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + plugin.randomColor(color));
								return true;
							}
							// Rainbow
							else if (color.equalsIgnoreCase("rainbow")) {
								sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + plugin.rainbowColor(color));
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
							plugin.setColor(senderName, color);
							// Normal
							if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
								sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.valueOf(color.toUpperCase()) + color);
								return true;
							}
							// Random
							else if (color.equalsIgnoreCase("random")) {
								sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + plugin.randomColor(color));
								return true;
							}
							// Rainbow
							else if (color.equalsIgnoreCase("rainbow")) {
								sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + plugin.rainbowColor(color));
								return true;
							}
						}
					}
					// No economy
					else {
						plugin.setColor(senderName, color);
						// Normal
						if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.valueOf(color.toUpperCase()) + color);
							return true;
						}
						// Random
						else if (color.equalsIgnoreCase("random")) {
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + plugin.randomColor(color));
							return true;
						}
						// Rainbow
						else if (color.equalsIgnoreCase("rainbow")) {
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + plugin.rainbowColor(color));
							return true;
						}
					}
				}
				// Deny access
				else {
					sender.sendMessage(ChatColor.RED + "You don't have the permission to color yourself!");
					return true;
				}
			}
		}
		// Coloring someone else (name and then color)
		if (args.length > 1) {
			// Check for permission
			if (sender.hasPermission("colorme.other")) {
				String senderName = sender.getName().toLowerCase();
				String target = args[0];
				String color = args[1];
				// Unsupported colors
				if (plugin.validColor(color) == false) {
					sender.sendMessage(ChatColor.RED + "'" + ChatColor.YELLOW + color + ChatColor.RED + "' is not a supported color.");
					return true;
				}
				// If the colors are the same
				if (color.equalsIgnoreCase(plugin.getColor(target))) {
					if (senderName.equalsIgnoreCase(target)) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.RED + " already have got this color");
						return true;
					}	
					sender.sendMessage(ChatColor.YELLOW + target + ChatColor.RED + " already has got this color");
					return true;
				}
				// Set the new color
				if (plugin.setColor(target, color)) {
					if (plugin.getServer().getPlayerExact(target) != null) {
						// Tell the affected player
						Player player = plugin.getServer().getPlayerExact(target);
						// Normal
						if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
							player.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.valueOf(color.toUpperCase()) + color);
						}
						// Random
						else if (color.equalsIgnoreCase("random")) {
							player.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + plugin.randomColor(color));
						}
						// Rainbow
						else if (color.equalsIgnoreCase("rainbow")) {
							player.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + plugin.rainbowColor(color));
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
								sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + plugin.randomColor(color));
								return true;
							}
							// Rainbow
							else if (color.equalsIgnoreCase("rainbow")) {
								sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + plugin.rainbowColor(color));
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
						sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + plugin.randomColor(color));
						return true;
					}
					// Rainbow
					else if (color.equalsIgnoreCase("rainbow")) {
						sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + plugin.rainbowColor(color));
						return true;
					}
				}
			}
			// Permission check failed
			else {
				sender.sendMessage(ChatColor.RED + "You don't have the permission to color players!");
				return true;
			}
		}
		return false;
	}

	// Reloads the config with /colorme reload
	private boolean ColorMeReload(CommandSender sender, String[] args) {
		PluginDescriptionFile pdfFile = plugin.getDescription();
		plugin.loadConfigAgain();		
		sender.sendMessage(ChatColor.GREEN + "ColorMe version " + ChatColor.RED + pdfFile.getVersion() + ChatColor.GREEN + " reloaded!");
		return true;
	}

	// Displays the help with /colorme help
	private boolean ColorMeHelp(CommandSender sender, String[] args) {
		PluginDescriptionFile pdfFile = plugin.getDescription();
		sender.sendMessage(ChatColor.GREEN	+ "Welcome to the ColorMe version " + ChatColor.RED + pdfFile.getVersion() + ChatColor.GREEN + " help!");
		sender.sendMessage(ChatColor.RED + "<> = Required, [] = Optional");
		sender.sendMessage("</command> help - Shows the help");
		sender.sendMessage("/<command> list - Shows list of colors");
		sender.sendMessage("/<command> get [name] - Gets the actual color");
		sender.sendMessage("/<command> remove [name] - Removes color");
		sender.sendMessage("/<command> me <color> - Sets your own color");
		sender.sendMessage("/<command> <name> <color> - Sets player's color");
		return true;
	}
}
