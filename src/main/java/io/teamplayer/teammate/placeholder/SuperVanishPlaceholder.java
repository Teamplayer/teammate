package io.teamplayer.teammate.placeholder;

import me.clip.placeholderapi.expansion.PlaceholderExpansion;
import me.quantiom.advancedvanish.util.AdvancedVanishAPI;
import org.bukkit.Bukkit;
import org.bukkit.entity.Player;

public class SuperVanishPlaceholder extends PlaceholderExpansion {

    private final String vanishIcon;

    public SuperVanishPlaceholder(String vanishIcon) {
        this.vanishIcon = vanishIcon;
    }

    @Override
    public String getIdentifier() {
        return PlaceholderConstants.PREFIX + "vanished";
    }

    @Override
    public String getAuthor() {
        return PlaceholderConstants.AUTHOR;
    }

    @Override
    public String getVersion() {
        return PlaceholderConstants.VERSION;
    }

    @Override
    public boolean persist() {
        return true;
    }

    @Override
    public boolean canRegister() {
        return Bukkit.getPluginManager().isPluginEnabled("SuperVanish")
                && Bukkit.getPluginManager().isPluginEnabled("PremiumVanish");
    }

    @Override
    public String onPlaceholderRequest(Player player, String params) {
        if (player == null || !AdvancedVanishAPI.INSTANCE.isPlayerVanished(player))
            return "";

        return vanishIcon;
    }
}
