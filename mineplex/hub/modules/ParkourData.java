package mineplex.hub.modules;

import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;


public class ParkourData
{
  public String Name;
  public String[] Desc;
  public int Gems;
  public Location Location;
  public double Distance;
  
  public ParkourData(String name, String[] desc, int gems, Location loc, double dist)
  {
    this.Name = name;
    this.Desc = desc;
    this.Gems = gems;
    this.Location = loc;
    this.Distance = dist;
  }
  

  public void Inform(Player player)
  {
    UtilPlayer.message(player, F.main("Parkour", "Welcome to the " + F.elem(this.Name) + " course."));
    
    for (String cur : this.Desc)
    {
      UtilPlayer.message(player, "  " + cur);
    }
    
    player.playSound(player.getLocation(), Sound.ORB_PICKUP, 1.0F, 2.0F);
  }
}
