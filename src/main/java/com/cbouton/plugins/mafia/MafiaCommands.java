/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package com.cbouton.plugins.mafia;

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
    public MafiaCommands(Mafia plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {

        if(cs instanceof Player && !cs.hasPermission("mafia.deny")){
            Player player = (Player) cs;
            if("Mafia".equals(cmnd.getName()) && args.length == 1){
                plugin.setMafia(plugin.getServer().getPlayer(args[1]), !plugin.isMafia(plugin.getServer().getPlayer(args[1])));
                return true;
                }
            else if("Signups".equals(cmnd.getName()) && plugin.phase == null){
                plugin.phase = "signups";
                plugin.mod = player.getDisplayName();
                //if(args.length == 0){
                    plugin.getServer().broadcastMessage(ChatColor.GOLD + plugin.mod + " is starting a new game of mafia.\n Use /Join to join.");
                //}
                //else{
                    //plugin.getServer().broadcastMessage(ChatColor.GOLD + plugin.mod + "'s " + args.toString() + "mafia game is starting. \n Use /Join to join.");
                //}
                return true;
            }
            else if("Join".equals(cmnd.getName()) && "signups".equals(plugin.phase)){
                if(!player.getDisplayName().equals(plugin.mod)){
                    plugin.setPlaying(player, true);
                    plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + " is now playing.");
                }
                    else{
                        player.sendMessage("Mods cannot play in the game.");
                    }
                return true;
            }
            else if("Day".equals(cmnd.getName()) && plugin.mod.equals(player.getDisplayName())){
                plugin.phase = "Day";
                plugin.getServer().broadcastMessage("It is now day, get to lynching.");
                player.getLocation().getWorld().setTime(800); //set to day
                return true;
            }
            else if("Night".equals(cmnd.getName()) && plugin.mod.equals(player.getDisplayName())){
                plugin.phase = "Night";
                plugin.getServer().broadcastMessage("It is now night, go to sleep.");
                player.getLocation().getWorld().setTime(18000); //set to night
                //display votecount
                plugin.votecount = null;
                plugin.voting = null;
                return true;
            }
            else if("Start".equals(cmnd.getName()) && player.getDisplayName().equals(plugin.mod) && plugin.phase.equals("signups") && !plugin.mafiascum.isEmpty()){
                plugin.phase = "Day";
                plugin.getServer().broadcastMessage("The game has started, it is now day.");
                return true;
            }
            else if("Part".equals(cmnd.getName()) && plugin.isPlaying(player) && plugin.phase.equals("signups")){
            if(!player.getDisplayName().equals(plugin.mod)){
                plugin.setPlaying(player, false);
                plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + " has left the game.");
            }
            else{
                
                player.sendMessage("Mods cannot part the game.");
            }
            return true;
        }
            else if ("Mafia".equals(cmnd.getName()) && player.getDisplayName().equals(plugin.mod) && args.length == 1){
            Player mafiaplayer = plugin.getServer().getPlayer(args[0]);
            plugin.setMafia(mafiaplayer, !plugin.isMafia(mafiaplayer));
            return true;
        }
            else if("MafiaKill".equals(cmnd.getName()) && plugin.isMafia(player) && args.length == 1 && plugin.isPlaying(plugin.getServer().getPlayer(args[0]))){
            plugin.getServer().getPlayer(plugin.mod).sendMessage(ChatColor.BLUE + args[0] + " is " + player + "'s choice for the mafia kill");
            return true;
        }
            else if("Dead".equals(cmnd.getName()) && player.getDisplayName().equals(plugin.mod) && args.length == 1 && plugin.isPlaying(plugin.getServer().getPlayer(args[0]))) {
            plugin.setPlaying(plugin.getServer().getPlayer(args[0]), false);
            plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + args[0] + " is now dead.");
            return true;
        }
            else if("End".equals(cmnd.getName()) && plugin.phase != null){
            plugin.phase = null;
            plugin.getServer().broadcastMessage(ChatColor.GOLD + "The game is now over.");
            plugin.mod = null;
            plugin.mafiascum.clear();
            plugin.mafiaplayers.clear();
            plugin.votecount = null;
            plugin.voting = null;
            return true;
        }
            else if("Vote".equals(cmnd.getName()) && plugin.isPlaying(player) && plugin.phase.equals("Day") && args.length > 0 && plugin.isPlaying(plugin.getServer().getPlayer(args[0]))){
            if (plugin.votecount.containsKey(args[0])) {
                if(plugin.voting.get(player.getDisplayName()) != null){
                    plugin.votecount.get(plugin.voting.get(player.getDisplayName())).replace(player.getDisplayName(), "");
                    plugin.voting.get(player.getDisplayName()).replace(plugin.voting.get(player.getDisplayName()), "");
                }
                plugin.votecount.get(args[0]).concat(player.getDisplayName());
                plugin.voting.get(player.getDisplayName()).concat(args[0]);
            }
            else{
                plugin.votecount.put(args[0], player.getDisplayName());
                plugin.voting.get(player.getDisplayName()).concat(args[0]);
            }
            plugin.getServer().broadcastMessage(plugin.votecount.toString());
            if(plugin.votecount.get(args[0]).length() > (plugin.mafiaplayers.size()/2)){
                plugin.phase = "Night";
                plugin.getServer().broadcastMessage(ChatColor.RED + args[0] + " has been lynched. It is now night, go to sleep.");
                player.getLocation().getWorld().setTime(18000); //set to night
                plugin.votecount = null;
                plugin.voting = null;
                return true;
            }
            return true;    
        }
            else if("Votecount".equals(cmnd.getName()) && plugin.phase.equals("Day")){
            plugin.getServer().broadcastMessage(plugin.votecount.toString());
            return true;    
        }
            else if("Insert".equals(cmnd.getName()) && player.getDisplayName().equals(plugin.mod) && args.length == 1 && (plugin.phase.equals("Day") || plugin.phase.equals("night"))){
            plugin.setPlaying(plugin.getServer().getPlayer(args[0]), true);
            plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + args[0] + " is now playing.");
            return true;
        }
            else {
                return false;
            }
        
    }
        else if (cs.hasPermission("mafia.deny")){
            cs.sendMessage(ChatColor.RED + "You are denied mafia privleges.");
            return true;
        }
        else {
            cs.sendMessage("Non-players cannot use Mafia commands.");
        return true;
        }
        }


    
}
