package naturix.lagfix;

import java.awt.Color;
import java.util.regex.Pattern;

import org.lwjgl.input.Keyboard;

import jline.internal.Log;
import naturix.lagfix.FPS.CounterPosition;
import naturix.lagfix.FPS.FPSRenderEventHandler;
import naturix.lagfix.FPS.PositionUtil;
import naturix.lagfix.proxy.CommonProxy;
import net.minecraft.client.Minecraft;
import net.minecraft.client.settings.KeyBinding;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraftforge.common.MinecraftForge;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.event.ConfigChangedEvent;
import net.minecraftforge.fml.client.registry.ClientRegistry;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.Mod.EventHandler;
import net.minecraftforge.fml.common.SidedProxy;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;
import net.minecraftforge.fml.common.eventhandler.SubscribeEvent;
import net.minecraftforge.fml.common.gameevent.InputEvent;

@Mod(modid = Main.MODID, version = Main.VERSION, name = Main.NAME, guiFactory = Main.GUI_FACTORY, updateJSON = Main.UPDATE_URL)
public class Main {
	public static final String MODID = "lagfix";
	public static final String VERSION = "1.12.2-1.0";
	public static final String NAME = "Lag Fix";
	public static final Integer nukeRangeDefault = Config.nukeRangeDefault;
	public static final Integer animalLimitDefault = Config.animalLimitDefault; // how many animals of each type are kept when limiting their population.
	public static final Integer animalLimitMinimum = Config.animalLimitMinimum;  // how many animals must be kept minimum.  this command should not remove all animals.
	public static boolean isSinglePlayer=true;
	public static boolean isMultiPlayer=false; // actual values set in serverStart()

	public static final String URL = "https://github.com/NicosaurusRex99/LagFix";
    public static final String UPDATE_URL = "https://raw.githubusercontent.com/NicosaurusRex99/LagFix/1.12.2/nsdailr.json";

	public static CounterPosition pos = CounterPosition.BOTTOM_LEFT;
    public static int counterColorCode = 0x00ff00;
    public static final String GUI_FACTORY = "naturix.lagfix.FPS.LFModGuiFactory";
	
	@SidedProxy(clientSide = "naturix.lagfix.proxy.ClientProxy", serverSide = "naturix.lagfix.proxy.ServerProxy")
    public static CommonProxy proxy;
	public static org.apache.logging.log4j.Logger logger;
	public static Configuration config;
    
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
    Do.Say(player, "/lag quick reference (version "+Main.VERSION+")");
    Do.Say(player, "  /entitycount or /ecount to count all entities in range");
    Do.Say(player, "  /listother /nukeother /nukearrows /nukemobs /lag");
    Do.Say(player, "  /nukeitems to remove all items such as floating on ground");
    Do.Say(player, "  /limitanimals <r> <limit> /nukenonanimals <r> (living only)");
    Do.Say(player, "  /nukeentities or /nukeents to remove all entities in range");
    Do.Say(player, "  /listtiles /nuketileentities or /nuketiles in range");
    Do.Say(player, "  /nukeup /filldown affects all blocks in range");
    //Do.Say(player, " /tps will display the current tps");
    Do.Say(player, "One optional parameter specifies the radius of the area.");
    Do.Say(player, "  Its default is "+ nukeRangeDefault +". Diameter is "+(nukeRangeDefault*2+1)+" = "+nukeRangeDefault+"+1+"+nukeRangeDefault+" a "+(nukeRangeDefault*2+1)+" by "+(nukeRangeDefault*2+1)+" area");
    }
  public static KeyBinding keyToggle;
  public static Minecraft mc;
  	@EventHandler
  	public void preInit(FMLPreInitializationEvent e) {
  		logger = e.getModLog();
  		proxy.preInit(e);
  		logger.info(Main.NAME + " adds alliases to Forge's /forge tps");

  		if(isSinglePlayer) {
  		mc = Minecraft.getMinecraft();
        keyToggle = new KeyBinding("FPS Display", Keyboard.KEY_G, Main.NAME);
        ClientRegistry.registerKeyBinding(keyToggle);
        loadConfig(e);
  		}
  	}
  	private void loadConfig(FMLPreInitializationEvent event) {
        config = new Configuration(event.getSuggestedConfigurationFile());
        MinecraftForge.EVENT_BUS.register(this);

        syncConfig();
    }
  	public static void syncConfig() {
        try {
            processConfig();
        } catch (Exception e) {
            Log.error(MODID + " has a problem loading its configuration!");
            e.printStackTrace();
        } finally {
            if(config.hasChanged()) {
                config.save();
            }
        }
    }

    private static void processConfig() {
        pos = PositionUtil.toPosition(config.get(Configuration.CATEGORY_GENERAL, "fpsDisplayPosition", "topLeft",
                "Position of the FPS display on screen", CounterPosition.positionValues()).getString());
        counterColorCode = Color.decode(config.get(Configuration.CATEGORY_GENERAL, "fpsDisplayColor",
                "#FF0000", "Hex color code for the FPS display", Pattern.compile("^#([A-Fa-f0-9]{6}|[A-Fa-f0-9]{3})$",
                        Pattern.CASE_INSENSITIVE)).getString()).getRGB();
    }
    
  	@EventHandler
	public void init(FMLInitializationEvent e) {
		proxy.init(e);
	}
  	@EventHandler
	public void postInit(FMLPostInitializationEvent e) {
		proxy.postInit(e);
		MinecraftForge.EVENT_BUS.register(new FPSRenderEventHandler());
	}
	@Mod.EventHandler
	public void serverLoad(FMLServerStartingEvent e) {
		proxy.serverLoad(e);
	 }
	@SubscribeEvent
    public void onConfigChanged(ConfigChangedEvent.OnConfigChangedEvent event) {
        if (event.getModID().equals(MODID)) {
            syncConfig();
        }
    }

    public static boolean shouldRender = true;

    @SubscribeEvent
    public void onKeyPressed(InputEvent.KeyInputEvent event) {
        if (Main.keyToggle.isPressed()) {
            shouldRender = !shouldRender;
        }
    }
}
