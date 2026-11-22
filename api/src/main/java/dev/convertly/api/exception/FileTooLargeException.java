package dev.convertly.api.exception;

import java.util.UUID;

public class FileTooLargeException extends RuntimeException {
    public FileTooLargeException(UUID id) {
        super("File is larger than 10 MB: " + id);
    }
}
