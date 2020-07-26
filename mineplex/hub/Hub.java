

mineplex.core.INautilusPlugin
mineplex.core.account.CoreClientManager
mineplex.core.antihack.AntiHack
mineplex.core.blockrestore.BlockRestore
mineplex.core.chat.Chat
mineplex.core.command.CommandCenter
mineplex.core.creature.Creature
mineplex.core.disguise.DisguiseManager
mineplex.core.donation.DonationManager
mineplex.core.energy.Energy
mineplex.core.itemstack.ItemStackFactory
mineplex.core.logger.Logger
mineplex.core.memory.MemoryFix
mineplex.core.message.MessageManager
mineplex.core.monitor.LagMeter
mineplex.core.movement.Movement
mineplex.core.npc.NpcManager
mineplex.core.packethandler.PacketHandler
mineplex.core.pet.PetManager
mineplex.core.portal.Portal
mineplex.core.projectile.ProjectileManager
mineplex.core.punish.Punish
mineplex.core.recharge.Recharge
mineplex.core.spawn.Spawn
mineplex.core.status.ServerStatusManager
mineplex.core.task.TaskManager
mineplex.core.teleport.Teleport
mineplex.core.updater.FileUpdater
mineplex.hub.modules.StackerManager
mineplex.hub.party.PartyManager
mineplex.hub.server.ServerManager
mineplex.minecraft.game.classcombat.Class.ClassManager
mineplex.minecraft.game.classcombat.Condition.SkillConditionManager
mineplex.minecraft.game.classcombat.Skill.SkillFactory
mineplex.minecraft.game.classcombat.item.ItemFactory
mineplex.minecraft.game.classcombat.shop.ClassCombatShop
mineplex.minecraft.game.classcombat.shop.ClassShopManager
mineplex.minecraft.game.core.IRelation
mineplex.minecraft.game.core.combat.CombatManager
mineplex.minecraft.game.core.condition.ConditionManager
mineplex.minecraft.game.core.damage.DamageManager
mineplex.minecraft.game.core.fire.Fire
org.bukkit.Server
org.bukkit.configuration.file.FileConfiguration
org.bukkit.entity.Player
org.bukkit.plugin.PluginManager
org.bukkit.plugin.java.JavaPlugin
org.bukkit.scheduler.BukkitScheduler

Hub, 

  WEB_CONFIG = "webServer"
  

  onEnable
  
    getConfig()addDefaultWEB_CONFIG, "http://accounts.mineplex.com/");
    
    getConfig().set(this.WEB_CONFIG, getConfig().getString(this.WEB_CONFIG));
    saveConfig();
    
    Logger.initialize(this);
    

    CoreClientManager clientManager = CoreClientManager.Initialize(this, GetWebServerAddress());
    

    CommandCenter.Initialize(this, clientManager);
    ItemStackFactory.Initialize(this, false);
    Recharge.Initialize(this);
    Punish punish = new Punish(this, GetWebServerAddress(), clientManager);
    Portal portal = new Portal(this);
    AntiHack.Initialize(this, punish, portal);
    
    DonationManager donationManager = new DonationManager(this, GetWebServerAddress());
    


    Creature creature = new Creature(this);
    new MessageManager(this, clientManager);
    NpcManager npcManager = new NpcManager(this, creature);
    new PetManager(this, clientManager, donationManager, creature, GetWebServerAddress());
    

    PacketHandler packetHandler = new PacketHandler(this);
    PartyManager partyManager = new PartyManager(this, clientManager);
    HubManager hubManager = new HubManager(this, new BlockRestore(this), clientManager, donationManager, new ConditionManager(this), new DisguiseManager(this, packetHandler), new TaskManager(this, GetWebServerAddress()), portal, partyManager);
    ServerStatusManager serverStatusManager = new ServerStatusManager(this, new LagMeter(this, clientManager));
    new ServerManager(this, clientManager, donationManager, portal, partyManager, serverStatusManager, hubManager, new StackerManager(hubManager));
    new Chat(this, clientManager, serverStatusManager.getCurrentServerName());
    new MemoryFix(this);
    new FileUpdater(this, portal);
    
    CombatManager combatManager = new CombatManager(this);
    BlockRestore blockRestore = new BlockRestore(this);
    ProjectileManager throwManager = new ProjectileManager(this);
    SkillConditionManager conditionManager = new SkillConditionManager(this);
    
    DamageManager damage = new DamageManager(this, combatManager, npcManager, new DisguiseManager(this, packetHandler));
    Fire fire = new Fire(this, conditionManager, damage);
    Teleport teleport = new Teleport(this, clientManager, new Spawn(this));
    Energy energy = new Energy(this);
    
    SkillFactory skillManager = new SkillFactory(this, damage, this, combatManager, conditionManager, throwManager, blockRestore, fire, new Movement(this), teleport, energy, GetWebServerAddress());
    ClassManager classManager = new ClassManager(this, clientManager, donationManager, skillManager, GetWebServerAddress());
    ItemFactory itemFactory = new ItemFactory(this, blockRestore, classManager, conditionManager, damage, energy, fire, throwManager, GetWebServerAddress());
    
    ClassShopManager shopManager = new ClassShopManager(this, classManager, skillManager, itemFactory);
    
    new ClassCombatShop(shopManager, clientManager, donationManager, "Brute", classManager.GetClass("Brute"));
    new ClassCombatShop(shopManager, clientManager, donationManager, "Mage", classManager.GetClass("Mage"));
    new ClassCombatShop(shopManager, clientManager, donationManager, "Ranger", classManager.GetClass("Ranger"));
    new ClassCombatShop(shopManager, clientManager, donationManager, "Knight", classManager.GetClass("Knight"));
    new ClassCombatShop(shopManager, clientManager, donationManager, "Assassin", classManager.GetClass("Assassin"));
    

    getServer().getScheduler().scheduleSyncRepeatingTask(this, new mineplex.core.updater.Updater(this), 1L, 1L);
  }
  



  public void onDisable() {}
  


  public JavaPlugin GetPlugin()
  {
    return this;
  }
  

  public String GetWebServerAddress()
  {
    String webServerAddress = getConfig().getString(this.WEB_CONFIG);
    
    return webServerAddress;
  }
  

  public Server GetRealServer()
  {
    return getServer();
  }
  

  public PluginManager GetPluginManager()
  {
    return GetRealServer().getPluginManager();
  }
  

  public boolean CanHurt(Player a, Player b)
  {
    return false;
  }
  

  public boolean CanHurt(String a, String b)
  {
    return false;
  }
  

  public boolean IsSafe(Player a)
  {
    return true;
  }
}
