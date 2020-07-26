package mineplex.hub.mount;

import java.util.HashSet;
import mineplex.core.MiniPlugin;
import mineplex.core.common.Rank;
import mineplex.core.common.util.UtilPlayer;
import mineplex.hub.HubManager;
import mineplex.hub.mount.types.Dragon;
import mineplex.hub.mount.types.Frost;
import mineplex.hub.mount.types.Undead;
import org.bukkit.Material;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.inventory.ItemStack;

public class MountManager extends MiniPlugin
{
  public HubManager Manager;
  private HashSet<Mount> _types;
  
  public MountManager(HubManager manager)
  {
    super("Mount Manager", manager.GetPlugin());
    
    this.Manager = manager;
    
    CreateGadgets();
  }
  
  private void CreateGadgets()
  {
    this._types = new HashSet();
    
    this._types.add(new Undead(this));
    this._types.add(new Frost(this));
    this._types.add(new mineplex.hub.mount.types.Mule(this));
    this._types.add(new Dragon(this));
  }
  
  public HashSet<Mount> getMounts()
  {
    return this._types;
  }
  
  @EventHandler
  public void onPlayerJoin(PlayerJoinEvent event)
  {
    if (this.Manager.GetClients().Get(event.getPlayer()).GetRank().Has(Rank.MODERATOR))
    {
      for (Mount mount : this._types)
      {
        this.Manager.GetDonation().Get(event.getPlayer().getName()).AddUnknownSalesPackagesOwned(mount.GetName());
      }
    }
  }
  

  public void DeregisterAll(Player player)
  {
    for (Mount mount : this._types) {
      mount.Disable(player);
    }
  }
  
  @EventHandler
  public void HorseInteract(PlayerInteractEntityEvent event) {
    if (!(event.getRightClicked() instanceof Horse)) {
      return;
    }
    Player player = event.getPlayer();
    Horse horse = (Horse)event.getRightClicked();
    
    if ((horse.getOwner() == null) || (!horse.getOwner().equals(player)))
    {
      UtilPlayer.message(player, mineplex.core.common.util.F.main("Mount", "This is not your Mount!"));
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void LeashDropCancel(ItemSpawnEvent event)
  {
    if (event.getEntity().getItemStack().getType() == Material.LEASH) {
      event.setCancelled(true);
    }
  }
}
