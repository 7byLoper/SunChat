package ru.loper.sunchat.config;

import lombok.Getter;
import org.bukkit.configuration.file.FileConfiguration;
import ru.loper.sunchat.SunChat;
import ru.loper.suncore.utils.Colorize;

import java.util.List;

@Getter
public class ConfigManager {
    private final SunChat plugin;

    private boolean localChatStatus;
    private int localChatRadius;
    private String localChatFormat;
    private String globalChatFormat;

    private int localChatCooldown;
    private int globalChatCooldown;
    private String cooldownChatMessage;

    private boolean newbieEnable;
    private int newbieCooldown;
    private String newbieChatBlockMessage;
    private String newbieCommandBlockMessage;
    private List<String> newbieBlockCommands;

    public ConfigManager(SunChat plugin) {
        this.plugin = plugin;
        plugin.saveDefaultConfig();
        loadValues();
    }

    public void reload() {
        plugin.reloadConfig();
        loadValues();
    }

    private void loadValues() {
        localChatStatus = getConfig().getBoolean("format.local.status");
        localChatRadius = getConfig().getInt("format.local.radius");
        localChatFormat = message("format.local.message");
        globalChatFormat = message("format.global.message");

        localChatCooldown = getConfig().getInt("cooldown.local-chat");
        globalChatCooldown = getConfig().getInt("cooldown.global-chat");
        cooldownChatMessage = message("cooldown.message");

        newbieEnable = getConfig().getBoolean("newbie.enable");
        newbieCooldown = getConfig().getInt("newbie.newbie-cooldown");
        newbieChatBlockMessage = message("newbie.newbie-chat-block");
        newbieCommandBlockMessage = message("newbie.newbie-command-block");
        newbieBlockCommands = getConfig().getStringList("newbie.newbie-block-commands");
    }

    public String message(String path) {
        return Colorize.parse(plugin.getConfig().getString(path, "unknown"));
    }

    public FileConfiguration getConfig() {
        return plugin.getConfig();
    }
}
