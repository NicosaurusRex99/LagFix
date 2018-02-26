package naturix.lagfix.proxy;

import java.io.File;

import naturix.lagfix.Config;
import naturix.lagfix.LagFix;
import naturix.lagfix.command.*;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod.EventBusSubscriber
public class CommonProxy {
	
	public static Configuration config;
	
    public void preInit(FMLPreInitializationEvent e) {
    	File directory = e.getModConfigurationDirectory();
        config = new Configuration(new File(directory.getPath(), "naturix/lagfix.cfg"));
        Config.readConfig();
    }

    public void init(FMLInitializationEvent e) {
    }

    public void postInit(FMLPostInitializationEvent e) {
    	if (config.hasChanged()) {
            config.save();
        }
        }
    
    @Mod.EventHandler
    public void serverLoad(FMLServerStartingEvent event) {
        event.registerServerCommand(new CommandNukeUp());
        event.registerServerCommand(new CommandEntityCount());
        event.registerServerCommand(new CommandLagFix());
        event.registerServerCommand(new CommandLimitAnimals());
        event.registerServerCommand(new CommandFillDown());
        event.registerServerCommand(new CommandListOther());
        event.registerServerCommand(new CommandListTiles());
        event.registerServerCommand(new CommandNukeArrows());
        event.registerServerCommand(new CommandNukeEntities());
        event.registerServerCommand(new CommandNukeItems());
        LagFix.logger.info(LagFix.NAME  + " commands have loaded!");
    }
    }
