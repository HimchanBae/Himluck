package himluck;

import java.util.ArrayList;
// import java.util.HashMap;
import java.util.List;
// import java.util.Map;

import static himluck.TokenType.*;

public class Scanner {
    public final String source;
    public final List<Token> tokens = new ArrayList<>();
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

        tokens.add(new Token(EOF, "", null, line));
        return tokens;
    }

    public void scanToken() {
        char c = advance();
        switch (c) {
            case '(':
                addToken(LEFT_PAREN);
                break;
            case ')':
                addToken(RIGHT_PAREN);
                break;
            case '{':
                addToken(LEFT_BRACE);
                break;
            case '}':
                addToken(RIGHT_BRACE);
                break;
            case ',':
                addToken(COMMA);
                break;
            case '.':
                addToken(DOT);
                break;
            case '-':
                addToken(MINUS);
                break;
            case '+':
                addToken(PLUS);
                break;
            case ';':
                addToken(SEMICOLON);
                break;
            case '*':
                addToken(STAR);
                break;
            case '!':
                addToken(match('=') ? BANG_EQUAL : BANG);
                break;
            case '=':
                addToken(match('=') ? EQUAL_EQUAL : EQUAL);
                break;
            case '<':
                addToken(match('=') ? LESS_EQUAL : LESS);
                break;
            case '>':
                addToken(match('=') ? GREATER_EQUAL : GREATER);
                break;
            case '/':
                if (match('/')) {
                    while (peek() != '\n' && !isAtEnd())
                        advance();
                } else {
                    addToken(SLASH);
                }
                break;
            case ' ':
            case '\r':
            case '\t':
                // Ignore whitespace.
                break;

            case '\n':
                line++;
                break;
            case '"':
                string();
                break;
            case ':':
                if (isAlpha(peek())) {
                    symbol();
                } else {
                    Himluck.error(line, "Unexpected character after ':'");
                }
                break;

            default:
                if (isDigit(c)) {
                    number();
                } else if (isAlpha(c)) {
                    identifier();
                } else {
                    Himluck.error(line, "Unexpected character.");
                }
                break;
        }
    }

    public void number() {
        while (isDigit(peek()))
            advance();

        // 소수부를 피크한다
        if (peek() == '.' && isDigit(peekNext())) {
            // "."을 소비한다
            advance();

            while (isDigit(peek()))
                advance();
        }

        addToken(NUMBER, Double.parseDouble(source.substring(start, current)));
    }

    public void identifier() {
        while (isAlphaNumeric(peek()))
            advance();

        addToken(IDENTIFIER);
    }

    public void string() {
        while (peek() != '"' && !isAtEnd()) {
            if (peek() == '\n')
                line++;
            advance();
        }

        if (isAtEnd()) {
            Himluck.error(line, "Unterminated string.");
            return;
        }

        // 닫는 큰따옴표
        advance();

        // 앞뒤 큰따옴표 제거
        String value = source.substring(start + 1, current - 1);
        addToken(STRING, value);
    }

    public boolean match(char expected) {
        if (isAtEnd())
            return false;
        if (source.charAt(current) != expected)
            return false;

        current++;
        return true;
    }

    public char peek() {
        if (isAtEnd())
            return '\0';
        return source.charAt(current);
    }

    public char peekNext() {
        if (current + 1 >= source.length())
            return '\0';
        return source.charAt(current + 1);
    }

    public boolean isAlpha(char c) {
        return (c >= 'a' && c <= 'z') || (c >= 'A' && c <= 'Z') || c == '_';
    }

    public boolean isAlphaNumeric(char c) {
        return isAlpha(c) || isDigit(c);
    }

    public boolean isDigit(char c) {
        return c >= '0' && c <= '9';
    }

    public boolean isAtEnd() {
        return current >= source.length();
    }

    public char advance() {
        return source.charAt(current++);
    }

    public void addToken(TokenType type) {
        addToken(type, null);
    }

    public void addToken(TokenType type, Object literal) {
        String text = source.substring(start, current);
        tokens.add(new Token(type, text, literal, line));
    }

    private void symbol() {
        while (isAlphaNumeric(peek()))
            advance();
        addToken(SYMBOL, source.substring(start + 1, current));
    }

}
