package mineplex.hub.mount.types;

import java.util.HashMap;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.mount.HorseMount;
import mineplex.hub.mount.MountManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.event.EventHandler;










public class Undead
  extends HorseMount
{
  public Undead(MountManager manager)
  {
    super(manager, "Infernal Horror", new String[] {C.cWhite + "The most ghastly horse in", C.cWhite + "existance, from the pits of", C.cWhite + "the Nether." }, Material.BONE, (byte)0, 30000, Horse.Color.BLACK, Horse.Style.BLACK_DOTS, Horse.Variant.SKELETON_HORSE, 0.8D, null);
  }
  
  @EventHandler
  public void Trail(UpdateEvent event)
  {
    if (event.getType() == UpdateType.TICK) {
      for (Horse horse : GetActive().values())
        UtilParticle.PlayParticle(UtilParticle.ParticleType.FLAME, horse.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.25F, 0.25F, 0.0F, 2);
    }
    if (event.getType() == UpdateType.FAST) {
      for (Horse horse : GetActive().values()) {
        UtilParticle.PlayParticle(UtilParticle.ParticleType.LAVA, horse.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.25F, 0.25F, 0.0F, 1);
      }
    }
  }
}
