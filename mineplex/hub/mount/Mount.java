package mineplex.hub.mount;

import java.util.HashMap;
import java.util.HashSet;
import mineplex.core.common.CurrencyType;
import mineplex.core.shop.item.SalesPackageBase;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;

public abstract class Mount<T>
  extends SalesPackageBase
  implements Listener
{
  protected HashSet<Player> _owners = new HashSet();
  protected HashMap<Player, T> _active = new HashMap();
  
  public MountManager Manager;
  
  public Mount(MountManager manager, String name, Material material, byte displayData, String[] description, int gems)
  {
    super(name, material, displayData, description, gems);
    
    this.Manager = manager;
  }
  

  public void Sold(Player player, CurrencyType currencyType) {}
  

  public abstract void Enable(Player paramPlayer);
  

  public abstract void Disable(Player paramPlayer);
  
  @EventHandler
  public void PlayerJoin(PlayerJoinEvent event)
  {
    if (event.getPlayer().isOp()) {
      this._owners.add(event.getPlayer());
    }
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event) {
    this._owners.remove(event.getPlayer());
    Disable(event.getPlayer());
  }
  
  public HashSet<Player> GetOwners()
  {
    return this._owners;
  }
  
  public HashMap<Player, T> GetActive()
  {
    return this._active;
  }
  
  public boolean IsActive(Player player)
  {
    return this._active.containsKey(player);
  }
  
  public boolean HasMount(Player player)
  {
    return this._owners.contains(player);
  }
}
