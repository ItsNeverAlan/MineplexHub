package mineplex.hub.party;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilTime;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Sound;
import org.bukkit.entity.Player;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.ScoreboardManager;
import org.bukkit.scoreboard.Team;

public class Party
{
  private PartyManager Manager;
  private String _creator = null;
  
  private ArrayList<String> _players = new ArrayList();
  private NautHashMap<String, Long> _invitee = new NautHashMap();
  
  private Scoreboard _scoreboard;
  private Objective _scoreboardObj;
  private ArrayList<String> _scoreboardLast = new ArrayList();
  
  private long _partyOfflineTimer = -1L;
  
  public Party(PartyManager manager)
  {
    this.Manager = manager;
    

    this._scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
    this._scoreboardObj = this._scoreboard.registerNewObjective("Party", "dummy");
    this._scoreboardObj.setDisplaySlot(org.bukkit.scoreboard.DisplaySlot.SIDEBAR);
    
    this._scoreboard.registerNewTeam(ChatColor.GREEN + "Members");
    

    for (Rank rank : Rank.values())
    {
      if (rank != Rank.ALL) {
        this._scoreboard.registerNewTeam(rank.Name).setPrefix(rank.GetTag(true, false) + ChatColor.RESET + " ");
      } else {
        this._scoreboard.registerNewTeam(rank.Name).setPrefix("");
      }
    }
    this._scoreboard.registerNewTeam("Party").setPrefix(ChatColor.LIGHT_PURPLE + mineplex.core.common.util.C.Bold + "Party" + ChatColor.RESET + " ");
    

    for (Player player : Bukkit.getOnlinePlayers())
    {
      this._scoreboard.getTeam(this.Manager.GetClients().Get(player).GetRank().Name).addPlayer(player);
    }
    

    this._scoreboard.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("Chiss"));
    this._scoreboard.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("defek7"));
    this._scoreboard.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("Spu_"));
    this._scoreboard.getTeam(Rank.OWNER.Name).addPlayer(Bukkit.getOfflinePlayer("sterling_"));
  }
  

  public void JoinParty(Player player)
  {
    if (this._players.isEmpty())
    {
      this._players.add(player.getName());
      
      UtilPlayer.message(player, F.main("Party", "You created a new Party."));
      
      this._creator = player.getName();
    }
    else
    {
      this._players.add(player.getName());
      this._invitee.remove(player.getName());
      
      Announce(F.elem(player.getName()) + " has joined the party!");
    }
    
    this._scoreboard.getTeam("Party").addPlayer(player);
  }
  
  public void InviteParty(Player player, boolean inviteeInParty)
  {
    this._invitee.put(player.getName(), Long.valueOf(System.currentTimeMillis()));
    

    if (this._players.contains(player.getName()))
    {
      UtilPlayer.message(player, F.main("Party", F.name(player.getName()) + " is already in the Party."));
      player.playSound(player.getLocation(), Sound.NOTE_BASS, 1.0F, 1.5F);
    }
    

    Announce(F.name(player.getName()) + " has been invited to your Party.");
    

    UtilPlayer.message(player, F.main("Party", F.name(GetLeader()) + " invited you to their Party."));
    

    if (inviteeInParty)
    {
      UtilPlayer.message(player, F.main("Party", "Type " + F.link("/party leave") + " then " + F.link(new StringBuilder("/party ").append(GetLeader()).toString()) + " to join."));
    }
    else
    {
      UtilPlayer.message(player, F.main("Party", "Type " + F.link(new StringBuilder("/party ").append(GetLeader()).toString()) + " to join."));
    }
    

    player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.5F);
  }
  

  public void LeaveParty(Player player)
  {
    Announce(F.name(player.getName()) + " has left the Party.");
    
    boolean leader = player.equals(GetLeader());
    
    this._players.remove(player.getName());
    

    this._scoreboard.getTeam(this.Manager.GetClients().Get(player).GetRank().Name).addPlayer(player);
    
    if ((leader) && (this._players.size() > 0))
    {
      Announce("Party Leadership passed on to " + F.name(GetLeader()) + ".");
    }
  }
  

  public void KickParty(String player)
  {
    Announce(F.name(player) + " was kicked from the Party.");
    
    this._players.remove(player);
  }
  

  public void PlayerJoin(Player player)
  {
    if (this._players.contains(player.getName())) {
      this._scoreboard.getTeam("Party").addPlayer(player);
    } else if (this.Manager.GetClients().Get(player) != null) {
      this._scoreboard.getTeam(this.Manager.GetClients().Get(player).GetRank().Name).addPlayer(player);
    }
    if (this._creator.equals(player.getName()))
    {
      this._players.remove(player.getName());
      this._players.add(0, player.getName());
      
      Announce("Party Leadership returned to " + F.name(GetLeader()) + ".");
    }
  }
  

  public void PlayerQuit(Player player)
  {
    if (player.getName().equals(GetLeader()))
    {
      this._players.remove(player.getName());
      this._players.add(1, player.getName());
      
      Announce("Party Leadership passed on to " + F.name(GetLeader()) + ".");
    }
  }
  
  public void Announce(String message)
  {
    for (String name : this._players)
    {
      Player player = UtilPlayer.searchExact(name);
      
      if ((player != null) && (player.isOnline()))
      {
        UtilPlayer.message(player, F.main("Party", message));
        player.playSound(player.getLocation(), Sound.NOTE_PLING, 1.0F, 1.5F);
      }
    }
  }
  
  public void ExpireInvitees()
  {
    Iterator<String> inviteeIterator = this._invitee.keySet().iterator();
    
    while (inviteeIterator.hasNext())
    {
      String name = (String)inviteeIterator.next();
      
      if (UtilTime.elapsed(((Long)this._invitee.get(name)).longValue(), 60000L))
      {
        Announce(F.name(name) + " did not respond to the Party invite.");
        inviteeIterator.remove();
      }
    }
  }
  
  public String GetLeader()
  {
    if (this._players.isEmpty()) {
      return this._creator;
    }
    return (String)this._players.get(0);
  }
  
  public Collection<String> GetPlayers()
  {
    return this._players;
  }
  
  public Collection<Player> GetPlayersOnline()
  {
    ArrayList<Player> players = new ArrayList();
    
    for (String name : this._players)
    {
      Player player = UtilPlayer.searchExact(name);
      if (player != null) {
        players.add(player);
      }
    }
    return players;
  }
  
  public Collection<String> GetInvitees()
  {
    return this._invitee.keySet();
  }
  
  public void UpdateScoreboard()
  {
    this._scoreboardObj.setDisplayName(GetLeader() + "'s Party");
    

    for (String pastLine : this._scoreboardLast)
      this._scoreboard.resetScores(pastLine);
    this._scoreboardLast.clear();
    
    int i = 16;
    
    String name;
    for (int j = 0; j < this._players.size(); j++)
    {
      name = (String)this._players.get(j);
      Player player = UtilPlayer.searchExact(name);
      
      ChatColor col = ChatColor.GREEN;
      if (player == null) {
        col = ChatColor.RED;
      }
      String line = col + name;
      
      if (line.length() > 16) {
        line = line.substring(0, 16);
      }
      this._scoreboardObj.getScore(line).setScore(i);
      
      this._scoreboardLast.add(line);
      
      i--;
    }
    

    for (String name : this._invitee.keySet())
    {
      int time = 1 + (int)((60000L - (System.currentTimeMillis() - ((Long)this._invitee.get(name)).longValue())) / 1000L);
      
      String line = time + " " + ChatColor.GRAY + name;
      
      if (line.length() > 16) {
        line = line.substring(0, 16);
      }
      this._scoreboardObj.getScore(line).setScore(i);
      
      this._scoreboardLast.add(line);
      
      i--;
    }
    

    for (String name : this._players)
    {
      Player player = UtilPlayer.searchExact(name);
      
      if (player != null)
      {
        if (!player.getScoreboard().equals(this._scoreboard))
        {
          player.setScoreboard(this._scoreboard);
        }
      }
    }
  }
  
  public boolean IsDead()
  {
    if (this._players.size() == 0) {
      return true;
    }
    if ((this._players.size() == 1) && (this._invitee.size() == 0)) {
      return true;
    }
    int online = 0;
    for (String name : this._players)
    {
      Player player = UtilPlayer.searchExact(name);
      if (player != null) {
        online++;
      }
    }
    

    if (online <= 1)
    {
      if (this._partyOfflineTimer == -1L)
      {
        this._partyOfflineTimer = System.currentTimeMillis();


      }
      else if (UtilTime.elapsed(this._partyOfflineTimer, 3600000L))
      {
        return true;
      }
    }
    

    return false;
  }
}
