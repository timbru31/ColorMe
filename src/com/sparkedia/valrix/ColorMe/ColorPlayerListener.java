package com.sparkedia.valrix.ColorMe;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerListener;

public class ColorPlayerListener extends PlayerListener {
    protected ColorMe plugin;
    
    public ColorPlayerListener(ColorMe plugin) {
        this.plugin = plugin;
    }
    
    @Override
    public void onPlayerJoin(PlayerJoinEvent e) {
        Player player = e.getPlayer();
        String name = player.getName().toLowerCase();
        //Property cf = plugin.colors.get(player.getWorld());
        
        //if (!cf.keyExists(name)) cf.setString(name, "");
        if (!plugin.colors.keyExists(name)) plugin.colors.setString(name, "");
        
        if (plugin.hasColor(name)) {
            player.setDisplayName(ChatColor.valueOf(plugin.findColor(plugin.colors.getString(name)))+ChatColor.stripColor(player.getDisplayName())+ChatColor.WHITE);
        }
    }
}
