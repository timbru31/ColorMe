package de.xghostkillerx.colorme;

import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

public class PrefixCommands implements CommandExecutor {
	
	ColorMe plugin;
	public PrefixCommands(ColorMe instance) {
		plugin = instance;
	}
	
	public boolean onCommand(CommandSender arg0, Command arg1, String arg2,
			String[] arg3) {
		// TODO Auto-generated method stub
		return false;
	}

}
 /*
String a0 = (args.length>0) ? args[0] : "";
if (args.length == 1) {
    // Player doesn't have permission over self. Stop here.
    if (!player.hasPermission("prefixer.self")) {
        player.sendMessage("You don't have permission to alter your own prefix.");
        return true;
    }
    // Player is allowed to alter self, are they removing their prefix?
    if (a0.equalsIgnoreCase("-r")) {
        // Do they have permission to remove it?
        if (!player.hasPermission("prefixer.remove")) {
            player.sendMessage("You don't have permission to remove prefixes.");
            return true;
        }
        removePrefix(player.getWorld(), player.getName());
        player.sendMessage("Your prefix has been removed.");
        return true;
    }
    // Not removing the prefix, so they're setting it.
    setPrefix(player.getWorld(), player.getName(), a0);
    player.sendMessage("Your prefix has been set.");
    return true;
} else if (args.length>1) {
    // /prefix <prefix> [name...]
    List<String> a = new ArrayList<String>();
    byte i = 0;
    while (i++<args.length-1) a.add(args[i]); // Skip the first argument since we only want the names
    for (String p : a) {
        // Removing prefixes
        if (a0.equalsIgnoreCase("-r")) {
            // Not allowed to remove prefixes.
            if (!player.hasPermission("prefixer.remove")) {
                player.sendMessage("You don't have permission to remove prefixes.");
                return true;
            }
            // Removing own prefix
            if (self(player, p)) {
                if (!player.hasPermission("prefixer.self")) {
                    player.sendMessage("You don't have permission to remove your own prefix.");
                    return true;
                }
                removePrefix(player.getWorld(), player.getName());
                player.sendMessage("Your prefix has been removed.");
                if (p.equalsIgnoreCase(args[args.length-1])) return true;
                continue;
            }
            if (!player.hasPermission("prefixer.other")) {
                player.sendMessage("You don't have permission to remove another player's prefix.");
                return true;
            }
            if (getServer().getPlayer(p) != null) {
                Player other = getServer().getPlayer(p);
                removePrefix(other.getWorld(), p);
                other.sendMessage("Your prefix has been removed.");
            } else {
                // Player isn't online, so remove prefix from all worlds.
                removePrefix(null, p);
            }
            player.sendMessage("Removed "+p+"'s prefix.");
            if (p.equalsIgnoreCase(args[args.length-1])) return true;
            continue;
        } else {
            if (self(player, p)) {
                if (!player.hasPermission("prefixer.self")) {
                    player.sendMessage("You don't have permission to set your own prefix.");
                    return true;
                }
                setPrefix(player.getWorld(), player.getName(), a0);
                player.sendMessage("Your prefix has been set.");
                if (p.equalsIgnoreCase(args[args.length-1])) return true;
                continue;
            } else {
                if (!player.hasPermission("prefixer.other")) {
                    player.sendMessage("You don't have permission to set "+p+"'s prefix.");
                    return true;
                }
                if (getServer().getPlayer(p) == null) {
                    player.sendMessage(p+" doesn't seem to be online. Can't set their prefix.");
                    return true;
                }
                Player other = getServer().getPlayer(p);
                setPrefix(other.getWorld(), p, a0);
                other.sendMessage("Your prefix has been set.");
                player.sendMessage("Set "+other.getName()+"'s prefix.");
                if (p.equalsIgnoreCase(args[args.length-1])) return true;
                continue;
            }
        }
    }
    return false;
}
}
return false;
}
*/