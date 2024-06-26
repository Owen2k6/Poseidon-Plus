package net.minecraft.server;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Formatter;
import java.util.logging.Level;
import java.util.logging.LogRecord;

class GuiLogFormatter extends Formatter {

    final GuiLogOutputHandler a;

    GuiLogFormatter(GuiLogOutputHandler guilogoutputhandler) {
        this.a = guilogoutputhandler;
    }

    public String format(LogRecord logrecord) {
        StringBuilder stringbuilder = new StringBuilder();
        Level level = logrecord.getLevel();

        if (level == Level.FINEST) {
            stringbuilder.append("[FINEST] ");
        } else if (level == Level.FINER) {
            stringbuilder.append("[FINER] ");
        } else if (level == Level.FINE) {
            stringbuilder.append("[FINE] ");
        } else if (level == Level.INFO) {
            stringbuilder.append("[INFO] ");
        } else if (level == Level.WARNING) {
            stringbuilder.append("[WARNING] ");
        } else if (level == Level.SEVERE) {
            stringbuilder.append("[").append(level.getLocalizedName()).append("] ");
        }

        stringbuilder.append(logrecord.getMessage());
        stringbuilder.append('\n');
        Throwable throwable = logrecord.getThrown();

        if (throwable != null) {
            StringWriter stringwriter = new StringWriter();

            throwable.printStackTrace(new PrintWriter(stringwriter));
            stringbuilder.append(stringwriter.toString());
        }

        return stringbuilder.toString();
    }
}
