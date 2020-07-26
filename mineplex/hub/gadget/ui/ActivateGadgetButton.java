package mineplex.hub.gadget.ui;

import mineplex.core.shop.item.IButton;
import mineplex.hub.gadget.types.Gadget;
import org.bukkit.entity.Player;

public class ActivateGadgetButton
  implements IButton
{
  private Gadget _gadget;
  private GadgetPage _page;
  
  public ActivateGadgetButton(Gadget gadget, GadgetPage page)
  {
    this._gadget = gadget;
    this._page = page;
  }
  

  public void ClickedLeft(Player player)
  {
    this._page.ActivateGadget(player, this._gadget);
  }
  

  public void ClickedRight(Player player)
  {
    this._page.ActivateGadget(player, this._gadget);
  }
}
