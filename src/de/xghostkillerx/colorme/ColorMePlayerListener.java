package de.xghostkillerx.colorme;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ColorMePlayerListener extends PlayerListener {
    ColorMe plugin;
    public ColorMePlayerListener(ColorMe instance) {
        instance = plugin;
    }
    
    public void onPlayerJoin(PlayerJoinEvent event) {
        Player player = event.getPlayer();
        String name = player.getName().toLowerCase();
        //Property cf = plugin.colors.get(player.getWorld());
        
        //if (!cf.keyExists(name)) cf.setString(name, "");
        if (!ColorMe.colors.contains(name)) ColorMe.colors.set(name, "");
        
        if (ColorMeActions.hasColor(name)) {
            player.setDisplayName(ChatColor.valueOf(ColorMeActions.findColor(ColorMe.colors.getString(name))) 
            + ChatColor.stripColor(player.getDisplayName())
            + ChatColor.WHITE);
        }
    }
}
