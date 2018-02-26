# LagFix
The Official Github of Andrenoel1's EntityLag fix / LagFix, ported by NicosaurusRex99
https://minecraft.curseforge.com/projects/entity-lagfix-lag-fix

WHAT DOES IT DO
 
The problem is too many items floating on the ground or too many invisible ones that stay in the server or too many generated effects or even a mystery area causing huge lag times every time a player goes near that place (lagging the whole server).
 
As an op you may go to the problem location, preferably the middle of it, and do an /entitycount to see the number of Entities and TileEntities and EntityItems in that area. If it seems excessive such as the 4100+ mysterious entities floating above a beehive farm, that only occasionally appear as flames in the sky, then try this: /nukeentities and it may almost instantly remove the lag, as it did for me. The command removed all entities in the area. The area of effect is a default distance of 32 blocks in all directions from you, so know what is around you before you use this. Also you may specify the radius as a parameter.
 
I needed this for my server several times under different curcumstances. I've seen cobble generators spew 1000s of blocks out causing lag time. Also server crashes leaving malfunctioning mods, and accidental(or not) explosions leaving 1000s of items floating about. Or even 700+ arrows still stuck in the walls and not despawning. The tool has been tested well. It works in single or multiplayer.
 
Be careful. If you use a large radius number you are probably affecting all active areas of the server. This means player's animal farms too. Farm animals, monsters, pets, and all other animals are entities. There are other types of entities in game as well. /nukeentities removes them all in an area. Note that TileEntities include machines, chests, and many plants. Entities and TileEntities are different sets of things in game. Removing them means they are gone. You do not get them back. There is a reason this is an operator only set of commands. Be smart and responsible.
 
This is an op only tool and only needs to be installed on the server side. Clients do not need it, for it to be used. Installing it is just dropping the downloaded file into the /mods/ folder and restarting the game as usual for any forge mod.
 
COMMANDS
In chat type...
 
/entitycount or /ecount to count all entities in range. It displays the number of: items, hostile monsters(mobs), animals, arrows, other entities, total entities, and tile entities.
 
/nukeitems to remove EntityItems such as those dropped on the ground. (a sub type of Entity)

/nukearrows Removes all arrows in range. Due to a bug in Minecraft, arrows sometimes do not despawn

/limitanimals range limit will remove all but the limit-number of animals of each type of animal in range. Farm animals are entity animals. If you have 300 chickens, 500 cows, and 26 pigs within the range, then if you do a /limitanimals 32 40 it will affect a radius of 32 and you should end up with 40 chickens, 40 cows, and 26 pigs. 40 is the default if you do not spefify the limit.

/nukenonanimals will remove all living entities but not remove any animals in range. Farm animals such as sheep and pigs are entity animals and would not be removed.
 
/listother Displays a list of other entity's short names and counts in range. This refers to the "other entities" counted by /entitycount. See example of this.
 
/nukeother Removes all other entities in range. This refers to the "other entities" counted by /entitycount.
 
/nukemobs to remove EntityMobs. (a sub type of Entity)
 
/nukeentities or /nukeents to remove all Entities in range

/listtiles Displays a list of tile entity types by their short names and counts.

/nuketileentities or /nuketiles to remove all TileEntities in range. Remember that TileEntities are different than Entities.

/nukeup to remove all blocks from your standing location to up all the way

/filldown to add fill from below your standing location to bedrock. Also it repairs any holes in the bedrock bottom layer.

 

/lagfix Displays a quick reference of all commands in this mod. Any command with "help" after it will do the same.

 

Specifically filldown fills all air and flowing water and flowing lava with layers as follows: grass(1 deep) dirt(2 deep) stone(3 deep) cobble(fill...) then bedrock(1 deep) at the very bottom layer. Do note that all caves below the area will be filled with cobble which should prevent monsters from spawning below the area. Its handy to make big flat grassy areas, or with a small radius (/filldown 0) make an instant location marker tower if you are flying. This command was just a quick fix for large gaping holes. It was added to crudely fill in holes from /nukeup. It is an operator only command.

 

One optional parameter specifies the radius of the area for any of the commands.

Its default is 32. So the block you are on then 32 blocks out.

Diameter is 65 = 32+1+32, covering a 65 by 65 square area.

 

CommandBlock Compatible. Command blocks using /nukeup will clear from the block directly above itself up. Since CommandBlocks are TileEntities, when they use /nuketileentities they will nuke every tile entity except command blocks within the radius.

 

NOTES
Both Single and Multi player.

Only needed on server side for multiplayer servers.

Source code is included.

This is a Forge mod. No Bukkit not is needed.

The commands are simple.

 
