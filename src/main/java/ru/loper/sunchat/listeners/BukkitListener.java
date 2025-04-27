package ru.loper.sunchat.listeners;

import lombok.RequiredArgsConstructor;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.player.AsyncPlayerChatEvent;
import org.bukkit.event.player.PlayerCommandPreprocessEvent;
import ru.loper.sunchat.config.ConfigManager;
import ru.loper.sunchat.utils.ChatUtils;

import java.util.List;

@RequiredArgsConstructor
public class BukkitListener implements Listener {
    private final ConfigManager configManager;

    @EventHandler(ignoreCancelled = true)
    public void onPlayerChat(AsyncPlayerChatEvent e) {
        Player player = e.getPlayer();
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

        String message = e.getMessage();
        if (!configManager.isLocalChatStatus() || message.startsWith("!")) {
            message = removeGlobalPrefix(message);
            String formatMessage = ChatUtils.replacePlaceholders(player, configManager.getGlobalChatFormat())
                    .replace("{player}", player.getName())
                    .replace("{message}", message);
            e.setFormat(formatMessage);
            return;
        }

        e.getRecipients().clear();
        e.getRecipients().add(player);
        List<Player> recipients = getRadius(player);
        if (!recipients.isEmpty()) {
            e.getRecipients().addAll(recipients);
        }

        String formatMessage = ChatUtils.replacePlaceholders(player, configManager.getGlobalChatFormat())
                .replace("{player}", player.getName())
                .replace("{message}", message);
        e.setFormat(formatMessage);
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
