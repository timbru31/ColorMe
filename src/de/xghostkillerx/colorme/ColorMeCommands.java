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
 * http://bit.ly/colormebukkitdev 
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

	// Commands (primary use /color <args>
	public boolean ColorMeCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		if ((command.getName().equalsIgnoreCase("colorme")) || (command.getName().equalsIgnoreCase("color"))) {
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
						// Update displayname if online
						Player affected = plugin.getServer().getPlayerExact(target);
						affected.setDisplayName(ChatColor.stripColor(affected.getDisplayName()));
						affected.sendMessage(ChatColor.YELLOW + "Your" + ChatColor.GREEN + " name color has been removed.");
						// If it's the console or not the same player, tell success
						if ((sender instanceof ConsoleCommandSender) || (!target.equalsIgnoreCase(senderName))) {
							sender.sendMessage(ChatColor.GREEN + "Removed "+ ChatColor.YELLOW + affected.getName() + ChatColor.GREEN + "'s color.");
							return true;
						}
						return true;
					}
					// If player is offline remove it from the config.
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
						// Update displayname if online
						Player player = plugin.getServer().getPlayerExact(senderName);
						player.setDisplayName(ChatColor.stripColor(player.getDisplayName()));
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
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + ChatColor.YELLOW + actualColor);
						return true;
					}
					sender.sendMessage(ChatColor.YELLOW + target + ChatColor.GREEN + " has got the color " + ChatColor.YELLOW + actualColor);
					return true;
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
					sender.sendMessage(ChatColor.RED + "You don't have a colored name!");
					return true;
				}
				// Self removal is okay that way
				else if (plugin.self(sender, senderName)) {
					// Check for no color
					if (((!plugin.hasColor(senderName) && plugin.colors.contains(senderName))) || !plugin.colors.contains(senderName)) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.RED + " don't have a colored name.");
						return true;
					}
					sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.GREEN + " have got the color " + ChatColor.YELLOW + actualColor);
					return true;
				}
			}
			// Self coloring
			if (args.length > 1 && args[0].equalsIgnoreCase("me")) {		
				if (sender.hasPermission("colorme.self")) {
					String senderName = sender.getName().toLowerCase();
					String newColor = args[1];
					String color = plugin.findColor(newColor);
					// Tell console only ingame command
					if (sender instanceof ConsoleCommandSender) {
						sender.sendMessage(ChatColor.RED + "Sorry, this command can only be run from ingame!");
						return true;
					}
					// Color is invalid
					if (color.equals(newColor)) {
						sender.sendMessage(ChatColor.RED + "'" + ChatColor.YELLOW +newColor + ChatColor.RED +"' is not a supported color.");
						return true;
					}
					// If the colors are the same
					if (newColor.equalsIgnoreCase(plugin.getColor(senderName))) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.RED + " already have got this color");
						return true;
					}
					// With economy
					if (plugin.economy != null) {
						double cost = plugin.config.getDouble("costs");
						// Charge costs :)
						if (cost >0 && plugin.economy.has(senderName, cost)) {
							plugin.economy.withdrawPlayer(senderName, cost);
							plugin.setColor(senderName, newColor);
							sender.sendMessage(ChatColor.GREEN + "You have been charged " + ChatColor.RED + plugin.economy.format(cost) + ChatColor.GREEN + '.');
							// Player is online, change their displayname immediately
							Player player = plugin.getServer().getPlayerExact(senderName);
							player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.YELLOW + newColor);
							return true;
						}
						// If player hasn't got enough money
						else if (cost >0 && plugin.economy.getBalance(senderName) < cost) {
							sender.sendMessage(ChatColor.RED + "Sorry, you don't have enough money to color your name.");
							sender.sendMessage(ChatColor.RED + "It costs " + ChatColor.YELLOW + plugin.economy.format(cost) + ChatColor.RED + " to color your name.");
							return true;
						}
						// If it's for free
						else if (cost == 0) {
							// Player is online, change their displayname immediately
							plugin.setColor(senderName, newColor);
							Player player = plugin.getServer().getPlayerExact(senderName);
							player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
							sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.YELLOW + newColor);
							return true;
						}
					}
					// No economy
					else {
						if (plugin.setColor(senderName, newColor)) {
							if (plugin.getServer().getPlayerExact(senderName) != null) {
								// Player is online, change their displayname immediately
								plugin.setColor(senderName, newColor);
								Player player = plugin.getServer().getPlayerExact(senderName);
								player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
								sender.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.YELLOW + newColor);
								return true;
							}
						}
					}
				}
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
				String newColor = args[1];
				String color = plugin.findColor(newColor);
				// Unsupported colors
				if (color.equals(newColor)) {
					sender.sendMessage(ChatColor.RED + "'" + ChatColor.YELLOW +newColor + ChatColor.RED +"' is not a supported color.");
					return true;
				}
				// If the colors are the same
				if (newColor.equalsIgnoreCase(plugin.getColor(target))) {
					if (senderName.equalsIgnoreCase(target)) {
						sender.sendMessage(ChatColor.YELLOW + "You" + ChatColor.RED + " already have got this color");
						return true;
					}	
					sender.sendMessage(ChatColor.YELLOW + target + ChatColor.RED + " already has got this color");
					return true;
				}
				// Set the new color
				if (plugin.setColor(target, newColor)) {
					if (plugin.getServer().getPlayerExact(target) != null) {
						// Player is online, change their displayname immediately
						plugin.setColor(target, newColor);
						Player player = plugin.getServer().getPlayerExact(target);
						player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
						// Tell the affected player
						player.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + ChatColor.YELLOW + newColor);
						// No double message
						if (!senderName.equalsIgnoreCase(target)) {
							sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + ChatColor.YELLOW + newColor);
							return true;
						}
						return true;
					}
					// If he is offline
					sender.sendMessage(ChatColor.GREEN + "Changed " + ChatColor.YELLOW + target + ChatColor.GREEN + "'s color to " + ChatColor.YELLOW + newColor);
					return true;
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
