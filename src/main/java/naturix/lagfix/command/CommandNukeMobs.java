package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;

import aln.LagFix.Do;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandNukeMobs implements ICommand {

  @Override
  public int compareTo(Object arg0) {
    return 0;
  }

  @Override
  public String getName() {
    return "nukemobs";
  }

  @Override
  public String getCommandUsage(ICommandSender icommandsender) {
    return "/nukemobs <range>     - Removes all EntityMob (subset of Entity)";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {"nukemob"});
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
      
      Do.Say(player, "Working...");
      int itcount = 0;
      for ( int k = 0; k < world.loadedEntityList.size(); k++ ) {
        Entity it = (Entity) world.loadedEntityList.get(k);
        if (!( it instanceof EntityPlayer )) {
          if ( (it.posX<=(px+range)) && (it.posX>=(px-range)) && (it.posZ<=(pz+range)) && (it.posZ>=(pz-range)) ) {   
            if ( it instanceof EntityMob ) { 
              itcount++;
              it.setDead(); // remove the entity
            }
          }
        } // end if not player
      } // end for k
      
      Do.Say(player, itcount + " Entity§eMob§rs removed in range +-"+range);
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
