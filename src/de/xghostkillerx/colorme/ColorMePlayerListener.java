package de.xghostkillerx.colorme;

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

	private void CheckRoutine(Player player, String name) {
		plugin.loadConfigAgain();
		// If the player isn't in the players.color add him
		if (!plugin.colors.contains(name)) {
			plugin.colors.set(name, "");
			plugin.saveColors();
		}
		plugin.updateName(name);
	}
}