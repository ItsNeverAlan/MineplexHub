package mineplex.hub.modules;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilBlock;
import mineplex.core.common.util.UtilEnt;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import org.bukkit.Effect;
import org.bukkit.GameMode;
import org.bukkit.Location;
import org.bukkit.block.Block;
import org.bukkit.block.BlockFace;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerToggleFlightEvent;
import org.bukkit.util.Vector;

public class JumpManager extends MiniPlugin
{
  public HubManager Manager;
  
  public JumpManager(HubManager manager)
  {
    super("Double Jump", manager.GetPlugin());
    
    this.Manager = manager;
  }
  
  @EventHandler
  public void FlightHop(PlayerToggleFlightEvent event)
  {
    Player player = event.getPlayer();
    
    if (player.getGameMode() == GameMode.CREATIVE) {
      return;
    }
    event.setCancelled(true);
    player.setFlying(false);
    

    player.setAllowFlight(false);
    

    if (this.Manager.GetParkour().InParkour(player))
    {
      UtilPlayer.message(player, mineplex.core.common.util.F.main("Parkour", "You cannot Double Jump near Parkour Challenges."));
      player.setVelocity(new Vector(0, 0, 0));
      player.teleport(player.getLocation());
      return;
    }
    

    UtilAction.velocity(player, 1.4D, 0.2D, 1.0D, true);
    

    player.playEffect(player.getLocation(), Effect.BLAZE_SHOOT, 0);
  }
  
  @EventHandler
  public void FlightUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if (player.getGameMode() != GameMode.CREATIVE)
      {

        if ((UtilEnt.isGrounded(player)) || (UtilBlock.solid(player.getLocation().getBlock().getRelative(BlockFace.DOWN))))
        {
          player.setAllowFlight(true);
          player.setFlying(false);
        }
      }
    }
  }
}
