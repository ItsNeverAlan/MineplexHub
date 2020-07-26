package mineplex.hub.server.ui;

import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class SelectSGButton
  implements IButton
{
  private ServerGameMenu _menu;
  
  public SelectSGButton(ServerGameMenu menu)
  {
    this._menu = menu;
  }
  

  public void ClickedLeft(Player player)
  {
    this._menu.OpenSG(player);
  }
  

  public void ClickedRight(Player player)
  {
    ClickedLeft(player);
  }
}
