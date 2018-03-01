package naturix.lagfix.command;

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import javax.annotation.Nonnull;
import javax.annotation.Nullable;

import com.google.common.collect.Lists;

import naturix.lagfix.Config;
import naturix.lagfix.Do;
import naturix.lagfix.LagFix;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandNukeUp implements ICommand {

  private ICommandSender sender;
private String[] params;
private final List<String> aliases;
	public CommandNukeUp(){
    aliases = Lists.newArrayList(LagFix.MODID, "nu");
}
	
@Override
  public String getName() {
    return "nukeup";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {});
  }

  public boolean canCommandSenderUse(ICommandSender sender) {
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
public String getUsage(ICommandSender sender) {
	return "/nukeup <range>     - wipes all blocks xz+-"+LagFix.nukeRangeDefault+" blocks from where you are standing to up all the way.";
}

@Override
public void execute(@Nonnull MinecraftServer server, @Nonnull ICommandSender sender, @Nonnull String[] params) throws CommandException {
    EntityPlayer player = null;
    Boolean isPlayer = false;
    if (sender instanceof EntityPlayer) { player = (EntityPlayer) sender; isPlayer=true; }
    Boolean isCommandBlock = false;
    if ( sender.toString().startsWith("net.minecraft.tileentity.TileEntityCommandBlock") ) { isCommandBlock = true; } // mc 1.12.2
    //if ( (sender instanceof TileEntityCommandBlock) ) { isCommandBlock = true; } // mc 1.6.4
    if ( (! isPlayer ) && (! isCommandBlock ) ) { return; }
    if ( isPlayer && (! Do.isOp(player)) ) { Do.Say(player,"Operator only command. You are not an op."); return; }
    World world = sender.getEntityWorld();
    if ( world.isRemote ) { return; }
    Do.Say(player, " ");
    
  int extra = this.parseInteger(params[0]);
  int range = Config.nukeRangeDefault; // arbitrary square distance to cover
  range = Math.abs(extra);
  if ( range != Config.nukeRangeDefault ) { Do.Say(player, "Range set to xz+-" + range); }
  
  double  px = Math.round(sender.getPosition().getX() - .5); // player's coordinates rounded down
  double  py = Math.round(sender.getPosition().getY() - .5); // using sender for compatibility with command blocks
  double  pz = Math.round(sender.getPosition().getZ() - .5);
  
  if ( isCommandBlock ) { py++; } // allow command blocks to not remove itself.
  
  Do.Say(player, "Working...");
  for(int yy=world.getActualHeight(); yy >= 0 ; yy--) { // top down, falling blocks...
    for(int xx= -(range); xx<=range; xx++) {
      for(int zz= -(range); zz<=range; zz++) {
        world.setBlockState(new BlockPos((int)(xx+px), (int)(yy+py), (int)(zz+pz)), Blocks.AIR.getDefaultState()); // mc1.8 ?? what about the update flag?
        //world.setBlock((int)(xx+px), (int)(yy+py), (int)(zz+pz), Blocks.air, 0, 2); // mc1.7.10 // X, Y, Z, new block ID, new metadata, flag 2.
      }
    }
  }
  Do.Say(player, "Removed blocks from here, y"+(int)py+", out +-"+range+",");
  Do.Say(player, "  all the way up to y"+world.getActualHeight()+".");
}

@Override
public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
    return sender.canUseCommand(2,"gamemode");
}

@Override
@Nonnull
public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args, @Nullable BlockPos targetPos) {
    return Collections.emptyList();
}

@Override
public int compareTo(ICommand arg0) {
	return 0;
}
public static int parseInteger(String string)
{
	int value;
	try
	{
		value = Integer.parseInt(string);
	}
	catch (NumberFormatException e)
	{
		e.printStackTrace();
		return LagFix.nukeRangeDefault;
		
	}

	return value;
}
}