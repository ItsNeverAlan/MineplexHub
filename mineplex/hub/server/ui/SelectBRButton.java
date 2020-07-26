package mineplex.hub.server.ui;

import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class SelectBRButton
  implements IButton
{
  private ServerGameMenu _menu;
  
  public SelectBRButton(ServerGameMenu menu)
  {
    this._menu = menu;
  }
  

  public void ClickedLeft(Player player)
  {
    this._menu.OpenBR(player);
  }
  

  public void ClickedRight(Player player)
  {
    this._menu.OpenBR(player);
  }
}
