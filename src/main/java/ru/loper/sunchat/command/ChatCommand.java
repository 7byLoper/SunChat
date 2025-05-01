package ru.loper.sunchat.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.bukkit.entity.Player;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.loper.sunchat.config.ConfigManager;
import ru.loper.suncore.utils.Colorize;
import ru.loper.suncore.utils.MessagesUtils;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ChatCommand implements CommandExecutor, TabCompleter {
    private final ConfigManager configManager;
    private final List<String> COMMANDS = List.of("reload", "status", "clear");

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 || !commandSender.hasPermission("sunchat.admin")) return true;

        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(commandSender);
            case "status" -> handleChatStatus(commandSender, args);
            case "clear" -> handleClearChat(commandSender);
            default -> sendUsage(commandSender);
        }

        return true;
    }

    private void sendUsage(CommandSender sender) {
        sender.sendMessage(Colorize.parse("&a ▶ &fДоступные аргументы: " + String.join(", ", COMMANDS)));
    }

    private void handleClearChat(CommandSender sender) {
        int clearedPlayers = 0;
        for (Player player : Bukkit.getOnlinePlayers()) {
            if (!player.hasPermission("sunchat.clearbypass")) {
                player.sendMessage("\n".repeat(100));
                clearedPlayers++;
            }
        }

        sender.sendMessage(Colorize.parse(String.format(
                "&a ▶ &fЧат очищен &7(%d игрокам)",
                clearedPlayers
        )));
    }

    private void handleReload(CommandSender sender) {
        configManager.reload();
        sender.sendMessage(Colorize.parse("&a ▶ &fПлагин успешно перезагружен"));
    }

    private void handleChatStatus(CommandSender sender, String[] args) {
        if (args.length != 2) {
            sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /sunchat status [close/open]"));
            return;
        }

        boolean newStatus;
        String statusKey;
        String statusDisplay;

        switch (args[1].toLowerCase()) {
            case "open" -> {
                newStatus = false;
                statusKey = "включен";
                statusDisplay = "открыт";
            }
            case "close" -> {
                newStatus = true;
                statusKey = "отключен";
                statusDisplay = "закрыт";
            }
            default -> {
                sender.sendMessage(Colorize.parse("&c ▶ &fИспользование: /sunchat status [close/open]"));
                return;
            }
        }

        if (configManager.isCloseChat() == newStatus) {
            sender.sendMessage(Colorize.parse(String.format("&c ▶ &fЧат уже %s", statusDisplay)));
            return;
        }

        configManager.setCloseChat(newStatus);

        if (configManager.isChangeCloseStatusMessageEnable()) {
            String message = configManager.getChangeCloseStatusMessage()
                    .replace("{player}", sender.getName())
                    .replace("{status}", statusKey);
            MessagesUtils.broadcast(message);
        }

        sender.sendMessage(Colorize.parse(String.format("&a ▶ &fВы успешно %s чат", statusDisplay)));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (!commandSender.hasPermission("sunchat.admin")) return Collections.emptyList();

        List<String> completions = new ArrayList<>();

        if (args.length == 1) {
            completions.addAll(COMMANDS);
        } else if (args.length == 2 && args[0].equalsIgnoreCase("status")) {
            completions.addAll(List.of("open", "close"));
        }

        return completions.stream()
                .filter(c -> c.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
