package ru.loper.sunchat.utils;

import me.clip.placeholderapi.PlaceholderAPI;
import org.bukkit.entity.Player;
import ru.loper.suncore.utils.Colorize;

public class ChatUtils {

    public static String replacePlaceholders(Player player, String message) {
        if (PlaceholderAPI.containsPlaceholders(message)) {
            message = PlaceholderAPI.setPlaceholders(player, message);
        }
        return Colorize.parse(message);
    }

    public static String formatTime(int seconds) {
        int minutes = seconds / 60;
        int hours = minutes / 60;
        int days = hours / 60;
        seconds = seconds % 60;

        StringBuilder format = new StringBuilder();

        if (days > 0) format.append(days).append(" д. ");
        if (hours > 0) format.append(hours).append(" ч. ");
        if (minutes > 0) format.append(minutes).append(" мин. ");
        if (seconds > 0) format.append(seconds).append(" сек. ");

        return format.toString();
    }
}
