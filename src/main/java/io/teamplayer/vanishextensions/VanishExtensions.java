package io.teamplayer.vanishextensions;

import io.teamplayer.vanishextensions.placeholder.SimpleVanishPlaceholder;
import org.bukkit.Bukkit;
import org.bukkit.ChatColor;
import org.bukkit.configuration.ConfigurationSection;
import org.bukkit.plugin.java.JavaPlugin;

public final class VanishExtensions extends JavaPlugin {

    @Override
    public void onEnable() {

        saveDefaultConfig();
        ConfigurationSection config = getConfig();

        // register placeholder(s)
        if (!Bukkit.getPluginManager().isPluginEnabled("PlaceholderAPI"))
            return;

        new SimpleVanishPlaceholder(ChatColor.translateAlternateColorCodes('&', config.getString("vanish-icon", "&7[V]"))).register();

    }

}
