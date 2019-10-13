package au.com.mineauz.minigames.script;

import java.util.NoSuchElementException;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ExpressionParser {
    private static Pattern pathSegmentPattern = Pattern.compile("([a-z]+)(?:\\[([a-z0-9]+)\\])?");

    /**
     * Parses a path and resolves an object from the path.
     * Example: path 'a.b.c' will resolve to the c object if given the a object
     *
     * @param pathString The path to resolve
     * @param root       The root object
     * @return The resolved object
     * @throws IllegalArgumentException Thrown if there is a syntax error in the path
     * @throws NoSuchElementException   Thrown if the objects referenced in the path cannot be resolved
     */
    public static ScriptReference resolveObject(String pathString, ScriptObject root) throws IllegalArgumentException, NoSuchElementException {
        String[] segments = pathString.split("\\.");

        ScriptReference lastRef = null;
        ScriptObject current = root;
        String pathToCurrent = "";

        for (String segment : segments) {
            // Keep track of path for error display
            if (!pathToCurrent.isEmpty()) {
                pathToCurrent += ".";
            }
            pathToCurrent += segment;

            if (current == null) {
                throw new NoSuchElementException("Unable to resolve '" + pathToCurrent + "'");
            }

            Matcher matcher = pathSegmentPattern.matcher(segment);

            if (matcher.matches()) {
                String objectName = matcher.group(1);
                ScriptReference ref = current.get(objectName.toLowerCase());

                if (ref == null) {
                    throw new NoSuchElementException("Unknown object " + objectName + " at '" + pathToCurrent + "'");
                }

                // Index group
                if (matcher.group(2) != null) {
                    if (ref instanceof ScriptCollection) {
                        try {
                            ref = ((ScriptCollection) ref).getValue(matcher.group(2));
                        } catch (IllegalArgumentException e) {
                            throw new NoSuchElementException("Invalid key '" + matcher.group(2) + "' for '" + pathToCurrent + "'");
                        } catch (NoSuchElementException e) {
                            throw new NoSuchElementException("Key '" + matcher.group(2) + "' is not found for '" + pathToCurrent + "'");
                        }
                    }
                }

                lastRef = ref;
                if (ref instanceof ScriptObject) {
                    current = (ScriptObject) ref;
                } else {
                    current = null;
                }
            } else {
                throw new IllegalArgumentException("Invalid path part: " + pathToCurrent);
            }
        }

        return lastRef;
    }

    /**
     * <p>
     * Resolves all tokens within the input string.
     * Tokens are expected to start with $.
     * Paths that are complex should be surrounded
     * in braces {}. To use an actual $ double it.
     * ie. $4.60 becomes $$4.60
     * </p>
     * Example tokens:
     * <ul>
     * <li>${player.name}</li>
     * <li>${teams[red].name}</li>
     * <li>$player</li>
     * </ul>
     *
     * @param input  The input string
     * @param object The object to resolve from
     * @return The input string with all tokens resolved
     * @throws NoSuchElementException   Thrown if an object in a path cannot be resolved.
     * @throws IllegalArgumentException Thrown if there is a syntax error in a path.
     */
    public static String stringResolve(String input, ScriptObject object) throws IllegalArgumentException, NoSuchElementException {
        return stringResolve(input, object, false, false);
    }

    /**
     * <p>
     * Resolves all tokens within the input string.
     * Tokens are expected to start with $.
     * Paths that are complex should be surrounded
     * in braces {}. To use an actual $ double it.
     * ie. $4.60 becomes $$4.60
     * </p>
     * Example tokens:
     * <ul>
     * <li>${player.name}</li>
     * <li>${teams[red].name}</li>
     * <li>$player</li>
     * </ul>
     *
     * @param input                  The input string
     * @param object                 The object to resolve from
     * @param ignoreSyntaxErrors     When true, all path syntax errors will be ignored (IllegalArgumentException)
     * @param ignoreResolutionErrors When true, all resolution errors will be ignored (NoSuchElementException)
     * @return The input string with all tokens resolved
     * @throws NoSuchElementException   Thrown if an object in a path cannot be resolved. Only thrown if ignoreResolutionErrors is false
     * @throws IllegalArgumentException Thrown if there is a syntax error in a path. Only thrown if ignoreSyntaxErrors is false
     */
    public static String stringResolve(String input, ScriptObject object, boolean ignoreSyntaxErrors, boolean ignoreResolutionErrors) throws IllegalArgumentException, NoSuchElementException {
        StringBuilder buffer = new StringBuilder(input);
        int start = 0;
        int index = 0;

        while (true) {
            index = buffer.indexOf("$", start);
            if (index == -1) {
                break;
            }

            start = index + 1;

            // Check for end of string
            if (start >= buffer.length()) {
                break;
            }

            // Check for $$
            if (buffer.charAt(start) == '$') {
                // Just delete one of the $ symbols
                buffer.delete(start - 1, start);
                continue;
            }

            String path;
            // Check for brace pattern
            if (buffer.charAt(start) == '{') {
                int braceEnd = buffer.indexOf("}", start + 1);
                if (braceEnd == -1) {
                    // Not a valid path
                    continue;
                }

                path = buffer.substring(start + 1, braceEnd);
                start = braceEnd + 1;
            } else {
                // Find a non alphanumeric char
                path = null;
                for (int i = start; i < buffer.length(); ++i) {
                    if (!Character.isLetterOrDigit(buffer.charAt(i))) {
                        path = buffer.substring(start, i);
                        start = i;
                        break;
                    }
                }

                if (path == null) {
                    path = buffer.substring(start);
                    start = buffer.length();
                }
            }

            // Attempt to resolve it
            try {
                ScriptReference ref = resolveObject(path, object);
                buffer.delete(index, start);
                String toInsert = asString(ref);

                buffer.insert(index, toInsert);
                start = index + toInsert.length();
            } catch (NoSuchElementException e) {
                if (ignoreResolutionErrors) {
                    buffer.delete(index, start);
                    start = index;
                } else {
                    throw e;
                }
            } catch (IllegalArgumentException e) {
                if (ignoreSyntaxErrors) {
                    buffer.delete(index, start);
                    start = index;
                } else {
                    throw e;
                }
            }
        }

        return buffer.toString();
    }

    private static String asString(ScriptReference ref) {
        if (ref == null) {
            return "";
        } else if (ref instanceof ScriptValue<?>) {
            return ref.toString();
        } else if (ref instanceof ScriptObject) {
            return ((ScriptObject) ref).getAsString();
        } else {
            return String.valueOf(ref);
        }
    }
}
