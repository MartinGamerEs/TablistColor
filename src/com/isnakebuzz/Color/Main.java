package com.isnakebuzz.Color;

import org.bukkit.plugin.java.*;
import org.zencode.mango.*;
import org.zencode.mango.factions.*;
import org.bukkit.plugin.*;
import org.bukkit.scheduler.*;
import org.bukkit.entity.*;
import org.bukkit.*;
import org.zencode.mango.factions.types.*;
import org.bukkit.scoreboard.*;
import java.util.*;
import org.bukkit.potion.PotionEffect;
import org.bukkit.potion.PotionEffectType;

public class Main extends JavaPlugin
{
    private Main instance;
    private Mango mango;
    private FactionManager fm;
    
    public void onEnable() {
        if (Bukkit.getPluginManager().getPlugin("Mango") == null) {
            Bukkit.getConsoleSender().sendMessage(ChatColor.RED + "Squad failed to hook into Mango, disabling.");
            Bukkit.getPluginManager().disablePlugin((Plugin)this);
            return;
        }
        this.instance = this;
        this.mango = Mango.getInstance();
        this.fm = this.mango.getFactionManager();
        this.saveDefaultConfig();
        new BukkitRunnable() {
            public void run() {
                Player[] onlinePlayers;
                for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, i = 0; i < length; ++i) {
                    final Player online = onlinePlayers[i];
                    Main.this.attemptScoreboard(online);
                }
            }
        }.runTaskTimer((Plugin)this, 0L, 10L);
    }
    
    private String getColor(final String path) {
        if (this.getConfig().contains(path)) {
            return ChatColor.translateAlternateColorCodes('&', this.getConfig().getString(path));
        }
        return new StringBuilder().append(ChatColor.WHITE).toString();
    }
    
    private void attemptScoreboard(final Player p) {
        Scoreboard scoreboard;
        if (p.getActivePotionEffects() == PotionEffectType.INVISIBILITY){
            return;
        }
        if (p.getScoreboard() != Bukkit.getScoreboardManager().getMainScoreboard()) {
            scoreboard = p.getScoreboard();
        }
        else {
            scoreboard = Bukkit.getScoreboardManager().getNewScoreboard();
        }
        final PlayerFaction faction = this.fm.getFaction(p);
        Team friendly;
        if (scoreboard.getTeam("friendly") != null) {
            friendly = scoreboard.getTeam("friendly");
        }
        else {
            friendly = scoreboard.registerNewTeam("friendly");
        }
        Team allies;
        if (scoreboard.getTeam("allies") != null) {
            allies = scoreboard.getTeam("allies");
        }
        else {
            allies = scoreboard.registerNewTeam("allies");
        }
        Team enemies;
        if (scoreboard.getTeam("enemies") != null) {
            enemies = scoreboard.getTeam("enemies");
        }
        else {
            enemies = scoreboard.registerNewTeam("enemies");
        }
        enemies.setPrefix(this.getColor("tab_color.enemy"));
        friendly.setPrefix(this.getColor("tab_color.friendly"));
        allies.setPrefix(this.getColor("tab_color.ally"));
        Player[] onlinePlayers;
        for (int length = (onlinePlayers = Bukkit.getOnlinePlayers()).length, i = 0; i < length; ++i) {
            final Player enemyPlayer = onlinePlayers[i];
            enemies.addPlayer((OfflinePlayer)enemyPlayer);
        }
        if (faction != null) {
            for (final Player friendlyPlayer : faction.getOnlinePlayers()) {
                allies.removePlayer((OfflinePlayer)friendlyPlayer);
                enemies.removePlayer((OfflinePlayer)friendlyPlayer);
                friendly.addPlayer((OfflinePlayer)friendlyPlayer);
            }
            for (final PlayerFaction ally : faction.getAllies()) {
                for (final Player allyPlayer : ally.getOnlinePlayers()) {
                    enemies.removePlayer((OfflinePlayer)allyPlayer);
                    friendly.removePlayer((OfflinePlayer)allyPlayer);
                    allies.addPlayer((OfflinePlayer)allyPlayer);
                }
            }
        }
        friendly.addPlayer((OfflinePlayer)p);
        p.setScoreboard(scoreboard);
    }
}
