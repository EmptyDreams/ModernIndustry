package top.kmar.mi.api.exception;

import java.io.PrintStream;
import java.io.PrintWriter;

/**
 * 表示这是一个转发的异常
 * @author EmptyDreams
 */
public class TransferException extends RuntimeException {

    private final Throwable src;
    private final String message;

    public static RuntimeException instance(Throwable throwable) {
        if (throwable instanceof RuntimeException) return (RuntimeException) throwable;
        else return new TransferException(throwable);
    }

    public static RuntimeException instance(String text, Throwable throwable) {
        return new TransferException(text, throwable);
    }

    private TransferException(String text, Throwable throwable) {
        if (throwable instanceof TransferException) {
            TransferException that = (TransferException) throwable;
            message = text + " -> " + that.message;
            src = that.src;
        } else {
            message = text;
            src = throwable;
        }
    }

    private TransferException(Throwable throwable) {
        this(null, throwable);
    }

    @Override
    public String getMessage() {
        return "[" + src.getClass().getName() + "]" + message + "\n\t\t\t-> " + src.getMessage();
    }

    @Override
    public String getLocalizedMessage() {
        return src.getLocalizedMessage();
    }

    @Override
    public synchronized Throwable getCause() {
        return src.getCause();
    }

    @Override
    public synchronized Throwable initCause(Throwable cause) {
        return src.initCause(cause);
    }

    @Override
    public String toString() {
        return message + " -> " + src.toString();
    }

    @Override
    public void printStackTrace() {
        src.printStackTrace();
    }

    @Override
    public void printStackTrace(PrintStream s) {
        src.printStackTrace(s);
    }

    @Override
    public void printStackTrace(PrintWriter s) {
        src.printStackTrace(s);
    }

    @Override
    public StackTraceElement[] getStackTrace() {
        return src.getStackTrace();
    }

    @Override
    public void setStackTrace(StackTraceElement[] stackTrace) {
        src.setStackTrace(stackTrace);
    }

}