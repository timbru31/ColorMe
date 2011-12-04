package de.xghostkillerx.colorme;

import org.bukkit.ChatColor;
import org.bukkit.craftbukkit.TextWrapper;
import org.bukkit.entity.Player;

public class ColorMeActions {
	public static ColorMe plugin;
	public ColorMeActions(ColorMe instance) {
		plugin = instance;
	}
	
	
	// Return the player's name color
	public String getColor(String name) {
		return (ColorMe.colors.contains(name.toLowerCase())) ? ColorMe.colors.getString(name.toLowerCase()) : "";
	}

	// Set player's color and update displayname if online
	public static boolean setColor(String name, String color) {
		String newColor = findColor(color); 
		if (newColor.equals(color)) return false;
		ColorMe.colors.set(name.toLowerCase(), newColor);
		ColorMe.saveColors();
		if (plugin.getServer().getPlayer(name) != null) {
			Player p =plugin.getServer().getPlayer(name);
			p.setDisplayName(ChatColor.valueOf(newColor)+ChatColor.stripColor(p.getDisplayName())+ChatColor.WHITE);
		}
		return true;
	}

	// Iterate through colors to try and find a match (resource expensive)
	// TODO verstehen!
	public static String findColor(String color) {
		String col;
		for (int i = 0; i <= 15; i++) {
			col = ChatColor.getByCode(i).name();
			if (color.equalsIgnoreCase(col.toLowerCase().replace("_", ""))) return col;
		}
		return color;
	}

	// Check if a player has a color or not
	public static boolean hasColor(String name) {
		if (ColorMe.colors.contains(name.toLowerCase())) {
			return (ColorMe.colors.getString(name.toLowerCase()).trim().length()>1) ? true : false;
		}
		return false;
	}

	// Removes a color if exists, otherwise returns false
	public static boolean removeColor(String name) {
		name = name.toLowerCase();
		if (hasColor(name)) {
			ColorMe.colors.set(name, "");
			ColorMe.saveColors();
			return true;
		}
		return false;
	}
	
	public boolean self(Player p, String n) {
		return (p.equals(plugin.getServer().getPlayer(n))) ? true : false;
	}

	public static void list(Player p) {
		p.sendMessage("Color List:");
		String color;
		String msg = "";
		for (int i = 0; i < ChatColor.values().length; i++) {
			color = ChatColor.getByCode(i).name();
			if (msg.length() == 0) {
				msg = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
				continue;
			}
			msg += (i == ChatColor.values().length-1) ? ChatColor.valueOf(color)+color.toLowerCase().replace("_", "") : ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
			TextWrapper.wrapText(msg);
		}
		p.sendMessage(msg);
	}
}
