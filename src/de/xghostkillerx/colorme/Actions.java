package de.xghostkillerx.colorme;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;

public class Actions {
	public static ColorMe plugin;
	public Actions(ColorMe instance) {
		plugin = instance;
	}

	// Checks if the player is itself
	static boolean self(CommandSender sender, String name) {
		return (sender.equals(Bukkit.getServer().getPlayerExact(name))) ? true : false;
	}

	// Return the player's name color/prefix/suffix
	public static String get(String name, String world, String pluginPart) {
		// Player in the config? Yes -> get the config, no -> nothing
		if (ColorMe.players.contains(name + "." + pluginPart + "." + world)) {
			String string = ColorMe.players.getString(name + "." + pluginPart + "." + world);
			String updatedString = replaceThings(string);
			return updatedString;
		}
		else return "";
	}

	// Get global default
	public static String getGlobal(String pluginPart) {
		String string = ColorMe.config.getString("global_default." + pluginPart);
		String updatedString = replaceThings(string);
		return updatedString;
	}

	public static String replaceThings(String string) {
		// While random is in there
		String sub;
		while (string.contains("&random")) {
			// Without random
			int i = string.indexOf("&random") + 7;
			int z = string.length();
			sub = string.substring(i, z);
			// Stop if other & or § is found
			if (sub.contains("&")) {
				sub = sub.substring(0, sub.indexOf("&"));
			}
			if (sub.contains("§")) {
				sub = sub.substring(0, sub.indexOf("§"));
			}
			// Replace
			string = string.replace(sub, randomColor(sub));
			// Replace FIRST random
			string = string.replaceFirst("&random", "");
			sub = "";
		}
		// While random (short) is in there
		while (string.contains("&ran")) {
			// Without random
			int i = string.indexOf("&ran") + 4;
			int z = string.length();
			sub = string.substring(i, z);
			// Stop if other & or § is found
			if (sub.contains("&")) {
				sub = sub.substring(0, sub.indexOf("&"));
			}
			if (sub.contains("§")) {
				sub = sub.substring(0, sub.indexOf("§"));
			}
			// Replace
			string = string.replace(sub, randomColor(sub));
			// Replace FIRST random
			string = string.replaceFirst("&ran", "");
			sub = "";
		}
		// While rainbow is in there
		while (string.contains("&rainbow")) {
			// Without rainbow
			int i = string.indexOf("&rainbow") + 8;
			int z = string.length();
			sub = string.substring(i, z);
			// Stop if other & or § is found
			if (sub.contains("&")) {
				sub = sub.substring(0, sub.indexOf("&"));
			}
			if (sub.contains("§")) {
				sub = sub.substring(0, sub.indexOf("§"));
			}
			// Replace
			string = string.replace(sub, rainbowColor(sub));
			// Replace FIRST rainbow
			string = string.replaceFirst("&rainbow", "");
			sub = "";
		}
		// While rainbow (short) is in there
		while (string.contains("&rai")) {
			// Without rainbow
			int i = string.indexOf("&rai") + 4;
			int z = string.length();
			sub = string.substring(i, z);
			// Stop if other & or § is found
			if (sub.contains("&")) {
				sub = sub.substring(0, sub.indexOf("&"));
			}
			if (sub.contains("§")) {
				sub = sub.substring(0, sub.indexOf("§"));
			}
			// Replace
			string = string.replace(sub, rainbowColor(sub));
			// Replace FIRST rainbow
			string = string.replaceFirst("&rai", "");
			sub = "";
		}
		// Normal color codes!
		string = string.replaceAll("&([0-9a-fk-or])", "\u00A7$1");
		return string;
	}

	// Set player's color/prefix/suffix
	static boolean set(String name, String value, String world, String pluginPart) {
		String actualValue = get(name, world, pluginPart);
		// If the colors are the same return false
		if (actualValue.equalsIgnoreCase(value)) {
			return false;
		}
		// Write to the config and save and update the names
		ColorMe.players.set(name + "." + pluginPart + "." + world, value);
		ColorMe.savePlayers();
		checkNames(name, world);
		return true;
	}

	// Check if a player has a color/prefix/suffix or not
	public static boolean has(String name, String world, String pluginPart) {
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
			checkNames(name, world);
			return true;
		}
		return false;
	}

	// Removes a color/prefix/suffix if exists, otherwise returns false
	static boolean removeGlobal(String pluginPart) {
		ColorMe.config.set("global_default." + pluginPart, "");
		ColorMe.saveConfigs();
		return false;
	}

	// Update the displayName, tabName, title, prefix & suffix in a specific world (after setting, removing, onJoin and onChat)
	@SuppressWarnings("null")
	static void updateName(String name, String color) {
		Player player = Bukkit.getServer().getPlayerExact(name);
		if (player != null) {
			String displayName = player.getDisplayName();
			String cleanDisplayName = ChatColor.stripColor(displayName);
			String newName;
			boolean tabList = ColorMe.config.getBoolean("ColorMe.tabList");
			boolean playerTitle = ColorMe.config.getBoolean("ColorMe.playerTitle");
			// Name color
			if (ColorMe.config.getBoolean("ColorMe.displayName")) {
				// Random
				if (color.equalsIgnoreCase("random")) {
					player.setDisplayName(randomColor(cleanDisplayName) + ChatColor.WHITE);
				}
				// Rainbow
				else if (color.equalsIgnoreCase("rainbow")) {
					player.setDisplayName(rainbowColor(cleanDisplayName) + ChatColor.WHITE);
				}
				// Custom colors
				else if (ColorMe.colors.contains(color) && (ColorMe.colors.getString(color).trim().length() > 1 ? true : false) == true) {
					player.setDisplayName(updateCustomColor(color, cleanDisplayName, player) + ChatColor.WHITE);
				}
				// Normal
				else {
					player.setDisplayName(ChatColor.valueOf(color.toUpperCase()) + cleanDisplayName + ChatColor.WHITE);
				}
			}
			// Check for playerList
			if (tabList) {
				if (color.equalsIgnoreCase("random")) {
					newName = randomColor(cleanDisplayName);
				}
				else if (color.equalsIgnoreCase("rainbow")) {
					newName = rainbowColor(cleanDisplayName);
				}
				else if (ColorMe.colors.contains(color) && (ColorMe.colors.getString(color).trim().length() > 1 ? true : false) == true) {
					newName = updateCustomColor(color, cleanDisplayName, player);
				}
				else newName = ChatColor.valueOf(color.toUpperCase()) + cleanDisplayName;
				// Shorten it, if too long
				if (newName != null || !newName.equals("")) {
					if (newName.length() > 16) {
						newName = newName.substring(0, 12) + ChatColor.WHITE + "..";
					}
					player.setPlayerListName(newName);
				}
			}
			// Check for Spout
			if (ColorMe.spoutEnabled && playerTitle) {
				SpoutPlayer spoutPlayer = (SpoutPlayer) player;
				// Random color
				if (color.equalsIgnoreCase("random")) {
					spoutPlayer.setTitle(randomColor(cleanDisplayName));
				}
				// Rainbow
				else if (color.equalsIgnoreCase("rainbow")) {
					spoutPlayer.setTitle(rainbowColor(cleanDisplayName));
				}
				else if (ColorMe.colors.contains(color) && (ColorMe.colors.getString(color).trim().length() > 1 ? true : false) == true) {
					spoutPlayer.setTitle(updateCustomColor(color, cleanDisplayName, player));
				}
				// Normal color
				else spoutPlayer.setTitle(ChatColor.valueOf(color.toUpperCase()) + cleanDisplayName);
			}
		}
	}

	// Restore the "clean", white name
	static void restoreName(String name) {
		Player player = Bukkit.getServer().getPlayerExact(name);
		if (player != null) {
			String displayName = player.getDisplayName();
			String cleanDisplayName = ChatColor.stripColor(displayName);
			boolean tabList = ColorMe.config.getBoolean("tabList");
			boolean playerTitle = ColorMe.config.getBoolean("playerTitle");
			// No name -> back to white
			player.setDisplayName(ChatColor.WHITE + cleanDisplayName);
			if (tabList == true) {
				// If the TAB name is longer than 16 shorten it!
				String newName = cleanDisplayName;
				if (newName.length() > 16) {
					newName = cleanDisplayName.substring(0, 12) + ChatColor.WHITE + "..";
				}
				player.setPlayerListName(newName);
			}
			if (ColorMe.spoutEnabled == true && playerTitle == true) {
				SpoutPlayer spoutPlayer = (SpoutPlayer) player;
				spoutPlayer.resetTitle();
			}
		}
	}

	// The list of colors
	static void listColors(CommandSender sender) {
		String message = ColorMe.localization.getString("color_list");
		ColorMe.message(sender, null, message, null, null, null, null);
		String msg = "";
		// As long as all colors aren't reached, including magic manual
		for (ChatColor value : ChatColor.values()) {
			// get the name from the integer
			String color = value.name().toLowerCase();
			String colorChar = Character.toString(value.getChar());
			if (colorChar.equalsIgnoreCase("r")) continue;
			if (colorChar.equalsIgnoreCase("n")) continue;
			if (colorChar.equalsIgnoreCase("m")) continue;
			if (colorChar.equalsIgnoreCase("k")) continue;
			// color the name of the color
			if (ColorMe.config.getBoolean("colors." + color)) {
				msg += ChatColor.valueOf(color.toUpperCase()) + color + " (&" + colorChar + ") " + ChatColor.WHITE;
			}
		}
		if (ColorMe.config.getBoolean("colors.strikethrough")) msg += ChatColor.STRIKETHROUGH + "striketrough" + ChatColor.WHITE + " (&m) ";
		if (ColorMe.config.getBoolean("colors.underline")) msg += ChatColor.UNDERLINE + "underline" + ChatColor.WHITE + " (&n) ";
		if (ColorMe.config.getBoolean("colors.magic")) msg += "magic (" + ChatColor.MAGIC + "a" + ChatColor.WHITE + ", &k) ";
		// Include custom colors
		if (ColorMe.config.getBoolean("colors.random")) msg += randomColor("random (&random)" + " ");
		if (ColorMe.config.getBoolean("colors.rainbow")) msg += rainbowColor("rainbow (&rainbow)") + " ";
		if (ColorMe.config.getBoolean("colors.custom")) {
			msg += ColorMe.localization.getString("custom_colors_enabled").replaceAll("&([0-9a-fk-or])", "\u00A7$1");
		}
		sender.sendMessage(msg);
	}

	// Used to create a random effect
	static String randomColor(String name) {
		String newName = "";
		// As long as the length of the name isn't reached
		for (int i = 0; i < name.length(); i++) {
			// Roll the dice between 0 and 16 ;)
			int x = (int) (Math.random()*ChatColor.values().length);
			char ch = name.charAt(i);
			// Color the character
			newName += ChatColor.values()[x] + Character.toString(ch);
		}
		return newName;
	}

	// Used to create a rainbow effect
	static String rainbowColor(String name) {
		// Had to store the rainbow manually. Why did Mojang store it so..., forget it
		String newName = "";
		int z = 0;
		String rainbow[] = {"DARK_RED", "RED", "GOLD", "YELLOW", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "BLUE", "DARK_BLUE", "LIGHT_PURPLE", "DARK_PURPLE"};
		// As long as the length of the name isn't reached
		for (int i = 0; i < name.length(); i++) {
			// Reset if z reaches 12
			if (z == 12) z = 0;
			char ch = name.charAt(i);
			// Add to the new name the colored character
			newName += ChatColor.valueOf(rainbow[z]) + Character.toString(ch);
			z++;
		}
		return newName;
	}
	
	// Make the custom colors!
	static String updateCustomColor(String color, String text, Player player) {
		// Get color
		String updatedText = "";
		String colorChars = ColorMe.colors.getString(color).replaceAll("&([0-9a-fk-or])", "\u00A7$1");
		// No § or &? Not valid; doesn't start with §? Not valid! Ending without a char? Not valid!
		if (!colorChars.contains("§") || colorChars.contains("&") || !colorChars.startsWith("§") || colorChars.endsWith("§")) return text;
		// We use substrings
		String sub = colorChars, sub2 = "";
		for (int i = 0; i < text.length(); i++) {
			// If substring is empty of values, reset
			if (!sub.contains("§")) sub = colorChars;
			// Get the § extracted
			if (sub.contains("§")) {
				sub2 = sub.substring(0, 2);
			}
			// Add the substring (color value) plus the char
			updatedText += sub2 + text.charAt(i);
			// Now replace the color value. (.replaceFirst -> otherwise double things would be removed, too!)
			sub = sub.replaceFirst(sub2, "");
		}
		return updatedText;
	}

	// Check if the color is possible
	static boolean validColor(String color) {
		// if it's random or rainbow -> possible
		if (color.equalsIgnoreCase("rainbow") || color.equalsIgnoreCase("random")) {
			return true;
		}
		// Custom color? (Must contain something!!! NOT '' or null)
		if (ColorMe.colors.contains(color) && (ColorMe.colors.getString(color).trim().length() > 1 ? true : false) == true) return true;
		// Second place, cause random and rainbow aren't possible normally ;)
		else {
			for (ChatColor value : ChatColor.values()) {
				// Check if the color is one of the 17
				if (color.equalsIgnoreCase(value.name().toLowerCase())) {
					return true;
				}
			}
			return false;
		}
	}

	// If the config value is disabled, return true
	static boolean isDisabled(String color) {
		if (ColorMe.config.getBoolean("colors." + color.toLowerCase())) {
			return false;
		}
		// Custom color? (Must contain something!!! NOT '' or null)
		if ((ColorMe.colors.getString(color).trim().length() > 1 ? true : false) == true && ColorMe.config.getBoolean("colors.custom")) return false;
		return true;
	}

	// Displays the specific help
	static boolean help(CommandSender sender, String pluginPart) {
		for (int i = 1; i <= 9; i++) {
			String message = ColorMe.localization.getString("help_" + pluginPart + "_" + Integer.toString(i));
			ColorMe.message(sender, null, message, null, null, null, null);
		}
		return true;
	}

	// Reloads the plugin
	static boolean reload(CommandSender sender) {
		ColorMe.loadConfigsAgain();		
		String message = ColorMe.localization.getString("reload");
		ColorMe.message(sender, null, message, null, null, null, null);
		return true;
	}

	// Update the name
	static void checkNames(String name, String world) {
		String color;
		// Check for color and valid ones, else restore
		if (Actions.has(name, world, "colors")) {
			if (Actions.validColor(ColorMe.players.getString(name + ".colors." + world)) == true) {
				color = Actions.get(name, world, "colors");
				Actions.updateName(name, color);
			}
			else Actions.restoreName(name);
		}
		else if (Actions.has(name, "default", "colors")) {
			if (Actions.validColor(ColorMe.players.getString(name + ".colors.default")) == true) {
				color = Actions.get(name, "default", "colors");
				Actions.updateName(name, color);
			}
			else Actions.restoreName(name);
		}
		else if (ColorMe.globalColor) {
			if (Actions.validColor(ColorMe.config.getString("global_default.color")) == true) {
				color = Actions.getGlobal("color");
				Actions.updateName(name, color);
			}
			else Actions.restoreName(name);
		}
		else if (!Actions.has(name, world, "colors") || !Actions.has(name, "default", "colors") || !Actions.hasGlobal("color")) {
			Actions.restoreName(name);
		}
	}
}