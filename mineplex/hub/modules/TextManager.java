package mineplex.hub.modules;

import mineplex.core.MiniPlugin;
import mineplex.core.common.util.UtilText;
import mineplex.core.common.util.UtilText.TextAlign;
import mineplex.hub.HubManager;
import org.bukkit.Location;
import org.bukkit.block.BlockFace;






public class TextManager
  extends MiniPlugin
{
  public HubManager Manager;
  public Location locComp;
  public Location locArcade;
  public Location locSurvival;
  public Location locClassics;
  BlockFace faceComp = BlockFace.SOUTH;
  BlockFace faceArcade = BlockFace.WEST;
  BlockFace faceSurvival = BlockFace.NORTH;
  BlockFace faceOther = BlockFace.EAST;
  
  String[] arcadeGames;
  int arcadeIndex = 0;
  
  int smashIndex = 0;
  
  public TextManager(HubManager manager)
  {
    super("Text Creator", manager.GetPlugin());
    
    this.Manager = manager;
    
    this.locComp = manager.GetSpawn().add(40.0D, 10.0D, 0.0D);
    this.locArcade = manager.GetSpawn().add(0.0D, 10.0D, 40.0D);
    this.locSurvival = manager.GetSpawn().add(-40.0D, 10.0D, 0.0D);
    this.locClassics = manager.GetSpawn().add(0.0D, 10.0D, -40.0D);
    
    this.arcadeGames = 
      new String[] {
      "ONE IN THE QUIVER", 
      "DRAGON ESCAPE", 
      "MILK THE COW", 
      "SUPER SPLEEF", 
      "DEATH TAG", 
      "TURF WARS", 
      "DRAGONS", 
      "RUNNER", 
      "BACON BRAWL", 
      "SQUID SAUCE" };
    

    CreateText();
  }
  

  public void CreateText()
  {
    UtilText.MakeText("CHAMPIONS", this.locComp, this.faceComp, 159, (byte)5, UtilText.TextAlign.CENTER);
    UtilText.MakeText("CHAMPIONS", this.locComp.clone().add(1.0D, 0.0D, 0.0D), this.faceComp, 159, (byte)15, UtilText.TextAlign.CENTER);
    
    UtilText.MakeText("DOMINATE", this.locComp.clone().add(15.0D, 14.0D, 0.0D), this.faceComp, 159, (byte)4, UtilText.TextAlign.CENTER);
    UtilText.MakeText("DOMINATE", this.locComp.clone().add(16.0D, 14.0D, 0.0D), this.faceComp, 159, (byte)15, UtilText.TextAlign.CENTER);
    
    UtilText.MakeText("DEATHMATCH", this.locComp.clone().add(15.0D, 21.0D, 0.0D), this.faceComp, 159, (byte)1, UtilText.TextAlign.CENTER);
    UtilText.MakeText("DEATHMATCH", this.locComp.clone().add(16.0D, 21.0D, 0.0D), this.faceComp, 159, (byte)15, UtilText.TextAlign.CENTER);
    
    UtilText.MakeText("CAPTURE THE PIG", this.locComp.clone().add(15.0D, 28.0D, 0.0D), this.faceComp, 159, (byte)14, UtilText.TextAlign.CENTER);
    UtilText.MakeText("CAPTURE THE PIG", this.locComp.clone().add(16.0D, 28.0D, 0.0D), this.faceComp, 159, (byte)15, UtilText.TextAlign.CENTER);
    

    UtilText.MakeText("ARCADE", this.locArcade, this.faceArcade, 159, (byte)5, UtilText.TextAlign.CENTER);
    UtilText.MakeText("ARCADE", this.locArcade.clone().add(0.0D, 0.0D, 1.0D), this.faceArcade, 159, (byte)15, UtilText.TextAlign.CENTER);
    

    UtilText.MakeText("SURVIVAL", this.locSurvival, this.faceSurvival, 159, (byte)5, UtilText.TextAlign.CENTER);
    UtilText.MakeText("SURVIVAL", this.locSurvival.clone().add(-1.0D, 0.0D, 0.0D), this.faceSurvival, 159, (byte)15, UtilText.TextAlign.CENTER);
    
    UtilText.MakeText("THE BRIDGES", this.locSurvival.clone().add(-15.0D, 14.0D, 0.0D), this.faceSurvival, 159, (byte)4, UtilText.TextAlign.CENTER);
    UtilText.MakeText("THE BRIDGES", this.locSurvival.clone().add(-16.0D, 14.0D, 0.0D), this.faceSurvival, 159, (byte)15, UtilText.TextAlign.CENTER);
    
    UtilText.MakeText("SURVIVAL GAMES", this.locSurvival.clone().add(-15.0D, 21.0D, 0.0D), this.faceSurvival, 159, (byte)1, UtilText.TextAlign.CENTER);
    UtilText.MakeText("SURVIVAL GAMES", this.locSurvival.clone().add(-16.0D, 21.0D, 0.0D), this.faceSurvival, 159, (byte)15, UtilText.TextAlign.CENTER);
    


    UtilText.MakeText("CLASSICS", this.locClassics, this.faceOther, 159, (byte)5, UtilText.TextAlign.CENTER);
    UtilText.MakeText("CLASSICS", this.locClassics.add(0.0D, 0.0D, -1.0D), this.faceOther, 159, (byte)15, UtilText.TextAlign.CENTER);
    
    UtilText.MakeText("SUPER SMASH MOBS", this.locClassics.clone().add(0.0D, 14.0D, -15.0D), this.faceOther, 159, (byte)4, UtilText.TextAlign.CENTER);
    UtilText.MakeText("SUPER SMASH MOBS", this.locClassics.clone().add(0.0D, 14.0D, -16.0D), this.faceOther, 159, (byte)15, UtilText.TextAlign.CENTER);
    
    UtilText.MakeText("DRAW MY THING", this.locClassics.clone().add(0.0D, 21.0D, -15.0D), this.faceOther, 159, (byte)1, UtilText.TextAlign.CENTER);
    UtilText.MakeText("DRAW MY THING", this.locClassics.clone().add(0.0D, 21.0D, -16.0D), this.faceOther, 159, (byte)15, UtilText.TextAlign.CENTER);
    
    UtilText.MakeText("BLOCK HUNT", this.locClassics.clone().add(0.0D, 28.0D, -15.0D), this.faceOther, 159, (byte)14, UtilText.TextAlign.CENTER);
    UtilText.MakeText("BLOCK HUNT", this.locClassics.clone().add(0.0D, 28.0D, -16.0D), this.faceOther, 159, (byte)15, UtilText.TextAlign.CENTER);
  }
  




















  public String GetArcadeText(int offset)
  {
    int index = (this.arcadeIndex + offset) % this.arcadeGames.length;
    
    return this.arcadeGames[index];
  }
}
