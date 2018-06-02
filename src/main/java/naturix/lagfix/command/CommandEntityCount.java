package naturix.lagfix.command;

import java.util.Arrays;
import java.util.List;

import naturix.lagfix.Do;
import naturix.lagfix.Main;
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
import net.minecraft.tileentity.TileEntity;
import net.minecraft.util.math.BlockPos;
import net.minecraft.world.World;

public class CommandEntityCount implements ICommand {
	private String[] params;
  @Override
  public String getName() {
    return "entitycount";
  }

  @Override
  public List getAliases() {
    return Arrays.asList(new String[] {"ecount","ecounts","entitiescount", "ec"});
  }
 
  public boolean canCommandSenderUse(ICommandSender sender) {
    return true;
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
	return "/entitycount <range>    - Counts Entity and TileEntity in range.";
}

@Override
public void execute(MinecraftServer server, ICommandSender sender, String[] args) throws CommandException {
	if(sender instanceof EntityPlayer) {
	      EntityPlayer player = (EntityPlayer) sender;
	      World world = player.world;
	      if ( world.isRemote ) { return; }
	      
	      int range = Main.nukeRangeDefault; // arbitrary square distance to cover
	      if ( world.isRemote) { 
	        try { range = Integer.parseInt(params[0]); } catch (NumberFormatException e) { Main.ShowHelp(player); return; }
	      }
	      range = Math.abs(range);
	      //if ( range != LagFix.nukeRangeDefault ) { Do.Say(player, "§7Range set to xz+-" + range +"§r"); }
	      
	      double  px = Math.round(player.posX - .5); // player's coordinates rounded down
	      double  py = Math.round(player.posY - .5);
	      double  pz = Math.round(player.posZ - .5);
	      
	      int otherCount = 0;
	      int entityCount = 0;
	      int itemCount = 0;
	      int weatherEffectCount = 0;
	      int livingCount = 0;
	      int ambientCreatureCount = 0;
	      int flyingCount = 0;
	      int slimeCount = 0;
	      int creatureCount = 0;
	      int mobCount = 0;
	      int golemCount = 0;
	      int animalCount = 0;
	      int arrowCount = 0;
	      
	      for ( int k = 0; k < world.loadedEntityList.size(); k++ ) {
	        Entity it = (Entity) world.loadedEntityList.get(k);
	        if (!( it instanceof EntityPlayer )) {
	          if ( (it.posX<=(px+range)) && (it.posX>=(px-range)) && (it.posZ<=(pz+range)) && (it.posZ>=(pz-range)) ) {   
	            
	            if (!(  
	                    ( it instanceof EntityItem ) ||
//	                  ( it instanceof EntityWeatherEffect ) ||
//	                  ( it instanceof EntityLiving ) ||
//	                  ( it instanceof EntityAmbientCreature ) ||
//	                  ( it instanceof EntityFlying ) ||
//	                  ( it instanceof EntitySlime ) ||
//	                  ( it instanceof EntityCreature ) ||
	                    ( it instanceof EntityMob ) ||
//	                  ( it instanceof EntityGolem ) || 
	                    ( it instanceof EntityAnimal ) || 
	                    ( it instanceof EntityArrow )
	               ) )
	            { otherCount++; }
	            
	            entityCount++;
	            
	            if      ( it instanceof EntityItem ) { itemCount++; }                        // Entity
	            //if      ( it instanceof EntityWeatherEffect) { weatherEffectCount++; }     // Entity
	            //if      ( it instanceof EntityLiving) { livingCount++; }                   // Entity/LivingBase
	            //if      ( it instanceof EntityAmbientCreature) { ambientCreatureCount++; } // Entity/LivingBase/Living
	            //if      ( it instanceof EntityFlying) { flyingCount++; }                   // Entity/LivingBase/Living
	            //if      ( it instanceof EntitySlime) { slimeCount++; }                     // Entity/LivingBase/Living
	            //if      ( it instanceof EntityCreature) { creatureCount++; }               // Entity/LivingBase/Living
	            if      ( it instanceof EntityMob) { mobCount++; }                           // Entity/LivingBase/Living/Creature
	            //if      ( it instanceof EntityGolem) { golemCount++; }                     // Entity/LivingBase/Living/Creature
	            if      ( it instanceof EntityAnimal) { animalCount++; }                     // Entity/LivingBase/Living/Creature/Ageable
	            if      ( it instanceof EntityArrow) { arrowCount++; }                       // Entity/Projectile
	            
	          }
	        } // end if not player
	      } // end for k
	      
	      int tileEntityCount = 0;
	      for ( int k = 0; k < world.loadedTileEntityList.size(); k++ ) {
	        TileEntity it = (TileEntity) world.loadedTileEntityList.get(k);
	        if ( (it.getPos().getX()<=(px+range)) && (it.getPos().getX()>=(px-range)) && (it.getPos().getZ()<=(pz+range)) && (it.getPos().getZ()>=(pz-range)) ) {   
	          tileEntityCount++;
	        }
	      } // end for TileEntityList
	      
	      Do.Say(player, "  "+ itemCount + " Item"+ (itemCount == 1 ? "" : "s") +" §7(subset of §7Entity)§r");
//	    Do.Say(player, "  "+ weatherEffectCount + " WeatherEffect"+ (weatherEffectCount == 1 ? "" : "s") +" (subset of Entity)");
//	    Do.Say(player, "  "+ livingCount + " Living (subset of Entity/LivingBase)");
//	    Do.Say(player, "  "+ ambientCreatureCount + " AmbientCreature"+ (ambientCreatureCount == 1 ? "" : "s") +" (subset of Entity/LivingBase/Living)");
//	    Do.Say(player, "  "+ flyingCount + " Flying (subset of Entity/LivingBase/Living)");
//	    Do.Say(player, "  "+ slimeCount + " Slime"+ (slimeCount == 1 ? "" : "s") +" (subset of Entity/LivingBase/Living)");
//	    Do.Say(player, "  "+ creatureCount + " Creature"+ (creatureCount == 1 ? "" : "s") +" (subset of Entity/LivingBase/Living)");
	      Do.Say(player, "  "+ mobCount + " Mob"+ (mobCount == 1 ? "" : "s") +" §7(subset of §7Entity/LivingBase/Living/Creature)§r");
//	    Do.Say(player, "  "+ golemCount + " Golem"+ (golemCount == 1 ? "" : "s") +" (subset of Entity/LivingBase/Living/Creature)");
	      Do.Say(player, "  "+ animalCount + " Animal"+ (animalCount == 1 ? "" : "s") +" §7(subset of §7Entity/LivingBase/Living/Creature/Ageable)§r");
	      Do.Say(player, "  "+ arrowCount + " Arrow"+ (arrowCount == 1 ? "" : "s") +" §7(subset of §7Entity/Projectile)§r");
	      Do.Say(player, "  "+ otherCount + " other entities found. §7(try /listother)§r");
	      Do.Say(player, "Total "+ entityCount + " Entity and "+ tileEntityCount +" TileEntity in range +-"+ range);
	      
	      
	    } // end if player
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
