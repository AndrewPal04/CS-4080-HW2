package com.craftinginterpreters.lox;

import java.util.ArrayList;
import java.util.List;

class Scanner {

    private final String source;
    private final List<Token> tokens = new ArrayList<>();
    private int start = 0;
    private int current = 0;
    private int line = 1;

    Scanner(String source) {
        this.source = source;
    }

    List<Token> scanTokens() {
        while (!isAtEnd()) {
            start = current;
            scanToken();
        }

        tokens.add(new Token(TokenType.EOF, "", null, line));
        return tokens;
    }

    private void scanToken() {
        char c = advance();

        switch (c) {
            case '(' -> addToken(TokenType.LEFT_PAREN);
            case ')' -> addToken(TokenType.RIGHT_PAREN);
            case '{' -> addToken(TokenType.LEFT_BRACE);
            case '}' -> addToken(TokenType.RIGHT_BRACE);
            case ',' -> addToken(TokenType.COMMA);
            case '.' -> addToken(TokenType.DOT);
            case '-' -> addToken(TokenType.MINUS);
            case '+' -> addToken(TokenType.PLUS);
            case ';' -> addToken(TokenType.SEMICOLON);
            case '*' -> addToken(TokenType.STAR);

            case '/' -> {
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd()) advance();
                } else if (match('*')) {
                    blockComment();
                } else {
                    addToken(TokenType.SLASH);
                }
            }

            case ' ', '\r', '\t' -> {
                // ignore whitespace
            }

            case '\n' -> line++;

            default -> {
                // Ignore other characters
            }
        }
    }

    private void blockComment() {
        int depth = 1;

        while (depth > 0 && !isAtEnd()) {
            if (peek() == '\n') line++;

            if (peek() == '/' && peekNext() == '*') {
                advance();
                advance();
                depth++;
            } else if (peek() == '*' && peekNext() == '/') {
                advance();
                advance();
                depth--;
            } else {
                advance();
            }
        }
    }

    private boolean isAtEnd() {
        return current >= source.length();
    }

    private char advance() {
        return source.charAt(current++);
    }

    private void addToken(TokenType type) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, null, line));
    }

    private boolean match(char expected) {
        if (isAtEnd()) return false;
        if (source.charAt(current) != expected) return false;
        current++;
        return true;
    }

    private char peek() {
        if (isAtEnd()) return '\0';
        return source.charAt(current);
    }

    private char peekNext() {
        if (current + 1 >= source.length()) return '\0';
        return source.charAt(current + 1);
    }

    // for testing scanner useage
    static void selfTest() {
        String testSource = "/* block comment */ ()";
        Scanner scanner = new Scanner(testSource);
        scanner.scanTokens();
    }
}
