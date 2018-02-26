package naturix.lagfix;

import org.apache.logging.log4j.Level;

import naturix.lagfix.proxy.CommonProxy;
import net.minecraftforge.common.config.Configuration;

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
        nukeRangeDefault = cfg.getInt("nukeRangeDefault", CATEGORY_GENERAL, 32, 1, 256, "Amount of blocks /nukeup will remove");
        animalLimitMinimum = cfg.getInt("animalLimitMinimum", CATEGORY_GENERAL, 3, 1, 256, "Minimum amount of animals in loaded chunks");    
        animalLimitDefault = cfg.getInt("animalLimitDefault", CATEGORY_GENERAL, 40, 1, 256, "Default amount of animals in loaded chunks");    
        
    }

}