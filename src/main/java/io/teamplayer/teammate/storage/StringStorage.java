package io.teamplayer.teammate.storage;

import java.util.Set;
import java.util.concurrent.CompletableFuture;

/**
 * This interface represents a storage for managing string lists. It provides methods to add and remove strings from a
 * specific list, as well as retrieve the list.
 */
public interface StringStorage {

    /**
     * Adds a string to the list identified by name in the storage.
     *
     * @param listName Identifier of the list.
     * @param data     String to be added.
     * @return a future that completes when the string has been added
     */
    CompletableFuture<Void> addString(String listName, String data);

    /**
     * Removes a string from the list identified by name in the storage.
     *
     * @param listName Identifier of the list.
     * @param data     String to be removed.
     * @return a future that completes with if the string existed in the list
     */
    CompletableFuture<Boolean> removeString(String listName, String data);


    /**
     * Retrieves the list of strings identified by the given identifier from the storage.
     *
     * @param listName Identifier of the list.
     * @return a future that completes with the list of strings
     */
    CompletableFuture<Set<String>> getList(String listName);
}
