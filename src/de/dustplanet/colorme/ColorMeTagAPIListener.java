package de.dustplanet.colorme;

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
			String name = event.getNamedPlayer().getDisplayName();
			if (name.endsWith("\u00A7f")) {
				name = name.substring(0, (name.length() -2));
			}
			if (Actions.has(name, event.getNamedPlayer().getWorld().getName(), "colors")) {
				if (Actions.get(name, event.getNamedPlayer().getWorld().getName(), "colors").equalsIgnoreCase("rainbow")) return;
				if (Actions.get(name, event.getNamedPlayer().getWorld().getName(), "colors").equalsIgnoreCase("random")) return;
			}
			else if (Actions.has(name, "default", "colors")) {
				if (Actions.get(name, "default", "colors").equalsIgnoreCase("rainbow")) return;
				if (Actions.get(name, "default", "colors").equalsIgnoreCase("random")) return;
			}
			if (name != "" && name != null) {
				event.setTag(name);
			}
		}
	}
}
