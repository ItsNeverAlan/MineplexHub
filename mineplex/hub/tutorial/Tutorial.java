package mineplex.hub.tutorial;

import java.util.ArrayList;
import java.util.HashMap;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.hub.HubManager;
import org.bukkit.ChatColor;
import org.bukkit.entity.Player;



public class Tutorial
  extends MiniPlugin
{
  protected String _name;
  protected int _gems;
  protected String _task;
  protected ArrayList<TutorialPhase> _phases = new ArrayList();
  protected HashMap<Player, TutorialData> _tute = new HashMap();
  
  protected String _main = ChatColor.RESET;
  protected String _elem = C.cYellow + C.Bold;
  
  public Tutorial(HubManager manager, String name, int gems, String task)
  {
    super(task, manager.GetPlugin());
    
    this._name = name;
    this._gems = gems;
    this._task = task;
  }
  
  public String GetTutName()
  {
    return this._name;
  }
  
  public int GetGems()
  {
    return this._gems;
  }
  
  public String GetTask()
  {
    return this._task;
  }
  
  public void BeginTutorial(Player player)
  {
    this._tute.put(player, new TutorialData(player, (TutorialPhase)this._phases.get(0)));
  }
  
  public boolean InTutorial(Player player)
  {
    return this._tute.containsKey(player);
  }
  
  public void EndTutorial(Player player)
  {
    this._tute.remove(player);
  }
  
  public HashMap<Player, TutorialData> GetTutorial()
  {
    return this._tute;
  }
  
  public ArrayList<TutorialPhase> GetPhases()
  {
    return this._phases;
  }
}
