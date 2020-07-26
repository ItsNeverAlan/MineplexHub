package mineplex.hub.server.ui;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import mineplex.core.account.CoreClient;
import mineplex.core.account.CoreClientManager;
import mineplex.core.common.Rank;
import mineplex.core.common.util.C;
import mineplex.core.donation.DonationManager;
import mineplex.core.shop.item.ShopItem;
import mineplex.core.shop.page.ShopPageBase;
import mineplex.core.status.ServerStatusManager;
import mineplex.hub.server.LobbySorter;
import mineplex.hub.server.ServerInfo;
import mineplex.hub.server.ServerManager;
import mineplex.hub.server.ui.button.JoinServerButton;
import org.bukkit.ChatColor;
import org.bukkit.Material;
import org.bukkit.entity.Player;

public class LobbyMenu extends ShopPageBase<ServerManager, LobbyShop> implements IServerPage
{
  private String _serverGroup;
  
  public LobbyMenu(ServerManager plugin, LobbyShop lobbyShop, CoreClientManager clientManager, DonationManager donationManager, String name, Player player, String serverGroup)
  {
    super(plugin, lobbyShop, clientManager, donationManager, name, player, 54);
    
    this._serverGroup = serverGroup;
    
    BuildPage();
  }
  

  protected void BuildPage()
  {
    List<ServerInfo> serverList = new ArrayList(((ServerManager)this.Plugin).GetServerList(this._serverGroup));
    
    try
    {
      Collections.sort(serverList, new LobbySorter());
    }
    catch (Exception exception)
    {
      exception.printStackTrace();
    }
    
    int slot = 0;
    String openFull = ChatColor.RESET + C.Line + "Get Ultra to join full servers!";
    String openFullUltra = ChatColor.RESET + C.Line + "Click to join!";
    
    for (ServerInfo serverInfo : serverList)
    {
      Material status = Material.IRON_BLOCK;
      List<String> lore = new ArrayList();
      
      if (slot >= 54) {
        break;
      }
      if (serverInfo.Name.equalsIgnoreCase(((ServerManager)this.Plugin).getStatusManager().getCurrentServerName())) {
        status = Material.EMERALD_BLOCK;
      }
      lore.add(ChatColor.RESET);
      lore.add(ChatColor.RESET + ChatColor.YELLOW + "Players: " + ChatColor.WHITE + serverInfo.CurrentPlayers + "/" + serverInfo.MaxPlayers);
      lore.add(ChatColor.RESET);
      
      if (serverInfo.CurrentPlayers >= serverInfo.MaxPlayers)
      {
        if (!this.Client.GetRank().Has(Rank.ULTRA)) {
          lore.add(openFull);
        } else {
          lore.add(openFullUltra);
        }
      } else {
        lore.add(ChatColor.RESET + C.Line + "Click to join!");
      }
      if (status != Material.EMERALD_BLOCK) {
        AddButton(slot, new ShopItem(status, ChatColor.UNDERLINE + ChatColor.BOLD + ChatColor.WHITE + "Server " + serverInfo.Name.substring(serverInfo.Name.indexOf('-') + 1), (String[])lore.toArray(new String[lore.size()]), Integer.parseInt(serverInfo.Name.substring(serverInfo.Name.indexOf('-') + 1)), false), new JoinServerButton(this, serverInfo));
      } else {
        AddItem(slot, new ShopItem(status, ChatColor.UNDERLINE + ChatColor.BOLD + ChatColor.WHITE + "Server " + serverInfo.Name.substring(serverInfo.Name.indexOf('-') + 1), (String[])lore.toArray(new String[lore.size()]), Integer.parseInt(serverInfo.Name.substring(serverInfo.Name.indexOf('-') + 1)), false));
      }
      slot++;
    }
    
    while (slot < 54)
    {
      setItem(slot, null);
      slot++;
    }
  }
  
  public void Update()
  {
    BuildPage();
  }
  

  public void SelectServer(Player player, ServerInfo serverInfo)
  {
    int slots = ((ServerManager)this.Plugin).GetRequiredSlots(player, serverInfo.ServerType);
    
    if (serverInfo.MaxPlayers - serverInfo.CurrentPlayers < slots)
    {
      PlayDenySound(player);
      return;
    }
    
    ((ServerManager)this.Plugin).SelectServer(player, serverInfo);
  }
}
