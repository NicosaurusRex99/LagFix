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
import net.minecraft.entity.Entity;
import net.minecraft.entity.item.EntityItem;
import net.minecraft.entity.monster.EntityMob;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.entity.projectile.EntityArrow;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class CommandListOther implements ICommand {

	private int range;

	@Override
	public String getName() {
    return "listother";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {"listothers"});
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
    return "/listother <range>    - Displays a list of entity names of other entities in range.";
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
    double  py = Math.round(sender.getPosition().getY() - .5);
    double  pz = Math.round(sender.getPosition().getZ() - .5);
    
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
        tempOutput = shortName +"("+ otherTypeCountArray[i] +")"; 
      }
    }
    
    if (tempOutput.length() > 32000 ) { tempOutput = "[truncated]"+ tempOutput.substring(tempOutput.length() - 32000 ); } // prevent netty io error and client side crash.
    if ( otherTypeCount > 0 ) { Do.Say(player,":"+ tempOutput); }
    Do.Say(player, otherEntityCount + " other entit"+ ( otherEntityCount == 1 ? "y" : "ies" ) +" in range +-"+range);
}


@Override
public boolean checkPermission(MinecraftServer server, ICommandSender sender) {
	return true;
}

@Override
public List<String> getTabCompletions(MinecraftServer server, ICommandSender sender, String[] args,
		BlockPos targetPos) {
	return null;
}

}
