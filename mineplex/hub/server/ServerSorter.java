package mineplex.hub.server;

import java.util.Comparator;

public class ServerSorter implements Comparator<ServerInfo>
{
  private int _requiredSlots;
  
  public ServerSorter(int slots)
  {
    this._requiredSlots = slots;
  }
  
  public int compare(ServerInfo a, ServerInfo b)
  {
    if (b.MOTD.contains("Restarting")) {
      return -1;
    }
    if (a.MOTD.contains("Restarting")) {
      return 1;
    }
    if (((a.MOTD.contains("Recruiting")) || (a.MOTD.contains("Waiting")) || (a.MOTD.contains("Starting")) || (a.MOTD.contains("Cup"))) && (!b.MOTD.contains("Recruiting")) && (!b.MOTD.contains("Waiting")) && (!b.MOTD.contains("Starting")) && (!b.MOTD.contains("Cup"))) {
      return -1;
    }
    if (((b.MOTD.contains("Recruiting")) || (b.MOTD.contains("Waiting")) || (b.MOTD.contains("Starting")) || (b.MOTD.contains("Cup"))) && (!a.MOTD.contains("Recruiting")) && (!a.MOTD.contains("Waiting")) && (!a.MOTD.contains("Starting")) && (!a.MOTD.contains("Cup"))) {
      return 1;
    }
    if ((a.MaxPlayers - a.CurrentPlayers < this._requiredSlots) && (b.MaxPlayers - b.CurrentPlayers >= this._requiredSlots)) {
      return -1;
    }
    if ((b.MaxPlayers - b.CurrentPlayers < this._requiredSlots) && (a.MaxPlayers - a.CurrentPlayers >= this._requiredSlots)) {
      return 1;
    }
    if (a.CurrentPlayers > b.CurrentPlayers) {
      return -1;
    }
    if (b.CurrentPlayers > a.CurrentPlayers) {
      return 1;
    }
    if (Integer.parseInt(a.Name.split("-")[1]) < Integer.parseInt(b.Name.split("-")[1])) {
      return -1;
    }
    return 1;
  }
}
