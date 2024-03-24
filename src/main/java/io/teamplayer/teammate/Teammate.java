package io.teamplayer.teammate;

import co.aikar.commands.PaperCommandManager;
import io.teamplayer.teammate.pickup.PickupHandler;
import io.teamplayer.teammate.pickup.PickupStorage;
import io.teamplayer.teammate.pickup.PickupToggleCommand;
import io.teamplayer.teammate.pickup.YamlPickupStorage;
import io.teamplayer.teammate.placeholder.SimpleVanishPlaceholder;
import io.teamplayer.teammate.storage.YamlStringStorage;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.plugin.java.JavaPlugin;

import java.io.File;

public final class Teammate extends JavaPlugin {

    /**
     * File to store most data.
     */
    private final File dataFile = new File(getDataFolder(), "data.yml");

    @Override
    public void onEnable() {
        saveDefaultConfig();

        PaperCommandManager commandManager = new PaperCommandManager(this);

        registerPlaceholders();

        // register pickup handling
        PickupStorage pickupStorage = new YamlPickupStorage(new YamlStringStorage(dataFile));
        PickupHandler pickupHandler = new PickupHandler(this, pickupStorage);

        commandManager.registerCommand(new PickupToggleCommand(pickupHandler));
    }

    /**
     * Registers placeholders.
     */
    private void registerPlaceholders() {
        // register vanish placeholder(s)
        if (Bukkit.getPluginManager().isPluginEnabled("AdvancedVanish")) {
            new SimpleVanishPlaceholder(ChatColor.translateAlternateColorCodes('&', getConfig().getString("vanish-icon", "&7[V]"))).register();
        }
    }
}
