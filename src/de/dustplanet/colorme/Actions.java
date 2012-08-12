package de.dustplanet.colorme;

import java.io.File;
import java.io.IOException;
import java.util.List;

import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
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

	// Return the group's name color/prefix/suffix
	public static String getGroup(String groupName, String world, String groupPart) {
		ColorMe.logDebug("Actions -> get");
		ColorMe.logDebug("Asked to get the things from " + groupName + " in the world " + world + ", part " + groupPart);
		// Group in the config? Yes -> get the config, no -> nothing
		groupName = groupName.toLowerCase();
		world = world.toLowerCase();
		groupPart = groupPart.toLowerCase();
		String string = ColorMe.group.getString(groupName + "." + groupPart + "." + world);
		String updatedString = replaceThings(string);
		return updatedString;
	}

	// Check if a group has a color/prefix/suffix or not
	public static boolean hasGroup(String groupName, String world, String groupPart) {
		ColorMe.logDebug("Actions -> hasGroup");
		ColorMe.logDebug("Asked if " + groupName + " has got in the world " + world + ", part " + groupPart);
		groupName = groupName.toLowerCase();
		world = world.toLowerCase();
		groupPart = groupPart.toLowerCase();
		if (ColorMe.group.contains(groupName + "." + groupPart + "." + world)) {
			// if longer than 1 it's valid, return true - otherwise (means '') return false
			return (ColorMe.group.getString(groupName + "." + groupPart + "." + world)).trim().length() >= 1 ? true : false;
		}
		return false;
	}

	// Checks if a group exists
	public static boolean existsGroup(String groupName) {
		ColorMe.logDebug("Actions -> existsGroup");
		ColorMe.logDebug("Asked if " + groupName + " exists");
		groupName = groupName.toLowerCase();
		if (ColorMe.group.contains(groupName)) return true;
		return false;
	}

	// Deletes a group
	public static void deleteGroup(String groupName) {
		ColorMe.logDebug("Actions -> deleteGroup");
		ColorMe.logDebug("Asked to delete the group " + groupName);
		groupName = groupName.toLowerCase();
		List<String> members = ColorMe.group.getStringList(groupName + ".members");
		for (String member : members) {
			ColorMe.players.set(member + ".group", "");
		}
		save(ColorMe.playersFile, ColorMe.players);
		ColorMe.group.set(groupName, null);
		save(ColorMe.groupsFile, ColorMe.group);
	}
	// Lists the members
	public static List<String> listMembers(String groupName) {
		ColorMe.logDebug("Actions -> listMembers");
		ColorMe.logDebug("Asked to list the members of " + groupName);
		groupName = groupName.toLowerCase();
		List<String> members = ColorMe.group.getStringList(groupName + ".members");
		return members;
	}

	// Sets a value for a group
	public static void setGroup(String groupName, String value, String world, String groupPart) {
		ColorMe.logDebug("Actions -> set");
		ColorMe.logDebug("Asked to set the things from " + groupName + " in the world " + world + ", part " + groupPart);
		groupName = groupName.toLowerCase();
		world = world.toLowerCase();
		groupPart = groupPart.toLowerCase();
		// Write to the config and save and update the names
		ColorMe.group.set(groupName + "." + groupPart + "." + world, value);
		save(ColorMe.groupsFile, ColorMe.group);
	}

	// Create a group
	public static void createGroup(String groupName) {
		ColorMe.logDebug("Actions -> Asked to create the group " + groupName);
		groupName = groupName.toLowerCase();
		ColorMe.group.set(groupName + ".colors.default", "");
		ColorMe.group.set(groupName + ".prefix.default", "");
		ColorMe.group.set(groupName + ".suffix.default", "");
		ColorMe.group.set(groupName + ".members", "");
		save(ColorMe.groupsFile, ColorMe.group);
	}

	// Add a member to a group
	public static void addMember(String groupName, String name) {
		ColorMe.logDebug("Actions -> Add member " + name + " to the group " + groupName);
		name = name.toLowerCase();
		groupName = groupName.toLowerCase();
		ColorMe.players.set(name + ".group", groupName);
		save(ColorMe.playersFile, ColorMe.players);
		List<String> members = ColorMe.group.getStringList(groupName + ".members");
		members.add(name);
		ColorMe.group.set(groupName + ".members", members);
		save(ColorMe.groupsFile, ColorMe.group);
	}

	// Remove a player from a group
	public static void removeMember(String groupName, String name) {
		ColorMe.logDebug("Actions -> Add member " + name + " to the group " + groupName);
		ColorMe.players.set(name + ".group", "");
		name = name.toLowerCase();
		groupName = groupName.toLowerCase();
		save(ColorMe.playersFile, ColorMe.players);
		List<String> members = ColorMe.group.getStringList(groupName + ".members");
		members.remove(name);
		ColorMe.group.set(groupName + ".members", members);
		save(ColorMe.groupsFile, ColorMe.group);
	}

	// Check if a player has a color/prefix/suffix or not
	public static boolean isMember(String groupName, String name) {
		ColorMe.logDebug("Actions -> isMember");
		ColorMe.logDebug("Asked if " + name + " is a member of the group " + groupName);
		name = name.toLowerCase();
		groupName = groupName.toLowerCase();
		if (ColorMe.players.contains(name + ".group")) {
			// If the string is the same -> return true
			if (groupName.equalsIgnoreCase(ColorMe.players.getString(name + ".group"))) return true;
		}
		return false;
	}

	/*
	 * 
	 * Global value functions
	 * 
	 */


	// Get global default
	public static String getGlobal(String pluginPart) {
		ColorMe.logDebug("Actions -> getGlobal");
		ColorMe.logDebug("Asked to get the global part " + pluginPart);
		pluginPart = pluginPart.toLowerCase();
		String string = ColorMe.config.getString("global_default." + pluginPart);
		String updatedString = replaceThings(string);
		return updatedString;
	}

	// Check if the global default is not null
	public static boolean hasGlobal(String pluginPart) {
		ColorMe.logDebug("Actions -> hasGlobal");
		ColorMe.logDebug("Asked if the global value is set. " + pluginPart);
		pluginPart = pluginPart.toLowerCase();
		return ColorMe.config.getString("global_default." + pluginPart).trim().length() >= 1 ? true : false;
	}

	// Removes a color/prefix/suffix if exists, otherwise returns false
	public static void removeGlobal(String pluginPart) {
		ColorMe.logDebug("Actions -> removeGlobal");
		ColorMe.logDebug("Asked to remove the global part of " + pluginPart);
		pluginPart = pluginPart.toLowerCase();
		ColorMe.config.set("global_default." + pluginPart, "");
		save(ColorMe.configFile, ColorMe.config);
	}

	/*
	 * 
	 * Player functions
	 * 
	 */

	// Check if the player has got a group
	public static boolean playerHasGroup(String name) {
		ColorMe.logDebug("Actions -> playerHasGroup");
		ColorMe.logDebug("Asked if " + name + " has got in the a group");
		name = name.toLowerCase();
		if (ColorMe.players.contains(name + ".group")) {
			// if longer than 1 it's valid, return true - otherwise (means '') return false
			return (ColorMe.players.getString(name + ".group")).trim().length() >= 1 ? true : false;
		}
		return false;
	}

	// Get the group of a player
	public static String playerGetGroup(String name) {
		ColorMe.logDebug("Actions -> playerGetGroup");
		ColorMe.logDebug("Asked for the group of " + name);
		name = name.toLowerCase();
		return (ColorMe.players.getString(name + ".group"));
	}

	// Checks if the player is itself
	public static boolean self(CommandSender sender, String name) {
		ColorMe.logDebug("Actions -> self");
		return (sender.equals(Bukkit.getServer().getPlayerExact(name))) ? true : false;
	}

	// Return the player's name color/prefix/suffix
	public static String get(String name, String world, String pluginPart) {
		ColorMe.logDebug("Actions -> get");
		ColorMe.logDebug("Asked to get the things from " + name + " in the world " + world + ", part " + pluginPart);
		// Player in the config? Yes -> get the config, no -> nothing
		name = name.toLowerCase();
		world = world.toLowerCase();
		pluginPart = pluginPart.toLowerCase();
		String string = ColorMe.players.getString(name + "." + pluginPart + "." + world);
		String updatedString = replaceThings(string);
		return updatedString;
	}

	// Set player's color/prefix/suffix
	public static void set(String name, String value, String world, String pluginPart) {
		ColorMe.logDebug("Actions -> set");
		ColorMe.logDebug("Asked to set the things from " + name + " in the world " + world + ", part " + pluginPart);
		name = name.toLowerCase();
		world = world.toLowerCase();
		pluginPart = pluginPart.toLowerCase();
		// Write to the config and save and update the names
		ColorMe.players.set(name + "." + pluginPart + "." + world, value);
		save(ColorMe.playersFile, ColorMe.players);
	}

	// Check if a player has a color/prefix/suffix or not
	public static boolean has(String name, String world, String pluginPart) {
		ColorMe.logDebug("Actions -> has");
		ColorMe.logDebug("Asked if " + name + " has got in the world " + world + ", part " + pluginPart);
		name = name.toLowerCase();
		world = world.toLowerCase();
		pluginPart = pluginPart.toLowerCase();
		if (ColorMe.players.contains(name + "." + pluginPart + "." + world)) {
			// if longer than 1 it's valid, return true - otherwise (means '') return false
			return (ColorMe.players.getString(name + "." + pluginPart + "." + world)).trim().length() >= 1 ? true : false;
		}
		return false;
	}

	// Removes a color/prefix/suffix if exists, otherwise returns false
	public static void remove(String name, String world, String pluginPart) {
		ColorMe.logDebug("Actions -> remove");
		ColorMe.logDebug("Asked to remove the things from " + name + " in the world " + world + ", part " + pluginPart);
		name = name.toLowerCase();
		world = world.toLowerCase();
		pluginPart = pluginPart.toLowerCase();
		// If the player has got a color
		if (has(name, world, pluginPart)) {
			ColorMe.players.set(name  + "." + pluginPart + "." + world, "");
			save(ColorMe.playersFile, ColorMe.players);
			checkNames(name, world);
			ColorMe.logDebug("Removed");
		}
	}

	/*
	 * 
	 * Replace function
	 * Colors and whole String
	 * 
	 */

	// Used to create a random effect
	public static String randomColor(String name) {
		ColorMe.logDebug("Actions -> randomColor");
		ColorMe.logDebug("Asked to color the string " +name);
		String newName = "";
		// As long as the length of the name isn't reached
		for (int i = 0; i < name.length(); i++) {
			// Roll the dice between 0 and 16 ;)
			int x = (int) (Math.random() * ChatColor.values().length);
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
		String rainbow[] = {"DARK_RED", "RED", "GOLD", "YELLOW", "GREEN", "DARK_GREEN", "AQUA", "DARK_AQUA", "BLUE", "DARK_BLUE", "LIGHT_PURPLE", "DARK_PURPLE"};
		// As long as the length of the name isn't reached
		for (int i = 0, z= 0; i < name.length(); i++, z++) {
			// Reset if z reaches 12
			if (z == 12) z = 0;
			char ch = name.charAt(i);
			// Add to the new name the colored character
			newName += ChatColor.valueOf(rainbow[z]) + Character.toString(ch);
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
		// Split the color values
		String colorValues[] = colorChars.split(",");
		// For the text length
		for (int i = 0, x = 0; i < text.length(); i++, x++) {
			// No colors left? -> Start from 0
			if (x == colorValues.length) x = 0;
			updatedText += colorValues[x] + text.charAt(i);
		}
		return updatedText;
	}

	// Replace all in a String
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

	/*
	 * 
	 * Checking functions
	 * 
	 */

	// Check the whole names
	public static void checkNames(String name, String world) {
		ColorMe.logDebug("Actions -> checkNames");
		ColorMe.logDebug("Asked to check the color of the player " + name + " in the world " + world);
		name = name.toLowerCase();
		world = world.toLowerCase();
		String color = null;
		// Check for color and valid ones, else restore
		// Check player specific world
		if (has(name, world, "colors")) {
			color = get(name, world, "colors");
		}
		// Check player default
		else if (has(name, "default", "colors")) {
			color = Actions.get(name, "default", "colors");
		}
		// If groups enabled
		else if (ColorMe.groups && ColorMe.ownSystem) {
			// If group available
			if (playerHasGroup(name)) {
				String group = playerGetGroup(name);
				// Group specific world
				if (hasGroup(group, world, "colors")) {
					color = Actions.getGroup(group, world, "colors");
				}
				// Group default
				else if (hasGroup(group, "default", "colors")) {
					color = Actions.getGroup(group, "default", "colors");
				}
			}
		}
		// Then check if still nothing found and globalColor
		if (ColorMe.globalColor && color == null) {
			if (hasGlobal("color")) {
				color = getGlobal("color");
			}
		}
		// Restore if nothing found...
		if (color == null) {
			restoreName(name);
			return;
		}
		// Check if valid
		String[] colors = color.split("-");
		for (String colorPart : colors) {
			if (!validColor(colorPart)) {
				restoreName(name);
				return;
			}
		}
		// Update the name
		updateName(name, color);
	}

	// Update the displayName, tabName, title, prefix & suffix in a specific world (after setting, removing, onJoin and onChat)
	public static void updateName(String name, String color) {
		ColorMe.logDebug("Actions -> updateName");
		ColorMe.logDebug("Asked to update the color of " + name + " to the color " + color);
		name = name.toLowerCase();
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
		name = name.toLowerCase();
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

	/*
	 * 
	 * Misc funtions
	 * 
	 */

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

	// Saves a file
	private static void save(File file, FileConfiguration config) {
		try {
			config.save(file);
			ColorMe.logDebug("Saved to the " + file);
		} catch (IOException e) {
			Bukkit.getServer().getLogger().warning("Failed to save the " + file + "! Please report this! IOException");
			ColorMe.logDebug("Failed to save");
		}
	}
}