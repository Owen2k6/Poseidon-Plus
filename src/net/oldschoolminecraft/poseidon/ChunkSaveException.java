package net.oldschoolminecraft.poseidon;

public class ChunkSaveException extends Exception
{
    private Throwable originalThrowable;

    public ChunkSaveException(String message)
    {
        super(message);
    }

    public ChunkSaveException(String message, Throwable cause)
    {
        super(message, cause);
        this.originalThrowable = cause;
    }

    public ChunkSaveException(Throwable cause)
    {
        super(cause);
    }
}
