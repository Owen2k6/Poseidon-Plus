package net.oldschoolminecraft.poseidon;

import java.io.OutputStream;
import java.io.PrintStream;
import java.nio.charset.StandardCharsets;

public class CompatibilityPrintStream extends PrintStream
{
    private boolean lastWasNewline = false;

    public CompatibilityPrintStream(OutputStream out)
    {
        super(out, true, StandardCharsets.UTF_8);
    }

    @Override
    public void write(int b)
    {
        if (b == '\n')
        {
            if (lastWasNewline)
                return; // suppress duplicate newline
            lastWasNewline = true;
        } else lastWasNewline = false;

        super.write(b);
    }

    @Override
    public void write(byte[] buf, int off, int len)
    {
        for (int i = off; i < off + len; i++)
        {
            write(buf[i]);
        }
    }
}