package de.bloodeko.dformatter.core;


import java.util.ArrayList;
import java.util.List;

/** 
 * <pre>
 *  {
 *  }
 * - if true {
 * - if true
 *   && true {
 *  } else {
 *  } else
 *  else {
 *  else if {
 *  </pre>
 *  
 *  Applies changes to the lines that represent a script file.
 *  Converts bracket syntax to colon syntax by going through
 *  most possible scenarios. Also replaces shortcut tokens.
 */
public class Parser {
    
    private final List<String> rawLines;
    private final List<String> modified;
    
    private int index;
    private String line;
    
    public Parser(List<String> lines) {
        rawLines = lines;
        modified = new ArrayList<>();
    }
    
    /**
     * Uses the input rawLines to create a new List with modified lines.
     * In the case of an error, gives an exception with the line index.
     */
    public List<String> convert() {
        if (index > 0) {
            return modified;
        }
        try {
            for (; index < rawLines.size(); index++) {
                this.line = rawLines.get(index);
                convertLine();
            }
            return modified;
        } catch (Exception ex) {
            throw new RuntimeException("Could not parse Line " + (index+1), ex);
        }
    }
    
    /**
     * Converts a single line. Tries to keep the line count identical.
     * The order, for which changes are applied, is defined here.
     */
    private void convertLine() {
        if (addComment()) return;
        convertTags();
        
        if (addSingleBracket()) return;
        if (addHasBracket()) return;
        if (addElseElseIf()) return;
        if (addOpSymbol()) return;
        addLine();
    }
    
    /* ------------------------|
     *                         |
     *     Syntax Handling     |
     *                         |
     * ------------------------|
     */
    
    private boolean addComment() {
        if (isBlank()) {
            addLine();
            return true;
        }
        return false;
    }
    
    private void convertTags() {
        line = line.replace("<pl.", "<player.")
                   .replace("<pl>", "<player>")
                   .replace("<int[", "<element[")
                   .replace("<key[", "<queue.script.yaml_key[")
                   .replace("<c.", "<context.");
    }
    
    private boolean addSingleBracket() {
        if (isSingleSymbol('{')) {
            appendColonToLast();
            addEmptyLine();
            return true;
        }
        if (isSingleSymbol('}')) {
            addEmptyLine();
            return true;
        }
        return false;
    }
    
    private boolean addHasBracket() {
        boolean beginsWithClose = startsWithSymbol('}');
        boolean endsWithOpen = endsWithSymbol('{');
        
        if (beginsWithClose) {
            if (endsWithOpen) {
                line = replaceBeginningBracket(line);
                line = stripEndingBracket(line);
                line = shiftPrefixCommand(line);
                line = suffixColon(line);
            } else {
                line = replaceBeginningBracket(line);
                line = shiftPrefixCommand(line);
                line = suffixColon(line);
            }
            addLine();
            return true;
        }
        
        if (endsWithOpen) {
            if (startsWithSymbol('&') || startsWithSymbol('|')) {
                line = stripEndingBracket(line);
                line = stripPrefix(line);
                line = suffixColon(line);
                appendToLast(line);
                addEmptyLine();
            } else {
                if (startsWithSymbol('-')) {
                    line = stripEndingBracket(line);
                    line = suffixColon(line);
                } else {
                    line = stripEndingBracket(line);
                    line = suffixColon(line);
                    line = shiftPrefixCommand(line);
                }
                addLine();
            }
            return true;
        }
        return false;
    }
    
    private boolean addElseElseIf() {
        if (line.trim().startsWith("else")) {
            String last = rawLines.get(index-1).trim();
            
            if (last.isEmpty() || last.equals("}")) {              
                String token = shiftPrefixCommand(line);
                modified.add(token);
                return true;
            }
        }
        return false;
    }
    
    private boolean addOpSymbol() {
        if (startsWithSymbol('&') || startsWithSymbol('|')) {
            appendToLast(stripPrefix(line));
            addEmptyLine();
            return true;
        }
        return false;
    }
    
    
    
    private String replaceBeginningBracket(String token) {
        return token.replace('}', ' ').substring(2);
    }
    
    private String stripEndingBracket(String token) {
        return token.substring(0, token.lastIndexOf('{') - 1);
    }
    
    private String shiftPrefixCommand(String token) {
        int spaces = getIndention(token);
        String prefix = token.substring(0, spaces-2);
        String content = token.substring(spaces);
        return prefix + "- " + content;
    }
    
    private String stripPrefix(String token) {
        return token.substring(getIndention(token)-1);
    }
    
    private String suffixColon(String token) {
        return trimTrailing(token) + ":";
    }
    
    private int getLastNonBlankLine() {
        int last = index-1;
        while (isBlank(modified.get(last))) {
            last--;
        }
        return last;
    }
    
    
    
    private void appendToLast(String token) {
        int last = getLastNonBlankLine();
        modified.set(last, modified.get(last) + token);
    }
    
    private void appendColonToLast() {
        int last = getLastNonBlankLine();
        modified.set(last, trimTrailing(modified.get(last)) + ":");
    }
    
    /* ------------------------|
     *                         |
     *     Helper methods      |
     *                         |
     * ------------------------|
     */
    
    private void addLine() {
        modified.add(line);
    }
    
    private void addEmptyLine() {
        modified.add("");
    }
    
    private boolean isBlank() {
        return isBlank(line);
    }
    
    private boolean isSingleSymbol(char ch) {
        return isSingleSymbol(line, ch);
    }
    
    private boolean startsWithSymbol(char ch) {
        return startsWithSymbol(line, ch);
    }
    
    private boolean endsWithSymbol(char ch) {
        return endsWithSymbol(line, ch);
    }
    
    /* ------------------------|
     *                         |
     *     String cutting      |
     *                         |
     * ------------------------|
     */
    
    static boolean isBlank(String line) {
        for (char c : line.toCharArray()) {
            if (c == '#') return true;
            if (c == ' ')  continue;
            if (c == '\t') continue;
            return false;
        }
        return true;
    }

    static boolean isSingleSymbol(String line, char ch) {
        char[] array = line.toCharArray();
        boolean found = false;
        for (int i = 0; i < array.length; i++) {
            char c = array[i];

            if (c == ' ')  continue;
            if (c == '\t') continue;
            if (c == ch)  {
                found = true;
                continue;
            }
            return false;
        }
        return found;
    }
    
    static String trimTrailing(String str) {
        int i;
        for (i = str.length()-1; i >= 0; i--) {
            char c = str.charAt(i);
            if (c == ' ') continue;
            if (c == '\t') continue;
            break;
        }
        return str.substring(0, i+1);
    }
    
    static int getIndention(String line) {
        char[] array = line.toCharArray();
        char c;
        
        for (int i = 0; i < array.length; i++) {
            c = array[i];
            
            if (c == ' ') continue;
            if (c == '\t') continue;
            return i;
        }
        return 0;
    }
    
    static boolean startsWithSymbol(String line, char ch) {
        for (char c : line.toCharArray()) {
            if (c == ch) return true;
            if (c == '\t') continue;
            if (c == ' ')  continue;
            return false;
        }
        return false;
    }

    static boolean endsWithSymbol(String line, char ch) {
        char[] array = line.toCharArray();
        char c;
        
        for (int i = line.length()-1; i >= 0; i--) {
            c = array[i];
            
            if (c == ch) return true;
            if (c == '\t') continue;
            if (c == ' ')  continue;
            return false;
        }
        return false;
    }
}

