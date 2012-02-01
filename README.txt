ORIGINAL README
------------------------------------------------------------------------------
My fifth plugin which is the sister to Prefixer. It adds color to a player's
name in the chat window.
------------------------------------------------------------------------------

This is the README of ColorMe!
For support visit the old forum thread: http://bit.ly/colormebukkit
or the new dev.bukkit.org page: http://bit.ly/bukkitdevcolorme
Thanks to Valrix for the original plugin!
Thanks for using!

This plugin sends usage statistics! If you wish to disable the usage stats, look at plugins/PluginMetrics/config.yml!
This plugin is released under the Creative Commons Attribution-NonCommercial-ShareAlike 3.0 Unported (CC BY-NC-SA 3.0) license.

This plugin supports economy! Required is Vault, if you want to use economy!
Additionally support for Spout to change the color of the player title.

Standard config:

# For help please refer to http://bit.ly/colormebukkit or http://bit.ly/bukkitdevcolorme
costs: 5.00
forceUpdate: false
tabList: true

Commands & Permissions (if no permissions system is detected, only OPs are able to use the commands!)
Only bukkit's permissions system is supported!

<> = Required, [] = Optional

/color reload
Node: colorme.reload
Description: Reloads the config

/color list
Node: colorme.list
Description: Displays the color list

/color help
Description: Displays the help

/color remove [name]
Node: colorme.remove
Description: Removes color (no name -> yourself)

/color get [name]
Node: colorme.get
Description: Gets actual color (no name -> yourself)

/color me <color>
Node: colorme.self
Description: Sets your own color

/color <name> <color>
Node: colorme.other
Description: Sets color for an other player