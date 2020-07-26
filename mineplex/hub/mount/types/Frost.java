package mineplex.hub.mount.types;

import java.util.Collection;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;
import java.util.Set;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.hub.mount.HorseMount;
import mineplex.hub.mount.MountManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.block.Block;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.event.EventHandler;








public class Frost
  extends HorseMount
{
  public Frost(MountManager manager)
  {
    super(manager, "Glacial Steed", new String[] {C.cWhite + "Born in the North Pole,", C.cWhite + "it leaves a trail of frost", C.cWhite + "as it moves!" }, Material.SNOW_BALL, (byte)0, 30000, Horse.Color.WHITE, Horse.Style.WHITE, Horse.Variant.HORSE, 1.0D, null);
  }
  

  @EventHandler
  public void Trail(UpdateEvent event)
  {
    if (event.getType() == UpdateType.TICK) {
      for (Horse horse : GetActive().values())
        UtilParticle.PlayParticle(UtilParticle.ParticleType.SNOW_SHOVEL, horse.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.25F, 0.25F, 0.1F, 4);
    }
  }
  
  @EventHandler
  public void SnowAura(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK)
      return;
    Iterator<Map.Entry<Block, Double>> blockIterator;
    for (Iterator localIterator1 = GetActive().values().iterator(); localIterator1.hasNext(); 
        




        blockIterator.hasNext())
    {
      Horse horse = (Horse)localIterator1.next();
      

      double duration = 2000.0D;
      HashMap<Block, Double> blocks = UtilBlock.getInRadius(horse.getLocation(), 2.5D);
      
      blockIterator = blocks.entrySet().iterator(); continue;
      
      Block block = (Block)((Map.Entry)blockIterator.next()).getKey();
      HashMap<Block, Double> snowBlocks = UtilBlock.getInRadius(block.getLocation(), 2.0D);
      
      boolean addSnow = true;
      
      for (Block surroundingBlock : snowBlocks.keySet())
      {
        if ((surroundingBlock.getType() == Material.PORTAL) || (surroundingBlock.getType() == Material.CACTUS))
        {
          blockIterator.remove();
          addSnow = false;
          break;
        }
      }
      

      if (addSnow) {
        this.Manager.Manager.GetBlockRestore().Snow(block, (byte)1, (byte)1, (duration * (1.0D + ((Double)blocks.get(block)).doubleValue())), 250L, 0);
      }
    }
  }
}
