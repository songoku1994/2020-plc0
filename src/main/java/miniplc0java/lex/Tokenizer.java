package miniplc0java.lex;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;

public class Tokenizer {

    private ArrayList<Token> tokens = new ArrayList<>();
    private int point = -1;
    private ArrayList<Integer> savedPoint = new ArrayList<>();
    CharWiseInput c;
    private TokenType[] tokenTypes = {
        TokenType.FN_KW,
        TokenType.LET_KW,
        TokenType.CONST_KW,
        TokenType.AS_KW,
        TokenType.WHILE_KW,
        TokenType.IF_KW,
        TokenType.ELSE_KW,
        TokenType.RETURN_KW,
        TokenType.BREAK_KW,
        TokenType.CONTINUE_KW,
    };

    public Tokenizer(File file) throws IOException {
        c = new CharWiseInput(file);
        Token t;
        while ((t = getToken()) != null) {
            tokens.add(t);
        }
    }

    public Token peekToken() {
        return (point + 1 < tokens.size()) ? tokens.get(point + 1) : null;
    }

    public Token getThisToken() {
        return tokens.get(point);
    }

    public Token nextToken() {
        return (point + 1 < tokens.size()) ? tokens.get(++point) : null;
    }

    public boolean ifNextToken(TokenType tokenType) {
        if (peekToken() != null && peekToken().getTokenType() == tokenType) {
            nextToken();
            return true;
        }
        return false;
    }

    public Token expectToken(TokenType tokenType) {
        if (peekToken().getTokenType() == tokenType) {
            nextToken();
            return getThisToken();
        }
        throw new Error(
            "expect token \'" + tokenType.toString() + "\' at: row" + peekToken().getStartCoords()
                .getRow() + " col" + peekToken().getStartCoords().getCol());
    }

    private Token getToken() throws IOException {
        skipSpaceCharacters();
        Coords coords = c.getCoords();
        StringBuilder s = new StringBuilder();
        if (c.ifNextChar('0', '9')) {
            s.append(c.getChar());
            while (c.ifNextChar('0', '9')) {
                s.append(c.getChar());
            }
            if (!c.ifNextChar('.')) {
                return new Token(TokenType.INT, Long.valueOf(s.toString()),
                    new Coords(coords.getRow(), coords.getCol()));
            }
            s.append('.');
            s.append(c.expectChar('0', '9'));
            while (c.ifNextChar('0', '9')) {
                s.append(c.getChar());
            }
            if (!c.ifNextChar('e') && !c.ifNextChar('E')) {
                return new Token(TokenType.DOUBLE, Double.valueOf(s.toString()),
                    new Coords(coords.getRow(), coords.getCol()));
            }
            s.append('E');
            if (c.ifNextChar('-')) {
                s.append('-');
            } else if (c.ifNextChar('+')) {
                s.append('+');
            }
            s.append(c.expectChar('0', '9'));
            while (c.ifNextChar('0', '9')) {
                s.append(c.getChar());
            }
            return new Token(TokenType.DOUBLE, Double.valueOf(s.toString()),
                new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('_') || c.ifNextChar('a', 'z') || c.ifNextChar('A', 'Z')) {
            s.append(c.getChar());
            while (c.ifNextChar('_') || c.ifNextChar('a', 'z') || c.ifNextChar('A', 'Z') || c
                .ifNextChar('0', '9')) {
                s.append(c.getChar());
            }
            for (int i = 0; i < tokenTypes.length; i++) {
                if (s.toString().equals(tokenTypes[i].toString())) {
                    return new Token(tokenTypes[i], s, new Coords(coords.getRow(), coords.getCol()));
                }
            }
            if (s.toString().equals("int")) {
                return new Token(TokenType.TYPE_KW, "int", new Coords(coords.getRow(), coords.getCol()));
            }
            if (s.toString().equals("void")) {
                return new Token(TokenType.TYPE_KW, "void", new Coords(coords.getRow(), coords.getCol()));
            }
            if (s.toString().equals("double")) {
                return new Token(TokenType.TYPE_KW, "double", new Coords(coords.getRow(), coords.getCol()));
            }
            return new Token(TokenType.IDENT, s, new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('\'')) {
            if (c.ifNextChar('\\')) {
                if (c.ifNextChar('\'')) {
                    c.expectChar('\'');
                    return new Token(TokenType.CHAR, '\'', new Coords(coords.getRow(), coords.getCol()));
                } else if (c.ifNextChar('\"')) {
                    c.expectChar('\'');
                    return new Token(TokenType.CHAR, '\"', new Coords(coords.getRow(), coords.getCol()));
                } else if (c.ifNextChar('\\')) {
                    c.expectChar('\'');
                    return new Token(TokenType.CHAR, '\\', new Coords(coords.getRow(), coords.getCol()));
                } else if (c.ifNextChar('n')) {
                    c.expectChar('\'');
                    return new Token(TokenType.CHAR, '\n', new Coords(coords.getRow(), coords.getCol()));
                } else if (c.ifNextChar('r')) {
                    c.expectChar('\'');
                    return new Token(TokenType.CHAR, '\r', new Coords(coords.getRow(), coords.getCol()));
                } else if (c.ifNextChar('t')) {
                    c.expectChar('\'');
                    return new Token(TokenType.CHAR, '\t', new Coords(coords.getRow(), coords.getCol()));
                } else {
                    throw new Error(
                        "Cannot identity token at: row" + coords.getRow() + " col" + coords.getCol());
                }
            } else {
                char temp = c.nextChar();
                c.expectChar('\'');
                return new Token(TokenType.CHAR, temp, new Coords(coords.getRow(), coords.getCol()));
            }
        } else if (c.ifNextChar('\"')) {
            while (!c.ifNextChar('\"')) {
                if (c.ifNextChar('\\')) {
                    if (c.ifNextChar('\'')) {
                        s.append('\'');
                    } else if (c.ifNextChar('\"')) {
                        s.append('\"');
                    } else if (c.ifNextChar('\\')) {
                        s.append('\\');
                    } else if (c.ifNextChar('n')) {
                        s.append('\n');
                    } else if (c.ifNextChar('r')) {
                        s.append('\r');
                    } else if (c.ifNextChar('t')) {
                        s.append('\t');
                    } else {
                        throw new Error(
                            "Cannot identity token at: row" + coords.getRow() + " col" + coords.getCol());
                    }
                } else if (c.peekChar() == (char) 65535) {
                    throw new Error("lack of \" at: row" + coords.getRow() + " col" + coords.getCol());
                } else {
                    s.append(c.nextChar());
                }
            }
            return new Token(TokenType.STRING, s.toString(), new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('+')) {
            return new Token(TokenType.PlUS, TokenType.PlUS, new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('-')) {
            if (c.ifNextChar('>')) {
                return new Token(TokenType.ARROW, TokenType.ARROW,
                    new Coords(coords.getRow(), coords.getCol()));
            }
            return new Token(TokenType.MINUS, TokenType.MINUS, new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('*')) {
            return new Token(TokenType.MUL, TokenType.MUL, new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('/')) {
            if (!c.ifNextChar('/')) {
                return new Token(TokenType.DIV, TokenType.DIV, new Coords(coords.getRow(), coords.getCol()));
            } else {
                char temp;
                do {
                    temp = c.nextChar();
                } while (temp != '\n' && temp != (char) 65535);
                return this.getToken();
            }
        } else if (c.ifNextChar('=')) {
            if (c.ifNextChar('=')) {
                return new Token(TokenType.EQ, TokenType.EQ, new Coords(coords.getRow(), coords.getCol()));
            }
            return new Token(TokenType.ASSIGN, TokenType.ASSIGN,
                new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('!')) {
            if (c.ifNextChar('=')) {
                return new Token(TokenType.NEQ, 0, new Coords(coords.getRow(), coords.getCol()));
            }
            throw new Error("expect char \'=\' at: row" + coords.getRow() + " col" + coords.getCol());
        } else if (c.ifNextChar('>')) {
            if (c.ifNextChar('=')) {
                return new Token(TokenType.GE, TokenType.GE, new Coords(coords.getRow(), coords.getCol()));
            }
            return new Token(TokenType.GT, TokenType.GT, new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('<')) {
            if (c.ifNextChar('=')) {
                return new Token(TokenType.LE, TokenType.LE, new Coords(coords.getRow(), coords.getCol()));
            }
            return new Token(TokenType.LT, TokenType.LT, new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('(')) {
            return new Token(TokenType.L_PAREN, TokenType.L_PAREN,
                new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar(')')) {
            return new Token(TokenType.R_PAREN, TokenType.R_PAREN,
                new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('{')) {
            return new Token(TokenType.L_BRACE, TokenType.L_BRACE,
                new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar('}')) {
            return new Token(TokenType.R_BRACE, TokenType.R_BRACE,
                new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar(',')) {
            return new Token(TokenType.COMMA, TokenType.COMMA, new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar(':')) {
            return new Token(TokenType.COLON, TokenType.COLON, new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar(';')) {
            return new Token(TokenType.SEMICOLON, TokenType.SEMICOLON,
                new Coords(coords.getRow(), coords.getCol()));
        } else if (c.ifNextChar((char) 65535)) {
            return null;
        } else {
            throw new Error("Cannot identity token at: row" + coords.getRow() + " col" + coords.getCol());
        }
    }

    private void skipSpaceCharacters() throws IOException {
        while (c.ifNextChar(' ') || c.ifNextChar('\n') || c.ifNextChar('\r') || c
            .ifNextChar('\t')) {
            ;
        }
    }

    public void savePoint() {
        savedPoint.add(point);
    }

    public void loadPoint() {
        point = savedPoint.get(savedPoint.size() - 1);
    }

    public void removePoint() {
        savedPoint.remove(savedPoint.size() - 1);
    }
}
