package mineplex.hub.modules;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.UtilDisplay;
import mineplex.core.common.util.UtilServer;
import mineplex.core.common.util.UtilTime;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.hub.mount.Mount;
import mineplex.hub.mount.MountManager;
import mineplex.hub.mount.types.Dragon;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;

public class NewsManager
  extends MiniPlugin
{
  public HubManager Manager;
  private String[] _news;
  private int _newsIndex = 0;
  private long _newsTime = System.currentTimeMillis();
  
  private int _mineplexIndex = 0;
  
  public NewsManager(HubManager manager)
  {
    super("News Manager", manager.GetPlugin());
    
    this.Manager = manager;
    
    this._news = 
      new String[] {
      "Champions Update: " + C.cYellow + C.Bold + "Skill Levels", 
      "Champions Update: " + C.cGreen + C.Bold + "More Customisation", 
      "Champions Update: " + C.cYellow + C.Bold + "Item Selection", 
      "Champions Update: " + C.cGreen + C.Bold + "More Balanced" };
  }
  

  @EventHandler
  public void FlightUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTEST) {
      return;
    }
    
    ChatColor col = ChatColor.RED;
    if (this._mineplexIndex == 1) { col = ChatColor.GOLD;
    } else if (this._mineplexIndex == 2) { col = ChatColor.YELLOW;
    } else if (this._mineplexIndex == 3) { col = ChatColor.GREEN;
    } else if (this._mineplexIndex == 4) { col = ChatColor.AQUA;
    } else if (this._mineplexIndex == 5) col = ChatColor.LIGHT_PURPLE;
    this._mineplexIndex = ((this._mineplexIndex + 1) % 6);
    

    if (UtilTime.elapsed(this._newsTime, 4500L))
    {
      this._newsIndex = ((this._newsIndex + 1) % this._news.length);
      this._newsTime = System.currentTimeMillis();
    }
    

    String text = col + C.Bold + "MINEPLEX" + ChatColor.RESET + " - " + this._news[this._newsIndex];
    if (text.length() > 64) {
      text = text.substring(0, 64);
    }
    for (Player player : UtilServer.getPlayers()) {
      UtilDisplay.displayTextBar(this.Manager.GetPlugin(), player, this._newsIndex / (this._news.length - 1), text);
    }
    for (Mount mount : this.Manager.GetMount().getMounts())
    {
      if ((mount instanceof Dragon))
      {
        ((Dragon)mount).SetName(text);
      }
    }
  }
}
