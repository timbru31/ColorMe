package de.dustplanet.colorme;

import java.io.IOException;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;
import org.getspout.spoutapi.player.SpoutPlayer;
import org.kitteh.tag.TagAPI;

public class Actions {
	public static ColorMe plugin;
	public Actions(ColorMe instance) {
		plugin = instance;
	}
	
	/*
	 * 
	 * Group functions
	 * 
	 */
	
	// Sets a value for a group
	public static void setGroup(String groupName, String value, String world, String groupPart) {
		ColorMe.logDebug("Actions -> set");
		ColorMe.logDebug("Asked to set the things from " + groupName + " in the world " + world + ", part " + groupPart);
		// Write to the config and save and update the names
		ColorMe.group.set(groupName + "." + groupPart + "." + world, value);
		try {
			ColorMe.group.save(ColorMe.groupsFile);
			ColorMe.logDebug("Saved to the groupsFile");
		} catch (IOException e) {
			Bukkit.getServer().getLogger().warning("Failed to save the groups.yml! Please report this! IOException");
			ColorMe.logDebug("Failed to save");
		}
	}

	// Create a group
	public static void createGroup(String groupName) {
		ColorMe.logDebug("Actions -> Asked to create the group " + groupName);
		ColorMe.group.set(groupName + ".color.default", "");
		ColorMe.group.set(groupName + ".prefix.default", "");
		ColorMe.group.set(groupName + ".suffix.default", "");
		try {
			ColorMe.group.save(ColorMe.groupsFile);
			ColorMe.logDebug("Saved to the groupsFile");
		} catch (IOException e) {
			Bukkit.getServer().getLogger().warning("Failed to save the groups.yml! Please report this! IOException");
			ColorMe.logDebug("Failed to save");
		}
	}

	// Add a member to a group
	public static void addMember(String groupName, String name) {
		name = name.toLowerCase();
		ColorMe.logDebug("Actions -> Add member " + name + " to the group " + groupName);
		ColorMe.players.set(name + ".group", groupName);
	}

	// Remove a player from a group
	public static void removeMember(String groupName, String name) {
		name = name.toLowerCase();
	}

	// Check if a player has a color/prefix/suffix or not
	public static boolean isMember(String groupName, String name) {
		ColorMe.logDebug("Actions -> isMember");
		ColorMe.logDebug("Asked if " + name + " is a member of the group " + groupName);
		name = name.toLowerCase();
		if (ColorMe.players.contains(name + ".group")) {
			// If the string is the same -> return true
			if (groupName.equalsIgnoreCase(ColorMe.players.getString(name + ".group"))) return true;
		}
		return false;
	}

	// Checks if the player is itself
	public static boolean self(CommandSender sender, String name) {
		ColorMe.logDebug("Actions -> self");
		return (sender.equals(Bukkit.getServer().getPlayerExact(name))) ? true : false;
	}

	// Return the player's name color/prefix/suffix
	public static String get(String name, String world, String pluginPart) {
		ColorMe.logDebug("Actions -> get");
		ColorMe.logDebug("Asked to get the things from " + name + " in the world " + world + " part " + pluginPart);
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
		ColorMe.logDebug("Actions -> getGlobal");
		ColorMe.logDebug("Asked to get the global part " + pluginPart);
		String string = ColorMe.config.getString("global_default." + pluginPart);
		String updatedString = replaceThings(string);
		return updatedString;
	}

	public static String replaceThings(String string) {
		ColorMe.logDebug("Actions -> replaceThings");
		// While random is in there
		String sub;
		while (string.contains("\u0026random")) {
			// Without random
			int i = string.indexOf("\u0026random") + 7;
			int z = string.length();
			sub = string.substring(i, z);
			// Stop if other & or § is found
			if (sub.contains("\u0026")) {
				sub = sub.substring(0, sub.indexOf("\u0026"));
			}
			if (sub.contains("\u00A7")) {
				sub = sub.substring(0, sub.indexOf("\u00A7"));
			}
			// Replace
			string = string.replace(sub, randomColor(sub));
			// Replace FIRST random
			string = string.replaceFirst("\u0026random", "");
			sub = "";
		}
		// While random (short) is in there
		while (string.contains("\u0026ran")) {
			// Without random
			int i = string.indexOf("\u0026ran") + 4;
			int z = string.length();
			sub = string.substring(i, z);
			// Stop if other & or § is found
			if (sub.contains("\u0026")) {
				sub = sub.substring(0, sub.indexOf("\u0026"));
			}
			if (sub.contains("\u00A7")) {
				sub = sub.substring(0, sub.indexOf("\u00A7"));
			}
			// Replace
			string = string.replace(sub, randomColor(sub));
			// Replace FIRST random
			string = string.replaceFirst("\u0026ran", "");
			sub = "";
		}
		// While rainbow is in there
		while (string.contains("\u0026rainbow")) {
			// Without rainbow
			int i = string.indexOf("\u0026rainbow") + 8;
			int z = string.length();
			sub = string.substring(i, z);
			// Stop if other & or § is found
			if (sub.contains("\u0026")) {
				sub = sub.substring(0, sub.indexOf("\u0026"));
			}
			if (sub.contains("\u00A7")) {
				sub = sub.substring(0, sub.indexOf("\u00A7"));
			}
			// Replace
			string = string.replace(sub, rainbowColor(sub));
			// Replace FIRST rainbow
			string = string.replaceFirst("\u0026rainbow", "");
			sub = "";
		}
		// While rainbow (short) is in there
		while (string.contains("\u0026rai")) {
			// Without rainbow
			int i = string.indexOf("\u0026rai") + 4;
			int z = string.length();
			sub = string.substring(i, z);
			// Stop if other & or § is found
			if (sub.contains("\u0026")) {
				sub = sub.substring(0, sub.indexOf("\u0026"));
			}
			if (sub.contains("\u00A7")) {
				sub = sub.substring(0, sub.indexOf("\u00A7"));
			}
			// Replace
			string = string.replace(sub, rainbowColor(sub));
			// Replace FIRST rainbow
			string = string.replaceFirst("\u0026rai", "");
			sub = "";
		}
		// Normal color codes!
		string = string.replaceAll("\u0026((?i)[0-9a-fk-or])", "\u00A7$1");
		return string;
	}

	// Set player's color/prefix/suffix
	public static void set(String name, String value, String world, String pluginPart) {
		ColorMe.logDebug("Actions -> set");
		ColorMe.logDebug("Asked to set the things from " + name + " in the world " + world + " part " + pluginPart);
		// Write to the config and save and update the names
		ColorMe.players.set(name + "." + pluginPart + "." + world, value);
		try {
			ColorMe.players.save(ColorMe.playersFile);
			ColorMe.logDebug("Saved to the config");
		} catch (IOException e) {
			Bukkit.getServer().getLogger().warning("Failed to save the players.yml! Please report this! IOException");
			ColorMe.logDebug("Failed to save");
		}
		checkNames(name, world);
	}

	// Check if a player has a color/prefix/suffix or not
	public static boolean has(String name, String world, String pluginPart) {
		ColorMe.logDebug("Actions -> has");
		ColorMe.logDebug("Asked if " + name + " has got in the world " + world + " part " + pluginPart);
		name = name.toLowerCase();
		if (ColorMe.players.contains(name + "." + pluginPart + "." + world)) {
			// if longer than 1 it's a color, return true - otherwise (means '') return false
			return (ColorMe.players.getString(name + "." + pluginPart + "." + world)).trim().length() > 1 ? true : false;
		}
		return false;
	}

	// Check if the global default is not null
	public static boolean hasGlobal(String pluginPart) {
		ColorMe.logDebug("Actions -> hasGlobal");
		ColorMe.logDebug("Asked if the global value is set. " + pluginPart);
		return ColorMe.config.getString("global_default." + pluginPart).trim().length() > 1 ? true : false;
	}

	// Removes a color/prefix/suffix if exists, otherwise returns false
	public static void remove(String name, String world, String pluginPart) {
		ColorMe.logDebug("Actions -> remove");
		ColorMe.logDebug("Asked to remove the things from " + name + " in the world " + world + " part " + pluginPart);
		name = name.toLowerCase();
		// If the player has got a color
		if (has(name, world, pluginPart)) {
			ColorMe.players.set(name  + "." + pluginPart + "." + world, "");
			try {
				ColorMe.players.save(ColorMe.playersFile);
			} catch (IOException e) {
				Bukkit.getServer().getLogger().warning("Failed to save the players.yml! Please report this! IOException");
			}
			checkNames(name, world);
			ColorMe.logDebug("Removed");
		}
	}

	// Removes a color/prefix/suffix if exists, otherwise returns false
	public static void removeGlobal(String pluginPart) {
		ColorMe.logDebug("Actions -> removeGlobal");
		ColorMe.logDebug("Asked to remove the global part of " + pluginPart);
		ColorMe.config.set("global_default." + pluginPart, "");
		try {
			ColorMe.config.save(ColorMe.configFile);
			ColorMe.logDebug("Removed global part");
		} catch (IOException e) {
			Bukkit.getServer().getLogger().warning("Failed to save the config.yml! Please report this! IOException");
			ColorMe.logDebug("Unable to remove global part");
		}
	}

	// Update the displayName, tabName, title, prefix & suffix in a specific world (after setting, removing, onJoin and onChat)
	public static void updateName(String name, String color) {
		ColorMe.logDebug("Actions -> updateName");
		ColorMe.logDebug("Asked to update the color of " + name + " to the color " + color);
		final Player player = Bukkit.getServer().getPlayerExact(name);
		if (player != null) {
			String displayName = player.getDisplayName();
			String cleanDisplayName = ChatColor.stripColor(displayName);
			player.setDisplayName(cleanDisplayName);
			player.setPlayerListName(cleanDisplayName);
			if (ColorMe.tagAPI && ColorMe.playerTitleWithoutSpout) TagAPI.refreshPlayer(player);
			String newName;
			// Name color
			if (ColorMe.displayName) {
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
					player.setDisplayName(updateCustomColor(color, cleanDisplayName) + ChatColor.WHITE);
				}
				// Normal
				else {
					String [] colors = color.split("-");
					String tempDispName = "";
					for (String colorPart : colors) {
						tempDispName += ChatColor.valueOf(colorPart.toUpperCase());
					}
					player.setDisplayName(tempDispName + cleanDisplayName + ChatColor.WHITE);
				}
			}
			// Check for playerList
			if (ColorMe.tabList) {
				if (color.equalsIgnoreCase("random")) {
					newName = randomColor(cleanDisplayName);
				}
				else if (color.equalsIgnoreCase("rainbow")) {
					newName = rainbowColor(cleanDisplayName);
				}
				else if (ColorMe.colors.contains(color) && (ColorMe.colors.getString(color).trim().length() > 1 ? true : false) == true) {
					newName = updateCustomColor(color, cleanDisplayName);
				}
				else newName = ChatColor.valueOf(color.toUpperCase()) + cleanDisplayName + ChatColor.WHITE;
				// Shorten it, if too long
				if (!newName.equals("") && newName != null) {
					if (newName.length() > 16) {
						newName = newName.substring(0, 12) + ChatColor.WHITE + "..";
					}
					player.setPlayerListName(newName);
				}
			}
			// Check for Spout
			if (ColorMe.spoutEnabled && ColorMe.playerTitle && player.hasPermission("colorme.nametag")) {
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
					spoutPlayer.setTitle(updateCustomColor(color, cleanDisplayName));
				}
				// Normal color
				else spoutPlayer.setTitle(ChatColor.valueOf(color.toUpperCase()) + cleanDisplayName);
			}
			// Check if TagAPI should be used -> above the head!
			if (ColorMe.playerTitleWithoutSpout && ColorMe.tagAPI && player.hasPermission("colorme.nametag")) {
				if (!color.equalsIgnoreCase("rainbow") && !color.equalsIgnoreCase("random")) TagAPI.refreshPlayer(player);
			}
		}
	}

	// Restore the "clean", white name
	public static void restoreName(String name) {
		ColorMe.logDebug("Actions -> restoreName");
		ColorMe.logDebug("Asked to restore the name " + name);
		Player player = Bukkit.getServer().getPlayerExact(name);
		if (player != null) {
			ColorMe.logDebug("Player found and valid");
			String displayName = player.getDisplayName();
			String cleanDisplayName = ChatColor.stripColor(displayName);
			// No name -> back to white
			player.setDisplayName(ChatColor.WHITE + cleanDisplayName);
			if (ColorMe.tabList) {
				// If the TAB name is longer than 16 shorten it!
				String newName = cleanDisplayName;
				if (newName.length() > 16) {
					newName = cleanDisplayName.substring(0, 12) + ChatColor.WHITE + "..";
				}
				player.setPlayerListName(newName);
			}
			if (ColorMe.spoutEnabled && ColorMe.playerTitle && player.hasPermission("colorme.nametag")) {
				SpoutPlayer spoutPlayer = (SpoutPlayer) player;
				spoutPlayer.resetTitle();
			}
			// Check if TagAPI should be used -> above the head!
			if (ColorMe.playerTitleWithoutSpout && ColorMe.tagAPI && player.hasPermission("colorme.nametag")) {
				TagAPI.refreshPlayer(player);
			}
		}
	}

	// The list of colors
	public static void listColors(CommandSender sender) {
		ColorMe.logDebug("Actions -> listColors");
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
				msg += ChatColor.valueOf(color.toUpperCase()) + color + " (\u0026" + colorChar + ") " + ChatColor.WHITE;
			}
		}
		if (ColorMe.config.getBoolean("colors.strikethrough")) msg += ChatColor.STRIKETHROUGH + "striketrough" + ChatColor.WHITE + " (\u0026m) ";
		if (ColorMe.config.getBoolean("colors.underline")) msg += ChatColor.UNDERLINE + "underline" + ChatColor.WHITE + " (\u0026n) ";
		if (ColorMe.config.getBoolean("colors.magic")) msg += "magic (" + ChatColor.MAGIC + "a" + ChatColor.WHITE + ", \u0026k) ";
		// Include custom colors
		if (ColorMe.config.getBoolean("colors.random")) msg += randomColor("random (\u0026random)" + " ");
		if (ColorMe.config.getBoolean("colors.rainbow")) msg += rainbowColor("rainbow (\u0026rainbow)") + " ";
		if (ColorMe.config.getBoolean("colors.custom"))	msg += ColorMe.localization.getString("custom_colors_enabled").replaceAll("\u0026((?i)[0-9a-fk-or])", "\u00A7$1");
		sender.sendMessage(msg);
	}

	// Used to create a random effect
	public static String randomColor(String name) {
		ColorMe.logDebug("Actions -> randomColor");
		ColorMe.logDebug("Asked to color the string " +name);
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
	public static String rainbowColor(String name) {
		ColorMe.logDebug("Actions -> rainbowColor");
		ColorMe.logDebug("Asked to color the string " +name);
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
	public static String updateCustomColor(String color, String text) {
		ColorMe.logDebug("Actions -> updateCustomColor");
		ColorMe.logDebug("Asked to color the string " + text + " with the color " +color);
		// Get color
		String updatedText = "";
		String colorChars = ColorMe.colors.getString(color).replaceAll("\u0026((?i)[0-9a-fk-or])", "\u00A7$1");
		// No § or &? Not valid; doesn't start with §? Not valid! Ending without a char? Not valid!
		if (!colorChars.contains("\u00A7") || colorChars.contains("\u0026") || !colorChars.startsWith("\u00A7") || colorChars.endsWith("\u00A7")) return text;
		// We use substrings
		String sub = colorChars, sub2 = "";
		for (int i = 0; i < text.length(); i++) {
			// If substring is empty of values, reset
			if (!sub.contains("\u00A7")) sub = colorChars;
			// Get the § extracted
			if (sub.contains("\u00A7")) {
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
	public static boolean validColor(String color) {
		ColorMe.logDebug("Actions -> validColor");
		// if it's random or rainbow -> possible
		if (color.equalsIgnoreCase("rainbow") || color.equalsIgnoreCase("random")) {
			ColorMe.logDebug("Color " + color + " is valid");
			return true;
		}
		// Custom color? (Must contain something!!! NOT '' or null)
		if (ColorMe.colors.contains(color) && (ColorMe.colors.getString(color).trim().length() > 1 ? true : false) == true) {
			ColorMe.logDebug("Color " + color + " is valid");
			return true;
		}
		// Second place, cause random and rainbow aren't possible normally ;)
		else {
			for (ChatColor value : ChatColor.values()) {
				// Check if the color is one of the 17
				if (color.equalsIgnoreCase(value.name().toLowerCase())) {
					ColorMe.logDebug("Color " + color + " is valid");
					return true;
				}
			}
			ColorMe.logDebug("Color " + color + " is invalid");
			return false;
		}
	}

	// If the config value is disabled, return true
	public static boolean isDisabled(String color) {
		ColorMe.logDebug("Actions -> isDisabled");
		if (ColorMe.config.getBoolean("colors." + color.toLowerCase())) {
			ColorMe.logDebug("Color " + color + " is enabled");
			return false;
		}
		// Custom color? (Must contain something!!! NOT '' or null)
		else if (ColorMe.colors.contains(color) && ColorMe.colors.getString(color).trim().length() > 1 && ColorMe.config.getBoolean("colors.custom")) {
			ColorMe.logDebug("Color " + color + " is enabled");
			return false;
		}
		ColorMe.logDebug("Color " + color + " is enabled");
		return true;
	}

	// Displays the specific help
	public static void help(CommandSender sender, String pluginPart) {
		ColorMe.logDebug("Actions -> help");
		for (int i = 1; i <= 9; i++) {
			String message = ColorMe.localization.getString("help_" + pluginPart + "_" + Integer.toString(i));
			ColorMe.message(sender, null, message, null, null, null, null);
		}
	}

	// Reloads the plugin
	public static void reload(CommandSender sender) {
		ColorMe.logDebug("Actions -> reload");
		ColorMe.loadConfigsAgain();	
		String message = ColorMe.localization.getString("reload");
		ColorMe.message(sender, null, message, null, null, null, null);
	}

	// Update the name
	public static void checkNames(String name, String world) {
		ColorMe.logDebug("Actions -> checkNames");
		ColorMe.logDebug("Asked to check the color of the player " + name + " in the world " + world);
		String color;
		// Check for color and valid ones, else restore
		if (Actions.has(name, world, "colors")) {
			String[] colors = ColorMe.players.getString(name + ".colors." + world).split("-");
			for (String colorPart : colors) {
				if (!Actions.validColor(colorPart)) {
					restoreName(name);
					return;
				}
			}
			color = Actions.get(name, world, "colors");
			Actions.updateName(name, color);
		}
		else if (Actions.has(name, "default", "colors")) {
			String[] colors = ColorMe.players.getString(name + ".colors.default").split("-");
			for (String colorPart : colors) {
				if (!Actions.validColor(colorPart)) {
					restoreName(name);
					return;
				}
			}
			color = Actions.get(name, "default", "colors");
			Actions.updateName(name, color);
		}
		// Group TODO Add method to get color from group
		else if (Actions.has(name, "default", "colors")) {
			String[] colors = ColorMe.players.getString(name + ".colors.default").split("-");
			for (String colorPart : colors) {
				if (!Actions.validColor(colorPart)) {
					restoreName(name);
					return;
				}
			}
			// Get groupColor
			color = Actions.get(name, "default", "colors");
			Actions.updateName(name, color);
		}
		else if (ColorMe.globalColor) {
			String[] colors = ColorMe.config.getString("global_default.color").split("-");
			for (String colorPart : colors) {
				if (!Actions.validColor(colorPart)) {
					restoreName(name);
					return;
				}
			}
			color = Actions.getGlobal("color");
			Actions.updateName(name, color);
		}
		else if (!Actions.has(name, world, "colors") || !Actions.has(name, "default", "colors") || !Actions.hasGlobal("color")) {
			Actions.restoreName(name);
		}
	}
}