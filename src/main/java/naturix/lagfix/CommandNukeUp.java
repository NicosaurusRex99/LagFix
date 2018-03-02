package aln.LagFix;

import java.util.Arrays;
import java.util.Date;
import java.util.List;

import aln.LagFix.Do;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandNukeUp implements ICommand {

  @Override
  public int compareTo(Object arg0) {
    return 0;
  }

  @Override
  public String getName() {
    return "nukeup";
  }

  @Override
  public String getCommandUsage(ICommandSender icommandsender) {
    return "/nukeup <range>     - wipes all blocks xz+-"+LagFix.nukeRangeDefault+" blocks from where you are standing to up all the way.";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {});
  }

  @Override
  public void execute(ICommandSender icommandsender, String[] params) {
	    EntityPlayer player = null;
	    Boolean isPlayer = false;
	    if (icommandsender instanceof EntityPlayer) { player = (EntityPlayer) icommandsender; isPlayer=true; }
	    Boolean isCommandBlock = false;
	    if ( icommandsender.toString().startsWith("net.minecraft.tileentity.TileEntityCommandBlock") ) { isCommandBlock = true; } // mc 1.7.2 and 1.7.10
	    //if ( (icommandsender instanceof TileEntityCommandBlock) ) { isCommandBlock = true; } // mc 1.6.4
	    if ( (! isPlayer ) && (! isCommandBlock ) ) { return; }
	    if ( isPlayer && (! Do.IsOp(player)) ) { Do.Say(player,"Operator only command. You are not an op."); return; }
	    World world = icommandsender.getEntityWorld();
	    if ( world.isRemote ) { return; }
	    Do.Say(player, " ");
	    if ((params.length > 0) && (params[0].equalsIgnoreCase("help"))) { LagFix.ShowHelp(player); return; }
	    if  (params.length > 1) { LagFix.ShowHelp(player); return; }
	    Do.Say(player, " ");
	    
      int range = LagFix.nukeRangeDefault; // arbitrary square distance to cover
      if ( params.length == 1) { 
        try { range = Integer.parseInt(params[0]); } catch (NumberFormatException e) { LagFix.ShowHelp(player); return; }
      }
      range = Math.abs(range);
      if ( range != LagFix.nukeRangeDefault ) { Do.Say(player, "Range set to xz+-" + range); }
      
      double  px = Math.round(icommandsender.getPosition().getX() - .5); // player's coordinates rounded down
      double  py = Math.round(icommandsender.getPosition().getY() - .5); // using icommandsender for compatibility with command blocks
      double  pz = Math.round(icommandsender.getPosition().getZ() - .5);
      
      if ( isCommandBlock ) { py++; } // allow command blocks to not remove itself.
      
      Do.Say(player, "Working...");
      for(int yy=world.getActualHeight(); yy >= 0 ; yy--) { // top down, falling blocks...
        for(int xx= -(range); xx<=range; xx++) {
          for(int zz= -(range); zz<=range; zz++) {
            world.setBlockState(new BlockPos((int)(xx+px), (int)(yy+py), (int)(zz+pz)), Blocks.air.getDefaultState()); // mc1.8 ?? what about the update flag?
            //world.setBlock((int)(xx+px), (int)(yy+py), (int)(zz+pz), Blocks.air, 0, 2); // mc1.7.10 // X, Y, Z, new block ID, new metadata, flag 2.
          }
        }
      }
      Do.Say(player, "Removed blocks from here, y"+(int)py+", out +-"+range+",");
      Do.Say(player, "  all the way up to y"+world.getActualHeight()+".");
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