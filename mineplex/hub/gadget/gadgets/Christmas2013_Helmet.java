package mineplex.hub.gadget.gadgets;

import mineplex.core.common.util.C;
import mineplex.core.disguise.DisguiseManager;
import mineplex.core.disguise.disguises.DisguiseSnowman;
import mineplex.hub.HubManager;
import mineplex.hub.gadget.GadgetManager;
import mineplex.hub.gadget.types.ArmorGadget;
import mineplex.hub.gadget.types.ArmorGadget.ArmorSlot;
import org.bukkit.Material;
import org.bukkit.entity.Player;










public class Christmas2013_Helmet
  extends ArmorGadget
{
  public Christmas2013_Helmet(GadgetManager manager)
  {
    super(manager, "Snowmans Head", new String[] {C.cWhite + "Transforms the wearer into", C.cWhite + "a Christmas Snowman!", "", C.cYellow + "Earned by defeating the Pumpkin King", C.cYellow + "in the 2013 Christmas Chaos Event." }, -1, ArmorGadget.ArmorSlot.Helmet, Material.SNOW_BLOCK, (byte)0);
  }
  


  public void Enable(Player player)
  {
    ApplyArmor(player);
    
    DisguiseSnowman disguise = new DisguiseSnowman(player);
    disguise.SetName(player.getName());
    disguise.SetCustomNameVisible(true);
    this.Manager.Manager.GetDisguise().disguise(disguise);
  }
  

  public void Disable(Player player)
  {
    RemoveArmor(player);
    this.Manager.Manager.GetDisguise().undisguise(player);
  }
}
