package mineplex.hub.tutorial.types;

import java.util.ArrayList;
import mineplex.hub.HubManager;
import mineplex.hub.tutorial.TutorialPhase;
import org.bukkit.Location;

public class WelcomeTutorial extends mineplex.hub.tutorial.Tutorial
{
  public WelcomeTutorial(HubManager manager, mineplex.hub.modules.TextManager text)
  {
    super(manager, "Welcome Tutorial", 5000, "Hub_JoinTutorial");
    
    double y = -manager.GetSpawn().getY();
    

    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(-40.0D, y + 85.0D, 0.0D), 
      manager.GetSpawn(), 
      "Welcome to Mineplex", 
      
      new String[] {
      "Welcome!", 
      "", 
      "This is a very quick tutorial to help you start.", 
      "Seriously, it will only take 30 seconds!!!", 
      "", 
      "Mineplex has many different games to play.", 
      "I will show you them, and tell you how to join!" }));
    



    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(0.0D, -3.0D, 13.0D), 
      text.locArcade, 
      "Arcade", 
      
      new String[] {
      "This is the " + this._elem + "Arcade" + this._main + " game mode.", 
      "", 
      "Servers will rotate through many different games.", 
      "So there's no need to quit after each game!", 
      "", 
      "They are all quick, fun 16 player games!" }));
    



    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(-13.0D, -3.0D, 0.0D), 
      text.locSurvival, 
      "Survival", 
      
      new String[] {
      "This is the " + this._elem + "The Bridges" + this._main + " game mode.", 
      "This is a great team combat game.", 
      "You get 10 minutes to prepare for battle,", 
      "then the bridges drop, and you fight to the death!", 
      "", 
      this._elem + "Hunger Games" + this._main + " will be added here soon!" }));
    



    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(-9.0D, y + 73.0D, 53.0D), 
      manager.GetSpawn().add(-11.0D, y + 72.5D, 57.0D), 
      "???", 
      
      new String[] {
      "", 
      "", 
      "This is a pig standing on a log." }));
    



    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(0.0D, -3.0D, -13.0D), 
      text.locClassics, 
      "Classics", 
      
      new String[] {
      "Here, you can play our " + this._elem + "Classics" + this._main + " game modes.", 
      "", 
      this._elem + "MineKart" + this._main + " is an exciting racing game.", 
      "Complete with weapons, drifting and more!", 
      "", 
      "In " + this._elem + "Super Smash Mobs" + this._main + " you become a monster,", 
      "then fight to the death with other players, using fun skills!" }));
    



    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(13.0D, -3.0D, 0.0D), 
      text.locComp, 
      "Champions", 
      
      new String[] {
      "Finally, these are the " + this._elem + "Champions" + this._main + " games.", 
      "These are extremely competitive skill based games.", 
      "", 
      "Each class can be customised with unlockable skills.", 
      "", 
      "Fight with others in three different game types!" }));
    



    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(0.0D, -3.0D, 19.0D), 
      manager.GetSpawn().add(0.0D, -3.1D, 23.0D), 
      "Joining Games", 
      
      new String[] {
      "You can join a game in two ways.", 
      "", 
      "The easiest way is to walk through the portal.", 
      "This will join the best available server!", 
      "", 
      "Click the " + this._elem + "Wither Skeleton" + this._main + " to open the " + this._elem + "Server Menu" + this._main + ".", 
      "Here, you can manually pick which server to join!" }));
    



    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(0.0D, -3.0D, 0.0D), 
      manager.GetSpawn().add(0.0D, -3.1D, 5.0D), 
      "End", 
      
      new String[] {
      "", 
      "Easy huh?", 
      "", 
      "Thanks for listening! Have fun!", 
      "" }));
  }
}
