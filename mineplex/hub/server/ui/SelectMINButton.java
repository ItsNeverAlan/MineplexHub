package mineplex.hub.server.ui;

import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class SelectMINButton
  implements IButton
{
  private ServerGameMenu _menu;
  
  public SelectMINButton(ServerGameMenu menu)
  {
    this._menu = menu;
  }
  

  public void ClickedLeft(Player player)
  {
    this._menu.OpenMIN(player);
  }
  

  public void ClickedRight(Player player)
  {
    ClickedLeft(player);
  }
}
