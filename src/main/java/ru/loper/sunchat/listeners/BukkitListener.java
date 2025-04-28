package ru.loper.sunchat.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import ru.loper.sunchat.SunChat;
import ru.loper.sunchat.config.ConfigManager;
import ru.loper.sunchat.utils.ChatUtils;

import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
public class BukkitListener implements Listener {
    private final Map<UUID, Long> lastMessageTime = new HashMap<>();
    private final ConfigManager configManager;
    private final SunChat plugin;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
        UUID uuid = player.getUniqueId();
        String message = e.getMessage();
        boolean isGlobal = !configManager.isLocalChatStatus() || message.startsWith("!");

        if (configManager.isNewbieEnable()) {
            long time = (System.currentTimeMillis() - player.getFirstPlayed()) / 1000L;
            if (!player.hasPermission("sunchat.newbie.bypass") && time <= configManager.getNewbieCooldown()) {
                int timeLeft = configManager.getNewbieCooldown() - (int) time;
                player.sendMessage(configManager.getNewbieChatBlockMessage()
                        .replace("{time}", ChatUtils.formatTime(timeLeft)));
                e.setCancelled(true);
                return;
            }
        }

        if (!player.hasPermission("sunchat.cooldown.bypass")) {
            long currentTime = System.currentTimeMillis();
            long cooldown = isGlobal ? configManager.getGlobalChatCooldown() * 1000L
                    : configManager.getLocalChatCooldown() * 1000L;

            if (lastMessageTime.containsKey(uuid)) {
                long timeLeft = (cooldown - (currentTime - lastMessageTime.get(uuid))) / 1000;
                if (timeLeft > 0) {
                    player.sendMessage(configManager.getCooldownChatMessage()
                            .replace("{time}", ChatUtils.formatTime((int) timeLeft)));
                    e.setCancelled(true);
                    return;
                }
            }
            lastMessageTime.put(uuid, currentTime);
        }

        String format = isGlobal ? configManager.getGlobalChatFormat()
                : configManager.getLocalChatFormat();
        e.setFormat(ChatUtils.replacePlaceholders(player, format)
                .replace("{player}", player.getName())
                .replace("{message}", isGlobal ? removeGlobalPrefix(message) : message));

        if (!isGlobal) {
            Bukkit.getScheduler().runTask(plugin, () -> {
                e.getRecipients().clear();
                e.getRecipients().add(player);
                e.getRecipients().addAll(getRadius(player));
            });
        }
    }

    public List<Player> getRadius(Player player) {
        return player.getLocation().getNearbyPlayers(configManager.getLocalChatRadius()).stream().toList();
    }

    @EventHandler(ignoreCancelled = true)
    public void playerCommand(PlayerCommandPreprocessEvent e) {
        if (!configManager.isNewbieEnable()) return;

        Player player = e.getPlayer();
        long time = (System.currentTimeMillis() - player.getFirstPlayed()) / 1000L;

        if (!player.hasPermission("sunchat.newbie.bypass") && time <= configManager.getNewbieCooldown()) {
            for (String newbieBlockCommand : configManager.getNewbieBlockCommands()) {
                if (e.getMessage().startsWith(newbieBlockCommand)) {
                    int timeLeft = configManager.getNewbieCooldown() - (int) time;
                    player.sendMessage(configManager.getNewbieChatBlockMessage()
                            .replace("{time}", ChatUtils.formatTime(timeLeft)));
                    e.setCancelled(true);
                    return;
                }
            }
        }
    }

    public String removeGlobalPrefix(String message) {
        message = message.substring(1);
        message = message.trim();
        return message;
    }
}
