package mineplex.hub.mount.types;

import mineplex.hub.mount.HorseMount;
import mineplex.hub.mount.MountManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;








public class Mule
  extends HorseMount
{
  public Mule(MountManager manager)
  {
    super(manager, "Mount Mule", new String[] {ChatColor.RESET + "Muley muley!" }, Material.HAY_BLOCK, (byte)0, 15000, Horse.Color.BLACK, Horse.Style.BLACK_DOTS, Horse.Variant.MULE, 1.0D, null);
  }
}
