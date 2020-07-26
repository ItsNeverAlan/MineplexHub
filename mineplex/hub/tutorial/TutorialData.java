package mineplex.hub.tutorial;

import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilPlayer;
import org.bukkit.Location;
import org.bukkit.Sound;
import org.bukkit.entity.Player;




public class TutorialData
{
  public Player Player;
  public TutorialPhase Phase;
  public int PhaseStep;
  public int TextStep;
  public long Sleep;
  
  public TutorialData(Player player, TutorialPhase phase)
  {
    this.Player = player;
    this.Phase = phase;
    
    this.TextStep = 0;
    this.PhaseStep = 0;
    
    this.Sleep = (System.currentTimeMillis() + 3000L);
  }
  
  public boolean Update()
  {
    if (!this.Player.getLocation().equals(this.Phase.Location)) {
      this.Player.teleport(this.Phase.Location);
    }
    if (System.currentTimeMillis() < this.Sleep) {
      return false;
    }
    
    if (this.TextStep >= this.Phase.Text.length)
    {
      this.PhaseStep += 1;
      this.Sleep = (System.currentTimeMillis() + 2000L);
      
      return true;
    }
    

    String text = this.Phase.Text[this.TextStep];
    
    UtilPlayer.message(this.Player, " ");
    UtilPlayer.message(this.Player, " ");
    UtilPlayer.message(this.Player, " ");
    UtilPlayer.message(this.Player, C.cGreen + C.Strike + C.Bold + "========================================");
    UtilPlayer.message(this.Player, C.cGold + C.Bold + this.Phase.Header);
    UtilPlayer.message(this.Player, " ");
    
    for (int i = 0; i <= this.TextStep; i++) {
      UtilPlayer.message(this.Player, "  " + this.Phase.Text[i]);
    }
    for (int i = this.TextStep; i <= 5; i++) {
      UtilPlayer.message(this.Player, " ");
    }
    UtilPlayer.message(this.Player, C.cGreen + C.Strike + C.Bold + "========================================");
    
    if (text.length() > 0)
    {
      this.Player.playSound(this.Player.getLocation(), Sound.ORB_PICKUP, 2.0F, 1.5F);
      this.Sleep = (System.currentTimeMillis() + 1000L + 50 * text.length());
    }
    else
    {
      this.Sleep = (System.currentTimeMillis() + 600L);
    }
    
    this.TextStep += 1;
    
    return false;
  }
  
  public void SetNextPhase(TutorialPhase phase)
  {
    this.Phase = phase;
    this.TextStep = 0;
    this.Player.teleport(this.Phase.Location);
  }
}
