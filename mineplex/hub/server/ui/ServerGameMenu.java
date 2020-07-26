package mineplex.hub.server.ui;

import java.util.ArrayList;
import java.util.List;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.util.C;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.itemstack.ItemStackFactory;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.hub.server.ServerManager;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;
import org.bukkit.inventory.ItemStack;

public class ServerGameMenu
  extends ShopPageBase<ServerManager, QuickShop>
{
  private List<ItemStack> _superSmashCycle = new ArrayList();
  private List<ItemStack> _minigameCycle = new ArrayList();
  private List<ItemStack> _turfFortsCycle = new ArrayList();
  
  private int _ssmIndex;
  private int _minigameIndex;
  private int _turfFortsIndex;
  
  public ServerGameMenu(ServerManager plugin, QuickShop quickShop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player)
  {
    super(plugin, quickShop, clientManager, donationManager, name, player, 9);
    
    createSuperSmashCycle();
    createMinigameCycle();
    createTurfFortsCycle();
    
    BuildPage();
  }
  

  protected void BuildPage()
  {
    setItem(1, (ItemStack)this._superSmashCycle.get(this._ssmIndex));
    setItem(2, (ItemStack)this._minigameCycle.get(this._minigameIndex));
    setItem(3, ItemStackFactory.Instance.CreateStack(Material.IRON_SWORD.getId(), (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Survival Games " + C.cGray + "Last Man Standing", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Search for chests to find loot and ", 
      ChatColor.RESET + "fight others to be the last man standing. ", 
      ChatColor.RESET + "Beware of the deep freeze!" }));
    
    setItem(4, ItemStackFactory.Instance.CreateStack(Material.IRON_PICKAXE.getId(), (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "The Bridges " + C.cGray + "4 Team Survival", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "4 Teams get 10 minutes to prepare.", 
      ChatColor.RESET + "Then the bridges drop, and all hell", 
      ChatColor.RESET + "breaks loose as you battle to the", 
      ChatColor.RESET + "death with the other teams." }));
    
    setItem(5, ItemStackFactory.Instance.CreateStack(Material.ANVIL.getId(), (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Block Hunt " + C.cGray + "Cat and Mouse", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Hide as blocks/animals, upgrade your ", 
      ChatColor.RESET + "weapon and fight to survive against", 
      ChatColor.RESET + "the Hunters!" }));
    

    setItem(6, ItemStackFactory.Instance.CreateStack(Material.BEACON.getId(), (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Champions " + C.cGray + "Team Game", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Customize one of five exciting champions", 
      ChatColor.RESET + "and battle with the opposing team for the", 
      ChatColor.RESET + "control points on the map." }));
    

    this.ButtonMap.put(Integer.valueOf(1), new SelectSSMButton(this));
    this.ButtonMap.put(Integer.valueOf(2), new SelectMINButton(this));
    this.ButtonMap.put(Integer.valueOf(3), new SelectSGButton(this));
    this.ButtonMap.put(Integer.valueOf(4), new SelectBRButton(this));
    this.ButtonMap.put(Integer.valueOf(5), new SelectBHButton(this));
    this.ButtonMap.put(Integer.valueOf(6), new SelectDOMButton(this));
  }
  
  private void createTurfFortsCycle()
  {
    this._turfFortsCycle.add(ItemStackFactory.Instance.CreateStack(Material.WOOL.getId(), (byte)11, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Turf Forts " + C.cGray + "Arcade Minigame", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Use your archery skills to kill your", 
      ChatColor.RESET + "enemies and take over their turf!" }));
    

    this._turfFortsCycle.add(ItemStackFactory.Instance.CreateStack(Material.WOOL.getId(), (byte)14, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Turf Forts " + C.cGray + "Arcade Minigame", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Use your archery skills to kill your", 
      ChatColor.RESET + "enemies and take over their turf!" }));
  }
  

  private void createMinigameCycle()
  {
    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(98, (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(Material.GOLD_BOOTS.getId(), (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(122, (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(Material.BOW, (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(Material.LEATHER_BOOTS.getId(), (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(Material.MILK_BUCKET.getId(), (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(Material.DIAMOND_BARDING.getId(), (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(159, (byte)14, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(309, (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(319, (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Bacon Brawl", 
      ChatColor.RESET + "Squid Sauce" }));
    

    this._minigameCycle.add(ItemStackFactory.Instance.CreateStack(351, (byte)0, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Arcade " + C.cGray + "Mixed Games", 
      new String[] {
      ChatColor.RESET, 
      ChatColor.RESET + "Play all of these fun minigames:", 
      ChatColor.RESET, 
      ChatColor.RESET + "Super Spleef", 
      ChatColor.RESET + "Runner", 
      ChatColor.RESET + "Dragons", 
      ChatColor.RESET + "One in the Quiver", 
      ChatColor.RESET + "Dragon Escape", 
      ChatColor.RESET + "Milk the Cow", 
      ChatColor.RESET + "Super Paintball", 
      ChatColor.RESET + "Turf Forts", 
      ChatColor.RESET + "Death Tag", 
      ChatColor.RESET + "Bacon Brawl", 
      ChatColor.RESET + C.Bold + ChatColor.GREEN + "Squid Sauce" }));
  }
  

  private void createSuperSmashCycle()
  {
    String[] desc = 
      {
      ChatColor.RESET, 
      ChatColor.RESET + "Pick from a selection of monsters,", 
      ChatColor.RESET + "then battle other players to the ", 
      ChatColor.RESET + "death with your monsters skills!" };
    

    this._superSmashCycle.add(ItemStackFactory.Instance.CreateStack(397, (byte)1, 1, ChatColor.RESET + C.Bold + ChatColor.YELLOW + "Super Smash Mobs", desc));
  }
  
  public void Update()
  {
    this._ssmIndex += 1;
    this._minigameIndex += 1;
    this._turfFortsIndex += 1;
    
    if (this._ssmIndex >= this._superSmashCycle.size()) {
      this._ssmIndex = 0;
    }
    if (this._minigameIndex >= this._minigameCycle.size()) {
      this._minigameIndex = 0;
    }
    if (this._turfFortsIndex >= this._turfFortsCycle.size()) {
      this._turfFortsIndex = 0;
    }
    BuildPage();
  }
  
  public void OpenMIN(Player player)
  {
    ((ServerManager)this.Plugin).getMixedArcadeShop().attemptShopOpen(player);
  }
  
  public void OpenSSM(Player player)
  {
    ((ServerManager)this.Plugin).getSuperSmashMobsShop().attemptShopOpen(player);
  }
  
  public void OpenDOM(Player player)
  {
    ((ServerManager)this.Plugin).getDominateShop().attemptShopOpen(player);
  }
  
  public void OpenBR(Player player)
  {
    ((ServerManager)this.Plugin).getBridgesShop().attemptShopOpen(player);
  }
  
  public void OpenBH(Player player)
  {
    ((ServerManager)this.Plugin).getBlockHuntShop().attemptShopOpen(player);
  }
  
  public void OpenSG(Player player)
  {
    ((ServerManager)this.Plugin).getSurvivalGamesShop().attemptShopOpen(player);
  }
}
