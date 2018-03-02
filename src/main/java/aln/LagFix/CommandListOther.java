package aln.LagFix;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.util.BlockPos;
import net.minecraft.world.World;


public class CommandListOther implements ICommand {

  @Override
  public int compareTo(Object arg0) {
    return 0;
  }

  @Override
  public String getName() {
    return "listother";
  }

  @Override
  public String getCommandUsage(ICommandSender icommandsender) {
    return "/listother <range>    - Displays a list of entity names of other entities in range.";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {"listothers"});
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
      if ( params.length > 0 ) {
        try { range = Integer.parseInt(params[0]); } catch (NumberFormatException e) { Do.Say(player,"Parameters are <range> and <limit>. Range is the number of blocks out from your standing location in all directions that will be affected."); return; }
      }
      range = Math.abs(range);
      //if ( range != LagFix.nukeRangeDefault ) { Do.Say(player, "§7Range set to xz+-" + range +"§r"); }
      
      double  px = Math.round(icommandsender.getPosition().getX() - .5); // player's coordinates rounded down
      double  py = Math.round(icommandsender.getPosition().getY() - .5);
      double  pz = Math.round(icommandsender.getPosition().getZ() - .5);
      
      int otherEntityCount = 0;
      int otherTypeCount = 0;
      
      // create string list of other entity types to split to an array
      String otherTypeStringList = "%"; // this must start with the separator char
      for ( int k = 0; k < world.loadedEntityList.size(); k++ ) {
        Entity it = (Entity) world.loadedEntityList.get(k);
        String itTypeName = it.toString();
        if (!( it instanceof EntityPlayer )) {
          if ( (it.posX<=(px+range)) && (it.posX>=(px-range)) && (it.posZ<=(pz+range)) && (it.posZ>=(pz-range)) ) {   
            if (!(  
                ( it instanceof EntityItem )   ||
                ( it instanceof EntityMob )    ||
                ( it instanceof EntityAnimal ) || 
                ( it instanceof EntityArrow )
               ) )
            { 
              Pattern pattern = Pattern.compile("^([^\\[]*)\\[");
              Matcher matcher = pattern.matcher(it.toString());
              if ( matcher.find() ) { itTypeName = matcher.group(1); }
              
              if (!( otherTypeStringList.contains("%"+itTypeName+"%") )) {
                otherTypeStringList += itTypeName + "%";
                otherTypeCount++;
              }
            }
          }
        } // end if not player
      } // end for k
      String otherTypeArray[] = otherTypeStringList.substring(1).split("%"); // create initial array; substring eliminates a leading blank entry from "%"
      Integer[] otherTypeCountArray = new Integer[otherTypeArray.length]; // create count array
      for (int i=0; i<otherTypeCountArray.length; i++) { otherTypeCountArray[i]=0; } // init array
      //Do.Say(player,otherTypeCount + " other entity type"+ (otherTypeCount == 1 ? "" : "s") +" found.");  // do not display this. In direwolf20 for 1.7.10 found many types showing up with coordinate names but ended up with zero counts for report. confusing information.
      
      for ( int k = 0; k < world.loadedEntityList.size(); k++ ) {
        Entity it = (Entity) world.loadedEntityList.get(k);
        String itTypeName = "";
        if (!( it instanceof EntityPlayer )) {
          if ( (it.posX<=(px+range)) && (it.posX>=(px-range)) && (it.posZ<=(pz+range)) && (it.posZ>=(pz-range)) ) {   
            if (!(  
                ( it instanceof EntityItem )   ||
                ( it instanceof EntityMob )    ||
                ( it instanceof EntityAnimal ) || 
                ( it instanceof EntityArrow )
               ) )
            { 
              Pattern pattern = Pattern.compile("^([^\\[]*)\\[");
              Matcher matcher = pattern.matcher(it.toString());
              if ( matcher.find() ) { itTypeName = matcher.group(1); }
              for(int i = 0; i < otherTypeCount; i++) {
                if ( otherTypeArray[i].equals(itTypeName)) {
                  otherTypeCountArray[i]++;
                  otherEntityCount++;
                  i = otherTypeCount; // found it, end the loop
                }
              }
            }
          }
        } // end if not player
      } // end for k
      
      String tempOutput = "";
      for(int i = 0;i<otherTypeCount;i++) {
        String shortName = otherTypeArray[i];
        
        if ( ( shortName.length() > 6 ) && ( shortName.substring(0,6).equalsIgnoreCase("Entity") ) ) { // remove leading word "tile"
          shortName = shortName.substring(6);
        }
        
        if ( otherTypeCountArray[i] > 0 ) {  // flooded with coordinate names and zero counts in direwolf20 for 1.7.10 pack.  do not display zero counts.
          tempOutput += " §e"+ shortName +"§r("+ otherTypeCountArray[i] +")"; 
        }
      }
      
      if (tempOutput.length() > 32000 ) { tempOutput = "[truncated]"+ tempOutput.substring(tempOutput.length() - 32000 ); } // prevent netty io error and client side crash.
      if ( otherTypeCount > 0 ) { Do.Say(player,":"+ tempOutput); }
      Do.Say(player, "§e"+ otherEntityCount + "§r other entit"+ ( otherEntityCount == 1 ? "y" : "ies" ) +" in range +-"+range);
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
