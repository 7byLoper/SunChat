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

import java.util.Collections;
import java.util.List;

@RequiredArgsConstructor
public class ChatCommand implements CommandExecutor, TabCompleter {
    private final ConfigManager configManager;

    @Override
    public boolean onCommand(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 0) return true;
        if (args[0].equalsIgnoreCase("reload")) {
            configManager.reload();
            commandSender.sendMessage(Colorize.parse("&a ▶ &fПлагин успешно перезагружен"));
        }
        return true;
    }

    @Override
    public @Nullable List<String> onTabComplete(@NotNull CommandSender commandSender, @NotNull Command command, @NotNull String s, @NotNull String[] args) {
        if (args.length == 1) {
            return List.of("reload");
        }
        return Collections.emptyList();
    }
}
