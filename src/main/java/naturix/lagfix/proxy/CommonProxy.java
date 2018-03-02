package naturix.lagfix.proxy;

import java.io.File;

import naturix.lagfix.Config;
import naturix.lagfix.LagFix;
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
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.common.Mod;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPostInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;
import net.minecraftforge.fml.common.event.FMLServerStartingEvent;

@Mod.EventBusSubscriber
public class CommonProxy {
	
	public static Configuration config;

	public void preInit(FMLPreInitializationEvent e) 
	{
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
        event.registerServerCommand(new CommandNukeMobs());
        event.registerServerCommand(new CommandNukeNonAnimal());
        event.registerServerCommand(new CommandNukeOther());
        event.registerServerCommand(new CommandNukeTileEntities());
        LagFix.logger.info(LagFix.NAME  + " commands have loaded!");
    }
    }
