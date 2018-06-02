package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;

import naturix.lagfix.Config;
import naturix.lagfix.Do;
import naturix.lagfix.Main;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandNukeTileEntities implements ICommand {


  private int range;

@Override
  public String getName() {
    return "nuketileentities";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {"nuketileentity","nuketiles","nuketile"});
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
    return "/nuketileentities <range>   - Kills all entities xz+-"+Main.nukeRangeDefault+" blocks from where you are standing, up and down all the way.";
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
  int tileEntityCount = 0;
  boolean foundOne = true;
  while ( foundOne ) { // must use this method as it messes with the actual structure of the TileEntity list as it loops.
    foundOne = false;
  	for ( int k = 0; k < world.loadedTileEntityList.size(); k++ ) {
	      TileEntity it = (TileEntity) world.loadedTileEntityList.get(k);
	      if ( (it.getPos().getX()<=(px+range)) && (it.getPos().getX()>=(px-range)) && (it.getPos().getZ()<=(pz+range)) && (it.getPos().getZ()>=(pz-range)) ) {
	        
	        TileEntity theTileEntity = world.getTileEntity(it.getPos());
	    	if ( theTileEntity != null ) {
	    		if (!( ( isCommandBlock ) && ( theTileEntity.toString().startsWith("net.minecraft.tileentity.TileEntityCommandBlock") ) )) { 
			        theTileEntity.invalidate();// invalidates the tile entity. next line removes it.
			        world.getChunkFromBlockCoords(it.getPos()).removeInvalidTileEntity(it.getPos());//removes "invalid" tile entities. prevents dropping contents when block is removed.
			        world.setBlockState(it.getPos(), Blocks.AIR.getDefaultState()); // mc1.8; ?? what about the update flag?
	            //world.setBlock(it.getPos(), Blocks.air, 0, 2); // mc1.7.10 //  X, Y, Z, new block ID, new metadata, flag 2.
			        tileEntityCount++;
			        foundOne = true;
			        k = world.loadedTileEntityList.size();
	    		}
	        }
	      }
	    } // end for k
  }
  Do.Say(player, tileEntityCount + " Tile Entities removed in range +-"+range);
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
