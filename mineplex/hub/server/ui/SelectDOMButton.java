package mineplex.hub.server.ui;

import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class SelectDOMButton
  implements IButton
{
  private ServerGameMenu _menu;
  
  public SelectDOMButton(ServerGameMenu menu)
  {
    this._menu = menu;
  }
  

  public void ClickedLeft(Player player)
  {
    this._menu.OpenDOM(player);
  }
  

  public void ClickedRight(Player player)
  {
    ClickedLeft(player);
  }
}
