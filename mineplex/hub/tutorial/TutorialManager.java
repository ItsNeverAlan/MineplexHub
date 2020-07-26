package mineplex.hub.tutorial;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.Set;
import mineplex.core.MiniPlugin;
import mineplex.core.common.util.C;
import mineplex.core.common.util.Callback;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.core.donation.DonationManager;
import mineplex.core.task.TaskManager;
import mineplex.core.updater.UpdateType;
import mineplex.core.updater.event.UpdateEvent;
import mineplex.hub.HubManager;
import mineplex.hub.modules.TextManager;
import mineplex.hub.tutorial.types.PartyTutorial;
import mineplex.hub.tutorial.types.WelcomeTutorial;
import org.bukkit.Sound;
import org.bukkit.entity.LivingEntity;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.player.PlayerInteractEntityEvent;
import org.bukkit.event.player.PlayerInteractEvent;
import org.bukkit.event.player.PlayerMoveEvent;
import org.bukkit.event.player.PlayerQuitEvent;


public class TutorialManager
  extends MiniPlugin
{
  private HashSet<Tutorial> _tutorials;
  protected DonationManager _donationManager;
  protected TaskManager _taskManager;
  
  public TutorialManager(HubManager manager, DonationManager donation, TaskManager task, TextManager text)
  {
    super("Tutorial Manager", manager.GetPlugin());
    
    this._taskManager = task;
    this._donationManager = donation;
    
    this._tutorials = new HashSet();
    
    this._tutorials.add(new WelcomeTutorial(manager, text));
    this._tutorials.add(new PartyTutorial(manager));
  }
  
  @EventHandler
  public void EntityInteract(PlayerInteractEntityEvent event)
  {
    if (InTutorial(event.getPlayer())) {
      return;
    }
    if (!(event.getRightClicked() instanceof LivingEntity)) {
      return;
    }
    LivingEntity ent = (LivingEntity)event.getRightClicked();
    
    String name = ent.getCustomName();
    if (name == null) {
      return;
    }
    for (Tutorial tut : this._tutorials)
    {
      if (name.contains(tut.GetTutName()))
      {
        UtilPlayer.message(event.getPlayer(), F.main("Tutorial", "You started " + F.elem(tut.GetTutName()) + "."));
        tut.BeginTutorial(event.getPlayer());
        return;
      }
    }
  }
  
  @EventHandler
  public void PlayerQuit(PlayerQuitEvent event)
  {
    for (Tutorial tut : this._tutorials) {
      tut.EndTutorial(event.getPlayer());
    }
  }
  
  @EventHandler
  public void InteractCancel(PlayerInteractEvent event) {
    if (InTutorial(event.getPlayer())) {
      event.setCancelled(true);
    }
  }
  
  @EventHandler
  public void MoveCancel(PlayerMoveEvent event) {
    if (InTutorial(event.getPlayer())) {
      event.setTo(event.getFrom());
    }
  }
  
  @EventHandler
  public void Update(UpdateEvent event) {
    if (event.getType() != UpdateType.TICK)
      return;
    Iterator<Player> tuteIterator;
    for (Iterator localIterator = this._tutorials.iterator(); localIterator.hasNext(); 
        


        tuteIterator.hasNext())
    {
      final Tutorial tut = (Tutorial)localIterator.next();
      
      tuteIterator = tut.GetTutorial().keySet().iterator();
      
      continue;
      
      final Player player = (Player)tuteIterator.next();
      TutorialData data = (TutorialData)tut.GetTutorial().get(player);
      

      if (data.Update())
      {

        if (data.PhaseStep < tut.GetPhases().size())
        {
          data.SetNextPhase((TutorialPhase)tut.GetPhases().get(data.PhaseStep));

        }
        else
        {
          tuteIterator.remove();
          

          UtilPlayer.message(player, F.main("Tutorial", "You completed " + F.elem(tut.GetTutName()) + "."));
          

          if (!this._taskManager.hasCompletedTask(player, tut.GetTask()))
          {
            this._donationManager.RewardGems(new Callback()
            {
              public void run(Boolean completed)
              {
                UtilPlayer.message(player, F.main("Tutorial", "You received " + F.elem(new StringBuilder(String.valueOf(C.cGreen)).append(tut.GetGems()).append(" Gems").toString()) + "."));
                TutorialManager.this._taskManager.completedTask(player, tut.GetTask());
                
                player.playSound(player.getLocation(), Sound.LEVEL_UP, 2.0F, 1.5F);
              }
            }, "Tutorial " + tut.GetTutName(), player.getName(), tut.GetGems());
          }
        }
      }
    }
  }
  

  public boolean InTutorial(Player player)
  {
    for (Tutorial tut : this._tutorials) {
      if (tut.InTutorial(player))
        return true;
    }
    return false;
  }
}
