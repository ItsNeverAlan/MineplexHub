package mineplex.hub.gadget.ui;

import mineplex.core.shop.item.IButton;
import mineplex.hub.mount.Mount;
import org.bukkit.entity.Player;


public class DeactivateMountButton
  implements IButton
{
  private Mount _mount;
  private GadgetPage _page;
  
  public DeactivateMountButton(Mount mount, GadgetPage page)
  {
    this._mount = mount;
    this._page = page;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.DeactivateMount(player, this._mount);
  }
  

  public void ClickedRight(Player player)
  {
    this._page.DeactivateMount(player, this._mount);
  }
}
