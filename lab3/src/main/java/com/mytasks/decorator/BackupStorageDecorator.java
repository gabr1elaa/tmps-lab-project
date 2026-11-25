package com.mytasks.decorator;

import com.mytasks.model.Task;
import com.mytasks.storage.TaskStorage;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardCopyOption;
import java.util.List;

/**
 * Concrete DECORATOR: before every save, copies the current file to "<file>.bak",
 * so the previous version can be recovered if something goes wrong.
 */
public class BackupStorageDecorator extends StorageDecorator {

    private final Path file;
    private final Path backup;

    public BackupStorageDecorator(TaskStorage wrapped, Path file) {
        super(wrapped);
        this.file = file;
        this.backup = file.resolveSibling(file.getFileName() + ".bak");
    }

    @Override
    public void save(List<Task> tasks) {
        if (Files.exists(file)) {
            try {
                Files.copy(file, backup, StandardCopyOption.REPLACE_EXISTING);   // the added behaviour
            } catch (IOException e) {
                throw new UncheckedIOException("Could not create backup " + backup, e);
            }
        }
        wrapped.save(tasks);                                                    // then the normal save
    }

    public Path getBackupFile() {
        return backup;
    }
}
