package org.bukkit.plugin;

public class AuthorNagException extends RuntimeException {
    private final String message;

    /**
     * Constructs a new UnknownDependencyException based on the given Exception
     */
    public AuthorNagException(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
