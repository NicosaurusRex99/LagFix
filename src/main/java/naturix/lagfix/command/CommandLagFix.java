package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandLagFix implements ICommand {

  @Override
  public int compareTo(Object arg0) {
    return 0;
  }

  @Override
  public String getName() {
    return "lagfix";
  }

  @Override
  public String getCommandUsage(ICommandSender icommandsender) {
    return "/lagfix - Displays a quick reference help page for all commands in this mod.";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] { });
  }

  @Override
  public void execute(ICommandSender icommandsender, String[] params) {
    if(icommandsender instanceof EntityPlayer) {
      EntityPlayer player = (EntityPlayer) icommandsender;
      World world = player.worldObj;
      if ( world.isRemote ) { return; }
      LagFix.ShowHelp(player); return;
    }
  }

  @Override
  public boolean canCommandSenderUse(ICommandSender icommandsender) {
    return true;
  }

  @Override
  public List addTabCompletionOptions(ICommandSender sender, String[] args, BlockPos pos) {
    return null;
  }

  @Override
  public boolean isUsernameIndex(String[] astring, int i) {
    return false;
  }

}
