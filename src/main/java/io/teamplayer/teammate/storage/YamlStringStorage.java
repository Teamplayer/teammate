package io.teamplayer.teammate.storage;

import org.bukkit.Bukkit;
import org.bukkit.configuration.file.FileConfiguration;
import org.bukkit.configuration.file.YamlConfiguration;

import java.io.File;
import java.io.IOException;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.logging.Level;

/**
 * An implementation of {@link StringStorage} that uses a YAML file to store strings.
 */
public class YamlStringStorage implements StringStorage {

    /**
     * Executor to run all file operations on.
     */
    private final ExecutorService executor = Executors.newSingleThreadExecutor();

    /**
     * File to save data to.
     */
    private final File file;

    /**
     * File configuration used to interact with data.
     */
    private final FileConfiguration configFile;

    /**
     * Construct a new {@link YamlStringStorage}.
     *
     * @param file file to store strings in
     */
    public YamlStringStorage(File file) {
        this.file = file;
        configFile = YamlConfiguration.loadConfiguration(file);
    }

    @Override
    public CompletableFuture<Void> addString(String listName, String data) {
        return CompletableFuture.runAsync(() -> {
            List<String> stringList = configFile.getStringList(listName);

            stringList.add(data);
            configFile.set(listName, stringList);
            save();
        }, executor);
    }

    @Override
    public CompletableFuture<Boolean> removeString(String listName, String data) {
        return CompletableFuture.supplyAsync(() -> {
            List<String> stringList = configFile.getStringList(listName);

            if (stringList.remove(data)) {
                configFile.set(listName, stringList);
                save();

                return true;
            }

            return false;
        }, executor);
    }

    @Override
    public CompletableFuture<Set<String>> getList(String listName) {
        return CompletableFuture.supplyAsync(() -> new HashSet<>(configFile.getStringList(listName)), executor);
    }

    /**
     * Save the configuration file to disk.
     */
    private void save() {
        try {
            configFile.save(file);
        } catch (IOException e) {
            Bukkit.getLogger().log(Level.WARNING, "Error while attempting to save strings.", e);
        }
    }
}