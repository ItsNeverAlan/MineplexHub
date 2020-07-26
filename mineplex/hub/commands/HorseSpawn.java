package mineplex.hub.commands;

import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.hub.modules.AdminMountManager;
import org.bukkit.entity.Player;

public class HorseSpawn
  extends CommandBase<AdminMountManager>
{
  public HorseSpawn(AdminMountManager plugin)
  {
    super(plugin, Rank.OWNER, new String[] { "horse" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    ((AdminMountManager)this.Plugin).HorseCommand(caller, args);
  }
}
