package de.dustplanet.colorme.commands;

import java.util.List;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import de.dustplanet.colorme.Actions;
import de.dustplanet.colorme.ColorMe;

/**
 * ColorMe for CraftBukkit/Bukkit
 * Handles the group command
 * 
 * Refer to the forum thread:
 * http://bit.ly/colormebukkit
 * 
 * Refer to the dev.bukkit.org page:
 * http://bit.ly/bukkitdevcolorme
 * 
 * @author xGhOsTkiLLeRx
 * thanks to Valrix for the original ColorMe plugin!!
 * 
 */

public class GroupCommands implements CommandExecutor {
    private Actions actions;
    private ColorMe plugin;

    public GroupCommands(ColorMe instance, Actions actionsInstance) {
	plugin = instance;
	actions = actionsInstance;
    }

    // Commands for groups
    public boolean onCommand(CommandSender sender, Command command, String commandLabel, String[] args) {
	String message, world = "default";
	// Reloads the configs
	if (args.length > 0 && args[0].equalsIgnoreCase("reload")) {
	    if (sender.hasPermission("groups.reload")) {
		actions.reload(sender);
		return true;
	    } else {
		message = plugin.localization.getString("permission_denied");
		plugin.message(sender, null, message, null, null, null, null);
		return true;
	    }
	}
	// Stop here if own group system is unwanted
	if (!plugin.groups || !plugin.ownSystem) {
	    message = plugin.localization.getString("part_disabled");
	    plugin.message(sender, null, message, null, null, null, null);
	    return true;
	}
	// Displays the help
	if (args.length > 0 && args[0].equalsIgnoreCase("help")) {
	    actions.help(sender, "group");
	    return true;
	}
	// Create a group
	if (args.length > 1 && args[0].equalsIgnoreCase("create")) {
	    if (sender.hasPermission("groups.create")) {
		String groupName = args[1].toLowerCase();
		// If the group exists, stop here
		if (actions.existsGroup(groupName)) {
		    message = plugin.localization.getString("group_already_existing");
		    plugin.message(sender, null, message, groupName, null, null, null);
		    return true;
		}
		// Create group and message
		actions.createGroup(groupName);
		message = plugin.localization.getString("created_group");
		plugin.message(sender, null, message, groupName, null, null, null);
	    } else {
		message = plugin.localization.getString("permission_denied");
		plugin.message(sender, null, message, null, null, null, null);
	    }
	    return true;
	}
	// Adds a member to an existing group
	else if (args.length > 2 && args[0].equalsIgnoreCase("add")) {
	    if (sender.hasPermission("groups.addmember")) {
		String groupName = args[1].toLowerCase();
		String name = args[2].toLowerCase();
		// Stop if the group isn't existing
		if (!actions.existsGroup(groupName)) {
		    message = plugin.localization.getString("group_not_existing");
		    plugin.message(sender, null, message, groupName, null, null, null);
		    return true;
		}
		// Stop if the "name" is already a member
		if (actions.isMember(groupName, name)) {
		    message = plugin.localization.getString("already_a_member");
		    plugin.message(sender, null, message, groupName, name, null, null);
		    return true;
		}
		// Add and message
		actions.addMember(groupName, name);
		message = plugin.localization.getString("added_member");
		plugin.message(sender, null, message, groupName, name, null, null);
	    } else {
		message = plugin.localization.getString("permission_denied");
		plugin.message(sender, null, message, null, null, null, null);
	    }
	    return true;
	}
	// Removes a member from an existing group
	else if (args.length > 2 && args[0].equalsIgnoreCase("remove")) {
	    if (sender.hasPermission("groups.removemember")) {
		String groupName = args[1].toLowerCase();
		String name = args[2].toLowerCase();
		// Stop if the group isn't existing
		if (!actions.existsGroup(groupName)) {
		    message = plugin.localization.getString("group_not_existing");
		    plugin.message(sender, null, message, groupName, null, null, null);
		    return true;
		}
		// Stop if the "name" is not a member
		if (!actions.isMember(groupName, name)) {
		    message = plugin.localization.getString("not_a_member");
		    plugin.message(sender, null, message, groupName, name, null, null);
		    return true;
		}
		// Remove the member and message
		actions.removeMember(groupName, name);
		message = plugin.localization.getString("removed_member");
		plugin.message(sender, null, message, groupName, name, null, null);
	    } else {
		message = plugin.localization.getString("permission_denied");
		plugin.message(sender, null, message, null, null, null, null);
	    }
	    return true;
	}
	// Deletes a group
	else if (args.length > 1 && args[0].equalsIgnoreCase("delete")) {
	    if (sender.hasPermission("groups.delete")) {
		String groupName = args[1].toLowerCase();
		// Stop if the group isn't existing
		if (!actions.existsGroup(groupName)) {
		    message = plugin.localization.getString("group_not_existing");
		    plugin.message(sender, null, message, groupName, null, null, null);
		    return true;
		}
		// Delete the group and message
		actions.deleteGroup(groupName);
		message = plugin.localization.getString("deleted_group");
		plugin.message(sender, null, message, groupName, null, null, null);
	    } else {
		message = plugin.localization.getString("permission_denied");
		plugin.message(sender, null, message, null, null, null, null);
	    }
	    return true;
	}
	// Sets either a color, prefix or suffix for a group
	else if (args.length > 3 && args[0].equalsIgnoreCase("set")) {
	    if (sender.hasPermission("groups.set")) {
		String groupName = args[1].toLowerCase(), part = args[2].toLowerCase(), value = args[3].toLowerCase();
		if (args.length > 4) {
		    world = args[4].toLowerCase();
		}
		// Check if the wrong part is included
		if (!part.equalsIgnoreCase("colors") && !part.equalsIgnoreCase("prefix") && !part.equalsIgnoreCase("suffix")) {
		    message = plugin.localization.getString("unrecognized_part");
		    plugin.message(sender, null, message, part, null, null, null);
		    return true;
		}
		// Check if the group exists
		if (!actions.existsGroup(groupName)) {
		    message = plugin.localization.getString("group_not_existing");
		    plugin.message(sender, null, message, groupName, null, null, null);
		    return true;
		}
		// Sets the group and messages
		actions.setGroup(groupName, value, world, part);
		message = plugin.localization.getString("group_set_value");
		plugin.message(sender, null, message, groupName, world, value, null);
	    } else {
		message = plugin.localization.getString("permission_denied");
		plugin.message(sender, null, message, null, null, null, null);
	    }
	    return true;
	}
	// Lists the current members of a group
	else if (args.length > 1 && args[0].equalsIgnoreCase("members")) {
	    if (sender.hasPermission("groups.members")) {
		String groupName = args[1].toLowerCase();
		// Stop if the group isn't existing
		if (!actions.existsGroup(groupName)) {
		    message = plugin.localization.getString("group_not_existing");
		    plugin.message(sender, null, message, groupName, null, null, null);
		    return true;
		}
		// Get the lit and replace the "[" and "]". If the list is
		// empty, say no members
		message = plugin.localization.getString("memberlist");
		plugin.message(sender, null, message, groupName, null, null, null);
		List<String> list = actions.listMembers(groupName);
		if (list.isEmpty())
		    sender.sendMessage(actions.replaceThings(plugin.localization.getString("no_members")));
		else {
		    String msg = list.toString();
		    msg = msg.substring(1, (list.size() - 1));
		    sender.sendMessage(msg);
		}
	    } else {
		message = plugin.localization.getString("permission_denied");
		plugin.message(sender, null, message, null, null, null, null);
	    }
	    return true;
	}
	// Lists all current groups
	else if (args.length > 0 && args[0].equalsIgnoreCase("list")) {
	    if (sender.hasPermission("groups.grouplist")) {
		message = plugin.localization.getString("grouplist");
		plugin.message(sender, null, message, null, null, null, null);
		StringBuffer buf = new StringBuffer();
		// Iterate through the groups
		for (String key : plugin.group.getKeys(false)) {
		    buf.append(key + ", ");
		}
		String groups = buf.toString();
		// If groups is still empty change message, else replace last
		// ","
		if (groups.equalsIgnoreCase("")) {
		    groups = actions.replaceThings(plugin.localization.getString("no_groups"));
		} else {
		    groups = groups.substring(0, (groups.length() - 2));
		}
		sender.sendMessage(groups);
	    } else {
		message = plugin.localization.getString("permission_denied");
		plugin.message(sender, null, message, null, null, null, null);
	    }
	    return true;
	}
	// Gets a color, prefix or suffix from a group
	else if (args.length > 2 && args[0].equalsIgnoreCase("get")) {
	    if (sender.hasPermission("group.get")) {
		String groupName = args[1], part = args[2];
		if (args.length > 3) {
		    world = args[3];
		}
		// Stop if the group isn't existing
		if (!actions.existsGroup(groupName)) {
		    message = plugin.localization.getString("group_not_existing");
		    plugin.message(sender, null, message, groupName, null, null, null);
		    return true;
		}
		// Check if the wrong part is included
		if (!part.equalsIgnoreCase("colors") && !part.equalsIgnoreCase("prefix") && !part.equalsIgnoreCase("suffix")) {
		    message = plugin.localization.getString("unrecognized_part");
		    plugin.message(sender, null, message, part, null, null, null);
		    return true;
		}
		// If the group has nothing
		if (!actions.hasGroup(groupName, world, part)) {
		    message = plugin.localization.getString("group_has_nothing");
		    plugin.message(sender, null, message, groupName, world, null, null);
		    return true;
		}
		// Get the string and message
		String result = actions.getGroup(groupName, world, part);
		message = plugin.localization.getString("group_get_value");
		plugin.message(sender, null, message, result, world, null, null);
	    } else {
		message = plugin.localization.getString("permission_denied");
		plugin.message(sender, null, message, null, null, null, null);
	    }
	    return true;
	}
	return false;
    }
}