package naturix.lagfix;

import org.apache.logging.log4j.Level;

import naturix.lagfix.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;
import org.apache.logging.log4j.Level;

public class Config {

    private static final String CATEGORY_GENERAL = "general";

    public static int nukeRangeDefault = 32;
    public static int animalLimitMinimum = 4;
    public static int animalLimitDefault = 40;


    public static void readConfig() {
        Configuration cfg = CommonProxy.config;
        try {
            cfg.load();
            initGeneralConfig(cfg);
        } catch (Exception e1) {
            LagFix.logger.log(Level.ERROR, "Problem loading config file!", e1);
        } finally {
            if (cfg.hasChanged()) {
                cfg.save();
            }
        }
    }

    private static void initGeneralConfig(Configuration cfg) {
        cfg.addCustomCategoryComment(CATEGORY_GENERAL, "General configuration");
        nukeRangeDefault = cfg.getInt("nukeRangeDefault", CATEGORY_GENERAL, 32, 1, 1000000, "default range of /nuke commands.");
        animalLimitMinimum = cfg.getInt("nukeRangeDefault", CATEGORY_GENERAL, 4, 1, 1000000, "minimum amount of loaded animals.");
        animalLimitDefault = cfg.getInt("nukeRangeDefault", CATEGORY_GENERAL, 40, 1, 1000000, "default amount of loaded animals.");
        
    }
    

}