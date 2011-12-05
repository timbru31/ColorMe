package de.xghostkillerx.colorme;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;
import org.bukkit.plugin.PluginDescriptionFile;

public class ColorMeCommands {
	ColorMe plugin;
	public ColorMeCommands(ColorMe instance) {
		plugin = instance;
	}


	// /color <color/name> [name] (name is optional since you can color your own name)
	public boolean ColorMeCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		if ((command.getName().equalsIgnoreCase("colorme")) || (command.getName().equalsIgnoreCase("color"))) {
			// reload
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
					sender.sendMessage(ChatColor.RED + "You don't have the permission to view the list!");
					return true;
				}
			}
			// Displays the help
			if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
					ColorMeHelp(sender, args);
					return true;
			}
			// Only if a player is the sender
			if (sender instanceof Player) {
				// The sender (in this case the player)
				Player player = (Player) sender;
				// Only one argument (color or name)
				if (args.length == 1) {
					String target = args[0];
					String newColor = target;
					// Remove own color or from another player with a permission
					if (plugin.hasColor(target) && (player.hasPermission("colorme.remove") || plugin.self(player, target))) {
						plugin.removeColor(target);
						if (plugin.getServer().getPlayerExact(target) != null) {
							// Update displayname if online
							Player affected = plugin.getServer().getPlayerExact(target);
							affected.setDisplayName(ChatColor.stripColor(affected.getDisplayName()));
							affected.sendMessage(ChatColor.GREEN + "Your name color has been removed.");
							if (affected != sender) {
								player.sendMessage(ChatColor.GREEN + "Removed "+ affected.getName() + "'s color.");
								return true;
							}
							return true;
						}
						// If player is offline remove it from the config.
						player.sendMessage(ChatColor.GREEN + "Removed "+ target + "'s color.");
						return true;
					}
					// Trying to remove a color from a color-less player
					if (!plugin.hasColor(target) && plugin.colors.contains(target)) {
						player.sendMessage(ChatColor.GREEN + target + " doesn't have a colored name.");
						return true;
					}
					// Return if player hasn't got the permission
					else if (!player.hasPermission("colorme.remove")) {
						player.sendMessage(ChatColor.RED + "You don't have permission to remove colors from others!");
						return true;
					}
					// Coloring self
					if (player.hasPermission("colorme.self")) {
						String color = plugin.findColor(newColor);
						if (color.equals(newColor)) {
							player.sendMessage(ChatColor.GREEN + "'" + newColor + "' is not a supported color.");
							return true;
						}
						// If the colors are the same
						if (newColor.equalsIgnoreCase(plugin.getColor(player.getName().toString()))) {
							player.sendMessage(ChatColor.GREEN + "You already have got this color!");
							return true;
						}
						// If Vault (economy) is enabled
						if (plugin.economy != null) {
							double cost = plugin.config.getDouble("cost");
							// Charge costs :)
							if (cost >0 && plugin.economy.has(player.getName(), cost)) {
								plugin.economy.withdrawPlayer(player.getName(), cost);
								plugin.setColor(player.getName(), newColor);
								player.sendMessage(ChatColor.GREEN + "You have been charged " + ChatColor.RED + plugin.economy.format(cost) + ChatColor.GREEN + '.');
								player.sendMessage(ChatColor.GREEN + "Changed your name's color to: " + ChatColor.valueOf(color) + newColor);
								player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
								return true;
							}
							// If player hasn't got enough money
							else if (cost >0 && plugin.economy.getBalance(player.getName()) < cost) {
								player.sendMessage(ChatColor.GREEN + "It costs " + ChatColor.GREEN + plugin.economy.format(cost) + ChatColor.GREEN + " to color your name.");
								return true;
							}
							// If it's for free
							else if (cost == 0) {
								plugin.setColor(player.getName(), newColor);
								player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
								player.sendMessage(ChatColor.GREEN + "Changed your name's color to: " + ChatColor.valueOf(color) + newColor);
								return true;
							}
						}
						// Wihtout economy
						else {
							plugin.setColor(player.getName(), newColor);
							player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
							player.sendMessage(ChatColor.GREEN + "Changed your name's color to: " + ChatColor.valueOf(color) + newColor);
							return true;
						}
					}
					// Deny access without permisison
					else if (!player.hasPermission("colorme.self")) {
						player.sendMessage(ChatColor.RED + "You don't have permission to color your own name.");
						return true;
					}
				}
				// If a player name and color are sent
				if (args.length > 1) {
					// If it equals remove
					if (args[0].equalsIgnoreCase("remove")) {
						String target = args[1];
						// If the player got the permission or is self
						if (plugin.hasColor(target) && (player.hasPermission("colorme.remove") || plugin.self(player, target))) {
							// Trying to remove a color from a color-less player
							if (!plugin.hasColor(target) && plugin.colors.contains(target)) {
								player.sendMessage(ChatColor.GREEN + target + " doesn't have a colored name.");
								return true;
							}
							plugin.removeColor(target);
							if (plugin.getServer().getPlayerExact(target) != null) {
								// Update displayname if online
								Player affected = plugin.getServer().getPlayerExact(target);
								affected.setDisplayName(ChatColor.stripColor(affected.getDisplayName()));
								affected.sendMessage(ChatColor.GREEN + "Your name color has been removed.");
								if (affected != sender) {
									player.sendMessage(ChatColor.GREEN + "Removed "+ affected.getName() + "'s color.");
									return true;
								}
								return true;
							}
							// If player is offline remove it from the config.
							player.sendMessage(ChatColor.GREEN + "Removed "+ target + "'s color.");
							return true;
						}
					}
					String target = args[0];
					String newColor = args[1];
					String color = plugin.findColor(newColor);
					// Return if color is not suitable
					if (color.equals(newColor)) {
						player.sendMessage(ChatColor.GREEN + "'" + newColor + "' is not a supported color.");
						return true;
					}
					// If the colors are the same
					if (newColor.equalsIgnoreCase(plugin.getColor(target))) {
						player.sendMessage(ChatColor.GREEN + target + " already has got this color!");
						return true;
					}
					// Coloring self with own name as argument
					if (plugin.self(player, target) && player.hasPermission("colorme.self")) {
						// Vault (economy) enabled
						if (plugin.economy != null) {
							double cost = plugin.config.getDouble("cost");
							// Player can afford to color their name
							if (cost > 0 && plugin.economy.getBalance(player.getName()) >= cost) {
								plugin.economy.withdrawPlayer(player.getName(), cost);
								plugin.setColor(player.getName(), newColor);
								player.sendMessage(ChatColor.GREEN + "You have been charged " + ChatColor.RED + plugin.economy.format(cost) + ChatColor.GREEN + '.');
								player.sendMessage(ChatColor.GREEN + "Changed your name's color to: " + ChatColor.valueOf(color) + newColor);
								player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
								return true;
							}
							// Player can't afford to color their name
							else if (cost >0 && plugin.economy.getBalance(player.getName()) < cost) {
								player.sendMessage(ChatColor.GREEN + "It costs " + ChatColor.RED  +plugin.economy.format(cost) + ChatColor.GREEN + " to color your name.");
								return true;
							}
							// No costs, color own name
							else if (cost == 0) {
								plugin.setColor(player.getName(), newColor);
								player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
								player.sendMessage(ChatColor.GREEN + "Changed your name's color to: " + ChatColor.valueOf(color) + newColor);
								return true;
							}
						}
						// Without economy
						else {
							plugin.setColor(player.getName(), newColor);
							player.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
							player.sendMessage(ChatColor.GREEN + "Changed your name's color to: " + ChatColor.valueOf(color) + newColor);
							return true;
						}
					}
					// Coloring someone else
					if (!plugin.self(player, target) && player.hasPermission("colorme.other")) {
						plugin.setColor(target, newColor);
						// Sets the display name if online
						if (plugin.getServer().getPlayerExact(target) != null) {
							Player affected = plugin.getServer().getPlayerExact(target);
							affected.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(player.getDisplayName()) + ChatColor.WHITE);
							player.sendMessage(ChatColor.GREEN + "Changed " + affected.getName() + "'s color to: "+ ChatColor.valueOf(color) + newColor);
							return true;
						}
						// Else changes the config value only
						player.sendMessage(ChatColor.GREEN  + "Changed " + target + "'s color to: " + ChatColor.valueOf(color) + newColor);
						return true;
					}
					// Return not enough permission, based on self or other
					else {
						player.sendMessage(ChatColor.RED + "You don't have permission to color " + (plugin.self(player, target) ? "your own" : "another player's") + " name.");
						return true;
					}
				}
			}
			// Console is a bit different
			else if (sender instanceof ConsoleCommandSender) {
				// Only removing
				if (args.length == 1) {
					String target = args[0];
					// Player has color, remove it
					if (plugin.hasColor(target)) {
						plugin.removeColor(target);
						// Player is online, update displayname
						if (plugin.getServer().getPlayerExact(target) != null) {
							Player affected = plugin.getServer().getPlayerExact(target);
							affected.setDisplayName(ChatColor.stripColor(affected.getDisplayName()));
							affected.sendMessage(ChatColor.GREEN + "Your name color has been removed.");
							sender.sendMessage("Removed " + affected.getName()+ "'s color.");
							return true;
						}
						// Else change only the config
						sender.sendMessage("Removed color from " + target);
						return true;
					}
					// Colorless
					sender.sendMessage(target + " doesn't have a colored name.");
					return true;
				}
				// Color a player
				if (args.length > 1) {
					// If it equals remove
					if (args[0].equalsIgnoreCase("remove")) {
						String target = args[1];
						// Trying to remove a color from a color-less player
						if (!plugin.hasColor(target) && plugin.colors.contains(target)) {
							sender.sendMessage(target + " doesn't have a colored name.");
							return true;
						}
						plugin.removeColor(target);
						if (plugin.getServer().getPlayerExact(target) != null) {
							// Update displayname if online
							Player affected = plugin.getServer().getPlayerExact(target);
							affected.setDisplayName(ChatColor.stripColor(affected.getDisplayName()));
							affected.sendMessage(ChatColor.GREEN + "Your name color has been removed.");
							sender.sendMessage("Removed "+ affected.getName() + "'s color.");
							return true;
						}
						// If player is offline remove it from the config.
						sender.sendMessage(ChatColor.GREEN + "Removed "+ target + "'s color.");
						return true;
					}
					String target = args[0];
					String newColor = args[1];
					if (plugin.setColor(target, newColor)) {
						String color = plugin.findColor(newColor);
						if (plugin.getServer().getPlayerExact(target) != null) {
							// Player is online, change their displayname immediately
							Player affected = plugin.getServer().getPlayerExact(target);
							affected.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(affected.getDisplayName()) + ChatColor.WHITE);
							affected.sendMessage(ChatColor.GREEN + "Your name color has been changed to " + newColor);
							sender.sendMessage("Changed " + affected.getName()+ "'s color to: " + newColor);
							return true;
						}
						// Else only config
						sender.sendMessage("Changed " + target + "'s color to: " + newColor);
						return true;
					}
					else {
						// If the colors are the same
						if (newColor.equalsIgnoreCase(plugin.getColor(target))) {
							sender.sendMessage(target + " already has got this color!");
							return true;
						}
						// Othwise tell that the color isn't okay!
						else {
							sender.sendMessage("'" + newColor + "' is not a supported color.");
							return true;
						}
					}
				}
			}
		}
		return false;
	}

	//Reloads the config with /colorme reload
	private boolean ColorMeReload(CommandSender sender, String[] args) {
		PluginDescriptionFile pdfFile = plugin.getDescription();
		plugin.loadConfigAgain();		
		sender.sendMessage(ChatColor.DARK_GREEN + "ColorMe version " + ChatColor.DARK_RED + pdfFile.getVersion() + ChatColor.DARK_GREEN + " reloaded!");
		return true;
	}
	
	// Displays the help with /colorme help
	private boolean ColorMeHelp(CommandSender sender, String[] args) {
		PluginDescriptionFile pdfFile = plugin.getDescription();
		sender.sendMessage(ChatColor.DARK_GREEN	+ "Welcome to the ColorMe version " + ChatColor.DARK_RED + pdfFile.getVersion() + ChatColor.DARK_GREEN + " help!");
		sender.sendMessage(ChatColor.RED + "<> = Required");
		sender.sendMessage("/<command> list - Shows list of colors");
		sender.sendMessage("/<command> <name> - Removes color");
		sender.sendMessage("/<command> remove <name> - Removes color");
		sender.sendMessage("/<command> list - Shows list of colors");
		sender.sendMessage("/<command> <color> - Sets your own color");
		sender.sendMessage("/<command> <name> <color> - Sets player's color");
		return true;
		
	}
}
