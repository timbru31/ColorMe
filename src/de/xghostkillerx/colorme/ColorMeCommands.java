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
	// TODO rewrite den ganze scheiß^^
	public boolean ColorMeCommand (CommandSender sender, Command command, String commandLabel, String[] args) {
		if ((command.getName().equalsIgnoreCase("colorme")) || (command.getName().equalsIgnoreCase("color"))) {
			// reload
			if (args.length > 0 && args[0].equals("reload")) {
				if (sender.hasPermission("colorme.reload")) {
					ColorMeReload(sender, args);
					return true;
				}
				else {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have the permission to do this!");
					return true;
				}
			}
			if (args.length > 0 && args[0].equals("list")) {
				if (sender.hasPermission("colorme.list")) {
					plugin.list(sender);
					return true;
				}
				else {
					sender.sendMessage(ChatColor.DARK_RED + "You don't have the permission to do this!");
					return true;
				}
			}
			// Only if a player is the sender
			if (sender instanceof Player && args.length >=1) {
				//Player player = ((Player) sender);
				//				switch(args.length) {
				//				case 1:
				//					if (plugin.hasColor(a0) && (player.hasPermission("colorme.remove") || plugin.self(player, a0))) {
				//						plugin.removeColor(a0);
				//						if (plugin.getServer().getPlayer(a0) != null) {
				//							// Update displayname
				//							Player other = plugin.getServer().getPlayer(a0);
				//							other.setDisplayName(ChatColor.stripColor(other.getDisplayName()));
				//							other.sendMessage(ChatColor.GREEN+"Your name color has been removed.");
				//							if (other != sender) sender.sendMessage(ChatColor.GREEN+"Removed "+other.getName()+"'s color.");
				//							return true;
				//						}
				//						sender.sendMessage((plugin.self(player, a0)) ? ChatColor.GREEN+"Removed your color." : ChatColor.GREEN+"Removed color from"+a0+'.');
				//						return true;
				//					}
				//					if (!plugin.hasColor(a0) && plugin.colors.contains(a0)) return true; // Trying to remove a color from a color-less player
				//					if (player.hasPermission("colorme.self")) {
				//						String color = plugin.findColor(a0);
				//						if (color.equals(a0)) {
				//							player.sendMessage(ChatColor.GREEN+"'"+a0+"' is not a supported color.");
				//							return true;
				//						}
				//						if (plugin.economy != null) {
				//							double cost = plugin.config.getDouble("cost");
				//							if (cost>0 && plugin.economy.has(player.getName(), cost)) {
				//								plugin.economy.withdrawPlayer(player.getName(), cost);
				//								plugin.setColor(player.getName(), a0);
				//								player.sendMessage(ChatColor.GREEN+"You have been charged "+ChatColor.RED+plugin.economy.format(cost)+ChatColor.GREEN+'.');
				//								player.setDisplayName(ChatColor.valueOf(color)+ChatColor.stripColor(player.getDisplayName())+ChatColor.WHITE);
				//							} else if (cost>0 && plugin.economy.getBalance(player.getName()) < cost) {
				//								player.sendMessage(ChatColor.GREEN+"It costs "+ChatColor.GREEN+plugin.economy.format(cost)+ChatColor.GREEN+" to color your name.");
				//							}
				//						} else {
				//							plugin.setColor(player.getName(), a0);
				//							player.setDisplayName(ChatColor.valueOf(color)+ChatColor.stripColor(player.getDisplayName())+ChatColor.WHITE);
				//							player.sendMessage(ChatColor.GREEN+"Your name color is now: "+ChatColor.valueOf(color)+a0);
				//						}
				//						return true;
				//					} else if (!player.hasPermission("colorme.self")) {
				//						player.sendMessage(ChatColor.GREEN+"You don't have permission to color your own name.");
				//						return true;
				//					}
				//					break;
				//				case 2:
				//					String a1 = args[1];
				//					String color = plugin.findColor(a1);
				//					if (color.equals(a1)) {
				//						player.sendMessage(ChatColor.GREEN+"'"+a1+"' is not a supported color.");
				//						return true;
				//					}
				//					if (plugin.self(player, a0) && player.hasPermission("colorme.self")) {
				//						// Coloring self
				//						if (plugin.economy != null) {
				//							// iConomy enabled
				//							double cost = plugin.config.getDouble("cost");
				//							if (cost>0 && plugin.economy.getBalance(player.getName()) >= cost) {
				//								// Player can afford to color their name
				//								plugin.economy.withdrawPlayer(player.getName(), cost);
				//								plugin.setColor(player.getName(), a1);
				//								player.sendMessage(ChatColor.GREEN+"You have been charged "+ChatColor.RED+plugin.economy.format(cost)+ChatColor.GREEN+'.');
				//								player.setDisplayName(ChatColor.valueOf(color)+ChatColor.stripColor(player.getDisplayName())+ChatColor.WHITE);
				//							} else if (cost>0 && plugin.economy.getBalance(player.getName()) < cost) {
				//								// Player can't afford to color their name
				//								player.sendMessage(ChatColor.GREEN+"It costs "+ChatColor.RED+plugin.economy.format(cost)+ChatColor.GREEN+" to color your name.");
				//							} else if (0 == cost) {
				//								// No cost, color own name
				//								plugin.setColor(player.getName(), a1);
				//								player.setDisplayName(ChatColor.valueOf(color)+ChatColor.stripColor(player.getDisplayName())+ChatColor.WHITE);
				//								player.sendMessage(ChatColor.GREEN+"Changed your name's color to: "+ChatColor.valueOf(color)+a1);
				//							}
				//							return true;
				//						} else {
				//							// iConomy NOT enabled
				//							plugin.setColor(player.getName(), a1);
				//							player.setDisplayName(ChatColor.valueOf(color)+ChatColor.stripColor(player.getDisplayName())+ChatColor.WHITE);
				//							player.sendMessage(ChatColor.GREEN+"Changed your name's color to: "+ChatColor.valueOf(color)+a1);
				//							return true;
				//						}
				//					} else if (!plugin.self(player, a0) && player.hasPermission("colorme.other")) {
				//						// Coloring someone else
				//						plugin.setColor(a0, a1);
				//						if (plugin.getServer().getPlayer(a0) != null) {
				//							Player other = plugin.getServer().getPlayer(a0);
				//							other.setDisplayName(ChatColor.valueOf(color)+ChatColor.stripColor(player.getDisplayName())+ChatColor.WHITE);
				//							player.sendMessage(ChatColor.GREEN+"Changed "+other.getName()+"'s color to: "+ChatColor.valueOf(color)+a1);
				//							return true;
				//						}
				//						player.sendMessage(ChatColor.GREEN+"Changed "+a0+"'s color to: "+ChatColor.valueOf(color)+a1);
				//						return true;
				//					} else {
				//						player.sendMessage(ChatColor.GREEN+"You don't have permission to color "+(plugin.self(player, a0) ? "your own" : "another player's")+" name.");
				//						return true;
				//					}
				//				default: return false;
				//				}
			}
			else if (sender instanceof ConsoleCommandSender) {
				if (args.length == 1) {
					String player = args[0];
					if (plugin.hasColor(player)) {
						// Player has color, remove it
						plugin.removeColor(player);
						if (plugin.getServer().getPlayerExact(player) != null) {
							// Player is online, update displayname
							Player other = plugin.getServer().getPlayerExact(player);
							other.setDisplayName(ChatColor.stripColor(other.getDisplayName()));
							other.sendMessage("Your name color has been removed.");
							sender.sendMessage("Removed " + other.getName()+ "'s color.");
							return true;
						}
						sender.sendMessage("Removed color from " + player);
						return true;
					}
					sender.sendMessage(player + " doesn't have a colored name.");
					return true;
				}
				if (args.length > 1) {
					String player = args[0];
					String newColor = args[1];
					if (plugin.setColor(player, newColor)) {
						String color = plugin.findColor(newColor);
						if (plugin.getServer().getPlayerExact(player) != null) {
							// Player is online, change their displayname immediately
							Player other = plugin.getServer().getPlayerExact(player);
							other.setDisplayName(ChatColor.valueOf(color) + ChatColor.stripColor(other.getDisplayName()) + ChatColor.WHITE);
							other.sendMessage("Your name color has been changed to " + newColor);
							sender.sendMessage("Changed " + other.getName()+ "'s color to: " + newColor);
							return true;
						}
						sender.sendMessage("Changed " + player + "'s color to: " + newColor);
						return true;
					}
					else {
						// If the colors are the same
						if (newColor.equalsIgnoreCase(plugin.getColor(player))) {
							sender.sendMessage(player + " already has got this color!");
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
}
