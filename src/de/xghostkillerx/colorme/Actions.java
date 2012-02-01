package de.xghostkillerx.colorme;

import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

public class Actions {
	
	static ColorMe plugin;
	public Actions(ColorMe instance) {
		plugin = instance;
	}

	// Return the player's name color
	public static String getColor(String name) {
		// Player in the config? Yes -> get the config, no -> nothing
		return (plugin.colors.contains(name.toLowerCase())) ? plugin.colors.getString(name.toLowerCase()) : "";
	}

	// Set player's color
	public static boolean setColor(String name, String color) {
		String actualColor = getColor(name);
		// If the colors are the same return false
		if (actualColor.equalsIgnoreCase(color)) {
			return false;
		}
		// Write to the config and save and update the names
		plugin.colors.set(name, color.toLowerCase());
		plugin.saveColors();
		updateName(name);
		return true;
	}

	// Update the displayName, tabName & title (after setting, removing, onJoin and onChat)
	@SuppressWarnings("deprecation")
	public static void updateName(String name) {
		Player player = plugin.getServer().getPlayerExact(name);
		if (player != null) {
			String color = getColor(name);
			String displayName = player.getDisplayName();
			String cleanDisplayName = ChatColor.stripColor(displayName);
			String newName = "";
			boolean tabList = plugin.config.getBoolean("tabList");
			boolean playerTitle = plugin.config.getBoolean("playerTitle");
			// If the player has a color change the displayname
			if (hasColor(name)) {
				if (validColor(plugin.colors.getString(name)) == true) {
					// Random
					if (color.equalsIgnoreCase("random")) {
						player.setDisplayName(randomColor(cleanDisplayName) + ChatColor.WHITE);
						if (tabList == true) {
							// If the TAB name is longer than 16 shorten it!
							newName = randomColor(cleanDisplayName);
							if (newName.length() > 16) {
								newName = newName.substring(0, 12) + ChatColor.WHITE + "..";
							}
							player.setPlayerListName(newName);
						}
					}
					// Rainbow
					if (color.equalsIgnoreCase("rainbow")) {
						player.setDisplayName(rainbowColor(cleanDisplayName) + ChatColor.WHITE);
						if (tabList == true) {
							// If the TAB name is longer than 16 shorten it!
							newName = rainbowColor(cleanDisplayName);
							if (newName.length() > 16) {
								newName = newName.substring(0, 12) + ChatColor.WHITE + "..";
							}
							player.setPlayerListName(newName);
						}
					}
					// Normal
					else if (!color.equalsIgnoreCase("random") && !color.equalsIgnoreCase("rainbow")) {
						player.setDisplayName(ChatColor.valueOf(color.toUpperCase()) + ChatColor.stripColor(displayName) + ChatColor.WHITE);
						if (tabList == true) {
							// If the TAB name is longer than 16 shorten it!
							newName = ChatColor.valueOf(color.toUpperCase()) + ChatColor.stripColor(displayName);
							if (newName.length() > 16) {
								newName = newName.substring(0, 12) + ChatColor.WHITE + "..";
							}
							player.setPlayerListName(newName);
						}
					}
					// Check for Spout
					if (plugin.spoutEnabled == true && playerTitle == true) {
						// Random color
						if (getColor(name).equalsIgnoreCase("random")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, randomColor(displayName));
						}
						// Rainbow
						if (getColor(name).equalsIgnoreCase("rainbow")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, rainbowColor(displayName));
						}
						// Normal color
						else if (!getColor(name).equalsIgnoreCase("random") && !getColor(name).equalsIgnoreCase("rainbow")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, ChatColor.valueOf(color.toUpperCase()) + ChatColor.stripColor(displayName));
						}
					}
				}
				else {
					// Tell player to report it, but suppress the error -> uses color before.
					player.sendMessage("Your name colors seems to be invalid. Ask your admin to check it,");
					player.sendMessage("or try re-coloring!");
				}
			}
			if (!hasColor(name)) {
				// No name -> back to white
				player.setDisplayName(ChatColor.WHITE + ChatColor.stripColor(displayName));
				if (tabList == true) {
					// If the TAB name is longer than 16 shorten it!
					newName = cleanDisplayName;
					if (newName.length() > 16) {
						newName = cleanDisplayName.substring(0, 12) + ChatColor.WHITE + "..";
					}
					player.setPlayerListName(newName);
				}
				if (plugin.spoutEnabled == true && playerTitle == true) {
					SpoutManager.getAppearanceManager().setGlobalTitle(player, ChatColor.WHITE + ChatColor.stripColor(displayName));
				}
			}
		}
	}

	// Check if a player has a color or not
	public static boolean hasColor(String name) {
		if (plugin.colors.contains(name.toLowerCase())) {
			// if longer than 1 it's a color, return true - otherwise (means '') return false
			return (plugin.colors.getString(name.toLowerCase())).trim().length() >1 ? true : false;
		}
		return false;
	}

	// Removes a color if exists, otherwise returns false | Spout causes deprecation
	public static boolean removeColor(String name) {
		name = name.toLowerCase();
		// If the player has got a color
		if (hasColor(name)) {
			plugin.colors.set(name, "");
			plugin.saveColors();
			updateName(name);
			return true;
		}
		return false;
	}

	// Checks if the player is itself
	public static boolean self(CommandSender sender, String name) {
		return (sender.equals(plugin.getServer().getPlayerExact(name))) ? true : false;
	}

	// The list of colors
	@SuppressWarnings("deprecation")
	public static void listColors(CommandSender sender) {
		sender.sendMessage("Color List:");     
		String msg = "";
		// As long as all colors aren't reached
		for (int i = 0; i < ChatColor.values().length; i++) {
			// get the name from the integer
			String color = ChatColor.getByCode(i).name();
			// color the name of the color
			msg += ChatColor.valueOf(color) + color.toLowerCase() + " ";
		}
		// Include custom colors
		sender.sendMessage(msg + randomColor("random") + " " + rainbowColor("rainbow"));
	}

	// Used to create a random effect
	@SuppressWarnings("deprecation")
	public static String randomColor(String name) {
		String newName = "";
		char ch;
		// As long as the length of the name isn't reached
		for (int i = 0; i < name.length(); i++) {
			// Roll the dice between 0 and 15 ;)
			int x = (int)(Math.random()*16);
			ch = name.charAt(i);
			// Color the character
			newName += ChatColor.getByCode(x) + Character.toString(ch);
		}
		return newName;
	}

	// Used to create a rainbow effect
	public static String rainbowColor(String name) {
		String newName = "";
		char ch;
		int z = 0;
		// Had to store the rainbow manually. Why did Mojang store it so..., forget it
		String rainbow[] = {"DARK_RED", "RED", "GOLD", "YELLOW", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "BLUE", "DARK_BLUE", "LIGHT_PURPLE", "DARK_PURPLE"};
		// As long as the length of the name isn't reached
		for (int i = 0; i < name.length(); i++) {
			// Reset if z reaches 12
			if (z == 12) z = 0;
			ch = name.charAt(i);
			// Add to the new name the colored character
			newName += ChatColor.valueOf(rainbow[z]) + Character.toString(ch);
			z++;
		}
		return newName;
	}

	// Check if the color is possible
	@SuppressWarnings("deprecation")
	public static boolean validColor(String color) {
		// if it's random or rainbow -> possible
		if (color.equalsIgnoreCase("rainbow") || color.equalsIgnoreCase("random")) {
			return true;
		}
		// Second place, cause random and rainbow aren't possible normally ;)
		else {
			for (int i=0; i < ChatColor.values().length; i++) {
				// Check if the color is one of the 16
				if (color.equalsIgnoreCase(ChatColor.getByCode(i).name())) {
					return true;
				}
			}
			return false;
		}
	}

	// If the config value is disabled, return true
	public static boolean isDisabled(String color) {
		if (plugin.config.getBoolean("colors." + color.toLowerCase()) == true) {
			return false;
		}
		return true;
	}
}
