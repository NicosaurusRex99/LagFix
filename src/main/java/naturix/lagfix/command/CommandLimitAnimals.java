package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import naturix.lagfix.Do;
import naturix.lagfix.LagFix;
import net.minecraft.command.CommandException;
import net.minecraft.command.ICommand;
import net.minecraft.command.ICommandSender;
import net.minecraft.entity.Entity;
import net.minecraft.entity.passive.EntityAnimal;
import net.minecraft.entity.player.EntityPlayer;
import net.minecraft.server.MinecraftServer;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;


public class CommandLimitAnimals implements ICommand {

  private String[] params;

@Override
  public String getName() {
    return "limitanimals";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {"limitanimal"});
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
	return "/limitanimals <range> <limit>    - Removes all EntityAnimal except for the limit number to keep.";
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
    
    int nLimit = LagFix.animalLimitDefault; // default animal limit for each type. 50 sheep and 50 cows and 50 chickens etc.
    if (params.length > 2) {
      try { nLimit = Integer.parseInt(params[1]); } catch (NumberFormatException e) { Do.Say(player,"Parameters are <range> and <limit>. The limit is the number of EntityAnimals to keep of each type."); return; }
      if (nLimit < LagFix.animalLimitMinimum) { 
        nLimit = LagFix.animalLimitMinimum;
        Do.Say(player,"Limit changed to the minimum."); }
    }
    Do.Say(player,"Animal limit is §e"+ nLimit +" per type§r.");
    
    int range = LagFix.nukeRangeDefault; // arbitrary square distance to cover
    if ( params.length > 0 ) {
      try { range = Integer.parseInt(params[0]); } catch (NumberFormatException e) { Do.Say(player,"Parameters are <range> and <limit>. Range is the number of blocks out from your standing location in all directions that will be affected."); return; }
    }
    range = Math.abs(range);
    if ( range != LagFix.nukeRangeDefault ) { Do.Say(player, "Range set to xz+-" + range); }
    
    double  px = Math.round(sender.getPosition().getX() - .5); // player's coordinates rounded down
    double  py = Math.round(sender.getPosition().getY() - .5);
    double  pz = Math.round(sender.getPosition().getZ() - .5);

    int killCount = 0;
    int animalCount = 0;
    
    // create string list of animal entity types to split to an array
    String animalTypeStringList = "%";
    for ( int k = 0; k < world.loadedEntityList.size(); k++ ) {
      Entity it = (Entity) world.loadedEntityList.get(k);
      String itTypeName = "";
      if (!( it instanceof EntityPlayer )) {
        if ( (it.posX<=(px+range)) && (it.posX>=(px-range)) && (it.posZ<=(pz+range)) && (it.posZ>=(pz-range)) ) {   
          if ( it instanceof EntityAnimal ) { 
            //if (!( ((EntityAnimal)it).hasCustomNameTag() )) { // mc1.7.10 // do NOT ever remove any animal with a custom name tag.
            if (!( ((EntityAnimal)it).hasCustomName() )) { // mc1.8 // do NOT ever remove any animal with a custom name tag.
              Pattern pattern = Pattern.compile("^([^\\[]*)\\[");
              Matcher matcher = pattern.matcher(it.toString());
              if ( matcher.find() ) { itTypeName = matcher.group(1); }
              if (!( animalTypeStringList.contains("%"+itTypeName+"%") )) {
                animalTypeStringList += itTypeName + "%";
              }
            }
          }
        }
      } // end if not player
    } // end for k
    String animalTypeArray[] = animalTypeStringList.substring(1).split("%"); // create initial array
    Integer[] animalTypeCountArray = new Integer[animalTypeArray.length]; // create count array
    for (int i=0; i<animalTypeCountArray.length; i++) { animalTypeCountArray[i]=0; } // init array
    Do.Say(player,animalTypeArray.length + " animal types found.");
    
    for ( int k = 0; k < world.loadedEntityList.size(); k++ ) {
      Entity it = (Entity) world.loadedEntityList.get(k);
      String itTypeName = "";
      if (!( it instanceof EntityPlayer )) {
        if ( (it.posX<=(px+range)) && (it.posX>=(px-range)) && (it.posZ<=(pz+range)) && (it.posZ>=(pz-range)) ) {   
          if ( it instanceof EntityAnimal ) { 
            if (!( ((EntityAnimal)it).hasCustomName() )) { // do NOT ever remove any animal with a custom name tag.
              Pattern pattern = Pattern.compile("^([^\\[]*)\\[");
              Matcher matcher = pattern.matcher(it.toString());
              if ( matcher.find() ) { itTypeName = matcher.group(1); }
              for(int i = 0; i < animalTypeArray.length; i++) {
                if ( animalTypeArray[i].equals(itTypeName)) {
                  animalTypeCountArray[i]++;
                  animalCount++;
                }
              }
            }
          }
        }
      } // end if not player
    } // end for k
    Do.Say(player,"Found "+ animalCount +" of these animals.");

    // for each type of animal found remove all but nLimit
    for ( int k = 0; k < world.loadedEntityList.size(); k++ ) {
      Entity it = (Entity) world.loadedEntityList.get(k);
      String itTypeName = "";
      if (!( it instanceof EntityPlayer )) {
        if ( (it.posX<=(px+range)) && (it.posX>=(px-range)) && (it.posZ<=(pz+range)) && (it.posZ>=(pz-range)) ) {   
          if ( it instanceof EntityAnimal ) { 
            if (!( ((EntityAnimal)it).hasCustomName() )) { // do NOT ever remove any animal with a custom name tag.
              Pattern pattern = Pattern.compile("^([^\\[]*)\\[");
              Matcher matcher = pattern.matcher(it.toString());
              if ( matcher.find() ) { itTypeName = matcher.group(1); }
              for(int i = 0; i < animalTypeArray.length; i++) {
                if ( animalTypeArray[i].equals(itTypeName)) { // a type in our list
                  if ( animalTypeCountArray[i] > nLimit ) { // apply limit
                    it.setDead();
                    animalTypeCountArray[i]--;
                    killCount++;
                  }
                }
              }
            }
          }
        }
      } // end if not player
    } // end for k
    
    Do.Say(player, "§e"+ killCount + "§r EntityAnimals removed in range +-"+range);
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
