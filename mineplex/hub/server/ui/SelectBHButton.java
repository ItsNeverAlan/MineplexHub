package mineplex.hub.server.ui;

import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class SelectBHButton
  implements IButton
{
  private ServerGameMenu _menu;
  
  public SelectBHButton(ServerGameMenu menu)
  {
    this._menu = menu;
  }
  

  public void ClickedLeft(Player player)
  {
    this._menu.OpenBH(player);
  }
  

  public void ClickedRight(Player player)
  {
    this._menu.OpenBH(player);
  }
}
