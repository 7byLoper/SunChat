package ru.loper.sunchat.command;

import lombok.RequiredArgsConstructor;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.TabCompleter;
import org.jetbrains.annotations.NotNull;
import org.jetbrains.annotations.Nullable;
import ru.loper.sunchat.config.ConfigManager;
import ru.loper.suncore.utils.Colorize;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ChatCommand implements CommandExecutor, TabCompleter {
    private final ConfigManager configManager;
    private final List<String> COMMANDS = List.of("reload");

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0 || !commandSender.hasPermission("sunchat.admin")) return true;
        switch (args[0].toLowerCase()) {
            case "reload" -> handleReload(commandSender);
        }

        return true;
    }

    private void handleReload(CommandSender sender) {
        configManager.reload();
        sender.sendMessage(Colorize.parse("&a ▶ &fПлагин успешно перезагружен"));
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) return Collections.emptyList();

        List<String> complete = new ArrayList<>();

        if (args.length == 1) {
            complete.addAll(COMMANDS);
        }

        return complete.stream()
                .filter(filter -> filter.toLowerCase().startsWith(args[args.length - 1].toLowerCase()))
                .toList();
    }
}
