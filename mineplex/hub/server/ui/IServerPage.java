package mineplex.hub.server.ui;

import mineplex.hub.server.ServerInfo;
import org.bukkit.entity.Player;

public abstract interface IServerPage
{
  public abstract void SelectServer(Player paramPlayer, ServerInfo paramServerInfo);
}
