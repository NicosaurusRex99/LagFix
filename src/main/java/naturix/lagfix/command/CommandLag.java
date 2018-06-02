package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;

import naturix.lagfix.Main;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandLag implements ICommand {


  @Override
  public String getName() {
    return "lag";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] { });
  }

  public boolean canCommandSenderUse(ICommandSender icommandsender) {
    return true;
  }

  public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
    return null;
  }

  @Override
  public boolean isUsernameIndex(String[] astring, int i) {
    return false;
  }

@Override
public int compareTo(ICommand o) {
	return 0;
}

@Override
public String getUsage(ICommandSender sender) {
	return "/lag - Displays a quick reference help page for all commands in this mod.";
}

@Override
public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
    if(sender instanceof EntityPlayer) {
        EntityPlayer player = (EntityPlayer) sender;
        World world = player.world;
        if ( world.isRemote ) { return; }
        Main.ShowHelp(player); return;
      }
}

@Override
public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
	return true;
}

@Override
public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
		BlockPos targetPos) {
	return null;
}

}
