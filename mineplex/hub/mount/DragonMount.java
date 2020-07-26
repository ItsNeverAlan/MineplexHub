package mineplex.hub.mount;

import java.util.HashMap;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class DragonMount extends Mount<DragonData>
{
  public DragonMount(MountManager manager, String name, String[] desc, Material displayMaterial, byte displayData, int cost)
  {
    super(manager, name, displayMaterial, displayData, desc, cost);
    
    this.KnownPackage = false;
    
    this.Manager.GetPlugin().getServer().getPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  

  public void Enable(Player player)
  {
    player.leaveVehicle();
    player.eject();
    

    this.Manager.DeregisterAll(player);
    

    UtilPlayer.message(player, F.main("Mount", "You spawned " + F.elem(GetName()) + "."));
    

    this._active.put(player, new DragonData(this, player));
  }
  

  public void Disable(Player player)
  {
    DragonData data = (DragonData)this._active.remove(player);
    if (data != null)
    {
      data.Dragon.remove();
      

      UtilPlayer.message(player, F.main("Mount", "You despawned " + F.elem(GetName()) + "."));
    }
  }
}
