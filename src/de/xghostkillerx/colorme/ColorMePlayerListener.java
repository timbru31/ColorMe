package de.xghostkillerx.colorme;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerJoinEvent;

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

public class ColorMePlayerListener implements Listener {
	public ColorMe plugin;
	public ColorMePlayerListener(ColorMe instance) {
		plugin = instance;
	}
	private String[] pluginPart = {"colors", "prefix", "suffix"};
	private String actualPart, name, world, sub1, sub2, suffix = null, prefix = null;
	private int i, length;

	// Loads the the values and set them to default one if not known
	@EventHandler
	public void onPlayerJoin(final PlayerJoinEvent event) {
		Player player = event.getPlayer();
		name = player.getName().toLowerCase();
		world = player.getWorld().getName().toLowerCase();
		CheckRoutine(player, name, world);
	}

	// Loads the the values and set them to default one if not known
	@EventHandler
	public void onPlayerChat(final PlayerChatEvent event) {
		Player player = event.getPlayer();
		name = player.getName().toLowerCase();
		world = player.getWorld().getName().toLowerCase();
		CheckRoutine(player, name, world);
		// Get world prefix if available
		if (Actions.has(name, world, "prefix")) {
			prefix = Actions.get(name, world, "prefix");
		}
		// Get default prefix
		else if (Actions.has(name, "default", "prefix")) {
			prefix = Actions.get(name, "default", "prefix");
		}
		// Get the global prefix
		else if (Actions.hasGlobal("prefix")) {
			prefix = Actions.getGlobal("prefix");
		}
		// If prefix is not null change the format
		if (prefix != null) {
			event.setFormat(prefix + ChatColor.WHITE + " " + event.getFormat());
		}
		// Get world suffix if available
		if (Actions.has(name, world, "suffix")) {
			suffix = Actions.get(name, world, "suffix");
		}
		// Get default suffix
		else if (Actions.has(name, "default", "suffix")) {
			suffix = Actions.get(name, "default", "suffix");
		}
		// Get the global suffix
		else if (Actions.hasGlobal("suffix")) {
			suffix = Actions.getGlobal("suffix");
		}
		// If suffix is not null
		if (suffix != null) {
			// Search the bracket
			if (event.getFormat().contains(">")) {
				i = event.getFormat().lastIndexOf(">") + 1;
				length = event.getFormat().length();
				// Substring 1 until the bracket, substring 2 after the bracket
				sub1 = event.getFormat().substring(0, i);
				sub2 = event.getFormat().substring(i, length);
				// Insert the suffix between ;)
				event.setFormat(sub1 + " " + suffix + ChatColor.WHITE + ":" + sub2);
			}
		}
		prefix = null;
		suffix = null;
	}

	// Check for the player and update the file is values are unknown
	private void CheckRoutine(Player player, String name, String world) {
		plugin.loadConfigsAgain();
		i = 0;
		// If the player isn't in the players.color add him
		if (!ColorMe.players.contains(name)) {
			ColorMe.players.set(name + ".colors.default", "");
			ColorMe.players.set(name + ".prefix.default", "");
			ColorMe.players.set(name + ".suffix.default", "");
			ColorMe.players.set(name + ".colors." + world, "");
			ColorMe.players.set(name + ".prefix." + world, "");
			ColorMe.players.set(name + ".suffix." + world, "");
			ColorMe.savePlayers();
		}
		for (i = 0; i <= 2; i++) {
			actualPart = pluginPart[i];
			if (!ColorMe.players.contains(name + "." + actualPart + "." + world)) {
				ColorMe.players.set(name + "." + actualPart + "." + world, "");
				ColorMe.savePlayers();
			}
			if (!ColorMe.players.contains(name + "." + actualPart + "." + "default")) {
				ColorMe.players.set(name + "." + actualPart + "." + "default", "");
				ColorMe.savePlayers();
			}
		}
		if (Actions.has(name, world, "colors")) {
			Actions.updateName(name, world);
		}
		else if (Actions.has(name, "default", "colors")) {
			Actions.updateName(name, "default");
		}
		else if (Actions.hasGlobal("color")) {
			Actions.updateNameGlobal(name);
		}
	}
}