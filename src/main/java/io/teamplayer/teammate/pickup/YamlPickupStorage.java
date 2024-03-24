package io.teamplayer.teammate.pickup;

import io.teamplayer.teammate.storage.StringStorage;

import java.util.Set;
import java.util.UUID;
import java.util.concurrent.CompletableFuture;
import java.util.stream.Collectors;

/**
 * A {@link PickupStorage} that uses a YAML file to store players with disabled pickups.
 */
public class YamlPickupStorage implements PickupStorage {

    private static final String LIST_NAME = "pickup-disabled-players";

    /**
     * The storage where UUID are stored.
     */
    private final StringStorage backingStorage;

    /**
     * Construct a new {@link YamlPickupStorage}.
     *
     * @param backingStorage the backing storage for the UUIDs
     */
    public YamlPickupStorage(StringStorage backingStorage) {
        this.backingStorage = backingStorage;
    }

    @Override
    public CompletableFuture<Void> addDisabled(UUID playerUUID) {
        return backingStorage.addString(LIST_NAME, playerUUID.toString());
    }

    @Override
    public CompletableFuture<Void> removeDisabled(UUID playerUUID) {
        return backingStorage.removeString(LIST_NAME, playerUUID.toString())
                .thenRun(() -> {});
    }

    @Override
    public CompletableFuture<Set<UUID>> getDisabled() {
        return backingStorage.getList(LIST_NAME)
                .thenApply(s -> s.stream()
                        .map(UUID::fromString)
                        .collect(Collectors.toSet()));
    }
}
