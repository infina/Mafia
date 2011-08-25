package me.cbouton.plugins.mafia;

import java.util.HashSet;
import java.util.Set;
import org.bukkit.command.CommandExecutor;
import org.bukkit.entity.Player;
import org.bukkit.event.Event.Priority;
import org.bukkit.event.Event.Type;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.plugin.PluginManager;
import org.bukkit.plugin.java.JavaPlugin;

public class Mafia extends JavaPlugin {
    public Set<Player> mafiaplayers = new HashSet<Player>();
    public Set<Player> mafiascum = new HashSet<Player>();
    private PlayerListener playerListener = new MafiaPlayerListener(this);
    public PluginManager pm = getServer().getPluginManager();
    private CommandExecutor commandListener = new MafiaCommands(this);
    public String mod = null;
    public String phase = null;
    public void onDisable() {
        
        System.out.println(this + " is now disabled!");
    }

    public void onEnable() {
        
        pm.registerEvent(Type.PLAYER_CHAT, playerListener, Priority.Highest, this);
        pm.registerEvent(Type.PLAYER_QUIT, playerListener, Priority.Monitor, this);
        
        getCommand("Vote").setExecutor(commandListener);
        getCommand("Start").setExecutor(commandListener); //done
        getCommand("End").setExecutor(commandListener); //done
        getCommand("Votecount").setExecutor(commandListener);
        getCommand("Day").setExecutor(commandListener); //done
        getCommand("Night").setExecutor(commandListener); //done
        getCommand("Dead").setExecutor(commandListener); //done
        getCommand("Mafia").setExecutor(commandListener); //done
        getCommand("MafiaKill").setExecutor(commandListener); //done
        getCommand("Join").setExecutor(commandListener); //done
        getCommand("Part").setExecutor(commandListener); //done
        getCommand("Signups").setExecutor(commandListener); //done
        System.out.println(this + " is now enabled!");
    }
    public boolean isPlaying(Player player){
        return mafiaplayers.contains(player);
    }
    public void setPlaying(Player player, boolean enabled){
        if(enabled){
            mafiaplayers.add(player);
        }
        else{
            mafiaplayers.remove(player);
        }
    }
    public boolean isMafia(Player player){
        return mafiascum.contains(player);
    }
    public void setMafia(Player player, boolean enabled){
        if(enabled){
            mafiascum.add(player);
        }
        else{
            mafiascum.remove(player);
        }
    }
}
