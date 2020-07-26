package mineplex.hub.modules;

import java.util.ArrayList;
import java.util.HashMap;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.hub.HubManager;
import mineplex.hub.commands.HorseSpawn;
import org.bukkit.Material;
import org.bukkit.World;
import org.bukkit.entity.Horse;
import org.bukkit.entity.Horse.Color;
import org.bukkit.entity.Horse.Style;
import org.bukkit.entity.Horse.Variant;
import org.bukkit.entity.Item;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.inventory.HorseInventory;
import org.bukkit.inventory.ItemStack;


public class AdminMountManager
  extends MiniPlugin
{
  private HubManager Manager;
  private HashMap<Player, Horse> _mounts = new HashMap();
  
  public AdminMountManager(HubManager manager)
  {
    super("Mount Manager", manager.GetPlugin());
    
    this.Manager = manager;
  }
  

  public void AddCommands()
  {
    AddCommand(new HorseSpawn(this));
  }
  
  @EventHandler
  public void HorseInteract(PlayerInteractEntityEvent event)
  {
    if (!(event.getRightClicked() instanceof Horse)) {
      return;
    }
    Player player = event.getPlayer();
    Horse horse = (Horse)event.getRightClicked();
    

    if ((!this._mounts.containsKey(player)) || (!((Horse)this._mounts.get(player)).equals(horse)))
    {
      UtilPlayer.message(player, F.main("Mount", "This is not your mount!"));
      event.setCancelled(true);
      return;
    }
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event)
  {
    Horse horse = (Horse)this._mounts.remove(event.getPlayer());
    if (horse != null) {
      horse.remove();
    }
  }
  
  public void HorseCommand(Player caller, String[] args) {
    if ((args == null) || (args.length == 0))
    {
      UtilPlayer.message(caller, F.main("Mount", "Mount Commands;"));
      UtilPlayer.message(caller, "spawn / kill / leash / unleash");
      UtilPlayer.message(caller, "age / color / style / armor");
      return;
    }
    
    if (args[0].equalsIgnoreCase("spawn"))
    {
      Horse.Variant var = Horse.Variant.HORSE;
      if (args.length > 1)
      {
        try
        {
          var = Horse.Variant.valueOf(args[1].toUpperCase());
        }
        catch (Exception localException) {}
      }
      



      Spawn(caller, var);
      return;
    }
    
    Horse horse = (Horse)this._mounts.get(caller);
    if (horse == null)
    {
      UtilPlayer.message(caller, F.main("Mount", "You do not have a mount."));
      return;
    }
    

    if (args[0].equalsIgnoreCase("leash"))
    {
      horse.setLeashHolder(caller);


    }
    else if (args[0].equalsIgnoreCase("unleash"))
    {
      horse.setLeashHolder(null);


    }
    else if (args[0].equalsIgnoreCase("kill"))
    {
      horse.remove();
      this._mounts.remove(caller);


    }
    else if (args[0].equalsIgnoreCase("age"))
    {
      if (args.length >= 2)
      {
        try
        {
          if (args[1].equalsIgnoreCase("adult"))
          {
            horse.setAdult();
          }
          else if (args[1].equalsIgnoreCase("baby"))
          {
            horse.setBaby();
          }
          return;
        }
        catch (Exception localException1) {}
      }
      



      UtilPlayer.message(caller, F.main("Mount", F.value("Age", "baby adult")));


    }
    else if (args[0].equalsIgnoreCase("color"))
    {
      if (args.length >= 2)
      {
        Horse.Color color = GetColor(caller, args[1]);
        if (color != null) {
          horse.setColor(color);
        }
        
      }
    }
    else if (args[0].equalsIgnoreCase("style"))
    {
      if (args.length >= 2)
      {
        Horse.Style style = GetStyle(caller, args[1]);
        if (style != null) {
          horse.setStyle(style);
        }
        
      }
    }
    else if ((args[0].equalsIgnoreCase("variant")) || (args[0].equalsIgnoreCase("var")))
    {
      if (args.length >= 2)
      {
        Horse.Variant variant = GetVariant(caller, args[1]);
        if (variant != null) {
          horse.setVariant(variant);
        }
        
      }
    }
    else if (args[0].equalsIgnoreCase("armor"))
    {
      if (args.length >= 2)
      {
        try
        {
          if (args[1].equalsIgnoreCase("iron"))
          {
            horse.getInventory().setArmor(new ItemStack(Material.IRON_BARDING));
            return;
          }
          if (args[1].equalsIgnoreCase("gold"))
          {
            horse.getInventory().setArmor(new ItemStack(Material.GOLD_BARDING));
            return;
          }
          if (args[1].equalsIgnoreCase("diamond"))
          {
            horse.getInventory().setArmor(new ItemStack(Material.DIAMOND_BARDING));
            return;
          }
        }
        catch (Exception localException2) {}
      }
      




      UtilPlayer.message(caller, F.main("Mount", F.value("Armor", "iron gold diamond")));
    }
  }
  
  public Horse.Style GetStyle(Player caller, String arg)
  {
    ArrayList<Horse.Style> match = new ArrayList();
    
    for (Horse.Style var : Horse.Style.values())
    {
      if (var.name().equals(arg.toUpperCase())) {
        return var;
      }
      if (var.name().contains(arg.toUpperCase())) {
        match.add(var);
      }
    }
    if (match.size() == 1) {
      return (Horse.Style)match.get(0);
    }
    String valids = "";
    for (Horse.Style valid : Horse.Style.values())
      valids = valids + valid.name() + " ";
    UtilPlayer.message(caller, F.main("Mount", F.value("Styles", valids)));
    
    return null;
  }
  
  public Horse.Color GetColor(Player caller, String arg)
  {
    ArrayList<Horse.Color> match = new ArrayList();
    
    for (Horse.Color var : Horse.Color.values())
    {
      if (var.name().equals(arg.toUpperCase())) {
        return var;
      }
      if (var.name().contains(arg.toUpperCase())) {
        match.add(var);
      }
    }
    if (match.size() == 1) {
      return (Horse.Color)match.get(0);
    }
    String valids = "";
    for (Horse.Color valid : Horse.Color.values())
      valids = valids + valid.name() + " ";
    UtilPlayer.message(caller, F.main("Mount", F.value("Colors", valids)));
    
    return null;
  }
  
  public Horse.Variant GetVariant(Player caller, String arg)
  {
    ArrayList<Horse.Variant> match = new ArrayList();
    
    for (Horse.Variant var : Horse.Variant.values())
    {
      if (var.name().equals(arg.toUpperCase())) {
        return var;
      }
      if (var.name().contains(arg.toUpperCase())) {
        match.add(var);
      }
    }
    if (match.size() == 1) {
      return (Horse.Variant)match.get(0);
    }
    String valids = "";
    for (Horse.Variant valid : Horse.Variant.values())
      valids = valids + valid.name() + " ";
    UtilPlayer.message(caller, F.main("Mount", F.value("Variants", valids)));
    
    return null;
  }
  
  public Horse Spawn(Player caller, Horse.Variant var)
  {
    Horse horse = (Horse)this._mounts.remove(caller);
    if (horse != null) { horse.remove();
    }
    horse = (Horse)caller.getWorld().spawn(caller.getLocation(), Horse.class);
    horse.setAdult();
    horse.setAgeLock(true);
    horse.setColor(Horse.Color.DARK_BROWN);
    horse.setStyle(Horse.Style.WHITE_DOTS);
    horse.setVariant(var);
    horse.setOwner(caller);
    horse.setMaxDomestication(1);
    horse.setJumpStrength(1.0D);
    horse.getInventory().setSaddle(new ItemStack(Material.SADDLE));
    
    horse.setCustomName(caller.getName() + "'s Mount");
    horse.setCustomNameVisible(true);
    
    this._mounts.put(caller, horse);
    
    return horse;
  }
  
  @EventHandler
  public void LeashSpawn(ItemSpawnEvent event)
  {
    if (event.getEntity().getItemStack().getType() == Material.LEASH) {
      event.setCancelled(true);
    }
  }
}
