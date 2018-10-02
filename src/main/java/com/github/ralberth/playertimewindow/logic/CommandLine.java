package com.github.ralberth.playertimewindow.logic;

import com.github.ralberth.playertimewindow.model.AllPlayerSchedules;
import com.github.ralberth.playertimewindow.util.EnabledStatus;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;
import org.bukkit.command.ConsoleCommandSender;
import org.bukkit.entity.Player;

import java.util.List;


/**
 * Class to handle users typing "/timewindows" inside the game or from the console.
 *
 * This is a separate command handler here just to have good separation between command handling
 * and PlayerTimeWindow main class: the main class is just concerned about getting everything up
 * and wired, not in implementing business logic.  All logic resides in classes here in the
 * com.git...logic namespace.
 */
public class CommandLine implements CommandExecutor {

    // Must agree with src/main/resources/plugin.yml
    private static final String PERMISSION_ENABLE_DISABLE = "playertimewindow.enabledisable";

    private final AllPlayerSchedules schedules;
    private final EnabledStatus status;


    public CommandLine(AllPlayerSchedules schedules, EnabledStatus status) {
        this.schedules = schedules;
        this.status = status;
    }


    /**
     * Main entrypoint -- this is the callback from the Minecraft server when someone types "/timewindows".
     */
    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        if (args.length == 0 || args.length == 1 && args[0].equalsIgnoreCase("list"))
            return list(sender);
        else if (args.length == 1 && args[0].equalsIgnoreCase("enable"))
            return enable(sender);
        else if (args.length == 2 && args[0].equalsIgnoreCase("disable"))
            return disable(sender, args[1]);
        else if (args.length == 1 && args[0].equalsIgnoreCase("status"))
            return status(sender);
        else
            return false;
    }


    /**
     * Handles "/timewindows list" and "/timewindows".
     */
    private boolean list(CommandSender sender) {
        sender.sendMessage("Player Time Windows:");
        List<String> scheds = schedules.dump();
        if (scheds.isEmpty())
            sender.sendMessage("   (empty");
        else
            for(String line : scheds)
                sender.sendMessage("   " + line);
        return true;
    }


    /**
     * Handles "/timewindows enable".
     */
    private boolean enable(CommandSender sender) {
        if (status.isEnabled()) {
            sender.sendMessage("Player time windows are already enabled.");
        } else {
            if (senderHasEnableDisablePermission(sender)) {
                status.enable();
                sender.getServer().broadcastMessage("Player time windows are now being enforced.");
            } else {
                sender.sendMessage("You don't have " + PERMISSION_ENABLE_DISABLE + " permission.");
            }
        }
        return true;
    }


    /**
     * Handles "/timewindows disable 6".
     */
    private boolean disable(CommandSender sender, String arg) {
        if (!status.isEnabled()) {
            sender.sendMessage("Player time windows are already disabled.");
            return true;
        } else {
            if (senderHasEnableDisablePermission(sender)) {
                try {
                    int hours = Integer.parseInt(arg);
                    if (hours > 0) {
                        status.disable(hours);
                        sender.getServer().broadcastMessage("Player time windows disabled for " + hours + " hours.");
                        return true;
                    } else {
                        sender.sendMessage(arg + " should be a positive number of hours.");
                        return false;
                    }
                } catch (NumberFormatException e) {
                    sender.sendMessage("\"" + arg + "\" should be a positive number of hours.");
                    return false;
                }
            } else {
                sender.sendMessage("You don't have " + PERMISSION_ENABLE_DISABLE + " permission.");
                return false;
            }
        }
    }


    /**
     * Handles "/timewindows status".
     */
    private boolean status(CommandSender sender) {
        sender.sendMessage("Player time windows are currently " + (status.isEnabled() ? "enabled" : "disabled"));
        return true;
    }


    /**
     * Predicate here in support of D.R.Y.: returns true when this class should allow the caller to
     * change the enable/disable state of the plugin.
     *
     * This isn't just a simple call to sender.hasPermission(): regardless of the permission plugin being used and
     * what permissions are set, all invocations from the console should be allowed.
     * Human players should be subject to the permissions plugin.
     * Everything else is disabled (like Command blocks, mobs, etc.).
     */
    private boolean senderHasEnableDisablePermission(CommandSender sender) {
        return sender instanceof ConsoleCommandSender ||
                (sender instanceof Player && sender.hasPermission(PERMISSION_ENABLE_DISABLE));
    }
}
