/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package me.cbouton.plugins.mafia;

import org.bukkit.ChatColor;
import org.bukkit.entity.Player;
import org.bukkit.event.player.PlayerChatEvent;
import org.bukkit.event.player.PlayerListener;
import org.bukkit.event.player.PlayerQuitEvent;

/**
 *
 * @author Corey
 */
public class MafiaPlayerListener extends PlayerListener {
    public Player[] scum = null;
    private final Mafia plugin;
    public MafiaPlayerListener(Mafia plugin){
        this.plugin = plugin;
    }
        

    @Override
    public void onPlayerChat(PlayerChatEvent event) {
        if("night".equals(plugin.phase) && plugin.isPlaying(event.getPlayer())){
        String message = event.getMessage();
        String[] messagesplit = message.split(":");
        String player = event.getPlayer().getDisplayName();
        event.setCancelled(true);
        if(plugin.isMafia(event.getPlayer())){
            plugin.mafiascum.toArray(scum);
            for (int i = 0; i < scum.length; i++) {
                scum[i].sendMessage(ChatColor.RED + player + ": " + ChatColor.WHITE + message);
            }
            }
        }



        }

    @Override
    public void onPlayerQuit(PlayerQuitEvent event) {
        Player player = event.getPlayer();
        if(plugin.isPlaying(player)){
            plugin.setPlaying(player, false);
            plugin.getServer().broadcastMessage(ChatColor.DARK_PURPLE + player.getDisplayName() + " is no longer playing");
        }
    }
    
    }    

