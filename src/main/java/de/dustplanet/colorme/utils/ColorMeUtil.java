package de.dustplanet.colorme.utils;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;

public class ColorMeUtil {
    
    
    public boolean cleanDisplayName(Player player) {
	if (player == null) {
	    return false;
	}
	return cleanDisplayName(player, player.getDisplayName());
    }
    
    public boolean cleanDisplayName(Player player, String dirtyDisplayName) {
	if (player != null) {
	    player.setDisplayName(cleanName(dirtyDisplayName));
	    return true;
	}
	return false;
    }
    
    public boolean cleanTabName(Player player) {
	if (player == null) {
	    return false;
	}
	return cleanTabName(player, player.getPlayerListName());
    }
    
    public boolean cleanTabName(Player player, String dirtyTabName) {
	if (player != null) {
	    String cleanName = cleanName(dirtyTabName);
	    // Fallback to the normal player name if given name is longer than 16 characters
	    if (cleanName.length() > 16) {
		player.setPlayerListName(player.getName());
	    } else {
		player.setPlayerListName(cleanName);
	    }
	    return true;
	}
	return false;
    }
    
    public String cleanName(String dirtyName) {
	if (dirtyName == null) {
	    return "";
	}
	return ChatColor.stripColor(dirtyName);
    }
}
