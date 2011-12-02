package de.xghostkillerx.colorme;

import java.io.File;
import java.util.LinkedHashMap;
//import java.util.Map;
import java.util.logging.Logger;

import net.milkbowl.vault.Vault;
import net.milkbowl.vault.economy.Economy;

import org.bukkit.ChatColor;
//import org.bukkit.World;
import org.bukkit.command.Command;
import org.bukkit.command.CommandSender;
import org.bukkit.craftbukkit.TextWrapper;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Type;
import org.bukkit.event.Event.Priority;
import org.bukkit.plugin.Plugin;
import org.bukkit.plugin.RegisteredServiceProvider;
import org.bukkit.plugin.java.JavaPlugin;


/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles some general stuff!
 * 
 * Refer to the forum thread:
 * 
 * Refer to the dev.bukkit.org page:
 * 
 *
 * @author xGhOsTkiLLeRx
 * @thanks to Valrix for the original ColorMe plugin!!
 * 
 */

public class ColorMe extends JavaPlugin {
	public Logger log;
	//public Map<World, Property> colors;
	public Property colors;
	public String pName;
	public String df;
	public String uf;
	Property conf;
	public Economy economy = null;
	private void mkDir(String...d) {
		for (String f : d) new File(f).mkdir();
	}
	/* TODO: Implement when events can be unregistered.
    private void reload() {
        getServer().getPluginManager().disablePlugin(this);
        getServer().getPluginManager().enablePlugin(this);
    }

    private boolean isAdmin(Player p) {
        if (p.getName().equalsIgnoreCase(conf.getString("admin"))) return true;
        return false;
    }*/

	public boolean self(Player p, String n) {
		return (p.equals(getServer().getPlayer(n))) ? true : false;
	}

	public void list(Player p) {
		p.sendMessage("Color List:");
		String color;
		String msg = "";
		for (int i = 0; i < ChatColor.values().length; i++) {
			color = ChatColor.getByCode(i).name();
			if (msg.length() == 0) {
				msg = ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
				continue;
			}
			msg += (i == ChatColor.values().length-1) ? ChatColor.valueOf(color)+color.toLowerCase().replace("_", "") : ChatColor.valueOf(color)+color.toLowerCase().replace("_", "")+' ';
			TextWrapper.wrapText(msg);
		}
		p.sendMessage(msg);
	}

	private Boolean setupEconomy()
	{
		RegisteredServiceProvider<Economy> economyProvider = getServer().getServicesManager().getRegistration(net.milkbowl.vault.economy.Economy.class);
		if (economyProvider != null) {
			economy = economyProvider.getProvider();
		}

		return (economy != null);
	}

	@Override
	public void onDisable() {
		// Clean up the garbage before disable
		colors.clear();
		colors = null;
		conf.clear();
		conf = null;
		log = null;
		pName = null;
		df = null;
		uf = null;

		getServer().getLogger().info("[ColorMe] has been disabled.");
	}

	@Override
	public void onEnable() {
		log = getServer().getLogger();
		pName = getDescription().getName();

		df = getDataFolder().toString();
		uf = df+"/../"+getServer().getUpdateFolder();
		mkDir(df, uf);


		/* Load each world's color list
        for (World w : getServer().getWorlds()) {
            Property worldFile = new Property(df+'/'+w.getName()+".color", this);
            colors.put(w, worldFile);
        }
		 */
		colors = new Property(df+'/'+"players.color", this);
		colors.save();

		// Does the config exist, if not then make a new blank one
		if (!(new File(df+"/config.txt").exists())) {
			conf = new Property(df+"/config.txt", this);
			conf.setString("admin", "");
			conf.setNumber("cost", 0);
			conf.save();
		} else {
			conf = new Property(df+"/config.txt", this);
			// Check if they have the updated prefix property file, otherwise update it to new format
			if (!getDescription().getVersion().equalsIgnoreCase(conf.getString(pName+"Version"))) {
				LinkedHashMap<String, Object> tmp = new LinkedHashMap<String, Object>();
				conf.remove(pName+"Version");
				tmp.put("admin", conf.getString("admin"));
				for (String key : conf.getKeys())
					tmp.put(key, conf.getString(key));
				conf.rebuild(tmp);
			}
		}
		getServer().getPluginManager().registerEvent(Type.PLAYER_JOIN, new ColorPlayerListener(this), Priority.Lowest, this);

		log.info('['+pName+"] v"+getDescription().getVersion()+" has been enabled.");
		Plugin x = this.getServer().getPluginManager().getPlugin("Vault");
		if(x != null & x instanceof Vault) {
			log.info(String.format("[%s] Enabled Version %s", getDescription().getName(), getDescription().getVersion()));
			setupEconomy();
		} else {
			/**
			 * Throw error & disable because we have Vault set as a dependency, you could give a download link
			 * or even download it for the user.  This is all up to you as a developer to decide the best option
			 * for your users!  For our example, we assume that our audience (developers) can find the Vault
			 * plugin and properly install it.  It's usually a bad idea however.
			 */
			log.warning(String.format("[%s] Vault was NOT found! Running without economy!", getDescription().getName()));
		}
	}
	
	public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
		ColorMeCommands cmd = new ColorMeCommands(this);
		return cmd.ColorMeCommand(sender, command, commandLabel, args);
	}

	// Return the player's name color
	public String getColor(String name) {
		return (colors.keyExists(name.toLowerCase())) ? colors.getString(name.toLowerCase()) : "";
	}

	// Set player's color and update displayname if online
	public boolean setColor(String name, String color) {
		String newColor = findColor(color); 
		if (newColor.equals(color)) return false;
		colors.setString(name.toLowerCase(), newColor);
		colors.save();
		if (getServer().getPlayer(name) != null) {
			Player p = getServer().getPlayer(name);
			p.setDisplayName(ChatColor.valueOf(newColor)+ChatColor.stripColor(p.getDisplayName())+ChatColor.WHITE);
		}
		return true;
	}

	// Iterate through colors to try and find a match (resource expensive)
	public String findColor(String color) {
		String col;
		for (int i = 0; i <= 15; i++) {
			col = ChatColor.getByCode(i).name();
			if (color.equalsIgnoreCase(col.toLowerCase().replace("_", ""))) return col;
		}
		return color;
	}

	// Check if a player has a color or not
	public boolean hasColor(String name) {
		return (colors.getString(name.toLowerCase()).trim().length()>1) ? true : false;
	}

	// Removes a color if exists, otherwise returns false
	public boolean removeColor(String name) {
		name = name.toLowerCase();
		if (hasColor(name)) {
			colors.setString(name, "");
			colors.save();
			return true;
		}
		return false;
	}
}
