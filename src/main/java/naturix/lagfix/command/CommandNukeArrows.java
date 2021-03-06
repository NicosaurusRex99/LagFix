package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;

import naturix.lagfix.Config;
import naturix.lagfix.Do;
import naturix.lagfix.Main;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class CommandNukeArrows implements ICommand {

  private Integer range;

@Override
  public String getName() {
    return "nukearrows";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {"nukearrow"});
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
public int compareTo(ICommand o) {
	return 0;
}

@Override
public String getUsage(ICommandSender sender) {
    return "/nukearrows <range>   - Removes all arrows xz+-"+Main.nukeRangeDefault+" blocks from where you are standing, up and down all the way.";
  }

@Override
public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	EntityPlayer player = null;
    Boolean isPlayer = false;
    if (sender instanceof EntityPlayer) { player = (EntityPlayer) sender; isPlayer=true; }
    Boolean isCommandBlock = false;
    if ( sender.toString().startsWith("net.minecraft.tileentity.TileEntityCommandBlock") ) { isCommandBlock = true; } // mc 1.7.2 and 1.7.10
    //if ( (sender instanceof TileEntityCommandBlock) ) { isCommandBlock = true; } // mc 1.6.4
    if ( (! isPlayer ) && (! isCommandBlock ) ) { return; }
    if ( isPlayer && (! Do.isOp(player)) ) { Do.Say(player,"Operator only command. You are not an op."); return; }
    World world = sender.getEntityWorld();
    if ( world.isRemote ) { return; }
    Do.Say(player, " ");
    if (args.length == 0) {
    	  range = Main.nukeRangeDefault;
    	  if ( range != Config.nukeRangeDefault ) { Do.Say(player, "Range set to xz+-" + range); 
    	  }
    }
    if (args.length >= 1) {
  	  range = Integer.parseInt(args[0]);;
  	  if ( range != Config.nukeRangeDefault ) { Do.Say(player, "Range set to xz+-" + range); 
  	  }	  
  }
    range = Math.abs(range);

    double  px = Math.round(sender.getPosition().getX() - .5); // player's coordinates rounded down
    double  py = Math.round(sender.getPosition().getY() - .5); // using sender for compatibility with command blocks
    double  pz = Math.round(sender.getPosition().getZ() - .5);
    
    Do.Say(player, "Working...");
    int itcount = 0;
    for ( int k = 0; k < world.loadedEntityList.size(); k++ ) {
      Entity it = (Entity) world.loadedEntityList.get(k);
      if (!( it instanceof EntityPlayer )) {
        if ( (it.posX<=(px+range)) && (it.posX>=(px-range)) && (it.posZ<=(pz+range)) && (it.posZ>=(pz-range)) ) {   
          if ( it instanceof EntityArrow ) {
            itcount++;
            it.setDead(); // remove the entity
          }
        }
      } // end if not player
    } // end for k
    
    Do.Say(player, itcount + " arrows removed in range +-"+range);
}

@Override
public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
	return sender.canUseCommand(2, "gamemode");
}

@Override
public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
		net.minecraft.util.math.BlockPos targetPos) {
	return null;
}

}
