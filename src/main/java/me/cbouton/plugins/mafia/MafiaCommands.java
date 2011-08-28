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
    public MafiaCommands(Mafia plugin){
        this.plugin = plugin;
    }

    public boolean onCommand(CommandSender cs, Command cmnd, String string, String[] args) {

        if(cs instanceof Player && !cs.hasPermission("mafia.deny")){
            Player player = (Player) cs;
        if("Mafia".equalsIgnoreCase(cmnd.getName()) && args.length == 1){
            plugin.setMafia(plugin.getServer().getPlayer(args[1]), !plugin.isMafia(plugin.getServer().getPlayer(args[1])));
            return true;
        }
        if("Signups".equalsIgnoreCase(cmnd.getName()) && plugin.phase == null && player.hasPermission("mafia.start")){
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
        if("Join".equalsIgnoreCase(cmnd.getName()) && "signups".equalsIgnoreCase(plugin.phase)){
            if(!player.toString().equalsIgnoreCase(plugin.mod)){
                plugin.setPlaying(player, true);
                plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + " is now playing.");
            }
            else{
                
                player.sendMessage("Mods cannot play in the game.");
            }
            return true;
        }
        if("Day".equalsIgnoreCase(cmnd.getName()) && plugin.mod.equalsIgnoreCase(player.toString())){
            plugin.phase = "Day";
            plugin.getServer().broadcastMessage("It is now day, get to lynching.");
            player.getLocation().getWorld().setTime(800); //set to day
            return true;
        }
        if("Night".equalsIgnoreCase(cmnd.getName()) && plugin.mod.equalsIgnoreCase(player.toString())){
            plugin.phase = "Night";
            plugin.getServer().broadcastMessage("It is now night, go to sleep.");
            player.getLocation().getWorld().setTime(18000); //set to night
            //display votecount
            plugin.votecount = null;
            plugin.voting = null;
            return true;
        }
        if("Start".equalsIgnoreCase(cmnd.getName()) && player.toString().equalsIgnoreCase(plugin.mod) && plugin.phase.equalsIgnoreCase("signups") && !plugin.mafiascum.isEmpty()){
            plugin.phase = "Day";
            plugin.getServer().broadcastMessage("The game has started, it is now day.");
            return true;
        }
        if("Part".equalsIgnoreCase(cmnd.getName()) && plugin.isPlaying(player) && plugin.phase.equalsIgnoreCase("signups")){
            if(!player.toString().equalsIgnoreCase(plugin.mod)){
                plugin.setPlaying(player, false);
                plugin.getServer().broadcastMessage(ChatColor.GOLD + player.getDisplayName() + " has left the game.");
            }
            else{
                
                player.sendMessage("Mods cannot part the game.");
            }
            return true;
        }
        if ("Mafia".equalsIgnoreCase(cmnd.getName()) && player.toString().equalsIgnoreCase(plugin.mod) && args.length == 1){
            Player mafiaplayer = plugin.getServer().getPlayer(args[0]);
            plugin.setMafia(mafiaplayer, !plugin.isMafia(mafiaplayer));
            return true;
        }
        if("MafiaKill".equalsIgnoreCase(cmnd.getName()) && plugin.isMafia(player) && args.length == 1 && plugin.isPlaying(plugin.getServer().getPlayer(args[0]))){
            plugin.getServer().getPlayer(plugin.mod).sendMessage(ChatColor.BLUE + args[0] + " is " + player + "'s choice for the mafia kill");
            return true;
        }
        if("Dead".equalsIgnoreCase(cmnd.getName()) && player.toString().equalsIgnoreCase(plugin.mod) && args.length == 1 && plugin.isPlaying(plugin.getServer().getPlayer(args[0]))) {
            plugin.setPlaying(plugin.getServer().getPlayer(args[0]), false);
            plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + args[0] + " is now dead.");
            return true;
        }
        if("End".equalsIgnoreCase(cmnd.getName()) && plugin.phase != null){
            plugin.phase = null;
            plugin.getServer().broadcastMessage(ChatColor.GOLD + "The game is now over.");
            plugin.mod = null;
            plugin.mafiascum.clear();
            plugin.mafiaplayers.clear();
            plugin.votecount = null;
            plugin.voting = null;
            return true;
        }
        if("Vote".equalsIgnoreCase(cmnd.getName()) && plugin.isPlaying(player) && plugin.phase.equalsIgnoreCase("Day") && args.length > 0 && plugin.isPlaying(plugin.getServer().getPlayer(args[0]))){
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
        if("Votecount".equalsIgnoreCase(cmnd.getName()) && plugin.phase.equalsIgnoreCase("Day")){
            plugin.getServer().broadcastMessage(plugin.votecount.toString());
            return true;    
        }
        if("Insert".equalsIgnoreCase(cmnd.getName()) && player.getDisplayName().equalsIgnoreCase(plugin.mod) && args.length == 1 && (plugin.phase.equalsIgnoreCase("Day") || plugin.phase.equalsIgnoreCase("night"))){
            plugin.setPlaying(plugin.getServer().getPlayer(args[0]), true);
            plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + args[0] + " is now playing.");
            return true;
        }
        return false;
        
    }
        if (cs.hasPermission("mafia.deny")){
            cs.sendMessage(ChatColor.RED + "You are denied mafia privleges.");
            return true;
        }
        cs.sendMessage("Non-players cannot use Mafia commands.");
        return true;}


    
}
