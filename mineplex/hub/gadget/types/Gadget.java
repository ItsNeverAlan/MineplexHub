package mineplex.hub.gadget.types;

import java.util.HashSet;
import mineplex.core.common.CurrencyType;
import mineplex.core.shop.item.SalesPackageBase;
import mineplex.hub.gadget.GadgetManager;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public abstract class Gadget extends SalesPackageBase implements Listener
{
  public GadgetManager Manager;
  protected HashSet<Player> _active = new HashSet();
  
  public Gadget(GadgetManager manager, String name, String[] desc, int cost, Material mat, byte data)
  {
    super(name, mat, data, desc, cost);
    this.KnownPackage = false;
    
    this.Manager = manager;
    
    this.Manager.GetPlugin().getServer().getPluginManager().registerEvents(this, this.Manager.GetPlugin());
  }
  
  public HashSet<Player> GetActive()
  {
    return this._active;
  }
  
  public boolean IsActive(Player player)
  {
    return this._active.contains(player);
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event)
  {
    Disable(event.getPlayer());
  }
  
  public abstract void Enable(Player paramPlayer);
  
  public abstract void Disable(Player paramPlayer);
  
  public void Sold(Player player, CurrencyType currencyType) {}
}
