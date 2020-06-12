package au.com.mineauz.minigames;

import java.io.PrintWriter;
import java.io.StringWriter;
import java.util.logging.Handler;
import java.util.logging.LogRecord;

/**
 * Created for the AddstarMC IT Project.
 * Created by Narimm on 12/06/2020.
 */
class StartUpLogHandler extends Handler {


    private final StringBuilder builder = new StringBuilder();
    private final StringBuilder exceptionBuilder = new StringBuilder();

    String getExceptionLog() {
        return exceptionBuilder.toString();
    }

    String getNormalLog() {
        return builder.toString();
    }
    @Override
    public void publish(LogRecord record) {
        builder.append('[').append(record.getLevel().getName()).append("] ").append(record.getMessage()).append('\n');
        if (record.getThrown() != null) {
            StringWriter stringWriter = new StringWriter();
            PrintWriter printWriter = new PrintWriter(stringWriter);
            record.getThrown().printStackTrace(printWriter);
            exceptionBuilder.append('[').append(record.getLevel().getName()).append("] ").append(record.getMessage()).append('\n')
                    .append(stringWriter.toString()).append('\n');
        }
    }

    @Override
    public void flush() {

    }

    @Override
    public void close() throws SecurityException {

    }
}
