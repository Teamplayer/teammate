package io.teamplayer.teammate.pickup;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;

/**
 * Storage for storing whether a player has their pickup disabled.
 */
public interface PickupStorage {

    /**
     * Add a player to the storage that has their pickup disabled.
     *
     * @param playerUUID UUID of player
     * @return future that completes when operation is successful
     */
    CompletableFuture<Void> addDisabled(UUID playerUUID);

    /**
     * Remove a player from storage that no longer has their pickup disabled.
     *
     * @param playerUUID UUID of player
     * @return future that completes when operation is successful
     */
    CompletableFuture<Void> removeDisabled(UUID playerUUID);

    /**
     * Get UUIDs of players that have their pickup disabled.
     *
     * @return future that contains set of players with disabled pickups
     */
    CompletableFuture<Set<UUID>> getDisabled();

}
