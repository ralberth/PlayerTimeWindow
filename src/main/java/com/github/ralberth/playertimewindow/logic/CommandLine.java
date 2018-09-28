package com.github.ralberth.playertimewindow.logic;

import com.github.ralberth.playertimewindow.model.AllPlayerSchedules;
import org.bukkit.command.Command;
import org.bukkit.command.CommandExecutor;
import org.bukkit.command.CommandSender;

import java.util.logging.Logger;


/**
 * Class to handle users typing "/timewindows" inside the game or from the console.
 *
 * This is a separate command handler here just to have good separation between command handling
 * and PlayerTimeWindow main class: the main class is just concerned about getting everything up
 * and wired, not in implementing business logic.  All logic resides in classes here in the
 * com.git...logic namespace.
 */
public class CommandLine implements CommandExecutor {

    private final AllPlayerSchedules schedules;
    private boolean active;


    public CommandLine(AllPlayerSchedules schedules) {
        this.schedules = schedules;
        this.active = true;
    }


    public void setActive(boolean active) {
        this.active = active;
    }


    @Override
    public boolean onCommand(CommandSender sender, Command cmd, String label, String[] args) {
        Logger log = sender.getServer().getLogger();
        if (active) {
            log.info("Player Time Windows:");
            schedules.dump(log);
        } else {
            log.info("Player Time Windows is disabled.");
        }
        return true;
    }
}
