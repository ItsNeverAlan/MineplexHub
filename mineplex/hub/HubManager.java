package mineplex.hub;

import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.MiniClientPlugin;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.blockrestore.BlockRestore;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilParticle;
import mineplex.core.common.util.UtilParticle.ParticleType;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.common.util.UtilServer;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSlime;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.logger.Logger;
import mineplex.core.portal.Portal;
import mineplex.core.stats.StatsManager;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.gadget.GadgetManager;
import mineplex.hub.modules.ParkourManager;
import mineplex.hub.modules.TextManager;
import mineplex.hub.modules.VisibilityManager;
import mineplex.hub.mount.MountManager;
import mineplex.hub.party.PartyManager;
import mineplex.hub.tutorial.TutorialManager;
import mineplex.minecraft.game.classcombat.Skill.event.SkillTriggerEvent;
import mineplex.minecraft.game.classcombat.item.event.ItemTriggerEvent;
import mineplex.minecraft.game.core.condition.ConditionManager;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.Location;
import org.bukkit.Material;
import org.bukkit.Server;
import org.bukkit.craftbukkit.v1_7_R3.CraftWorld;
import org.bukkit.entity.Entity;
import org.bukkit.entity.Item;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.EventPriority;
import org.bukkit.event.entity.EntityDamageEvent;
import org.bukkit.event.entity.ItemSpawnEvent;
import org.bukkit.event.inventory.InventoryClickEvent;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import org.bukkit.event.player.PlayerDropItemEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerJoinEvent;
import org.bukkit.event.player.PlayerLoginEvent;
import org.bukkit.event.player.PlayerQuitEvent;
import org.bukkit.event.player.PlayerRespawnEvent;
import org.bukkit.event.server.ServerListPingEvent;
import org.bukkit.event.world.ChunkLoadEvent;
import org.bukkit.inventory.ItemStack;
import org.bukkit.inventory.PlayerInventory;
import org.bukkit.plugin.java.JavaPlugin;
import org.bukkit.scheduler.BukkitScheduler;
import org.bukkit.scoreboard.DisplaySlot;
import org.bukkit.scoreboard.Objective;
import org.bukkit.scoreboard.Score;
import org.bukkit.scoreboard.Scoreboard;
import org.bukkit.scoreboard.Team;

public class HubManager extends MiniClientPlugin<HubClient>
{
  public String Mode = "Normal";
  
  private BlockRestore _blockRestore;
  
  private CoreClientManager _clientManager;
  private ConditionManager _conditionManager;
  private DonationManager _donationManager;
  private DisguiseManager _disguiseManager;
  private PartyManager _partyManager;
  private Portal _portal;
  private StatsManager _statsManager;
  private GadgetManager _gadgetManager;
  private MountManager _mountManager;
  private VisibilityManager _visibilityManager;
  private TutorialManager _tutorialManager;
  private TextManager _textCreator;
  private ParkourManager _parkour;
  private Location _spawn;
  private int _scoreboardTick = 0;
  
  private HashMap<Player, Scoreboard> _scoreboards = new HashMap();
  
  private String _pigStacker = "0 - Nobody";
  
  private boolean _shuttingDown;
  
  private HashMap<String, Long> _portalTime = new HashMap();
  
  private int _slot = 7;
  private HashSet<Player> _disabled = new HashSet();
  
  public HubManager(JavaPlugin plugin, BlockRestore blockRestore, CoreClientManager clientManager, DonationManager donationManager, ConditionManager conditionManager, DisguiseManager disguiseManager, mineplex.core.task.TaskManager taskManager, Portal portal, PartyManager partyManager)
  {
    super("Hub Manager", plugin);
    
    this._blockRestore = blockRestore;
    this._clientManager = clientManager;
    this._conditionManager = conditionManager;
    this._donationManager = donationManager;
    this._disguiseManager = disguiseManager;
    
    this._portal = portal;
    
    this._spawn = new Location(mineplex.core.common.util.UtilWorld.getWorld("world"), 0.5D, 74.0D, 0.5D);
    
    this._textCreator = new TextManager(this);
    this._parkour = new ParkourManager(this, donationManager, taskManager);
    
    new mineplex.hub.modules.MapManager(this);
    new mineplex.hub.modules.WorldManager(this);
    new mineplex.hub.modules.JumpManager(this);
    new mineplex.hub.modules.NewsManager(this);
    
    this._mountManager = new MountManager(this);
    this._gadgetManager = new GadgetManager(this, this._mountManager);
    
    this._partyManager = partyManager;
    this._tutorialManager = new TutorialManager(this, donationManager, taskManager, this._textCreator);
    this._visibilityManager = new VisibilityManager(this);
    
    this._statsManager = new StatsManager(plugin);
    
    ((CraftWorld)Bukkit.getWorlds().get(0)).getHandle().pvpMode = true;
  }
  
  @EventHandler(priority=EventPriority.HIGHEST)
  public void reflectMotd(ServerListPingEvent event)
  {
    if (this._shuttingDown)
    {
      event.setMotd("Restarting soon");
    }
  }
  
  @EventHandler
  public void BumpJoin(PlayerJoinEvent event)
  {
    event.getPlayer().getInventory().setItem(this._slot, ItemStackFactory.Instance.CreateStack(Material.SLIME_BALL, (byte)0, 1, 
      C.cYellow + "Stacker" + C.cWhite + " - " + C.cGreen + "Enabled"));
  }
  
  @EventHandler
  public void BumpQuit(PlayerQuitEvent event)
  {
    this._disabled.remove(event.getPlayer());
  }
  
  public boolean BumpDisabled(Entity ent)
  {
    if (ent == null) {
      return false;
    }
    return this._disabled.contains(ent);
  }
  
  @EventHandler
  public void BumpToggle(PlayerInteractEvent event)
  {
    if (event.getAction() == org.bukkit.event.block.Action.PHYSICAL) {
      return;
    }
    Player player = event.getPlayer();
    
    if (player.getInventory().getHeldItemSlot() != this._slot) {
      return;
    }
    event.setCancelled(true);
    
    if (this._disabled.remove(player))
    {
      UtilPlayer.message(player, F.main("Hub Games", "You are back in the Hub Games!"));
      
      player.getInventory().setItem(this._slot, ItemStackFactory.Instance.CreateStack(Material.SLIME_BALL, (byte)0, 1, 
        C.cYellow + "Stacker" + C.cWhite + " - " + C.cGreen + "Enabled"));
    }
    else
    {
      this._disabled.add(player);
      UtilPlayer.message(player, F.main("Hub Games", "You are no longer partaking in Hub Games."));
      
      player.getInventory().setItem(this._slot, ItemStackFactory.Instance.CreateStack(Material.MAGMA_CREAM, (byte)0, 1, 
        C.cYellow + "Stacker" + C.cWhite + " - " + C.cRed + "Disabled"));
    }
  }
  
  @EventHandler
  public void orderThatItem(final PlayerDropItemEvent event)
  {
    if ((event.getItemDrop().getItemStack().getType() == Material.REDSTONE_TORCH_OFF) || (event.getItemDrop().getItemStack().getType() == Material.REDSTONE_TORCH_ON))
    {
      Bukkit.getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
      {
        public void run()
        {
          if (event.getPlayer().isOnline())
          {
            event.getPlayer().getInventory().remove(event.getItemDrop().getItemStack().getType());
            event.getPlayer().getInventory().setItem(HubManager.this._slot, ItemStackFactory.Instance.CreateStack(event.getItemDrop().getItemStack().getType(), (byte)0, 1, ChatColor.RESET + C.cYellow + "Stacker" + C.cWhite + (HubManager.this._disabled.contains(event.getPlayer()) ? C.cWhite + " - " + C.cRed + "Disabled" : new StringBuilder(" - ").append(C.cGreen).append("Enabled").toString())));
            event.getPlayer().updateInventory();
          }
        }
      });
    }
  }
  
  @EventHandler
  public void redirectStopCommand(PlayerCommandPreprocessEvent event)
  {
    if ((event.getPlayer().isOp()) && (event.getMessage().equalsIgnoreCase("/stop")))
    {
      this._shuttingDown = true;
      
      Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(this._plugin, new Runnable()
      {
        public void run()
        {
          HubManager.this._portal.SendAllPlayers("Lobby");
          
          Bukkit.getServer().getScheduler().scheduleSyncDelayedTask(HubManager.this._plugin, new Runnable()
          {

            public void run() {}


          }, 40L);
        }
      }, 60L);
      
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void preventEggSpawn(ItemSpawnEvent event)
  {
    if ((event.getEntity() instanceof org.bukkit.entity.Egg))
    {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void PlayerRespawn(PlayerRespawnEvent event)
  {
    event.setRespawnLocation(GetSpawn());
  }
  
  @EventHandler(priority=EventPriority.MONITOR)
  public void OnChunkLoad(ChunkLoadEvent event)
  {
    for (Entity entity : event.getChunk().getEntities())
    {
      if ((entity instanceof LivingEntity))
      {
        if ((((LivingEntity)entity).isCustomNameVisible()) && (((LivingEntity)entity).getCustomName() != null))
        {
          if (ChatColor.stripColor(((LivingEntity)entity).getCustomName()).equalsIgnoreCase("Minekart"))
          {
            this._disguiseManager.disguise(new mineplex.core.disguise.disguises.DisguisePlayer(entity, ChatColor.YELLOW + "MineKart"));
          }
          else if (ChatColor.stripColor(((LivingEntity)entity).getCustomName()).equalsIgnoreCase("Block Hunt"))
          {
            DisguiseSlime disguise = new DisguiseSlime(entity);
            disguise.SetCustomNameVisible(true);
            disguise.SetName(((LivingEntity)entity).getCustomName());
            disguise.SetSize(2);
            this._disguiseManager.disguise(disguise);
          }
        }
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void login(final PlayerLoginEvent event)
  {
    CoreClient client = this._clientManager.Get(event.getPlayer().getName());
    

    if (Bukkit.getOnlinePlayers().length >= Bukkit.getServer().getMaxPlayers())
    {
      if (!client.GetRank().Has(Rank.ULTRA))
      {
        Bukkit.getScheduler().scheduleSyncDelayedTask(GetPlugin(), new Runnable()
        {
          public void run()
          {
            HubManager.this._portal.SendPlayerToServer(event.getPlayer(), "Lobby");
          }
          
        });
        event.allow();
      }
    }
  }
  
  @EventHandler(priority=EventPriority.LOW)
  public void PlayerJoin(PlayerJoinEvent event)
  {
    try
    {
      Player player = event.getPlayer();
      

      player.setGameMode(org.bukkit.GameMode.SURVIVAL);
      

      event.setJoinMessage(null);
      

      player.teleport(GetSpawn());
      

      player.setAllowFlight(true);
      
      mineplex.core.common.util.UtilInv.Clear(player);
      

      Scoreboard board = Bukkit.getScoreboardManager().getNewScoreboard();
      player.setScoreboard(board);
      this._scoreboards.put(player, board);
      

      Objective obj = board.registerNewObjective(C.Bold + "Player Data", "dummy");
      obj.setDisplaySlot(DisplaySlot.SIDEBAR);
      

      for (Rank rank : Rank.values())
      {
        if (rank != Rank.ALL) {
          board.registerNewTeam(rank.Name).setPrefix(rank.GetTag(true, true) + ChatColor.RESET + " ");
        } else {
          board.registerNewTeam(rank.Name).setPrefix("");
        }
      }
      for (Player otherPlayer : Bukkit.getOnlinePlayers())
      {
        if (this._clientManager.Get(otherPlayer) != null)
        {

          String rankName = this._clientManager.Get(player).GetRank().Name;
          String otherRankName = this._clientManager.Get(otherPlayer).GetRank().Name;
          
          if ((!this._clientManager.Get(player).GetRank().Has(Rank.ULTRA)) && (this._donationManager.Get(player.getName()).OwnsUltraPackage()))
          {
            rankName = Rank.ULTRA.Name;
          }
          
          if ((!this._clientManager.Get(otherPlayer).GetRank().Has(Rank.ULTRA)) && (this._donationManager.Get(otherPlayer.getName()).OwnsUltraPackage()))
          {
            otherRankName = Rank.ULTRA.Name;
          }
          

          board.getTeam(otherRankName).addPlayer(otherPlayer);
          

          otherPlayer.getScoreboard().getTeam(rankName).addPlayer(player);
        }
      }
    }
    catch (Exception ex)
    {
      Logger.Instance.log(ex);
      System.out.println("[HubManager] Player Join exception");
      throw ex;
    }
  }
  










































  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event)
  {
    event.setQuitMessage(null);
    
    event.getPlayer().leaveVehicle();
    event.getPlayer().eject();
    
    for (Player player : UtilServer.getPlayers()) {
      player.getScoreboard().resetScores(event.getPlayer().getName());
    }
    this._scoreboards.remove(event.getPlayer());
    
    this._portalTime.remove(event.getPlayer().getName());
  }
  
  @EventHandler
  public void PlayerChat(AsyncPlayerChatEvent event)
  {
    if (event.isCancelled()) {
      return;
    }
    Player player = event.getPlayer();
    
    Rank rank = GetClients().Get(player).GetRank();
    
    boolean ownsUltra = this._donationManager.Get(player.getName()).OwnsUltraPackage();
    

    String rankStr = "";
    if (rank != Rank.ALL) {
      rankStr = rank.GetTag(true, true) + " ";
    }
    if ((ownsUltra) && (!rank.Has(Rank.ULTRA))) {
      rankStr = Rank.ULTRA.GetTag(true, true) + " ";
    }
    Player other;
    if (event.getMessage().charAt(0) == '@')
    {
      mineplex.hub.party.Party party = this._partyManager.GetParty(player);
      if (party != null)
      {
        event.getRecipients().clear();
        
        event.setMessage(event.getMessage().substring(1, event.getMessage().length()));
        event.setFormat(C.cDPurple + C.Bold + "Party " + C.cWhite + C.Bold + "%1$s " + C.cPurple + "%2$s");
        
        for (Iterator localIterator = party.GetPlayers().iterator(); localIterator.hasNext();) { name = (String)localIterator.next();
          
          other = UtilPlayer.searchExact(name);
          
          if (other != null) {
            event.getRecipients().add(other);
          }
        }
      }
      else {
        UtilPlayer.message(player, F.main("Party", "You are not in a Party."));
        event.setCancelled(true);
      }
      
      return;
    }
    

    String str1 = (other = UtilServer.getPlayers()).length; for (String name = 0; name < str1; name++) { Player other = other[name];
      
      if (this._tutorialManager.InTutorial(other))
      {
        event.getRecipients().remove(other);
      }
      else
      {
        event.setMessage(event.getMessage());
        event.setFormat(rankStr + C.cYellow + "%1$s " + C.cWhite + "%2$s");
      }
    }
  }
  
  @EventHandler
  public void Damage(EntityDamageEvent event)
  {
    if (event.getCause() == org.bukkit.event.entity.EntityDamageEvent.DamageCause.VOID)
      if ((event.getEntity() instanceof Player))
      {
        event.getEntity().eject();
        event.getEntity().leaveVehicle();
        event.getEntity().teleport(GetSpawn());
      }
      else
      {
        event.getEntity().remove();
      }
    event.setCancelled(true);
  }
  

  @EventHandler
  public void FoodHealthUpdate(UpdateEvent event)
  {
    if (event.getType() != UpdateType.SLOW) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      player.setHealth(20.0D);
      player.setFoodLevel(20);
    }
  }
  
  @EventHandler
  public void InventoryCancel(InventoryClickEvent event)
  {
    if (((event.getWhoClicked() instanceof Player)) && (((Player)event.getWhoClicked()).getGameMode() != org.bukkit.GameMode.CREATIVE)) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void UpdateScoreboard(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK) {
      return;
    }
    this._scoreboardTick = ((this._scoreboardTick + 1) % 3);
    
    if (this._scoreboardTick != 0) {
      return;
    }
    int bestPig = 0;
    for (Player player : UtilServer.getPlayers())
    {
      if (player.getVehicle() == null)
      {

        int count = 0;
        
        Entity ent = player;
        while (ent.getPassenger() != null)
        {
          ent = ent.getPassenger();
          count++;
        }
        
        if (count > bestPig)
        {
          this._pigStacker = player.getName();
          bestPig = count;
        }
      } }
    if (bestPig == 0)
    {
      this._pigStacker = "0 - Nobody";
    }
    else
    {
      this._pigStacker = (bestPig + " - " + this._pigStacker);
      
      if (this._pigStacker.length() > 16) {
        this._pigStacker = this._pigStacker.substring(0, 16);
      }
    }
    for (Player player : UtilServer.getPlayers())
    {

      if (this._partyManager.GetParty(player) == null)
      {


        if (!player.getScoreboard().equals(this._scoreboards.get(player))) {
          player.setScoreboard((Scoreboard)this._scoreboards.get(player));
        }
        
        Objective obj = player.getScoreboard().getObjective(DisplaySlot.SIDEBAR);
        

        obj.setDisplayName(C.cWhite + C.Bold + ((HubClient)Get(player)).GetScoreboardText());
        
        int line = 15;
        
        obj.getScore(C.cGreen + C.Bold + "Gems").setScore(line--);
        

        player.getScoreboard().resetScores(((HubClient)Get(player.getName())).GetLastGemCount());
        
        obj.getScore(GetDonation().Get(player.getName()).GetGems()).setScore(line--);
        
        ((HubClient)Get(player.getName())).SetLastGemCount(GetDonation().Get(player.getName()).GetGems());
        

        obj.getScore(" ").setScore(line--);
        








        obj.getScore(C.cGray + C.Bold + "Stacker").setScore(line--);
        player.getScoreboard().resetScores(((HubClient)Get(player)).BestPig);
        ((HubClient)Get(player)).BestPig = this._pigStacker;
        obj.getScore(((HubClient)Get(player)).BestPig).setScore(line--);
        

        obj.getScore("  ").setScore(line--);
        

        if (GetClients().Get(player).GetRank().Has(Rank.HERO))
        {
          obj.getScore(C.cPurple + C.Bold + "Hero Rank").setScore(line--);
          
          player.getScoreboard().resetScores(((HubClient)Get(player)).GetUltraText(false));
          obj.getScore(((HubClient)Get(player)).GetUltraText(true)).setScore(line--);
        }
        else if (GetClients().Get(player).GetRank().Has(Rank.ULTRA))
        {
          obj.getScore(C.cAqua + C.Bold + "Ultra Rank").setScore(line--);
          
          player.getScoreboard().resetScores(((HubClient)Get(player)).GetUltraText(false));
          obj.getScore(((HubClient)Get(player)).GetUltraText(true)).setScore(line--);
        }
        else
        {
          obj.getScore(C.cRed + C.Bold + "No Rank").setScore(line--);
          
          player.getScoreboard().resetScores(((HubClient)Get(player)).GetPurchaseText(false));
          obj.getScore(((HubClient)Get(player)).GetPurchaseText(true)).setScore(line--);
        }
        

        obj.getScore("   ").setScore(line--);
        

        obj.getScore(C.cGold + C.Bold + "Online Staff").setScore(line--);
        String staff = "";
        for (Player other : UtilServer.getPlayers())
        {
          Rank rank = GetClients().Get(other).GetRank();
          
          if (rank.Has(Rank.HELPER))
          {

            staff = staff + other.getName() + "   "; }
        }
        if (staff.length() == 0) {
          staff = "None";
        }
        player.getScoreboard().resetScores(((HubClient)Get(player)).GetStaffText(false));
        ((HubClient)Get(player)).StaffString = staff;
        obj.getScore(((HubClient)Get(player)).GetStaffText(true)).setScore(line--);
        

        obj.getScore("    ").setScore(line--);
        

        obj.getScore(C.cYellow + C.Bold + "Website").setScore(line--);
        obj.getScore("www.mineplex.com").setScore(line--);
        obj.getScore("----------------").setScore(line--);
      }
    }
  }
  

  protected HubClient AddPlayer(String player)
  {
    return new HubClient(player);
  }
  
  public BlockRestore GetBlockRestore()
  {
    return this._blockRestore;
  }
  
  public CoreClientManager GetClients()
  {
    return this._clientManager;
  }
  
  public ConditionManager GetCondition()
  {
    return this._conditionManager;
  }
  
  public DonationManager GetDonation()
  {
    return this._donationManager;
  }
  
  public DisguiseManager GetDisguise()
  {
    return this._disguiseManager;
  }
  
  public GadgetManager GetGadget()
  {
    return this._gadgetManager;
  }
  
  public MountManager GetMount()
  {
    return this._mountManager;
  }
  
  public ParkourManager GetParkour()
  {
    return this._parkour;
  }
  
  public Location GetSpawn()
  {
    return this._spawn.clone();
  }
  
  public TutorialManager GetTutorial()
  {
    return this._tutorialManager;
  }
  
  public StatsManager GetStats()
  {
    return this._statsManager;
  }
  
  public VisibilityManager GetVisibility()
  {
    return this._visibilityManager;
  }
  
  public void SetPortalDelay(Entity ent)
  {
    if ((ent instanceof Player)) {
      this._portalTime.put(((Player)ent).getName(), Long.valueOf(System.currentTimeMillis()));
    }
  }
  
  public boolean CanPortal(Player player)
  {
    if ((player.getVehicle() != null) || (player.getPassenger() != null)) {
      return false;
    }
    
    if (!this._portalTime.containsKey(player.getName())) {
      return true;
    }
    return mineplex.core.common.util.UtilTime.elapsed(((Long)this._portalTime.get(player.getName())).longValue(), 5000L);
  }
  
  @EventHandler
  public void HeartDisplay(UpdateEvent event)
  {
    if (event.getType() != UpdateType.FASTER) {
      return;
    }
    for (Player player : UtilServer.getPlayers())
    {
      if ((player.isOp()) || (player.getName().equals("MonsieurApple")))
      {

        for (Player other : UtilServer.getPlayers())
        {
          if (player.getName().equalsIgnoreCase("Chiss")) {
            UtilParticle.PlayParticle(other, UtilParticle.ParticleType.HEART, player.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.5F, 0.25F, 0.0F, 1);
          }
          else if (player.getName().equalsIgnoreCase("defek7")) {
            UtilParticle.PlayParticle(other, UtilParticle.ParticleType.FIREWORKS_SPARK, player.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.5F, 0.25F, 0.0F, 2);
          }
          else if (player.getName().equalsIgnoreCase("Spu_")) {
            UtilParticle.PlayParticle(other, UtilParticle.ParticleType.FLAME, player.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.5F, 0.25F, 0.0F, 2);
          }
          else if (player.getName().equalsIgnoreCase("sterling_"))
            UtilParticle.PlayParticle(other, UtilParticle.ParticleType.WITCH_MAGIC, player.getLocation().add(0.0D, 1.0D, 0.0D), 0.25F, 0.5F, 0.25F, 0.0F, 2);
        }
      }
    }
  }
  
  @EventHandler
  public void SkillTrigger(SkillTriggerEvent event) {
    event.SetCancelled(true);
  }
  
  @EventHandler
  public void ItemTrigger(ItemTriggerEvent event)
  {
    event.SetCancelled(true);
  }
}
