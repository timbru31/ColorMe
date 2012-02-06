package de.xghostkillerx.colorme;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.SpoutManager;

public class Actions {
	public static ColorMe plugin;
	public Actions(ColorMe instance) {
		plugin = instance;
	}

	private static String actualValue, pluginPart, color, colorChar, displayName, cleanDisplayName, newName, msg, message, sub;
	private static int i, z = 0;
	private static char ch;

	// Checks if the player is itself
	static boolean self(CommandSender sender, String name) {
		return (sender.equals(Bukkit.getServer().getPlayerExact(name))) ? true : false;
	}

	// Return the player's name color/prefix/suffix
	static String get(String name, String world, String pluginPart) {
		// Player in the config? Yes -> get the config, no -> nothing
		if (ColorMe.players.contains(name + "." + pluginPart + "." + world)) {
			String string = ColorMe.players.getString(name + "." + pluginPart + "." + world);
			// While rainbow is in there
			while (string.contains("&rainbow")) {
				// Without rainbow
				int i = string.indexOf("&rainbow") + 8;
				int z = string.length();
				sub = string.substring(i, z);
				// Stop if other & is found
				if (sub.contains("&")) {
					sub = sub.substring(0, sub.indexOf("&"));
				}
				// Replace
				string = string.replace(sub, rainbowColor(sub));
				// Replace FIRST rainbow
				string = string.replaceFirst("&rainbow", "");
				sub = "";
			}
			// While random is in there
			while (string.contains("&random")) {
				// Without rainbow
				int i = string.indexOf("&random") + 7;
				int z = string.length();
				sub = string.substring(i, z);
				sub = string.substring(i, z);
				// Stop if other & is found
				if (sub.contains("&")) {
					sub = sub.substring(0, sub.indexOf("&"));
				}
				// Replace
				string = string.replace(sub, randomColor(sub));
				// Replace FIRST random
				string = string.replaceFirst("&random", "");
				sub = "";
			}
			// Normal color codes!
			string = string.replaceAll("&([0-9a-fk])", "\u00A7$1");
			return string;
		}
		else return "";
	}

	// Get global default if not null
	public static String getGlobal(String pluginPart) {
		String string = ColorMe.config.getString("global_default." + pluginPart);
		// While rainbow is in there
		while (string.contains("&rainbow")) {
			// Without rainbow
			int i = string.indexOf("&rainbow") + 8;
			int z = string.length();
			sub = string.substring(i, z);
			// Stop if other & is found
			if (sub.contains("&")) {
				sub = sub.substring(0, sub.indexOf("&"));
			}
			// Replace
			string = string.replace(sub, rainbowColor(sub));
			// Replace FIRST rainbow
			string = string.replaceFirst("&rainbow", "");
			sub = "";
		}
		// While random is in there
		while (string.contains("&random")) {
			// Without rainbow
			int i = string.indexOf("&random") + 7;
			int z = string.length();
			sub = string.substring(i, z);
			sub = string.substring(i, z);
			// Stop if other & is found
			if (sub.contains("&")) {
				sub = sub.substring(0, sub.indexOf("&"));
			}
			// Replace
			string = string.replace(sub, randomColor(sub));
			// Replace FIRST random
			string = string.replaceFirst("&random", "");
			sub = "";
		}
		// Normal color codes!
		string = string.replaceAll("&([0-9a-fk])", "\u00A7$1");
		return string;
	}

	// Set player's color/prefix/suffix
	static boolean set(String name, String value, String world, String pluginPart) {
		actualValue = get(name, world, pluginPart);
		// If the colors are the same return false
		if (actualValue.equalsIgnoreCase(value)) {
			return false;
		}
		// Write to the config and save and update the names
		ColorMe.players.set(name + "." + pluginPart + "." + world, value);
		ColorMe.savePlayers();
		updateName(name, world);
		return true;
	}

	// Check if a player has a color/prefix/suffix or not
	static boolean has(String name, String world, String pluginPart) {
		name = name.toLowerCase();
		if (ColorMe.players.contains(name + "." + pluginPart + "." + world)) {
			// if longer than 1 it's a color, return true - otherwise (means '') return false
			return (ColorMe.players.getString(name + "." + pluginPart + "." + world)).trim().length() > 1 ? true : false;
		}
		return false;
	}

	// Check if the global default is not null
	public static boolean hasGlobal(String pluginPart) {
		return ColorMe.config.getString("global_default." + pluginPart).trim().length() > 1 ? true : false;
	}

	// Removes a color/prefix/suffix if exists, otherwise returns false
	static boolean remove(String name, String world, String pluginPart) {
		name = name.toLowerCase();
		// If the player has got a color
		if (has(name, world, pluginPart)) {
			ColorMe.players.set(name  + "." + pluginPart + "." + world, "");
			ColorMe.savePlayers();
			updateName(name, world);
			return true;
		}
		return false;
	}

	// Update the displayName, tabName, title, prefix & suffix in a specific world (after setting, removing, onJoin and onChat)
	@SuppressWarnings("deprecation")
	static void updateName(String name, String world) {
		Player player = Bukkit.getServer().getPlayerExact(name);
		if (player != null) {
			pluginPart = "colors";
			displayName = player.getDisplayName();
			cleanDisplayName = ChatColor.stripColor(displayName);
			boolean tabList = ColorMe.config.getBoolean("tabList");
			boolean playerTitle = ColorMe.config.getBoolean("playerTitle");
			// If the player has a color change the displayname
			if (has(name, world, pluginPart)) {
				if (validColor(ColorMe.players.getString(name + "." + pluginPart + "." + world)) == true) {
					// Random
					color = get(name, world, pluginPart);
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
					if (ColorMe.spoutEnabled == true && playerTitle == true) {
						// Random color
						if (get(name, world, pluginPart).equalsIgnoreCase("random")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, randomColor(displayName));
						}
						// Rainbow
						if (get(name, world, pluginPart).equalsIgnoreCase("rainbow")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, rainbowColor(displayName));
						}
						// Normal color
						else if (!get(name, world, pluginPart).equalsIgnoreCase("random") && !get(name, world, pluginPart).equalsIgnoreCase("rainbow")) {
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
			if (!has(name, world, pluginPart)) {
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
				if (ColorMe.spoutEnabled == true && playerTitle == true) {
					SpoutManager.getAppearanceManager().setGlobalTitle(player, ChatColor.WHITE + ChatColor.stripColor(displayName));
				}
			}
		}
	}

	@SuppressWarnings("deprecation")
	public static void updateNameGlobal(String name) {
		Player player = Bukkit.getServer().getPlayerExact(name);
		if (player != null) {
			pluginPart = "color";
			displayName = player.getDisplayName();
			cleanDisplayName = ChatColor.stripColor(displayName);
			boolean tabList = ColorMe.config.getBoolean("tabList");
			boolean playerTitle = ColorMe.config.getBoolean("playerTitle");
			// If the player has a color change the displayname
			if (hasGlobal(pluginPart)) {
				if (validColor(ColorMe.config.getString("global_default." + pluginPart)) == true) {
					// Random
					color = getGlobal(pluginPart);
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
					if (ColorMe.spoutEnabled == true && playerTitle == true) {
						// Random color
						if (getGlobal(pluginPart).equalsIgnoreCase("random")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, randomColor(displayName));
						}
						// Rainbow
						if (getGlobal(pluginPart).equalsIgnoreCase("rainbow")) {
							SpoutManager.getAppearanceManager().setGlobalTitle(player, rainbowColor(displayName));
						}
						// Normal color
						else if (!getGlobal(pluginPart).equalsIgnoreCase("random") && !getGlobal(pluginPart).equalsIgnoreCase("rainbow")) {
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
			if (!hasGlobal(pluginPart)) {
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
				if (ColorMe.spoutEnabled == true && playerTitle == true) {
					SpoutManager.getAppearanceManager().setGlobalTitle(player, ChatColor.WHITE + ChatColor.stripColor(displayName));
				}
			}
		}
	}

	// The list of colors
	@SuppressWarnings("deprecation")
	static void listColors(CommandSender sender) {
		message = ColorMe.localization.getString("color_list");
		ColorMe.message(sender, null, message, null, null, null, null);
		msg = "";
		i = 0;
		// As long as all colors aren't reached
		for (i = 0; i < ChatColor.values().length; i++) {
			// get the name from the integer
			color = ChatColor.getByCode(i).name().toLowerCase();
			colorChar = Character.toString(ChatColor.getByCode(i).getChar());
			// color the name of the color
			if (ColorMe.config.getBoolean("colors." + color) == true) {
				msg += ChatColor.valueOf(color.toUpperCase()) + color + " (&" + colorChar + ") ";
			}
		}
		// Include custom colors
		if (ColorMe.config.getBoolean("colors.random") == true) {
			msg += randomColor("random") + " (&rainbow) ";
		}
		if (ColorMe.config.getBoolean("colors.rainbow") == true) {
			msg += rainbowColor("rainbow") + " (&random)";
		}
		sender.sendMessage(msg);
	}

	// Used to create a random effect
	@SuppressWarnings("deprecation")
	static String randomColor(String name) {
		newName = "";
		i = 0;
		// As long as the length of the name isn't reached
		for (i = 0; i < name.length(); i++) {
			// Roll the dice between 0 and 15 ;)
			int x = (int)(Math.random()*16);
			ch = name.charAt(i);
			// Color the character
			newName += ChatColor.getByCode(x) + Character.toString(ch);
		}
		return newName;
	}

	// Used to create a rainbow effect
	static String rainbowColor(String name) {
		// Had to store the rainbow manually. Why did Mojang store it so..., forget it
		newName = "";
		i = 0;
		z = 0;
		String rainbow[] = {"DARK_RED", "RED", "GOLD", "YELLOW", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "BLUE", "DARK_BLUE", "LIGHT_PURPLE", "DARK_PURPLE"};
		// As long as the length of the name isn't reached
		for (i = 0; i < name.length(); i++) {
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
	static boolean validColor(String color) {
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
	static boolean isDisabled(String color) {
		if (ColorMe.config.getBoolean("colors." + color.toLowerCase()) == true) {
			return false;
		}
		return true;
	}
	
	// Displays the specific help
	static boolean help(CommandSender sender, String pluginPart) {
		for (i = 1; i <= 8; i++) {
			message = ColorMe.localization.getString("help_" + pluginPart + "_" + Integer.toString(i));
			ColorMe.message(sender, null, message, null, null, null, null);
		}
		return true;
	}
	
	// Reloads the plugin
	static boolean reload(CommandSender sender) {
		plugin.loadConfigsAgain();		
		message = ColorMe.localization.getString("reload");
		ColorMe.message(sender, null, message, null, null, null, null);
		return true;
	}
}
