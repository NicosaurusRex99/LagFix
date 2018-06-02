package naturix.lagfix.proxy;

import naturix.lagfix.utils.UpdateChecker;
import net.minecraftforge.fml.common.event.FMLInitializationEvent;
import net.minecraftforge.fml.common.event.FMLPreInitializationEvent;

public class ClientProxy extends CommonProxy {

	@Override
	public void preInit(FMLPreInitializationEvent e) {
		super.preInit(e);
	}
	@Override
	public void init(FMLInitializationEvent e) {
		super.init(e);
		UpdateChecker.check();
	}

}