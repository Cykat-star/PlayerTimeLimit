package com.cykatstar.PlayerTimeLimit;

import com.cykatstar.PlayerTimeLimit.managers.MessageManager;
import com.cykatstar.PlayerTimeLimit.managers.PlayerManager;
import com.cykatstar.PlayerTimeLimit.model.TimeLimitPlayer;
import com.cykatstar.PlayerTimeLimit.utils.UtilsTime;
import org.bukkit.Bukkit;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.entity.Player;

public class Comando implements CommandExecutor {

    private final PlayerTimeLimit plugin;

    public Comando(PlayerTimeLimit plugin) {
        this.plugin = plugin;
    }

    @Override
    public boolean onCommand(CommandSender sender, Command command, String label, String[] args) {
        FileConfiguration messages = plugin.getMessages();
        MessageManager msgManager = plugin.getMessageManager();

        boolean isAdmin = sender.isOp() || sender.hasPermission("playertimelimit.admin");
        
        if (args.length == 0) {
            if (isAdmin) help(sender);
            else msgManager.sendMessage(sender, messages.getString("noPermissions"), true);
            return true;
        }

        String subCommand = args[0].toLowerCase();
        
        if (!sender.hasPermission("playertimelimit.command." + subCommand) && !isAdmin) {
            msgManager.sendMessage(sender, messages.getString("noPermissions"), true);
            return true;
        }

        switch (subCommand) {
            case "reload": reload(sender, messages, msgManager); break;
            case "message": 
                if (sender instanceof Player) message((Player) sender, messages, msgManager); 
                else sender.sendMessage("Only players can toggle messages.");
                break;
            case "info": 
                if (sender instanceof Player) info((Player) sender, messages, msgManager); 
                break;
            case "check": check(sender, args, messages, msgManager); break;
            case "resettime": resettime(sender, args, messages, msgManager); break;
            case "taketime": taketime(sender, args, messages, msgManager); break;
            case "addtime": addtime(sender, args, messages, msgManager); break;
            default: help(sender); break;
        }

        return true;
    }

    private void reload(CommandSender sender, FileConfiguration messages, MessageManager msgManager) {
        plugin.reloadPluginConfigs();
        msgManager.sendMessage(sender, messages.getString("commandReload"), true);
    }

    private void message(Player player, FileConfiguration messages, MessageManager msgManager) {
        TimeLimitPlayer p = plugin.getPlayerManager().getPlayerByUUID(player.getUniqueId().toString());
        if (p == null) return;

        p.setMessageEnabled(!p.isMessageEnabled());
        String key = p.isMessageEnabled() ? "messageEnabled" : "messageDisabled";
        msgManager.sendMessage(player, messages.getString(key), true);
    }

    private void info(Player player, FileConfiguration messages, MessageManager msgManager) {
        String timeReset = plugin.getConfigsManager().getMainConfigManager().getResetTime();
        String remaining = plugin.getServerManager().getRemainingTimeForTimeReset();

        for (String m : messages.getStringList("infoCommandMessage")) {
            player.sendMessage(MessageManager.getColoredMessage(
                    m.replace("%reset_time%", timeReset).replace("%remaining%", remaining)
            ));
        }
    }

    private void check(CommandSender sender, String[] args, FileConfiguration messages, MessageManager msgManager) {
        String targetName = (args.length > 1) ? args[1] : sender.getName();
        
        TimeLimitPlayer p = plugin.getPlayerManager().getPlayers().stream()
                .filter(player -> player.getName().equalsIgnoreCase(targetName))
                .findFirst().orElse(null);

        if (p == null) {
            msgManager.sendMessage(sender, messages.getString("playerNotOnline"), true);
            return;
        }

        Player targetPlayer = p.getPlayer();
        int timeLimit = plugin.getPlayerManager().getTimeLimitPlayer(targetPlayer);
        String timeLeft = plugin.getPlayerManager().getTimeLeft(p, timeLimit);
        String totalTime = UtilsTime.getTime(p.getTotalTime(), msgManager);

        for (String m : messages.getStringList("checkCommandMessage")) {
            sender.sendMessage(MessageManager.getColoredMessage(
                    m.replace("%player%", p.getName())
                     .replace("%time_left%", timeLeft)
                     .replace("%total_time%", totalTime)
            ));
        }
    }

    private void resettime(CommandSender sender, String[] args, FileConfiguration messages, MessageManager msgManager) {
        if (args.length < 2) {
            msgManager.sendMessage(sender, messages.getString("commandResetTimeError"), true);
            return;
        }

        TimeLimitPlayer p = plugin.getPlayerManager().getPlayers().stream()
                .filter(player -> player.getName().equalsIgnoreCase(args[1]))
                .findFirst().orElse(null);

        if (p == null) {
            msgManager.sendMessage(sender, messages.getString("playerNotOnline"), true);
            return;
        }

        p.resetTime();
        msgManager.sendMessage(sender, messages.getString("commandResetTimeCorrect").replace("%player%", args[1]), true);
    }

    private void taketime(CommandSender sender, String[] args, FileConfiguration messages, MessageManager msgManager) {
        if (args.length < 3) {
            msgManager.sendMessage(sender, messages.getString("commandTakeTimeError"), true);
            return;
        }
        handleTimeAdjustment(sender, args[1], args[2], false, messages, msgManager);
    }

    private void addtime(CommandSender sender, String[] args, FileConfiguration messages, MessageManager msgManager) {
        if (args.length < 3) {
            msgManager.sendMessage(sender, messages.getString("commandAddTimeError"), true);
            return;
        }
        handleTimeAdjustment(sender, args[1], args[2], true, messages, msgManager);
    }

    private void handleTimeAdjustment(CommandSender sender, String targetName, String timeStr, boolean isAdd, FileConfiguration messages, MessageManager msgManager) {
        int time;
        try {
            time = Integer.parseInt(timeStr);
            if (time <= 0) throw new NumberFormatException();
        } catch (NumberFormatException e) {
            msgManager.sendMessage(sender, messages.getString("invalidNumber"), true);
            return;
        }

        TimeLimitPlayer p = plugin.getPlayerManager().getPlayers().stream()
                .filter(player -> player.getName().equalsIgnoreCase(targetName))
                .findFirst().orElse(null);

        if (p == null) {
            msgManager.sendMessage(sender, messages.getString("playerNotOnline"), true);
            return;
        }

        if (isAdd) plugin.getPlayerManager().addTime(p, time);
        else plugin.getPlayerManager().takeTime(p, time);

        String msgKey = isAdd ? "commandAddTimeCorrect" : "commandTakeTimeCorrect";
        msgManager.sendMessage(sender, messages.getString(msgKey)
                .replace("%player%", targetName)
                .replace("%time%", String.valueOf(time)), true);
    }

    public void help(CommandSender sender) {
        sender.sendMessage(MessageManager.getColoredMessage("&c&m-----------------------------------------"));
        sender.sendMessage(MessageManager.getColoredMessage("      &b&lPlayerTime&c&lLimit &eCommands"));
        sender.sendMessage(MessageManager.getColoredMessage(" "));
        sender.sendMessage(MessageManager.getColoredMessage("&8- &c/ptl message &7Toggle time limit info message."));
        sender.sendMessage(MessageManager.getColoredMessage("&8- &c/ptl info &7Shows time until next reset."));
        sender.sendMessage(MessageManager.getColoredMessage("&8- &c/ptl check [player] &7Check time stats."));
        sender.sendMessage(MessageManager.getColoredMessage("&8- &c/ptl resettime <player> &7Resets player's time."));
        sender.sendMessage(MessageManager.getColoredMessage("&8- &c/ptl addtime <player> <seconds> &7Adds time."));
        sender.sendMessage(MessageManager.getColoredMessage("&8- &c/ptl taketime <player> <seconds> &7Removes time."));
        sender.sendMessage(MessageManager.getColoredMessage("&8- &c/ptl reload &7Reloads the configuration."));
        sender.sendMessage(MessageManager.getColoredMessage("&c&m-----------------------------------------"));
    }
}