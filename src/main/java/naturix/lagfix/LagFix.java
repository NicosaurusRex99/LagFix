package aln.LagFix;

import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import aln.LagFix.ModInfo;

@Mod(modid=ModInfo.ID,name=ModInfo.NAME,version=ModInfo.VERS,acceptableRemoteVersions="*") // mc1.7.2, 1.7.10, 1.8
//@Mod(modid=ModInfo.ID,name=ModInfo.NAME,version=ModInfo.VERS) // mc1.6.4
//@NetworkMod(clientSideRequired=false,serverSideRequired=true) // mc1.6.4

public class LagFix {
  public static final Integer nukeRangeDefault = 32;
  public static final Integer animalLimitDefault = 40; // how many animals of each type are kept when limiting their population.
  public static final Integer animalLimitMinimum = 4;  // how many animals must be kept minimum.  this command should not remove all animals.
  
  public static boolean isSinglePlayer=true;
  public static boolean isMultiPlayer=false; // actual values set in serverStart()
  
  @SidedProxy(clientSide=ModInfo.CLIENTPROXY,serverSide=ModInfo.COMMONPROXY)
  public static CommonProxy proxy;
  
  @EventHandler
  public void serverLoad(FMLServerStartingEvent event) {
    MinecraftServer server = MinecraftServer.getServer();
    ICommandManager command = server.getCommandManager();
    ServerCommandManager manager = (ServerCommandManager) command;
    
    manager.registerCommand(new CommandLagFix());
    manager.registerCommand(new CommandEntityCount());
    manager.registerCommand(new CommandNukeItems());
    manager.registerCommand(new CommandNukeArrows());
    manager.registerCommand(new CommandNukeMobs());
    manager.registerCommand(new CommandLimitAnimals());       // limit animals <radius> <number 4 to any, default 50>. per type of animal.
    manager.registerCommand(new CommandNukeNonAnimal());      // nonanimals but still entityliving
    manager.registerCommand(new CommandNukeOther());          
    manager.registerCommand(new CommandListOther());          // future: should dump long named version to file listother.txt
    manager.registerCommand(new CommandNukeTileEntities());
    manager.registerCommand(new CommandListTiles());
    manager.registerCommand(new CommandNukeEntities());
    manager.registerCommand(new CommandNukeUp());
    manager.registerCommand(new CommandFillDown());
    
    //future:
    //manager.registerCommand(new CommandEntityList());       // outputs to chat and a text file. EntityList.txt
    //manager.registerCommand(new CommandEntitySearch());     // outputs to chat and to text file. EntitySearch.txt includes username and date and parameters. and summary counts.
    //manager.registerCommand(new CommandNukeEntitySearch()); // /nukeentitysearch [<radius>] <searchterm>
    
  }
  
  @EventHandler
  public void serverStart(FMLServerStartingEvent e) {
    // detect if singleplayer or multiplayer server.  there should be a better way to detect this.
    isSinglePlayer = true;
    try {
      isSinglePlayer = (Minecraft.getMinecraft() != null);
    } catch (NoClassDefFoundError e1) {
      isSinglePlayer = false;
    }
    isMultiPlayer = ! isSinglePlayer;
  }
  
  public static void ShowHelp(EntityPlayer player) {
    Do.Say(player, "/"+ModInfo.NAME+" quick reference (version "+ModInfo.VERS+")");
    Do.Say(player, "  §e/entitycount§r or /ecount to count all entities in range");
    Do.Say(player, "  §e/listother /nukeother /nukearrows /nukemobs /lagfix§r");
    Do.Say(player, "  §e/nukeitems§r to remove all items such as floating on ground");
    Do.Say(player, "  §e/limitanimals <r> <limit> /nukenonanimals <r>§r (living only)");
    Do.Say(player, "  §e/nukeentities§r or /nukeents to remove all entities in range");
    Do.Say(player, "  §e/listtiles§r §e/nuketileentities§r or /nuketiles in range");
    Do.Say(player, "  §e/nukeup§r §e/filldown§r affects all blocks in range");
    Do.Say(player, "One optional parameter specifies the radius of the area.");
    Do.Say(player, "  Its default is "+ nukeRangeDefault +". Diameter is "+(nukeRangeDefault*2+1)+" = "+nukeRangeDefault+"+1+"+nukeRangeDefault+" a "+(nukeRangeDefault*2+1)+" by "+(nukeRangeDefault*2+1)+" area");
  }
  
}
