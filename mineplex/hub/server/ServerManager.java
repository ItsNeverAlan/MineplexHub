package mineplex.hub.server;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import mineplex.core.MiniPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.common.util.UtilAction;
import mineplex.core.common.util.UtilAlg;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.logger.Logger;
import mineplex.core.portal.Portal;
import mineplex.core.recharge.Recharge;
import mineplex.core.status.ServerStatusData;
import mineplex.core.status.ServerStatusManager;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.hub.modules.StackerManager;
import mineplex.hub.party.Party;
import mineplex.hub.party.PartyManager;
import mineplex.hub.server.ui.LobbyShop;
import mineplex.hub.server.ui.QuickShop;
import mineplex.hub.server.ui.ServerNpcShop;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityPortalEnterEvent;
import org.bukkit.event.entity.EntityPortalEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerPortalEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.plugin.messaging.Messenger;
import org.bukkit.util.Vector;

public class ServerManager extends MiniPlugin
{
  private CoreClientManager _clientManager;
  private DonationManager _donationManager;
  private Portal _portal;
  private PartyManager _partyManager;
  private ServerStatusManager _statusManager;
  private HubManager _hubManager;
  private StackerManager _stackerManager;
  private NautHashMap<String, HashSet<ServerInfo>> _serverKeyInfoMap = new NautHashMap();
  private NautHashMap<String, String> _serverKeyTagMap = new NautHashMap();
  private NautHashMap<String, ServerNpcShop> _serverNpcShopMap = new NautHashMap();
  private NautHashMap<String, ServerInfo> _serverInfoMap = new NautHashMap();
  private NautHashMap<String, Long> _serverUpdate = new NautHashMap();
  private NautHashMap<Vector, String> _serverPortalLocations = new NautHashMap();
  
  private QuickShop _quickShop;
  
  private LobbyShop _lobbyShop;
  private boolean _alternateUpdateFire = false;
  private boolean _retrieving = false;
  private long _lastRetrieve = 0L;
  
  public ServerManager(JavaPlugin plugin, CoreClientManager clientManager, DonationManager donationManager, Portal portal, PartyManager partyManager, ServerStatusManager statusManager, HubManager hubManager, StackerManager stackerManager)
  {
    super("Server Manager", plugin);
    
    this._clientManager = clientManager;
    this._donationManager = donationManager;
    this._portal = portal;
    this._partyManager = partyManager;
    this._statusManager = statusManager;
    this._hubManager = hubManager;
    this._stackerManager = stackerManager;
    
    plugin.getServer().getMessenger().registerOutgoingPluginChannel(plugin, "BungeeCord");
    
    LoadServers();
    
    new ServerManagerUpdater(this);
    this._quickShop = new QuickShop(this, clientManager, donationManager, "Quick Menu");
    this._lobbyShop = new LobbyShop(this, clientManager, donationManager, "Lobby Menu");
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void playerPortalEvent(PlayerPortalEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void entityPortalEvent(EntityPortalEvent event)
  {
    event.setCancelled(true);
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void playerCheckPortalEvent(EntityPortalEnterEvent event)
  {
    if (!(event.getEntity() instanceof Player))
    {
      UtilAction.velocity(event.getEntity(), UtilAlg.getTrajectory(event.getEntity().getLocation(), this._hubManager.GetSpawn()), 1.0D, true, 0.8D, 0.0D, 1.0D, true);
      return;
    }
    
    Player player = (Player)event.getEntity();
    
    if (!this._hubManager.CanPortal(player))
    {
      UtilAction.velocity(player, UtilAlg.getTrajectory(player.getLocation(), this._hubManager.GetSpawn()), 1.0D, true, 0.8D, 0.0D, 1.0D, true);
      return;
    }
    
    if (!Recharge.Instance.use(player, "Portal Server", 1000L, false, false)) {
      return;
    }
    String serverName = (String)this._serverPortalLocations.get(player.getLocation().getBlock().getLocation().toVector());
    
    if (serverName != null)
    {
      List<ServerInfo> serverList = new java.util.ArrayList(GetServerList(serverName));
      
      int slots = 1;
      
      if (serverList.size() > 0)
      {
        slots = GetRequiredSlots(player, ((ServerInfo)serverList.get(0)).ServerType);
      }
      
      try
      {
        java.util.Collections.sort(serverList, new ServerSorter(slots));
        
        for (ServerInfo serverInfo : serverList)
        {
          if (((serverInfo.MOTD.contains("Starting")) || (serverInfo.MOTD.contains("Recruiting")) || (serverInfo.MOTD.contains("Waiting")) || (serverInfo.MOTD.contains("Cup"))) && (serverInfo.MaxPlayers - serverInfo.CurrentPlayers >= slots))
          {
            SelectServer(player, serverInfo);
            return;
          }
        }
      }
      catch (Exception exception)
      {
        Logger.Instance.log(exception);
        exception.printStackTrace();
      }
      
      player.sendMessage(F.main("Server Portal", "There are currently no joinable servers!"));
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void playerJoin(PlayerJoinEvent event)
  {
    event.getPlayer().getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.COMPASS.getId(), 0, 1, ChatColor.GREEN + "Game Menu") });
    event.getPlayer().getInventory().addItem(new ItemStack[] { ItemStackFactory.Instance.CreateStack(Material.WATCH.getId(), 0, 1, ChatColor.GREEN + "Lobby Menu") });
  }
  
  @EventHandler(priority=EventPriority.LOWEST)
  public void playerInteract(PlayerInteractEvent event)
  {
    if ((event.getItem() != null) && (event.getItem().getType() == Material.COMPASS))
    {
      this._quickShop.attemptShopOpen(event.getPlayer());
    }
    else if ((event.getItem() != null) && (event.getItem().getType() == Material.WATCH))
    {
      this._lobbyShop.attemptShopOpen(event.getPlayer());
    }
  }
  
  public void RemoveServer(String serverName)
  {
    for (String key : this._serverKeyInfoMap.keySet())
    {
      ((HashSet)this._serverKeyInfoMap.get(key)).remove(serverName);
    }
    
    this._serverInfoMap.remove(serverName);
  }
  
  public void addServerGroup(String serverKey, String serverTag)
  {
    this._serverKeyInfoMap.put(serverKey, new HashSet());
    this._serverKeyTagMap.put(serverTag, serverKey);
  }
  
  public void AddServerNpc(String serverNpcName, String serverTag)
  {
    addServerGroup(serverNpcName, serverTag);
    this._serverNpcShopMap.put(serverNpcName, new ServerNpcShop(this, this._clientManager, this._donationManager, serverNpcName));
  }
  
  public void RemoveServerNpc(String serverNpcName)
  {
    Set<ServerInfo> mappedServers = (Set)this._serverKeyInfoMap.remove(serverNpcName);
    this._serverNpcShopMap.remove(serverNpcName);
    
    if (mappedServers != null)
    {
      for (ServerInfo mappedServer : mappedServers)
      {
        boolean isMappedElseWhere = false;
        
        for (String key : this._serverKeyInfoMap.keySet())
        {
          for (ServerInfo value : (HashSet)this._serverKeyInfoMap.get(key))
          {
            if (value.Name.equalsIgnoreCase(mappedServer.Name))
            {
              isMappedElseWhere = true;
              break;
            }
          }
          
          if (isMappedElseWhere) {
            break;
          }
        }
        if (!isMappedElseWhere) {
          this._serverInfoMap.remove(mappedServer.Name);
        }
      }
    }
  }
  
  public Collection<ServerInfo> GetServerList(String serverNpcName) {
    return (Collection)this._serverKeyInfoMap.get(serverNpcName);
  }
  
  public Set<String> GetAllServers()
  {
    return this._serverInfoMap.keySet();
  }
  
  public ServerInfo GetServerInfo(String serverName)
  {
    return (ServerInfo)this._serverInfoMap.get(serverName);
  }
  
  public boolean HasServerNpc(String serverNpcName)
  {
    return this._serverKeyInfoMap.containsKey(serverNpcName);
  }
  
  @EventHandler
  public void updatePages(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FAST) {
      return;
    }
    this._quickShop.UpdatePages();
    
    for (ServerNpcShop shop : this._serverNpcShopMap.values())
    {
      shop.UpdatePages();
    }
  }
  
  @EventHandler
  public void updateServers(UpdateEvent event)
  {
    if ((event.getType() != UpdateType.SEC) || ((this._retrieving) && (System.currentTimeMillis() - this._lastRetrieve <= 5000L))) {
      return;
    }
    this._alternateUpdateFire = (!this._alternateUpdateFire);
    
    if (!this._alternateUpdateFire) {
      return;
    }
    this._retrieving = true;
    

    this._statusManager.retrieveServerStatuses(new Callback()
    {
      public void run(List<ServerStatusData> serverStatusList)
      {
        for (ServerStatusData serverStatus : serverStatusList)
        {
          if (!ServerManager.this._serverInfoMap.containsKey(serverStatus.Name))
          {
            ServerInfo newServerInfo = new ServerInfo();
            newServerInfo.Name = serverStatus.Name;
            ServerManager.this._serverInfoMap.put(serverStatus.Name, newServerInfo);
          }
          
          String[] args = serverStatus.Motd.split("\\|");
          String tag = (serverStatus.Name != null) && (serverStatus.Name.contains("-")) ? serverStatus.Name.split("-")[0] : "N/A";
          
          ServerInfo serverInfo = (ServerInfo)ServerManager.this._serverInfoMap.get(serverStatus.Name);
          serverInfo.MOTD = (args.length > 0 ? args[0] : serverStatus.Motd);
          serverInfo.CurrentPlayers = serverStatus.Players;
          serverInfo.MaxPlayers = serverStatus.MaxPlayers;
          
          if (args.length > 1) {
            serverInfo.ServerType = args[1];
          }
          if (args.length > 2) {
            serverInfo.Game = args[2];
          }
          if (args.length > 3) {
            serverInfo.Map = args[3];
          }
          ServerManager.this._serverUpdate.put(serverStatus.Name, Long.valueOf(System.currentTimeMillis()));
          
          if (ServerManager.this._serverKeyTagMap.containsKey(tag))
          {
            ((HashSet)ServerManager.this._serverKeyInfoMap.get((String)ServerManager.this._serverKeyTagMap.get(tag))).add(serverInfo);
          }
        }
        
        for (String name : ServerManager.this._serverUpdate.keySet())
        {
          if ((((Long)ServerManager.this._serverUpdate.get(name)).longValue() != -1L) && (System.currentTimeMillis() - ((Long)ServerManager.this._serverUpdate.get(name)).longValue() > 5000L))
          {
            ServerInfo serverInfo = (ServerInfo)ServerManager.this._serverInfoMap.get(name);
            serverInfo.MOTD = (ChatColor.DARK_RED + "OFFLINE");
            serverInfo.CurrentPlayers = 0;
            serverInfo.MaxPlayers = 0;
            
            ServerManager.this._serverUpdate.put(name, Long.valueOf(-1L));
          }
        }
        

        ServerManager.this._retrieving = false;
        ServerManager.this._lastRetrieve = System.currentTimeMillis();
      }
    });
  }
  
  public void Help(Player caller, String message)
  {
    UtilPlayer.message(caller, F.main(this._moduleName, "Commands List:"));
    UtilPlayer.message(caller, F.help("/servernpc create <name>", "<name> is name of npc.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/servernpc delete <name>", "<name> is name of npc.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/servernpc addserver <servernpc> | <name>", "Adds server.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/servernpc removeserver <name>", "Removes server.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/servernpc listnpcs", "Lists all server npcs.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/servernpc listservers <servernpc>", "Lists all servers.", Rank.OWNER));
    UtilPlayer.message(caller, F.help("/servernpc listoffline", "Shows all servers offline.", Rank.OWNER));
    
    if (message != null) {
      UtilPlayer.message(caller, F.main(this._moduleName, ChatColor.RED + message));
    }
  }
  
  public void Help(Player caller) {
    Help(caller, null);
  }
  
  public PartyManager getPartyManager()
  {
    return this._partyManager;
  }
  
  public void SelectServer(Player player, ServerInfo serverInfo)
  {
    Party party = this._partyManager.GetParty(player);
    
    if (party != null)
    {
      if (player.getName().equals(party.GetLeader()))
      {
        for (String name : party.GetPlayers())
        {
          Player partyPlayer = UtilPlayer.searchExact(name);
          
          if (partyPlayer != null)
          {

            if ((!serverInfo.Name.contains("BETA")) || 
            
              (this._clientManager.Get(partyPlayer).GetRank().Has(Rank.ULTRA)))
            {


              if ((this._clientManager.Get(partyPlayer).GetRank().Has(Rank.MODERATOR)) || (serverInfo.CurrentPlayers < serverInfo.MaxPlayers * 1.5D))
              {

                if ((!this._clientManager.Get(partyPlayer).GetRank().Has(Rank.ULTRA)) && (!this._donationManager.Get(partyPlayer.getName()).OwnsUnknownPackage(serverInfo.ServerType + " ULTRA")))
                {

                  partyPlayer.leaveVehicle();
                  partyPlayer.eject();
                  
                  this._portal.SendPlayerToServer(partyPlayer, serverInfo.Name);
                } } } }
        }
        for (String name : party.GetPlayers())
        {
          Player partyPlayer = UtilPlayer.searchExact(name);
          
          if (partyPlayer != null)
          {

            if ((!serverInfo.Name.contains("BETA")) || 
            
              (this._clientManager.Get(partyPlayer).GetRank().Has(Rank.ULTRA)))
            {


              if ((this._clientManager.Get(partyPlayer).GetRank().Has(Rank.MODERATOR)) || (serverInfo.CurrentPlayers < serverInfo.MaxPlayers * 1.5D))
              {

                if ((this._clientManager.Get(partyPlayer).GetRank().Has(Rank.ULTRA)) || (this._donationManager.Get(partyPlayer.getName()).OwnsUnknownPackage(serverInfo.ServerType + " ULTRA")))
                {
                  partyPlayer.leaveVehicle();
                  partyPlayer.eject();
                  
                  this._portal.SendPlayerToServer(partyPlayer, serverInfo.Name);
                } }
            }
          }
        }
      }
    } else {
      player.leaveVehicle();
      player.eject();
      
      this._portal.SendPlayerToServer(player, serverInfo.Name);
    }
  }
  
  public void ListServerNpcs(Player caller)
  {
    UtilPlayer.message(caller, F.main(GetName(), "Listing Server Npcs:"));
    
    for (String serverNpc : this._serverKeyInfoMap.keySet())
    {
      UtilPlayer.message(caller, F.main(GetName(), C.cYellow + serverNpc));
    }
  }
  
  public void ListServers(Player caller, String serverNpcName)
  {
    UtilPlayer.message(caller, F.main(GetName(), "Listing Servers for '" + serverNpcName + "':"));
    
    for (ServerInfo serverNpc : (HashSet)this._serverKeyInfoMap.get(serverNpcName))
    {
      UtilPlayer.message(caller, F.main(GetName(), C.cYellow + serverNpc.Name + C.cWhite + " - " + serverNpc.MOTD + " " + serverNpc.CurrentPlayers + "/" + serverNpc.MaxPlayers));
    }
  }
  
  public void ListOfflineServers(Player caller)
  {
    UtilPlayer.message(caller, F.main(GetName(), "Listing Offline Servers:"));
    
    for (ServerInfo serverNpc : this._serverInfoMap.values())
    {
      if (serverNpc.MOTD.equalsIgnoreCase(ChatColor.DARK_RED + "OFFLINE"))
      {
        UtilPlayer.message(caller, F.main(GetName(), C.cYellow + serverNpc.Name + C.cWhite + " - " + F.time(mineplex.core.common.util.UtilTime.convertString(System.currentTimeMillis() - ((Long)this._serverUpdate.get(serverNpc.Name)).longValue(), 0, mineplex.core.common.util.UtilTime.TimeUnit.FIT))));
      }
    }
  }
  
  public void LoadServers()
  {
    this._serverInfoMap.clear();
    this._serverUpdate.clear();
    
    for (String npcName : this._serverKeyInfoMap.keySet())
    {
      ((HashSet)this._serverKeyInfoMap.get(npcName)).clear();
    }
    
    this._serverKeyTagMap.clear();
    
    FileInputStream fstream = null;
    BufferedReader br = null;
    
    HashSet<String> npcNames = new HashSet();
    String line;
    try
    {
      File npcFile = new File("ServerManager.dat");
      
      if (npcFile.exists())
      {
        fstream = new FileInputStream(npcFile);
        br = new BufferedReader(new InputStreamReader(fstream));
        
        line = br.readLine();
        
        while (line != null)
        {
          String serverNpcName = line.substring(0, line.indexOf('|')).trim();
          String serverTag = line.substring(line.indexOf('|') + 1, line.indexOf('|', line.indexOf(124) + 1)).trim();
          String[] locations = line.substring(line.indexOf('|', line.indexOf(124) + 1) + 1).trim().split(",");
          
          for (String location : locations)
          {
            this._serverPortalLocations.put(ParseVector(location), serverNpcName);
          }
          
          if (!HasServerNpc(serverNpcName))
          {
            AddServerNpc(serverNpcName, serverTag);
          }
          
          npcNames.add(serverNpcName);
          
          line = br.readLine();
        }
      }
    }
    catch (Exception e)
    {
      Logger.Instance.log(e);
      System.out.println("ServerManager - Error parsing servers file : " + e.getMessage());
      


      if (br != null)
      {
        try
        {
          br.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    finally
    {
      if (br != null)
      {
        try
        {
          br.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
      
      if (fstream != null)
      {
        try
        {
          fstream.close();
        }
        catch (IOException e)
        {
          e.printStackTrace();
        }
      }
    }
    
    for (String npcName : npcNames)
    {
      if (!this._serverNpcShopMap.containsKey(npcName)) {
        this._serverNpcShopMap.remove(npcName);
      }
      if (!this._serverKeyInfoMap.containsKey(npcName)) {
        this._serverKeyInfoMap.remove(npcName);
      }
    }
  }
  
  public int GetRequiredSlots(Player player, String serverType) {
    int slots = 0;
    
    Party party = this._partyManager.GetParty(player);
    
    if (party != null)
    {
      if (player.getName().equals(party.GetLeader()))
      {
        for (String name : party.GetPlayers())
        {
          Player partyPlayer = UtilPlayer.searchExact(name);
          
          if (partyPlayer != null)
          {

            if ((!this._clientManager.Get(partyPlayer).GetRank().Has(Rank.ULTRA)) && (!this._donationManager.Get(partyPlayer.getName()).OwnsUnknownPackage(serverType + " ULTRA")))
            {

              slots++;
            }
          }
        }
      }
    }
    else if ((!this._clientManager.Get(player).GetRank().Has(Rank.ULTRA)) && (!this._donationManager.Get(player.getName()).OwnsUnknownPackage(serverType + " ULTRA"))) {
      slots++;
    }
    
    return slots;
  }
  
  public ServerNpcShop getMixedArcadeShop()
  {
    return (ServerNpcShop)this._serverNpcShopMap.get("Mixed Arcade");
  }
  
  public ServerNpcShop getSuperSmashMobsShop()
  {
    return (ServerNpcShop)this._serverNpcShopMap.get("Super Smash Mobs");
  }
  
  public ServerNpcShop getDominateShop()
  {
    return (ServerNpcShop)this._serverNpcShopMap.get("Dominate");
  }
  
  public ServerNpcShop getBridgesShop()
  {
    return (ServerNpcShop)this._serverNpcShopMap.get("The Bridges");
  }
  
  public ServerNpcShop getSurvivalGamesShop()
  {
    return (ServerNpcShop)this._serverNpcShopMap.get("Survival Games");
  }
  
  public ServerNpcShop getBlockHuntShop()
  {
    return (ServerNpcShop)this._serverNpcShopMap.get("Block Hunt");
  }
  
  private Vector ParseVector(String vectorString)
  {
    Vector vector = new Vector();
    
    String[] parts = vectorString.trim().split(" ");
    
    vector.setX(Double.parseDouble(parts[0]));
    vector.setY(Double.parseDouble(parts[1]));
    vector.setZ(Double.parseDouble(parts[2]));
    
    return vector;
  }
  
  public ServerStatusManager getStatusManager()
  {
    return this._statusManager;
  }
}
