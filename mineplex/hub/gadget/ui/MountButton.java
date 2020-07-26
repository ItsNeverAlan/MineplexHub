package mineplex.hub.gadget.ui;

import mineplex.core.shop.item.IButton;
import mineplex.hub.mount.Mount;
import org.bukkit.entity.Player;


public class MountButton
  implements IButton
{
  private Mount _mount;
  private GadgetPage _page;
  
  public MountButton(Mount mount, GadgetPage page)
  {
    this._mount = mount;
    this._page = page;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.PurchaseMount(player, this._mount);
  }
  

  public void ClickedRight(Player player)
  {
    this._page.PurchaseMount(player, this._mount);
  }
}
