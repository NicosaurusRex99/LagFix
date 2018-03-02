package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;

import naturix.lagfix.Config;
import naturix.lagfix.Do;
import naturix.lagfix.LagFix;
import net.minecraft.block.Block;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.player.EntityPlayerMP;
import net.minecraft.init.Blocks;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandFillDown implements ICommand {

  private int extra;

@Override
  public String getName() {
    return "filldown";
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
		return "/filldown <range>    - fills empty blocks +-"+LagFix.nukeRangeDefault+" blocks from where you are standing down all the way.";
	}

	@Override
	public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
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
	    if (args.length > 0) {
	        try {
	            extra = Integer.parseInt(args[0]);
	        } catch (NumberFormatException e) {
	            System.err.println("Argument" + args[0] + " must be an integer.");
	            System.exit(1);
	        }
	    }
	  int range = Config.nukeRangeDefault; // arbitrary square distance to cover
	  range = Math.abs(range);
	  if ( range != Config.nukeRangeDefault ) { Do.Say(player, "Range set to xz+-" + range); }
	  
      double  px = Math.round(player.getPosition().getX() - .5); // player's coordinates rounded down
      double  py = Math.round(player.getPosition().getY() - .5); // using icommandsender for compatibility with command blocks
      double  pz = Math.round(player.getPosition().getZ() - .5);
      
      Do.Say(player, "Working...");
      for(int xx= -(range); xx<=range; xx++) {
        for(double yy=(1) -py; yy < 0; yy++) {
          for(int zz= -(range); zz<=range; zz++) {
        	  
            Block blockID = world.getBlockState(new BlockPos((int)(xx+px), (int)(yy+py), (int)(zz+pz))).getBlock();
            if ( ( blockID == Blocks.AIR ) || ( blockID == Blocks.FLOWING_LAVA) || (blockID == Blocks.FLOWING_WATER) || ( yy+py < 1 ) ) {
            Block replacementBlockID = Blocks.COBBLESTONE; // replacement block id = cobblestone (default); arbitrary cobblestone because this is an abnormal fill.
            if ( (yy >= -6) && (yy < -3) ) { replacementBlockID = Blocks.STONE; }
            if ( (yy >= -3) && (yy < -1) ) { replacementBlockID = Blocks.DIRT; }
            if ( yy == -1 ) { replacementBlockID = Blocks.GRASS; }
            if ( yy+py < 2 ) { replacementBlockID = Blocks.BEDROCK; }
            BlockPos pos = new BlockPos((int)(xx+px), (int)(yy+py), (int)(zz+pz));
            TileEntity theTileEntity = world.getTileEntity(pos);
            if ( theTileEntity != null ) { theTileEntity.invalidate(); world.getChunkFromChunkCoords((int)(xx+px),(int)(zz+pz)).removeInvalidTileEntity(pos); } // mc 1.8 // prevents dropping contents on ground when block is destroyed.
            world.setBlockState(pos, Blocks.AIR.getDefaultState()); // it used to be necessary to hit it with AIR then the next one. not sure if this is needed any more.
            world.setBlockState(pos, replacementBlockID.getDefaultState());
            // DEBUG what about the update parameters used in 1.7.10?
            //world.setBlock((int)(xx+px), (int)(yy+py), (int)(zz+pz), Blocks.AIR, 0, 3); // mc1.7.10 // MUST set to AIR before setting to other block. Some blocks trigger a ClassCastException error when replaced with other blocks (a Minecraft "feature").
            //world.setBlock((int)(xx+px), (int)(yy+py), (int)(zz+pz), replacementBlockID, 0, 3); // mc1.7.10 // X, Y, Z, new block ID, new metadata, flag 2
          }
        }
      }
    }
    naturix.lagfix.Do.Say(player, "Filled empty blocks from here, y"+(int)py+", out +-"+range+", and all the way down.");
  }


	@Override
	public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
		return sender.canUseCommand(2,"gamemode");
	}

	@Override
	public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
			BlockPos targetPos) {
		return null;
	}

	}
