package de.dustplanet.colorme;

import org.bukkit.ChatColor;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.kitteh.tag.PlayerReceiveNameTagEvent;

/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles the player name (TagAPI)
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

public class ColorMeTagAPIListener implements Listener {

	public ColorMe plugin;
	public ColorMeTagAPIListener(ColorMe instance) {
		plugin = instance;
	}

	@EventHandler(ignoreCancelled = true)
	public void onNameTag(final PlayerReceiveNameTagEvent event) {
		if (event.getNamedPlayer().hasPermission("colorme.nametag")) {
			String name = ChatColor.stripColor(event.getNamedPlayer().getDisplayName());
			String world = event.getNamedPlayer().getWorld().getName();
			// Fallback
			String color = "WHITE";
			if (Actions.has(name, world, "colors"))	color = Actions.get(name, world, "colors");
			// Check player default
			else if (Actions.has(name, "default", "colors")) color = Actions.get(name, "default", "colors");
			// If groups enabled
			else if (ColorMe.groups && ColorMe.ownSystem) {
				// If group available
				if (Actions.playerHasGroup(name)) {
					String group = Actions.playerGetGroup(name);
					// Group specific world
					if (Actions.hasGroup(group, world, "colors")) color = Actions.getGroup(group, world, "colors");
					// Group default
					else if (Actions.hasGroup(group, "default", "colors")) color = Actions.getGroup(group, "default", "colors");
				}
			}
			// Then check if still nothing found and globalColor
			if (ColorMe.globalColor && color == null) {
				if (Actions.hasGlobal("color")) color = Actions.getGlobal("color");
			}
			// Check if valid
			String[] colors = color.split("-");
			color = colors[0].toUpperCase();
			if (!Actions.isStandard(color)) return;
			name = ChatColor.valueOf(color.toUpperCase()) + name;
			if (name.endsWith("\u00A7f")) name = name.substring(0, (name.length() -2));
			if (name != "" && name != null) event.setTag(name);
		}
	}
}
