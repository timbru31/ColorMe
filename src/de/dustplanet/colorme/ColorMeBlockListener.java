package de.dustplanet.colorme;

import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.block.SignChangeEvent;

public class ColorMeBlockListener implements Listener {
	private ColorMe plugin;
	private Actions actions;
	public ColorMeBlockListener(ColorMe instance, Actions actionsInstance) {
		plugin = instance;
		actions = actionsInstance;
	}

	@EventHandler
	public void onSignChange(SignChangeEvent event) {
		if (!plugin.signColors) return;
		if (!event.getPlayer().hasPermission("colorme.sign")) return;
		// 4 lines a sign
		for (int i = 0; i < 4; i++) {
			// Leave empty out
			if (event.getLine(i).isEmpty()) continue;
			// Update the string
			String updated = actions.replaceThings(event.getLine(i));
			event.setLine(i, updated);
		}
	}
}