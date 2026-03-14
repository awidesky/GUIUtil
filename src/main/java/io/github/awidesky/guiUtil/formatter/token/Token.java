package io.github.awidesky.guiUtil.formatter.token;

import io.github.awidesky.guiUtil.level.Level;

/**
 * Represents a formatting token used by a log formatter.
 *
 * <p>A {@code Token} is responsible for appending a specific portion of a
 * formatted log message to a {@link StringBuilder}. Different implementations
 * provide behavior for various pattern elements such as log level, message,
 * thread name, prefix, or literal text.
 *
 * <p>During formatting, tokens are evaluated in sequence and each token
 * contributes its corresponding text to the final log output.
 */
public interface Token {

    /**
     * Appends this token's formatted content to the provided {@link StringBuilder}.
     *
     * @param sb the target {@code StringBuilder} receiving the formatted output
     * @param level the log level associated with the message
     * @param prefix the logger prefix, if any
     * @param msg the log message content
     */
    void append(StringBuilder sb, Level level, String prefix, CharSequence msg);
}