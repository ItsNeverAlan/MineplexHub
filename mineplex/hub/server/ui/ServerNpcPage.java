package mineplex.hub.server.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.common.util.NautHashMap;
import mineplex.core.donation.DonationManager;
import mineplex.core.donation.Donor;
import mineplex.core.logger.Logger;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.hub.server.ServerInfo;
import mineplex.hub.server.ServerManager;
import mineplex.hub.server.ServerSorter;
import mineplex.hub.server.ui.button.JoinServerButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class ServerNpcPage extends ShopPageBase<ServerManager, ServerNpcShop> implements IServerPage
{
  private String _serverNpcKey;
  
  public ServerNpcPage(ServerManager plugin, ServerNpcShop shop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player, String serverNpcKey)
  {
    super(plugin, shop, clientManager, donationManager, name, player, 54);
    
    this._serverNpcKey = serverNpcKey;
    
    BuildPage();
  }
  

  protected void BuildPage()
  {
    List<ServerInfo> serverList = new ArrayList(((ServerManager)this.Plugin).GetServerList(this._serverNpcKey));
    
    int slots = 1;
    
    if (serverList.size() > 0)
    {
      slots = ((ServerManager)this.Plugin).GetRequiredSlots(this.Player, ((ServerInfo)serverList.get(0)).ServerType);
    }
    
    try
    {
      Collections.sort(serverList, new ServerSorter(slots));
    }
    catch (Exception exception)
    {
      Logger.Instance.log(exception);
      exception.printStackTrace();
    }
    
    int slot = 10;
    int greenCount = 0;
    int yellowCount = 0;
    String openFull = ChatColor.RESET + C.Line + "Get Ultra to join full servers!";
    String beta = ChatColor.RESET + C.Line + "Get Ultra to join Beta servers!";
    String openFullUltra = ChatColor.RESET + C.Line + "Click to join!";
    
    for (ServerInfo serverInfo : serverList)
    {
      boolean ownsUltraPackage = (this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage(serverInfo.ServerType + " ULTRA")) || (this.Client.GetRank().Has(Rank.ULTRA));
      
      String inProgress = ChatColor.RESET + C.Line + "Click to spectate";
      String inProgressLine2 = ChatColor.RESET + C.Line + "and wait for next game!";
      
      Material status = Material.REDSTONE_BLOCK;
      List<String> lore = new ArrayList();
      
      if (slot >= 53) {
        break;
      }
      if (((serverInfo.MOTD.contains("Starting")) || (serverInfo.MOTD.contains("Recruiting")) || (serverInfo.MOTD.contains("Waiting")) || (serverInfo.MOTD.contains("Cup"))) && (slot < 15) && (serverInfo.MaxPlayers - serverInfo.CurrentPlayers >= slots))
      {
        if ((greenCount > 0) && (serverInfo.MaxPlayers == serverInfo.CurrentPlayers)) {
          continue;
        }
        slot++;
        status = Material.EMERALD_BLOCK;
        lore.add(ChatColor.RESET);
        
        if (serverInfo.Game != null) {
          lore.add(ChatColor.RESET + ChatColor.YELLOW + "Game: " + ChatColor.WHITE + serverInfo.Game);
        }
        if ((serverInfo.Map != null) && (!serverInfo.ServerType.equalsIgnoreCase("Competitive"))) {
          lore.add(ChatColor.RESET + ChatColor.YELLOW + "Map: " + ChatColor.WHITE + serverInfo.Map);
        }
        lore.add(ChatColor.RESET + ChatColor.YELLOW + "Players: " + ChatColor.WHITE + serverInfo.CurrentPlayers + "/" + serverInfo.MaxPlayers);
        lore.add(ChatColor.RESET);
        lore.add(ChatColor.RESET + serverInfo.MOTD);
        
        if ((serverInfo.Name.contains("BETA")) && (!ownsUltraPackage))
        {
          lore.add(beta);


        }
        else if (serverInfo.CurrentPlayers >= serverInfo.MaxPlayers)
        {
          if (serverInfo.Game.equalsIgnoreCase("Survival Games"))
          {
            lore.add(ChatColor.RESET + C.Line + "Full Survival Games servers");
            lore.add(ChatColor.RESET + C.Line + "cannot be joined.");


          }
          else if (!ownsUltraPackage) {
            lore.add(openFull);
          } else {
            lore.add(openFullUltra);
          }
          
        }
        else {
          lore.add(ChatColor.RESET + C.Line + "Click to join!");
        }
        

        greenCount++;
      } else {
        if ((!serverInfo.MOTD.contains("In")) && (!serverInfo.MOTD.contains("Restarting")))
          continue;
        if (slot <= 15) {
          slot = 27;
        } else {
          slot++;
        }
        status = Material.GOLD_BLOCK;
        lore.add(ChatColor.RESET);
        
        if (serverInfo.Game != null) {
          lore.add(ChatColor.RESET + ChatColor.YELLOW + "Game: " + ChatColor.WHITE + serverInfo.Game);
        }
        if ((serverInfo.Map != null) && (!serverInfo.ServerType.equalsIgnoreCase("Competitive"))) {
          lore.add(ChatColor.RESET + ChatColor.YELLOW + "Map: " + ChatColor.WHITE + serverInfo.Map);
        }
        lore.add(ChatColor.RESET + ChatColor.YELLOW + "Players: " + ChatColor.WHITE + serverInfo.CurrentPlayers + "/" + serverInfo.MaxPlayers);
        lore.add(ChatColor.RESET);
        lore.add(ChatColor.RESET + serverInfo.MOTD);
        
        if (serverInfo.MOTD.contains("Restarting"))
        {
          lore.add(ChatColor.RESET + C.Line + "This server will be open shortly!");
          status = Material.IRON_BLOCK;
        }
        else if (serverInfo.CurrentPlayers >= serverInfo.MaxPlayers)
        {
          if ((!this.Client.GetRank().Has(Rank.ULTRA)) || (ownsUltraPackage)) {
            lore.add(openFull);
          }
          else {
            lore.add(inProgress);
            
            if (inProgressLine2 != null) {
              lore.add(inProgressLine2);
            }
          }
        }
        else {
          lore.add(inProgress);
          
          if (inProgressLine2 != null) {
            lore.add(inProgressLine2);
          }
        }
        yellowCount++;
      }
      


      AddButton(slot, new ShopItem(status, ChatColor.UNDERLINE + ChatColor.BOLD + ChatColor.WHITE + "Server " + serverInfo.Name.substring(serverInfo.Name.indexOf('-') + 1), (String[])lore.toArray(new String[lore.size()]), Math.max(1, serverInfo.CurrentPlayers), false), new JoinServerButton(this, serverInfo));
    }
    
    while (greenCount < 3)
    {
      setItem(9 + (greenCount + 1) * 2, null);
      greenCount++;
    }
    
    while (yellowCount < 18)
    {
      setItem(yellowCount + 27, null);
      yellowCount++;
    }
  }
  
  public void Update()
  {
    this.ButtonMap.clear();
    BuildPage();
  }
  
  public void SelectServer(Player player, ServerInfo serverInfo)
  {
    int slots = ((ServerManager)this.Plugin).GetRequiredSlots(player, serverInfo.ServerType);
    
    if (((serverInfo.Name.contains("BETA")) && (!this.Client.GetRank().Has(Rank.ULTRA))) || ((serverInfo.MaxPlayers - serverInfo.CurrentPlayers < slots) && (!this.DonationManager.Get(this.Player.getName()).OwnsUnknownPackage(serverInfo.ServerType + " ULTRA")) && (!this.Client.GetRank().Has(Rank.ULTRA))))
    {
      PlayDenySound(player);
      return;
    }
    
    ((ServerManager)this.Plugin).SelectServer(player, serverInfo);
  }
}
