package naturix.lagfix.FPS;

import naturix.lagfix.Main;
import net.minecraft.client.gui.GuiScreen;
import net.minecraftforge.common.config.ConfigElement;
import net.minecraftforge.common.config.Configuration;
import net.minecraftforge.fml.client.config.GuiConfig;

public class GuiTFConfig extends GuiConfig {

    public GuiTFConfig(GuiScreen parent) {
        super(parent, new ConfigElement(Main.config.getCategory(Configuration.CATEGORY_GENERAL)).getChildElements(),
                Main.MODID, Main.MODID, false, false, GuiConfig.getAbridgedConfigPath(Main.config.toString()));
    }

}