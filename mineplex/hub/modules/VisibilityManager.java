package mineplex.hub.modules;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilEvent;
import mineplex.core.common.util.UtilEvent.ActionType;
import mineplex.core.common.util.UtilGear;
import mineplex.core.common.util.UtilMath;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.hub.tutorial.TutorialManager;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.craftbukkit.v1_7_R3.entity.CraftPlayer;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.inventory.InventoryType;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.InventoryView;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.scheduler.BukkitScheduler;

public class VisibilityManager extends MiniPlugin
{
  public HubManager Manager;
  private HashSet<Player> _hide = new HashSet();
  
  private int _slot = 8;
  
  public VisibilityManager(HubManager manager)
  {
    super("Visibility Manager", manager.GetPlugin());
    
    this.Manager = manager;
  }
  
  @EventHandler
  public void PlayerJoin(PlayerJoinEvent event)
  {
    event.getPlayer().getInventory().setItem(this._slot, ItemStackFactory.Instance.CreateStack(Material.EYE_OF_ENDER, (byte)0, 1, 
      C.cYellow + "Show Players" + C.cWhite + " - " + C.cGreen + "Enabled"));
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event)
  {
    this._hide.remove(event.getPlayer());
  }
  
  @EventHandler
  public void orderThatItem(final PlayerDropItemEvent event)
  {
    if ((event.getItemDrop().getItemStack().getType() == Material.EYE_OF_ENDER) || (event.getItemDrop().getItemStack().getType() == Material.ENDER_PEARL))
    {
      org.bukkit.Bukkit.getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
      {
        public void run()
        {
          if (event.getPlayer().isOnline())
          {
            event.getPlayer().getInventory().remove(event.getItemDrop().getItemStack().getType());
            event.getPlayer().getInventory().setItem(VisibilityManager.this._slot, ItemStackFactory.Instance.CreateStack(event.getItemDrop().getItemStack().getType(), (byte)0, 1, org.bukkit.ChatColor.RESET + C.cYellow + "Show Players" + C.cWhite + " - " + (VisibilityManager.this._hide.contains(event.getPlayer()) ? C.cRed + "Disabled" : new StringBuilder(String.valueOf(C.cGreen)).append("Enabled").toString())));
            event.getPlayer().updateInventory();
          }
        }
      });
    }
  }
  
  @EventHandler
  public void ToggleVisibility(PlayerInteractEvent event)
  {
    if (event.getAction() == org.bukkit.event.block.Action.PHYSICAL) {
      return;
    }
    Player player = event.getPlayer();
    
    if (player.getInventory().getHeldItemSlot() != this._slot) {
      return;
    }
    event.setCancelled(true);
    
    if (this._hide.remove(player))
    {
      UtilPlayer.message(player, F.main("Visibility", "All players are now visible."));
      
      player.getInventory().setItem(this._slot, ItemStackFactory.Instance.CreateStack(Material.EYE_OF_ENDER, (byte)0, 1, 
        C.cYellow + "Show Players" + C.cWhite + " - " + C.cGreen + "Enabled"));
    }
    else
    {
      this._hide.add(player);
      UtilPlayer.message(player, F.main("Visibility", "All players are now invisible."));
      
      player.getInventory().setItem(this._slot, ItemStackFactory.Instance.CreateStack(Material.ENDER_PEARL, (byte)0, 1, 
        C.cYellow + "Show Players" + C.cWhite + " - " + C.cRed + "Disabled"));
    }
  }
  
  @EventHandler
  public void UpdateVisibility(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      for (Player other : UtilServer.getPlayers())
      {
        if (!player.equals(other))
        {

          if ((this._hide.contains(other)) || (UtilMath.offset(player.getLocation(), this.Manager.GetSpawn()) < 4.0D) || 
            (this.Manager.GetTutorial().InTutorial(other)) || (this.Manager.GetTutorial().InTutorial(player)) || (
            (player.getOpenInventory().getType() != InventoryType.CRAFTING) && (player.getOpenInventory().getType() != InventoryType.CREATIVE)))
          {
            ((CraftPlayer)other).hidePlayer(player, true, false);
          }
          else
          {
            other.showPlayer(player);
          }
        }
      }
    }
  }
  
  public boolean IsHiding(LivingEntity ent) {
    return this._hide.contains(ent);
  }
  
  public HashMap<Player, Integer> _particle = new HashMap();
  
  @EventHandler
  public void ParticleSwap(PlayerInteractEvent event)
  {
    Player player = event.getPlayer();
    
    if (!player.isOp()) {
      return;
    }
    if (!UtilGear.isMat(player.getItemInHand(), Material.GOLD_NUGGET)) {
      return;
    }
    int past = 0;
    if (this._particle.containsKey(player)) {
      past = ((Integer)this._particle.get(player)).intValue();
    }
    if (UtilEvent.isAction(event, UtilEvent.ActionType.R))
    {
      past = (past + 1) % UtilParticle.ParticleType.values().length;
    }
    else if (UtilEvent.isAction(event, UtilEvent.ActionType.L))
    {
      past--;
      if (past < 0) {
        past = UtilParticle.ParticleType.values().length - 1;
      }
    }
    this._particle.put(player, Integer.valueOf(past));
    
    player.sendMessage("Particle: " + UtilParticle.ParticleType.values()[past]);
  }
  
  @EventHandler
  public void Particles(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) return;
    int j;
    int i;
    for (Iterator localIterator = this._particle.keySet().iterator(); localIterator.hasNext(); 
        
        i < j)
    {
      Player player = (Player)localIterator.next();
      Player[] arrayOfPlayer;
      j = (arrayOfPlayer = UtilServer.getPlayers()).length;i = 0; continue;Player other = arrayOfPlayer[i];
      
      UtilParticle.PlayParticle(other, UtilParticle.ParticleType.values()[((Integer)this._particle.get(player)).intValue()], player.getLocation().add(1.0D, 1.0D, 0.0D), 0.0F, 0.0F, 0.0F, 0.0F, 1);i++;
    }
  }
}
