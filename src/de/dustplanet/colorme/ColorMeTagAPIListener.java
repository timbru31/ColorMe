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
	private ColorMe plugin;
	private Actions actions;
	public ColorMeTagAPIListener(ColorMe instance, Actions actionsInstance) {
		plugin = instance;
		actions = actionsInstance;
	}

	@EventHandler(ignoreCancelled = true)
	public void onNameTag(final PlayerReceiveNameTagEvent event) {
		if (event.getNamedPlayer().hasPermission("colorme.nametag")) {
			String name = ChatColor.stripColor(event.getNamedPlayer().getDisplayName());
			String world = event.getNamedPlayer().getWorld().getName();
			// Fallback
			String color = "WHITE";
			if (actions.has(name, world, "colors"))	color = actions.get(name, world, "colors");
			// Check player default
			else if (actions.has(name, "default", "colors")) color = actions.get(name, "default", "colors");
			// If groups enabled
			else if (plugin.groups && plugin.ownSystem) {
				// If group available
				if (actions.playerHasGroup(name)) {
					String group = actions.playerGetGroup(name);
					// Group specific world
					if (actions.hasGroup(group, world, "colors")) color = actions.getGroup(group, world, "colors");
					// Group default
					else if (actions.hasGroup(group, "default", "colors")) color = actions.getGroup(group, "default", "colors");
				}
			}
			// Then check if still nothing found and globalColor
			if (plugin.globalColor && color == null) {
				if (actions.hasGlobal("color")) color = actions.getGlobal("color");
			}
			// Check if valid
			String[] colors = color.split("-");
			color = colors[0].toUpperCase();
			if (!actions.isStandard(color)) return;
			name = ChatColor.valueOf(color.toUpperCase()) + name;
			if (name.endsWith("\u00A7f")) name = name.substring(0, (name.length() -2));
			if (name != "" && name != null) event.setTag(name);
		}
	}
}
