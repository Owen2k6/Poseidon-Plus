package org.bukkit.plugin;

public class AuthorNagException extends RuntimeException {
    private final String message;

    /**
     * Constructs a new UnknownDependencyException based on the given Exception
     *
     * @param message Brief message explaining the cause of the exception
     */
    public AuthorNagException(final String message) {
        this.message = message;
    }

    @Override
    public String getMessage() {
        return message;
    }
}
