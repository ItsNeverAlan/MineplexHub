package mineplex.hub.tutorial;

import mineplex.core.common.util.UtilAlg;
import org.bukkit.Location;


public class TutorialPhase
{
  public Location Location;
  public String Header;
  public String[] Text;
  
  public TutorialPhase(Location player, Location target, String header, String[] text)
  {
    this.Location = player;
    
    this.Location.setYaw(UtilAlg.GetYaw(UtilAlg.getTrajectory(player, target)));
    this.Location.setPitch(UtilAlg.GetPitch(UtilAlg.getTrajectory(player, target)));
    
    this.Header = header;
    this.Text = text;
  }
}
