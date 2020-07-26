package mineplex.hub.party.commands;

import java.util.Collection;
import mineplex.core.account.CoreClientManager;
import mineplex.core.command.CommandBase;
import mineplex.core.common.Rank;
import mineplex.core.common.util.F;
import mineplex.core.common.util.UtilPlayer;
import mineplex.hub.party.Party;
import mineplex.hub.party.PartyManager;
import org.bukkit.Sound;
import org.bukkit.entity.Player;

public class PartyCommand extends CommandBase<PartyManager>
{
  public PartyCommand(PartyManager plugin)
  {
    super(plugin, Rank.ALL, new String[] { "party", "z" });
  }
  

  public void Execute(Player caller, String[] args)
  {
    if ((args == null) || (args.length == 0) || ((args[0].equalsIgnoreCase("kick")) && (args.length < 2)))
    {
      UtilPlayer.message(caller, F.main("Party", "Listing Party Commands;"));
      UtilPlayer.message(caller, F.value(0, "/party <Player>", "Join/Create/Invite Player"));
      UtilPlayer.message(caller, F.value(0, "/party leave", "Leave your current Party"));
      UtilPlayer.message(caller, F.value(0, "/party kick <Player>", "Kick player from your Party"));
      
      return;
    }
    

    Party party = ((PartyManager)this.Plugin).GetParty(caller);
    

    if (args[0].equalsIgnoreCase("leave"))
    {
      if (party == null)
      {
        UtilPlayer.message(caller, F.main("Party", "You are not in a Party."));
      }
      else
      {
        party.LeaveParty(caller);
      }
      
      return;
    }
    

    if (args[0].equalsIgnoreCase("kick"))
    {
      if (party == null)
      {
        UtilPlayer.message(caller, F.main("Party", "You are not in a Party."));


      }
      else if (party.GetLeader().equals(caller.getName()))
      {
        String target = UtilPlayer.searchCollection(caller, args[1], party.GetPlayers(), "Party ", true);
        if (target == null) { return;
        }
        if (target.equals(caller.getName()))
        {
          UtilPlayer.message(caller, F.main("Party", "You cannot kick yourself from the Party."));
          return;
        }
        
        party.KickParty(target);
      }
      else
      {
        UtilPlayer.message(caller, F.main("Party", "You are not the Party Leader."));
      }
      

      return;
    }
    

    Player target = UtilPlayer.searchOnline(caller, args[0], true);
    if (target == null) { return;
    }
    if (target.equals(caller))
    {
      UtilPlayer.message(caller, F.main("Party", "You cannot Party with yourself."));
      return;
    }
    

    if (((PartyManager)this.Plugin).GetClients().Get(target).GetRank() == Rank.YOUTUBE)
    {
      if (((PartyManager)this.Plugin).GetClients().Get(caller).GetRank().Has(Rank.YOUTUBE))
      {
        UtilPlayer.message(caller, F.main("Stacker", "You may not party with " + F.name(mineplex.core.common.util.UtilEnt.getName(target)) + "! Leave him/her alone!"));
        return;
      }
    }
    

    if (party != null)
    {
      if (party.GetPlayers().size() + party.GetInvitees().size() >= 16)
      {
        UtilPlayer.message(caller, "Your party cannot be larger than 16 players.");
        caller.playSound(caller.getLocation(), Sound.NOTE_BASS, 1.0F, 1.5F);

      }
      else if (party.GetPlayers().contains(target.getName()))
      {
        UtilPlayer.message(caller, F.main("Party", F.name(target.getName()) + " is already in the Party."));
        caller.playSound(caller.getLocation(), Sound.NOTE_BASS, 1.0F, 1.5F);

      }
      else if (party.GetInvitees().contains(target.getName()))
      {
        UtilPlayer.message(caller, F.main("Party", F.name(target.getName()) + " is already invited to the Party."));
        caller.playSound(caller.getLocation(), Sound.NOTE_BASS, 1.0F, 1.5F);

      }
      else if (party.GetLeader().equals(caller.getName()))
      {
        party.InviteParty(target, ((PartyManager)this.Plugin).GetParty(target) != null);

      }
      else
      {
        party.Announce(F.name(caller.getName()) + " suggested " + F.name(target.getName()) + " be invited to the Party.");
        UtilPlayer.message(UtilPlayer.searchExact(party.GetLeader()), F.main("Party", "Type " + F.link(new StringBuilder("/party ").append(target.getName()).toString()) + " to invite them."));
      }
      
    }
    else
    {
      Party targetParty = ((PartyManager)this.Plugin).GetParty(target);
      

      if (targetParty != null)
      {
        if (targetParty.GetInvitees().contains(caller.getName()))
        {
          targetParty.JoinParty(caller);
          return;
        }
      }
      

      party = ((PartyManager)this.Plugin).CreateParty(caller);
      party.InviteParty(target, ((PartyManager)this.Plugin).GetParty(target) != null);
    }
  }
}
