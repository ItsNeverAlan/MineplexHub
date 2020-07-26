package mineplex.hub.modules;

import java.io.PrintStream;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.common.Rank;
import mineplex.core.common.util.UtilGear;
import mineplex.core.logger.Logger;
import mineplex.core.map.Map;
import mineplex.hub.HubManager;
import org.bukkit.Effect;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;

public class MapManager extends MiniPlugin
{
  private HubManager Manager;
  private Map Map;
  
  public MapManager(HubManager manager)
  {
    super("Map Manager", manager.GetPlugin());
    this.Map = new Map(manager.GetPlugin());
    this.Manager = manager;
  }
  

  public void PlayerJoin(PlayerJoinEvent event)
  {
    try
    {
      this.Map.SetDefaultUrl("http://chivebox.com/img/mc/news.png");
      event.getPlayer().setItemInHand(this.Map.GetMap());
    }
    catch (Exception ex)
    {
      Logger.Instance.log(ex);
      System.out.println("[MapManager] Player Join exception");
      throw ex;
    }
  }
  
  @org.bukkit.event.EventHandler
  public void FrameInteract(PlayerInteractEntityEvent event)
  {
    if (!(event.getRightClicked() instanceof org.bukkit.entity.ItemFrame)) {
      return;
    }
    if (!this.Manager.GetClients().Get(event.getPlayer()).GetRank().Has(Rank.OWNER))
    {
      event.setCancelled(true);
      return;
    }
    
    if (!UtilGear.isMat(event.getPlayer().getItemInHand(), org.bukkit.Material.DIAMOND_AXE)) {
      return;
    }
    event.getRightClicked().getWorld().playEffect(event.getRightClicked().getLocation(), Effect.STEP_SOUND, 5);
    event.getRightClicked().remove();
  }
}
