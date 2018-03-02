package naturix.lagfix;

import org.apache.logging.log4j.core.Logger;

import naturix.lagfix.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod(modid = LagFix.MODID, version = LagFix.VERSION, name = LagFix.NAME)
public class LagFix {
	public static final String MODID = "lagfix";
	public static final String VERSION = "3.0";
	public static final String NAME = "Entity LagFix / Lag Fix";
	public static final Integer nukeRangeDefault = Config.nukeRangeDefault;
	public static final Integer animalLimitDefault = Config.animalLimitDefault; // how many animals of each type are kept when limiting their population.
	public static final Integer animalLimitMinimum = Config.animalLimitMinimum;  // how many animals must be kept minimum.  this command should not remove all animals.
	public static boolean isSinglePlayer=true;
	public static boolean isMultiPlayer=false; // actual values set in serverStart()

	
	@SidedProxy(clientSide = "naturix.lagfix.proxy.ClientProxy", serverSide = "naturix.lagfix.proxy.ServerProxy")
    public static CommonProxy proxy;
	public static org.apache.logging.log4j.Logger logger;
    
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
    Do.Say(player, "  /entitycount or /ecount to count all entities in range");
    Do.Say(player, "  /listother /nukeother /nukearrows /nukemobs /lagfix");
    Do.Say(player, "  /nukeitems to remove all items such as floating on ground");
    Do.Say(player, "  /limitanimals <r> <limit> /nukenonanimals <r> (living only)");
    Do.Say(player, "  /nukeentities or /nukeents to remove all entities in range");
    Do.Say(player, "  /listtiles /nuketileentities or /nuketiles in range");
    Do.Say(player, "  /nukeup /filldown affects all blocks in range");
    Do.Say(player, " /tps will display the current tps");
    Do.Say(player, "One optional parameter specifies the radius of the area.");
    Do.Say(player, "  Its default is "+ nukeRangeDefault +". Diameter is "+(nukeRangeDefault*2+1)+" = "+nukeRangeDefault+"+1+"+nukeRangeDefault+" a "+(nukeRangeDefault*2+1)+" by "+(nukeRangeDefault*2+1)+" area");
    }

  	@EventHandler
  	public void preInit(FMLPreInitializationEvent e) {
  		logger = e.getModLog();
  		proxy.preInit(e);
  		logger.info("LagFix adds alliases to Forge's /forge tps");
  	}
  
  	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}
  	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
	}
	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent e) {
		proxy.serverLoad(e);
	 }
}
