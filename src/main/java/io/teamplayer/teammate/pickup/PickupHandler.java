package io.teamplayer.teammate.pickup;

import org.bukkit.Bukkit;
import org.bukkit.entity.Player;
import org.bukkit.event.EventHandler;
import org.bukkit.event.Listener;
import org.bukkit.event.entity.EntityPickupItemEvent;
import org.bukkit.plugin.Plugin;

import java.util.HashSet;
import java.util.Set;
import java.util.UUID;

public class PickupHandler {

    /**
     * Plugin instance.
     */
    private final Plugin plugin;

    /**
     * Set of UUIDs of players that have their pickups disabled.
     */
    private final Set<UUID> disabledPickups = new HashSet<>();

    /**
     * Storage for players that have disabled pickups.
     */
    private final PickupStorage pickupStorage;

    /**
     * Constructs a new {@link PickupHandler}.
     *
     * @param plugin        The plugin instance.
     * @param pickupStorage Storage for players that have disabled pickups.
     */
    public PickupHandler(Plugin plugin, PickupStorage pickupStorage) {
        this.plugin = plugin;
        this.pickupStorage = pickupStorage;

        Bukkit.getPluginManager().registerEvents(new PickupListener(), plugin);
        pickupStorage.getDisabled().thenAccept(disabledPickups::addAll);
    }

    /**
     * Sets the toggle for whether a player can pick up items or not.
     *
     * @param playerUUID The UUID of the player.
     * @param canPickup  The toggle for enabling or disabling item pickup.
     */
    void setPickupToggle(UUID playerUUID, boolean canPickup) {
        if (canPickup) {
            pickupStorage.removeDisabled(playerUUID);
            disabledPickups.remove(playerUUID);
        } else {
            pickupStorage.addDisabled(playerUUID);
            disabledPickups.add(playerUUID);
        }
    }

    /**
     * Checks if the pickup is disabled for a player.
     *
     * @param playerUUID The UUID of the player.
     * @return if the pickup is disabled for the player
     */
    boolean isPickupDisabled(UUID playerUUID) {
        return disabledPickups.contains(playerUUID);
    }

    /**
     * Temporarily enables pickups for a player for a specified number of ticks.
     *
     * @param playerUUID The UUID of the player.
     * @param ticks      The number of ticks for which the pickups should be enabled.
     */
    void tempEnablePickups(UUID playerUUID, long ticks) {
        if (disabledPickups.remove(playerUUID)) {
            Bukkit.getScheduler().runTaskLater(plugin, () -> setPickupToggle(playerUUID, false), ticks);
        }
    }

    /**
     * Listens for entity item pickups and cancels them if needed.
     */
    public class PickupListener implements Listener {
        @EventHandler
        public void onPickup(EntityPickupItemEvent event) {
            if (event.getEntity() instanceof Player player) {
                if (disabledPickups.contains(player.getUniqueId())) {
                    event.setCancelled(true);
                }
            }
        }
    }
}
