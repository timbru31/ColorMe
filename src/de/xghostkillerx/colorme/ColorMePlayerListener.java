package de.xghostkillerx.colorme;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles the player activities
 * 
 * Refer to the forum thread:
 * http://bit.ly/colormebukkit
 * Refer to the dev.bukkit.org page:
 * http://bit.ly/colormebukkitdev 
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

	// Loads the color on join and sets if empty if player isn't in the list
	public void onPlayerJoin(PlayerJoinEvent event) {
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase();
		plugin.loadConfigAgain();
		CheckRoutine(player, name);
	}

	// Loads the color on chat and sets if empty if player isn't in the list
	public void onPlayerChat(PlayerChatEvent event) {
		Player player = event.getPlayer();
		String name = player.getName().toLowerCase();
		plugin.loadConfigAgain();
		CheckRoutine(player, name);
	}

	private void CheckRoutine(Player player, String name) {
		// If the player isn't in the players.color add him
		if (!this.plugin.colors.contains(name)) {
			plugin.colors.set(name, "");
			plugin.saveColors();
		}
		// If the player has a color change the displayname
		if (this.plugin.hasColor(name)) {
			player.setDisplayName(ChatColor.valueOf(this.plugin.findColor(this.plugin.colors.getString(name)))
					+ ChatColor.stripColor(player.getDisplayName())
					+ ChatColor.WHITE);
		}
	}
}