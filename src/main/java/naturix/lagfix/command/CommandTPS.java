package naturix.lagfix.command;

import java.text.DecimalFormat;
import java.util.Arrays;
import java.util.List;

import naturix.lagfix.LagFix;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.command.NumberInvalidException;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.DimensionType;
import net.minecraftforge.common.DimensionManager;
import net.minecraftforge.server.command.TextComponentHelper;

public class CommandTPS implements ICommand{
	    private static final DecimalFormat TIME_FORMATTER = new DecimalFormat("########0.000");

	    @Override
	    public String getName()
	    {
	        return "tps";
	    }

	    /**
	     * Gets the usage string for the command.
	     */
	    @Override
	    public String getUsage(ICommandSender sender)
	    {
	        return "/tps - Displays current TPS";
	    }

	    /**
	     * Return the required permission level for this command.
	     */
	    public int getRequiredPermissionLevel()
	    {
	        return 0;
	    }

	    /**
	     * Check if the given ICommandSender has permission to execute this command
	     */
	    @Override
	    public boolean checkPermission(MinecraftServer server, ICommandSender sender)
	    {
	        return sender.canUseCommand(0, "help");
	    }

	    /**
	     * Callback for when the command is executed
	     */
	    @Override
	    public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException
	    {
	        int dim = 0;
	        boolean summary = true;
	        if (args.length > 0)
	        {
	            dim = parseInt(args[0]);
	            summary = false;
	        }

	        if (summary)
	        {
	            for (Integer dimId : DimensionManager.getIDs())
	            {
	                double worldTickTime = mean(server.worldTickTimes.get(dimId)) * 1.0E-6D;
	                double worldTPS = Math.min(1000.0/worldTickTime, 20);
	                sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tps.summary", getDimensionPrefix(dimId), TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS)));
	            }
	            double meanTickTime = mean(server.tickTimeArray) * 1.0E-6D;
	            double meanTPS = Math.min(1000.0/meanTickTime, 20);
	            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tps.summary","Overall", TIME_FORMATTER.format(meanTickTime), TIME_FORMATTER.format(meanTPS)));
	        }
	        else
	        {
	            double worldTickTime = mean(server.worldTickTimes.get(dim)) * 1.0E-6D;
	            double worldTPS = Math.min(1000.0/worldTickTime, 20);
	            sender.sendMessage(TextComponentHelper.createComponentTranslation(sender, "commands.forge.tps.summary", getDimensionPrefix(dim), TIME_FORMATTER.format(worldTickTime), TIME_FORMATTER.format(worldTPS)));
	        }
	    }

	    public static int parseInt(String input) throws NumberInvalidException
    {
        return parseInt(input);
    }

		private static String getDimensionPrefix(int dimId)
	    {
	        DimensionType providerType = DimensionManager.getProviderType(dimId);
	        if (providerType == null)
	        {
	            return String.format("Dim %2d", dimId);
	        }
	        else
	        {
	            return String.format("Dim %2d (%s)", dimId, providerType.getName());
	        }
	    }

	    private static long mean(long[] values)
	    {
	        long sum = 0L;
	        for (long v : values)
	        {
	            sum += v;
	        }
	        return sum / values.length;
	    }

		@Override
		public int compareTo(ICommand arg0) {
			return 0;
		}

		@Override
		public List<String> getAliases() {
			return Arrays.asList(new String[] {});
		}

		@Override
		public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
				BlockPos targetPos) {
			return null;
		}

		@Override
		public boolean isUsernameIndex(String[] args, int index) {
			return false;
		}
	}