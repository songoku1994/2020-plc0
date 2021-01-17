package miniplc0java.lex;

public class Token {

    private TokenType tokenType;
    private Object value;
    private Coords startCoords;
    private boolean isConst;

    public Token(TokenType tokenType, Object value, Coords startCoords) {
        this.tokenType = tokenType;
        this.value = value;
        this.startCoords = startCoords;
    }

    public Object getValue() {
        return value;
    }

    public TokenType getTokenType() {
        return tokenType;
    }

    public Coords getStartCoords() {
        return startCoords;
    }
}
