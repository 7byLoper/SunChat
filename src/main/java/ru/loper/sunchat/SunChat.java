package ru.loper.sunchat;

import org.bukkit.Bukkit;
import org.bukkit.plugin.java.JavaPlugin;
import ru.loper.sunchat.command.ChatCommand;
import ru.loper.sunchat.config.ConfigManager;
import ru.loper.sunchat.listeners.BukkitListener;

import java.util.Optional;

public final class SunChat extends JavaPlugin {

    @Override
    public void onEnable() {
        ConfigManager configManager = new ConfigManager(this);
        Bukkit.getPluginManager().registerEvents(new BukkitListener(configManager), this);
        Optional.ofNullable(getCommand("sunchat")).orElseThrow().setExecutor(new ChatCommand(configManager));
    }
}
