package naturix.lagfix;

import org.apache.logging.log4j.core.Logger;

import naturix.lagfix.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.entity.player.EntityPlayer;
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
	public static final Integer nukeRangeDefault = 32;
	public static final Integer animalLimitDefault = 40; // how many animals of each type are kept when limiting their population.
	public static final Integer animalLimitMinimum = 4;  // how many animals must be kept minimum.  this command should not remove all animals.
	public static boolean isSinglePlayer=true;
	public static boolean isMultiPlayer=false; // actual values set in serverStart()

	
	@SidedProxy(serverSide = "naturix.lagfix.proxy.CommonProxy", clientSide = "naturix.lagfix.proxy.ClientProxy")
	public static CommonProxy PROXY;
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
  		logger = event.getModLog();
  	}
  
	@EventHandler
	public void init(FMLInitializationEvent event) {
		PROXY.init(event);
	}
	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent event) {
		PROXY.serverLoad(event);
	 }
}
