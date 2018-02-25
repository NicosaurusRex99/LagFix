package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;

import aln.LagFix.Do;
import net.minecraft.block.Block;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;

public class CommandFillDown implements ICommand {

  @Override
  public int compareTo(Object arg0) {
    return 0;
  }

  @Override
  public String getName() {
    return "filldown";
  }

  @Override
  public String getCommandUsage(ICommandSender icommandsender) {
    return "/filldown <range>    - fills empty blocks +-"+LagFix.nukeRangeDefault+" blocks from where you are standing down all the way.";
  }

  @Override
  public List getAliases() {
	    return Arrays.asList(new String[] { });
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
      for(int xx= -(range); xx<=range; xx++) {
        for(int yy=(int) -py; yy < 0; yy++) {
          for(int zz= -(range); zz<=range; zz++) {
            
            Block blockID = world.getBlockState(new BlockPos((int)(xx+px), (int)(yy+py), (int)(zz+pz))).getBlock();
            if ( ( blockID == Blocks.air ) || ( blockID == Blocks.flowing_lava) || (blockID == Blocks.flowing_water) || ( yy+py < 1 ) ) {
            
            Block replacementBlockID = Blocks.cobblestone; // replacement block id = cobblestone (default); arbitrary cobblestone because this is an abnormal fill.
            if ( (yy >= -6) && (yy < -3) ) { replacementBlockID = Blocks.stone; }
            if ( (yy >= -3) && (yy < -1) ) { replacementBlockID = Blocks.dirt; }
            if ( yy == -1 ) { replacementBlockID = Blocks.grass; }
            if ( yy+py < 1 ) { replacementBlockID = Blocks.bedrock; }
            
            BlockPos pos = new BlockPos((int)(xx+px), (int)(yy+py), (int)(zz+pz));
            TileEntity theTileEntity = world.getTileEntity(pos);
            if ( theTileEntity != null ) { theTileEntity.invalidate(); world.getChunkFromChunkCoords((int)(xx+px),(int)(zz+pz)).removeInvalidTileEntity(pos); } // mc 1.8 // prevents dropping contents on ground when block is destroyed.
            //if ( theTileEntity != null ) { theTileEntity.invalidate(); world.getChunkFromBlockCoords((int)(xx+px),(int)(zz+pz)).getTileEntityUnsafe((int)(xx+px), (int)(yy+py), (int)(zz+pz)); } // mc1.7.10 // prevents dropping contents on ground when block is destroyed.
            
            world.setBlockState(pos, Blocks.air.getDefaultState()); // it used to be necessary to hit it with air then the next one. not sure if this is needed any more.
            world.setBlockState(pos, replacementBlockID.getDefaultState());
            // DEBUG what about the update parameters used in 1.7.10?
            //world.setBlock((int)(xx+px), (int)(yy+py), (int)(zz+pz), Blocks.air, 0, 3); // mc1.7.10 // MUST set to air before setting to other block. Some blocks trigger a ClassCastException error when replaced with other blocks (a Minecraft "feature").
            //world.setBlock((int)(xx+px), (int)(yy+py), (int)(zz+pz), replacementBlockID, 0, 3); // mc1.7.10 // X, Y, Z, new block ID, new metadata, flag 2
          }
        }
      }
    }
    Do.Say(player, "Filled empty blocks from here, y"+(int)py+", out +-"+range+", and all the way down.");
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
