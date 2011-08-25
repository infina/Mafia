/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cbouton.plugins.mafia;

import org.bukkit.ChatColor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.entity.Player;

/**
 *
 * @author Corey
 */
class MafiaCommands implements CommandExecutor {
    private final Mafia plugin;
    private String[] Votes = null;
    public MafiaCommands(Mafia plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {
        String[] votee;
        String[] voters;
        if(cs instanceof Player){
            Player player = (Player) cs;
        if("Mafia".equals(cmnd.toString()) && args.length == 1){
            plugin.setMafia(plugin.getServer().getPlayer(args[1]), !plugin.isMafia(plugin.getServer().getPlayer(args[1])));
            return true;
        }
        if("Signups".equals(cmnd.toString()) && plugin.phase == null){
            plugin.phase = "signups";
            plugin.mod = player.toString();
            if(args.length == 0){
            plugin.getServer().broadcastMessage(ChatColor.GOLD + plugin.mod + " is starting a new game of mafia.\n Use /Join to join.");
            }
            else{
                plugin.getServer().broadcastMessage(ChatColor.GOLD + plugin.mod + "'s " + args + "mafia game is starting. \n Use /Join to join.");
            }
            return true;
        }
        if("Join".equals(cmnd.toString()) && "signups".equals(plugin.phase)){
            if(!player.toString().equals(plugin.mod)){
                plugin.setPlaying(player, true);
                plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + " is now playing.");
            }
            else{
                
                player.sendMessage("Mods cannot play in the game.");
            }
            return true;
        }
        if("Day".equals(cmnd.toString()) && plugin.mod.equals(player.toString())){
            plugin.phase = "Day";
            plugin.getServer().broadcastMessage("It is now day, get to lynching.");
            player.getLocation().getWorld().setTime(800); //set to day
            return true;
        }
        if("Night".equals(cmnd.toString()) && plugin.mod.equals(player.toString())){
            plugin.phase = "Night";
            plugin.getServer().broadcastMessage("It is now night, go to sleep.");
            player.getLocation().getWorld().setTime(18000); //set to night
            //display votecount
            Votes = null;
            return true;
        }
        if("Start".equals(cmnd.toString()) && player.toString().equals(plugin.mod) && plugin.phase.equals("signups") && !plugin.mafiascum.isEmpty()){
            plugin.phase = "Day";
            plugin.getServer().broadcastMessage("The game has started, it is now day.");
            return true;
        }
        if("Part".equals(cmnd.toString()) && plugin.isPlaying(player) && plugin.phase.equals("signups")){
            if(!player.toString().equals(plugin.mod)){
                plugin.setPlaying(player, false);
                plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + " has left the game.");
            }
            else{
                
                player.sendMessage("Mods cannot part the game.");
            }
            return true;
        }
        if ("Mafia".equals(cmnd.toString()) && player.toString().equals(plugin.mod) && args.length == 1){
            Player mafiaplayer = plugin.getServer().getPlayer(args[0]);
            plugin.setMafia(mafiaplayer, !plugin.isMafia(mafiaplayer));
            return true;
        }
        if("MafiaKill".equals(cmnd.toString()) && plugin.isMafia(player) && args.length == 1 && plugin.isPlaying(plugin.getServer().getPlayer(args[0]))){
            plugin.getServer().getPlayer(plugin.mod).sendMessage(ChatColor.BLUE + args[0] + " is " + player + "'s choice for the mafia kill");
            return true;
        }
        if("Dead".equals(cmnd.toString()) && player.toString().equals(plugin.mod) && args.length == 1 && plugin.isPlaying(plugin.getServer().getPlayer(args[0]))) {
            plugin.setPlaying(plugin.getServer().getPlayer(args[0]), false);
            plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + args[0] + " is now dead.");
            return true;
        }
        if("End".equals(cmnd.toString()) && plugin.phase != null){
            plugin.phase = null;
            plugin.getServer().broadcastMessage(ChatColor.GOLD + "The game is now over.");
            plugin.mod = null;
            plugin.mafiascum.clear();
            plugin.mafiaplayers.clear();
            Votes = null;
            return true;
        }
        if("Vote".equals(cmnd.toString()) && plugin.isPlaying(player) && plugin.phase.equals("Day") && args.length > 0 && plugin.isPlaying(plugin.getServer().getPlayer(args[0]))){
            for (int i = 0; i < Votes.length; i++) {
                if(Votes[i].startsWith(args[0])){
                    Votes[i] = Votes[i] + ", " + player.getDisplayName();
                    for (int j = 0; j < Votes.length; j++) {
                        votee = Votes[j].split(":");
                        voters = votee[1].split(", ");
                        int numvotes = voters.length;
                        if(numvotes == (plugin.mafiaplayers.size()/2 + 1)) {
                            plugin.getServer().broadcastMessage(ChatColor.RED + votee[0] + " has been lynched. It is now night.");
                            plugin.phase = "Night";
                            plugin.getServer().broadcastMessage("It is now night, go to sleep.");
                            player.getLocation().getWorld().setTime(18000); //set to night
                            //display votecount
                            Votes = null;
                            return true;
                        }
                        else{
                            plugin.getServer().broadcastMessage(ChatColor.BLUE + votee[0] + " - " + numvotes + "(" + votee[1] + ")");
                        }
                    }
                     
                }
            }
        }
        return false;
    }
        cs.sendMessage("Non-players cannot use Mafia commands.");
        return true;}


    
}
