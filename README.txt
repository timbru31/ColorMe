ORIGINAL README
------------------------------------------------------------------------------
ColorMe:

My fifth plugin which is the sister to Prefixer. It adds color to a player's
name in the chat window.

Prefixer:

This is another small plugin that allows OPs to give players a custom prefix.
The format is [PREFIX] <PLAYER_NAME> MESSAGE

So if I send "Hello" with the prefix of "Admin", it looks like:
[Admin] <Valrix> Hello

To set the prefix just use the /prefix command. For example, let's give me the
"Admin" prefix:

/prefix Valrix Admin
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
costs:
  color: 5.0
  prefix: 5.0
  suffix: 5.0
Prefixer: true
Suffixer: true
ColorMe:
  displayName: true
  tabList: false
  playerTitle: true
colors:
  black: true
  dark_blue: true
  dark_green: true
  dark_aqua: true
  dark_red: true
  dark_purple: true
  gold: true
  gray: true
  dark_gray: true
  blue: true
  green: true
  aqua: true
  red: true
  light_purple: true
  yellow: true
  white: true
  magic: true
  random: true
  rainbow: true
global_default:
  prefix: ''
  suffix: ''
  color: ''

Commands & Permissions (if no permissions system is detected, only OPs are able to use the commands!)
Only bukkit's permissions system is supported!

<> = Required, [] = Optional

Global:

/color list
Node: colorme.list or prefixer.list or suffixer.list
Description: Displays the color list

ColorMe:

/color reload
Node: colorme.reload
Description: Reloads the config

/color help
Description: Displays the help

/color remove <name> [world]
Node: colorme.remove
Description: Removes color ("me" as the name -> yourself)

/color remove global
Node: colorme.global
Description: Removes the global color

/color get <name> [world]
Node: colorme.get
Description: Gets actual color ("me" as the name -> yourself)

/color get global
Node: colorme.global
Description: Gets the global color

/color me <color> [world]
Node: colorme.self.<color>
Node: colorme.free -> No costs
Description: Sets your own color (no world -> default)

/color <name> <color> [world]
Node: colorme.other
Description: Sets color for an other player (no world -> default)

/color global <color>
Node: colorme.global
Description: Sets the global color

Prefixer:

/prefix reload
Node: prefixer.reload
Description: Reloads the config

/prefix help
Description: Displays the help

/prefix remove <name> [world]
Node: prefixer.remove
Description: Removes a prefix ("me" as the name -> yourself)

/prefix remove global
Node: prefixer.global
Description: Removes the global prefix

/prefix get <name> [world]
Node: prefixer.get
Description: Gets actual prefix ("me" as the name -> yourself)

/prefix get global
Node: prefixer.global
Description: Gets the global prefix

/prefix  me <prefix> [world]
Node: prefixer.self
Node: prefixer.free -> No costs
Description: Sets your own prefix (no world -> default)

/prefix <name> <prefix> [world]
Node: prefixer.other
Description: Sets prefix for an other player (no world -> default)

/prefix global <prefix>
Node: prefixer.global
Description: Sets the global prefix

Suffixer:

/suffix reload
Node: suffixer.reload
Description: Reloads the config

/suffix help
Description: Displays the help

/suffix remove <name> [world]
Node: suffixer.remove
Description: Removes a suffix ("me" as the name -> yourself)

/suffix remove global
Node: suffixer.global
Description: Removes the global suffix

/suffix get <name> [world]
Node: suffixer.get
Description: Gets actual suffix ("me" as the name -> yourself)

/suffix get global
Node: suffixer.global
Description: Gets the global suffix

/suffix  me <suffix> [world]
Node: suffixer.self
Node: suffixer.free -> No costs
Description: Sets your own suffix (no world -> default)

/suffix <name> <suffix> [world]
Node: suffixer.other
Description: Sets suffix for an other player (no world -> default)

/suffix global <suffix>
Node: suffixer.global
Description: Sets the global suffix