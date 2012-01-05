package de.xghostkillerx.colorme;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;
import org.getspout.spoutapi.SpoutManager;

/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles the player activities
 * 
 * Refer to the forum thread:
 * http://bit.ly/colormebukkit
 * Refer to the dev.bukkit.org page:
 * http://bit.ly/bukkitdevcolorme
 *
 * @author xGhOsTkiLLeRx
 * @thanks to Valrix for the original ColorMe plugin!!
 * 
 */

public class ColorMePlayerListener extends PlayerListener {
	protected ColorMe plugin;
	public ColorMePlayerListener(ColorMe plugin) {
		this.plugin = plugin;
	}

	// Loads the color on join and sets the color if player isn't known
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase();
		CheckRoutine(player, name);
	}

	// Loads the color on chat and sets the color if player isn't known
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase();
		CheckRoutine(player, name);
	}

	@SuppressWarnings("deprecation")
	private void CheckRoutine(Player player, String name) {
		plugin.loadConfigAgain();
		String exactName = player.getName();
		// If the player isn't in the players.color add him
		if (!plugin.colors.contains(name)) {
			plugin.colors.set(name, "");
			plugin.saveColors();
		}
		// If the player has a color change the displayname
		if (plugin.hasColor(name)) {
			if (plugin.validColor(plugin.colors.getString(name)) == true) {
				String color = plugin.getColor(name);
				// Random
				if (plugin.getColor(name).equalsIgnoreCase("random")) {
					player.setDisplayName(plugin.randomColor(exactName) + ChatColor.WHITE);
				}
				// Rainbow
				if (plugin.getColor(name).equalsIgnoreCase("rainbow")) {
					player.setDisplayName(plugin.rainbowColor(exactName) + ChatColor.WHITE);
				}
				// Normal
				else if (!plugin.getColor(name).equalsIgnoreCase("random") && !plugin.getColor(name).equalsIgnoreCase("rainbow")) {
					player.setDisplayName(ChatColor.valueOf((color.toUpperCase()))
							+ ChatColor.stripColor(exactName)
							+ ChatColor.WHITE);
				}
				// If Spout is enabled as a plugin, change the title above the head (visible in Spoutcraft only!)
				if (plugin.spoutEnabled == true) {
					// Random
					if (plugin.getColor(name).equalsIgnoreCase("random")) {
						SpoutManager.getAppearanceManager().setGlobalTitle(player, plugin.randomColor(exactName));
					}
					// Rainbow
					if (plugin.getColor(name).equalsIgnoreCase("rainbow")) {
						SpoutManager.getAppearanceManager().setGlobalTitle(player, plugin.rainbowColor(exactName));
					}
					// Normal
					else if (!plugin.getColor(name).equalsIgnoreCase("random") && !plugin.getColor(name).equalsIgnoreCase("rainbow")) {
						SpoutManager.getAppearanceManager().setGlobalTitle(player, ChatColor.valueOf(color.toUpperCase()) + ChatColor.stripColor(exactName));
					}
				}
			}
			else {
				// Tell player to report it, but suppres the error -> uses color before.
				player.sendMessage("Your name colors seems to be invalid. Ask your admin to check it,");
				player.sendMessage("or try re-coloring!");
			}
		}
		if (!plugin.hasColor(name)) {
			// No name -> back to white
			player.setDisplayName(ChatColor.WHITE + ChatColor.stripColor(exactName));
			SpoutManager.getAppearanceManager().setGlobalTitle(player, ChatColor.WHITE + ChatColor.stripColor(exactName));
		}
	}
}