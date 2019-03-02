package com.example.mytask.model;

class Util {

    enum Action {
        PLUS,
        MINUS,
        DIVIDE,
        MULTIPLY,
        OPEN,
        CLOSE,
        NUMBER,
        NOTNUMBER,
        ERROR
    }


    static Action isNumber(Action action) {
        switch (action) {
            case MINUS:
                return Action.NOTNUMBER;
            case PLUS:
                return Action.NOTNUMBER;
            case DIVIDE:
                return Action.NOTNUMBER;
            case MULTIPLY:
                return Action.NOTNUMBER;
            case NUMBER:
                return Action.NUMBER;
            default:
                return Action.ERROR;
        }
    }

    static Action convertAction(String str) {
        switch (str) {
            case "+":
                return Action.PLUS;
            case "-":
                return Action.MINUS;
            case "*":
                return Action.MULTIPLY;
            case "×":
                return Action.MULTIPLY;
            case "/":
                return Action.DIVIDE;
            case "(":
                return Action.OPEN;
            case ")":
                return Action.CLOSE;
            default:
                return Action.NUMBER;
        }
    }
}
