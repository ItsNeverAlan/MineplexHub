package mineplex.hub.server.ui;

import mineplex.core.shop.item.IButton;
import org.bukkit.entity.Player;

public class SelectSSMButton
  implements IButton
{
  private ServerGameMenu _menu;
  
  public SelectSSMButton(ServerGameMenu menu)
  {
    this._menu = menu;
  }
  

  public void ClickedLeft(Player player)
  {
    this._menu.OpenSSM(player);
  }
  

  public void ClickedRight(Player player)
  {
    ClickedLeft(player);
  }
}
