package mineplex.hub.party;

import java.util.Collection;
import java.util.HashSet;
import java.util.Iterator;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClientManager;
import mineplex.core.logger.Logger;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.party.commands.PartyCommand;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.plugin.java.JavaPlugin;


public class PartyManager
  extends MiniPlugin
{
  private CoreClientManager _clientManager;
  public HashSet<Party> _parties = new HashSet();
  
  public PartyManager(JavaPlugin plugin, CoreClientManager clientManager)
  {
    super("Party Manager", plugin);
    
    this._clientManager = clientManager;
  }
  

  public void AddCommands()
  {
    AddCommand(new PartyCommand(this));
  }
  
  public CoreClientManager GetClients()
  {
    return this._clientManager;
  }
  
  public Party CreateParty(Player player)
  {
    Party party = new Party(this);
    party.JoinParty(player);
    this._parties.add(party);
    
    return party;
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void PlayerJoin(PlayerJoinEvent event)
  {
    try
    {
      for (Party party : this._parties)
      {
        party.PlayerJoin(event.getPlayer());
      }
    }
    catch (Exception ex)
    {
      Logger.Instance.log(ex);
      throw ex;
    }
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event)
  {
    for (Party party : this._parties)
    {
      party.PlayerQuit(event.getPlayer());
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    ExpireParties();
    
    for (Party party : this._parties)
    {
      party.ExpireInvitees();
      party.UpdateScoreboard();
    }
  }
  
  public void ExpireParties()
  {
    Iterator<Party> partyIterator = this._parties.iterator();
    
    while (partyIterator.hasNext())
    {
      Party party = (Party)partyIterator.next();
      

      if (party.IsDead())
      {
        party.Announce("Your Party has been closed.");
        partyIterator.remove();
      }
    }
  }
  
  public Party GetParty(Player player)
  {
    for (Party party : this._parties)
    {
      if (party.GetPlayers().contains(player.getName()))
      {
        return party;
      }
    }
    
    return null;
  }
}
