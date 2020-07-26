package mineplex.hub.tutorial.types;

import java.util.ArrayList;
import mineplex.core.common.util.C;
import mineplex.core.common.util.F;
import mineplex.hub.HubManager;
import mineplex.hub.tutorial.Tutorial;
import mineplex.hub.tutorial.TutorialPhase;
import org.bukkit.Location;

public class PartyTutorial extends Tutorial
{
  public PartyTutorial(HubManager manager)
  {
    super(manager, "Party Tutorial", 1000, "Hub_PartyTutorial");
    
    double y = -manager.GetSpawn().getY();
    
    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(84.0D, y + 69.0D, 10.0D), 
      manager.GetSpawn().add(81.0D, y + 68.5D, 10.0D), 
      "Parties", 
      
      new String[] {
      "Hi there!", 
      "", 
      "This tutorial will teach you about Parties.", 
      "", 
      "Parties are used to group with other players", 
      "in order to easily play the same game together." }));
    


    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(84.0D, y + 69.0D, 10.0D), 
      manager.GetSpawn().add(81.0D, y + 68.5D, 10.0D), 
      "Creating a Party", 
      
      new String[] {
      "To create a Party with someone;", 
      "", 
      "Type " + F.link("/party <Player>"), 
      "", 
      "This will create a party, and invite them to it!", 
      "They will receive a notification on how to join." }));
    


    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(84.0D, y + 69.0D, 10.0D), 
      manager.GetSpawn().add(81.0D, y + 68.5D, 10.0D), 
      "Inviting and Suggesting Players", 
      
      new String[] {
      "To invite/suggest more players to a Party;", 
      "", 
      "Type " + F.link("/party <Player>"), 
      "", 
      "Invitations last for 60 seconds." }));
    


    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(84.0D, y + 69.0D, 10.0D), 
      manager.GetSpawn().add(81.0D, y + 68.5D, 10.0D), 
      "Leaving Parties", 
      
      new String[] {
      "To leave your current Party;", 
      "", 
      "Type " + F.link("/party leave") }));
    


    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(84.0D, y + 69.0D, 10.0D), 
      manager.GetSpawn().add(81.0D, y + 68.5D, 10.0D), 
      "Kicking Players from Party", 
      
      new String[] {
      "To kick players from your current Party;", 
      "", 
      "Type " + F.link("/party kick <Player>"), 
      "", 
      "Only the Party Leader can do this." }));
    


    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(84.0D, y + 69.0D, 10.0D), 
      manager.GetSpawn().add(81.0D, y + 68.5D, 10.0D), 
      "Joining Games Together", 
      
      new String[] {
      "Only the Party Leader can join games.", 
      "", 
      "The game must have enough slots for", 
      "all Party Members to fit.", 
      "", 
      "All members will be connected to the game." }));
    



    this._phases.add(new TutorialPhase(
      manager.GetSpawn().add(84.0D, y + 69.0D, 10.0D), 
      manager.GetSpawn().add(81.0D, y + 68.5D, 10.0D), 
      "Party Chat", 
      
      new String[] {
      "To send a message to your Party;", 
      "", 
      "Type " + F.link("@Hey guys, how are you?"), 
      "", 
      "They will see; ", 
      C.cDPurple + C.Bold + "Party " + C.cWhite + C.Bold + "YourName " + org.bukkit.ChatColor.RESET + C.cPurple + "Hey guys, how are you?" }));
    


    this._phases.add(new TutorialPhase(
      manager.GetSpawn(), 
      manager.GetSpawn().add(0.0D, y + 0.5D, 10.0D), 
      "End", 
      
      new String[] {
      "", 
      "", 
      "Thanks for doing the party tutorial!", 
      "", 
      "" }));
  }
}
