package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import naturix.lagfix.Config;
import naturix.lagfix.Do;
import naturix.lagfix.LagFix;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class CommandListTiles implements ICommand {


  private int range;

@Override
  public String getName() {
    return "listtiles";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {"listtile","listtileentities","listtileentity"});
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
	return "/nuketileentities <range>   - Kills all entities xz+-"+LagFix.nukeRangeDefault+" blocks from where you are standing, up and down all the way.";
}

@Override
public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	EntityPlayer player = null;
    Boolean isPlayer = false;
    if (sender instanceof EntityPlayer) { player = (EntityPlayer) sender; isPlayer=true; }
    Boolean isCommandBlock = false;
    if ( sender.toString().startsWith("net.minecraft.tileentity.TileEntityCommandBlock") ) { isCommandBlock = true; } // mc 1.7.2 and 1.7.10
    //if ( (sender instanceof TileEntityCommandBlock) ) { isCommandBlock = true; } // mc 1.6.4
    if ( isCommandBlock ) { return; } // output is not useful to command blocks at this time
    if ( (! isPlayer ) && (! isCommandBlock ) ) { return; }
    if ( isPlayer && (! Do.isOp(player)) ) { Do.Say(player,"Operator only command. You are not an op."); return; }
    World world = sender.getEntityWorld();
    if ( world.isRemote ) { return; }
    if (args.length == 0) {
    	  range = LagFix.nukeRangeDefault;
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
    
    // count types of tileEntity
    String tileTypeStringList = "%"; // this must start with the separator char
    int tileTypeCount = 0;
    int tileEntityCount = 0;
    for ( int k = 0; k < world.loadedTileEntityList.size(); k++ ) {
      TileEntity it = (TileEntity) world.loadedTileEntityList.get(k);
      if ( (it.getPos().getX()<=(px+range)) && (it.getPos().getX()>=(px-range)) && (it.getPos().getZ()<=(pz+range)) && (it.getPos().getZ()>=(pz-range)) ) {
        TileEntity theTileEntity = world.getTileEntity(it.getPos());
        if ( theTileEntity != null ) {
          String itTypeName = it.toString();
          
          Pattern pattern = Pattern.compile("([^\\@]*)\\@");
          Matcher matcher = pattern.matcher(it.toString());
          if ( matcher.find() ) { itTypeName = matcher.group(1); } // remove hex code info past the last @sign
          
          pattern = Pattern.compile("(.+)\\([xX]=[^\\)]*\\)$");
          matcher = pattern.matcher(itTypeName);
          if ( matcher.find() ) { itTypeName = matcher.group(1); } // remove coordinates from end of name; used by a mod in dw20 for mc1710
          
          if (!( tileTypeStringList.contains("%"+itTypeName+"%") )) { // add this type name to the list if it is not already there
            tileTypeStringList += itTypeName + "%";
            tileTypeCount++;
          }
        }
      }
    } // end for k
    String tileTypeArray[] = tileTypeStringList.substring(1).split("%"); // create initial array; substring eliminates a leading blank entry from "%"
    Integer[] tileTypeCountArray = new Integer[tileTypeArray.length]; // create count array
    for (int i=0; i<tileTypeCountArray.length; i++) { tileTypeCountArray[i]=0; } // init array
    //Do.Say(player,( tileTypeCount +" tile-entity type"+ (tileTypeCount == 1 ? "" : "s") +" found")); // do not display this. In direwolf20 for 1.7.10 found many types showing up with coordinate names but ended up with zero counts for report. confusing information.

    // count tileEntity
    for ( int k = 0; k < world.loadedTileEntityList.size(); k++ ) {
      TileEntity it = (TileEntity) world.loadedTileEntityList.get(k);
      if ( (it.getPos().getX()<=(px+range)) && (it.getPos().getX()>=(px-range)) && (it.getPos().getZ()<=(pz+range)) && (it.getPos().getZ()>=(pz-range)) ) {
        TileEntity theTileEntity = world.getTileEntity(it.getPos());
        if ( theTileEntity != null ) {
          // get long name for tileEntity
          Pattern pattern = Pattern.compile("([^\\@]*)\\@");
          Matcher matcher = pattern.matcher(it.toString());
          String itTypeName = "";
          if ( matcher.find() ) { itTypeName = matcher.group(1); }
          // find its type in the list and count it
          for(int i = 0; i < tileTypeCount; i++) {
            if ( tileTypeArray[i].contentEquals(itTypeName)) {
              tileTypeCountArray[i]++;
              tileEntityCount++;
              i = tileTypeCount; // found it, end the loop
            }
          }
        }
      }
    } // end for k
    
    // final output
    String tempOutput = "";
    for(int i = 0;i<tileTypeCount;i++) {
      // get the short name for the tileEntity
      String shortName = tileTypeArray[i];
      
      Pattern pattern = Pattern.compile("([^\\.]*)$");
      Matcher matcher = pattern.matcher(tileTypeArray[i]);
      if ( matcher.find() ) { shortName = matcher.group(1); } // extracts after the last dot in the name if any
      
      if ( ( shortName.length() > 10 ) && ( shortName.substring(0,10).equalsIgnoreCase("TileEntity") ) ) { // remove leading word
        shortName = shortName.substring(10);
      }

      if ( ( shortName.length() > 4 ) && ( shortName.substring(0,4).equalsIgnoreCase("Tile") ) ) { // remove leading word
        shortName = shortName.substring(4);
      }

      if ( ( shortName.length() > 6 ) && ( shortName.substring(0,6).equalsIgnoreCase("Entity") ) ) { // remove leading word
        shortName = shortName.substring(6);
      }

      pattern = Pattern.compile("(.+)\\([xX]=[^\\)]*\\)$");
      matcher = pattern.matcher(tileTypeArray[i]);
      if ( matcher.find() ) { shortName = matcher.group(1); } // remove coordinates from name, used by a mod in dw20for1710
      if ( tileTypeCountArray[i] == 1 ) {  // flooded with coordinate names and zero counts in direwolf20 for 1.7.10 pack.  do not display zero counts.
          tempOutput += shortName +"("+ tileTypeCountArray[i] +"). "; 
        }
      if ( tileTypeCountArray[i] > 2 ) {  // flooded with coordinate names and zero counts in direwolf20 for 1.7.10 pack.  do not display zero counts.
        tempOutput += shortName +"("+ tileTypeCountArray[i] +"), "; 
      }
    }
    if (tempOutput.length() > 32000 ) { tempOutput = "[truncated]"+ tempOutput.substring(tempOutput.length() - 32000 ); } // prevent netty io error and client side crash.
    if (tileTypeCount > 0) { Do.Say(player,":"+ tempOutput); }
    Do.Say(player, tileEntityCount + " tile-entit"+ ( tileEntityCount == 1 ? "y" : "ies" ) +" in range +-"+range);
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
