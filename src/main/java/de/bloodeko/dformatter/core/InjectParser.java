package de.bloodeko.dformatter.core;

import java.util.StringJoiner;

/**
 * Updates an inject line by replacing locally with the full 
 * scriptName and prefixing a given path, with the path argument.
 */
public class InjectParser {
    private final String cmdPattern = "- inject ";
    private String scriptName = "";
    
    /**
     * Returns a parsed inject line, or the input line. When a
     * scriptName is found, it will cache it, for the next call.
     */
    public String parseLine(String line) {
        int indention = Parser.getIndention(line);
        if (setScriptname(indention, line)) {
            return line;
        }
        
        String token = line.substring(indention);
        if (token.startsWith(cmdPattern)) {
            String spaces = line.substring(0, indention);
            String cmd = token.substring(cmdPattern.length());
            return spaces + cmdPattern + parseArguments(cmd);
        }
        
        return line;
    }
    
    private boolean setScriptname(int indention, String line) {
        if (indention == 0 && Parser.endsWithSymbol(line, ':')) {
            scriptName = line.substring(0, line.lastIndexOf(':'));
            return true;
        }
        return false;
    }
    
    /**
     * Applies very basic parsing logic to the arguments 
     * and splits them on spaces and colons.
     */
    private String parseArguments(String arguments) {
        StringJoiner joiner = new StringJoiner(" ");
        String[] args = arguments.split(" ");
        
        boolean script = false;
        boolean path = false;
        
        for (String arg : args) {
            if (!script) {
                script = true;
                if (arg.equals("locally")) {
                    joiner.add(scriptName);
                } else {
                    joiner.add(arg);
                }
            }
            else if (!path) {
                path = true;
                if (arg.split(":").length == 1) {
                    joiner.add("path:" + arg);
                } else {
                    joiner.add(arg);
                }
            }
            else {
                joiner.add(arg);
            }
        }
        return joiner.toString();
    }
}
