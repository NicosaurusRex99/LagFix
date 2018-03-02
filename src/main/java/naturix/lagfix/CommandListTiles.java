package aln.LagFix;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.init.Blocks;
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;


public class CommandListTiles implements ICommand {

  @Override
  public int compareTo(Object arg0) {
    return 0;
  }

  @Override
  public String getName() {
    return "listtiles";
  }

  @Override
  public String getCommandUsage(ICommandSender icommandsender) {
    return "/nuketileentities <range>   - Kills all entities xz+-"+LagFix.nukeRangeDefault+" blocks from where you are standing, up and down all the way.";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {"listtile","listtileentities","listtileentity"});
  }

  @Override
  public void execute(ICommandSender icommandsender, String[] params) {
      EntityPlayer player = null;
      Boolean isPlayer = false;
      if (icommandsender instanceof EntityPlayer) { player = (EntityPlayer) icommandsender; isPlayer=true; }
      Boolean isCommandBlock = false;
      if ( icommandsender.toString().startsWith("net.minecraft.tileentity.TileEntityCommandBlock") ) { isCommandBlock = true; } // mc 1.7.2 and 1.7.10
      //if ( (icommandsender instanceof TileEntityCommandBlock) ) { isCommandBlock = true; } // mc 1.6.4
      if ( isCommandBlock ) { return; } // output is not useful to command blocks at this time
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
      //if ( range != LagFix.nukeRangeDefault ) { Do.Say(player, "Range set to xz+-" + range); }
      
      double  px = Math.round(icommandsender.getPosition().getX() - .5); // player's coordinates rounded down
      double  py = Math.round(icommandsender.getPosition().getY() - .5); // using icommandsender for compatibility with command blocks
      double  pz = Math.round(icommandsender.getPosition().getZ() - .5);
      
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
        
        if ( tileTypeCountArray[i] > 0 ) {  // flooded with coordinate names and zero counts in direwolf20 for 1.7.10 pack.  do not display zero counts.
          tempOutput += " §e"+ shortName +"§r("+ tileTypeCountArray[i] +")"; 
        }
      }
      if (tempOutput.length() > 32000 ) { tempOutput = "[truncated]"+ tempOutput.substring(tempOutput.length() - 32000 ); } // prevent netty io error and client side crash.
      if (tileTypeCount > 0) { Do.Say(player,":"+ tempOutput); }
      Do.Say(player, "§e"+ tileEntityCount + "§r tile-entit"+ ( tileEntityCount == 1 ? "y" : "ies" ) +" in range +-"+range);
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
