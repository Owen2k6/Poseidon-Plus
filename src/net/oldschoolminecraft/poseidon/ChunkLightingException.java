package net.oldschoolminecraft.poseidon;

public class ChunkLightingException extends Exception
{
    private Throwable originalThrowable;

    public ChunkLightingException(String message)
    {
        super(message);
    }

    public ChunkLightingException(String message, Throwable cause)
    {
        super(message, cause);
        this.originalThrowable = cause;
    }

    public ChunkLightingException(Throwable cause)
    {
        super(cause);
        this.originalThrowable = cause;
    }
}
