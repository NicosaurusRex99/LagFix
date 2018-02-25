package naturix.lagfix;

import naturix.lagfix.command.CommandEntityCount;
import naturix.lagfix.command.CommandFillDown;
import naturix.lagfix.command.CommandLagFix;
import naturix.lagfix.command.CommandLimitAnimals;
import naturix.lagfix.command.CommandListOther;
import naturix.lagfix.command.CommandListTiles;
import naturix.lagfix.command.CommandNukeArrows;
import naturix.lagfix.command.CommandNukeEntities;
import naturix.lagfix.command.CommandNukeItems;
import naturix.lagfix.command.CommandNukeMobs;
import naturix.lagfix.command.CommandNukeNonAnimal;
import naturix.lagfix.command.CommandNukeOther;
import naturix.lagfix.command.CommandNukeTileEntities;
import naturix.lagfix.command.CommandNukeUp;
import naturix.lagfix.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.command.ICommandManager;
import net.minecraft.command.ServerCommandManager;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = LagFix.MODID, version = LagFix.VERSION, name = LagFix.NAME)
public class LagFix {
	public static final String MODID = "lagfix";
	public static final String VERSION = "1.12.2.0";
	public static final String NAME = "LagFix";
	
	@SidedProxy(serverSide = "naturix.lagfix.proxy.CommonProxy", clientSide = "naturix.lagfix.proxy.ClientProxy")
	public static CommonProxy PROXY;
  public static final Integer nukeRangeDefault = 32;
  public static final Integer animalLimitDefault = 40; // how many animals of each type are kept when limiting their population.
  public static final Integer animalLimitMinimum = 4;  // how many animals must be kept minimum.  this command should not remove all animals.
  
  public static boolean isSinglePlayer=true;
  public static boolean isMultiPlayer=false; // actual values set in serverStart()

  public static CommonProxy proxy;
  
  @EventHandler
  public void serverLoad(FMLServerStartingEvent event, MinecraftServer server) {
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
    Do.Say(player, "/"+LagFix.NAME+" quick reference (version "+LagFix.VERSION+")");
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
  @EventHandler
	public void preInit(FMLPreInitializationEvent event) {
		PROXY.preInit(event);
	}

	@EventHandler
	public void init(FMLInitializationEvent event) {
		PROXY.init(event);
	}
}
