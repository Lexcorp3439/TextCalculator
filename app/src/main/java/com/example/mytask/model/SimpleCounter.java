package com.example.mytask.model;

import android.util.Pair;

import java.util.ArrayDeque;
import java.util.ArrayList;
import java.util.List;

import com.example.mytask.model.Util.Action;

import static com.example.mytask.model.Util.convertAction;
import static com.example.mytask.model.Util.isNumber;

public class SimpleCounter implements Counter {
    private static SimpleCounter counter = new SimpleCounter();

    private SimpleCounter() {
    }

    @SuppressWarnings("WeakerAccess")
    public static Counter getInstance() {
        return counter;
    }

    private List<Pair<Action, Double>> parser(String str) {
        List<Pair<Action, Double>> list = new ArrayList<>();
        ArrayDeque<Integer> stack = new ArrayDeque<>();
        StringBuilder builder = new StringBuilder();
        for (char chr : str.toCharArray()) {
            String charStr = Character.toString(chr);
            if (charStr.matches(" ")) {
                continue;
            }
            if (Character.isDigit(chr) || (builder.length() > 0 && charStr.matches("\\."))) {
                builder.append(chr);
            } else if (charStr.matches("[+×\\-*/()]")) {
                Action cur = convertAction(charStr);
                if (builder.length() != 0) {
                    double value = Double.parseDouble(builder.toString());
                    list.add(new Pair<>(Action.NUMBER, value));
                    builder = new StringBuilder();
                }
                list.add(new Pair<>(cur, 0.0));
                if (cur == Action.OPEN){
                    stack.add(1);
                }
                if (cur == Action.CLOSE){
                    Integer poll =stack.poll();
                    if (poll == null) {
                        list.clear();
                        list.add(Pair.create(Action.ERROR, 3d));
                        System.out.println("1");
                        return list;
                    }
                }
            } else {
                list.clear();
                list.add(Pair.create(Action.ERROR, 1d));
                return list;
            }
        }
        if (builder.length() != 0) {
            list.add(new Pair<>(Action.NUMBER, Double.parseDouble(builder.toString())));
        }

        double errorCode = checkError(list, stack);
        if (errorCode > 0) {
            list.clear();
            list.add(Pair.create(Action.ERROR, errorCode));
        }
        return list;
    }

    private Action last = Action.NOTNUMBER;
    private Action lastAct = Action.PLUS;
    private double checkError(List<Pair<Action, Double>> list, ArrayDeque<Integer> stack) {
        if (stack.size() != 0){
            System.out.println("2");
            return 3d;
        }
        switch (list.size()) {
            case 0:
                return 2d;
            case 1:
                if (list.get(0).first != Action.NUMBER) {
                    System.out.println("3");
                    return 3d;
                }
                break;
            case 2:
                System.out.println(list);
                System.out.println(list.size());
                System.out.println("4");
                return 3d;
            default:
                break;
        }

        for (int i = 0; i < list.size(); i++) {
            Pair<Action, Double> cur = list.get(i);
            //заменяем унарный минус
            if (i < list.size() - 3) {
                if (cur.first == Action.OPEN && list.get(i + 1).first == Action.MINUS &&
                        list.get(i + 2).first == Action.NUMBER && list.get(i + 3).first == Action.CLOSE) {
                    list.remove(i + 1);
                    list.set(i + 1, new Pair<>(Action.NUMBER, list.get(i + 1).second * -1));
                }
            }
            System.out.println(list);

            if (isNumber(cur.first) == last) {                                       //2 знака подряд
                return 3d;
            }
            if (cur.first == Action.NUMBER && cur.second == 0d                       // деление на 0
                    && lastAct == Action.DIVIDE) {
                return 4d;
            }
            if (cur.first == Action.OPEN) {                                          // N( или (+
                if ((i > 0 && isNumber(list.get(i - 1).first) == Action.NUMBER)
                        || isNumber(list.get(i + 1).first) == Action.NOTNUMBER) {
                    return 3d;
                }
            }
            if (cur.first == Action.CLOSE) {                                         // +) или )N
                if ((i < list.size() - 1 && isNumber(list.get(i + 1).first) == Action.NUMBER)
                        || isNumber(list.get(i - 1).first) == Action.NOTNUMBER
                        ) {
                    return 3d;
                }
            }
            Action curAct = isNumber(cur.first);
            if (curAct != Action.ERROR) {
                last = isNumber(cur.first);
                if (curAct == Action.NOTNUMBER){
                    lastAct = cur.first;
                }
            }
        }
        last = Action.NOTNUMBER;
        lastAct = Action.PLUS;
        return 0d;
    }

    @Override
    public Pair<Integer, Double> count(String operands) {
        List<Pair<Action, Double>> op = parser(operands);
        last = Action.NOTNUMBER;
        if (op.size() == 0) {
            return Pair.create(2, 0d);
        }
        if (op.get(0).first != Action.ERROR) {
            return Pair.create(0, cnt(op, 0).second);
        } else {
            return Pair.create(op.get(0).second.intValue(), op.get(0).second);
        }
    }

    private Pair<Integer, Double> cnt(List<Pair<Action, Double>> op, int start) {
        ArrayDeque<Double> values = new ArrayDeque<>();
        ArrayDeque<Action> action = new ArrayDeque<>();
        for (int i = start; i < op.size(); i++) {
            Pair<Action, Double> elem = op.get(i);
            // приведение к счетному виду входные данные
            if (elem.first == Action.OPEN) {
                Pair<Integer, Double> pair = cnt(op, ++i);
                i = pair.first;
                values.add(pair.second);
            } else if (elem.first != Action.CLOSE && elem.first != Action.NUMBER) {
                action.add(elem.first);
            } else if (elem.first == Action.NUMBER) {
                values.add(elem.second);
            } else {
                calculations(values, action);
                return Pair.create(i, values.pollFirst());
            }

            // процесс счета
            if (values.size() == 3 && action.size() == 2) {
                calculations(values, action);
            }
        }
        calculations(values, action);
        return Pair.create(op.size(), values.pollFirst());
    }


    private void calculations(ArrayDeque<Double> values, ArrayDeque<Action> action) {
        System.out.println(values);
        System.out.println(action);
        double first;
        double second;
        if (action.peekFirst() == Action.DIVIDE || action.peekFirst() == Action.MULTIPLY) {
            first = values.pollFirst();
            second = values.pollFirst();
            values.addFirst(action.peekFirst() == Action.DIVIDE ? first / second : first * second);
            action.pollFirst();
        } else {
            Action peekLast = action.peekLast();
            if (peekLast == Action.PLUS || peekLast == Action.MINUS) {
                first = values.pollFirst();
                second = values.pollFirst();
                values.addFirst(peekLast == Action.PLUS ? first + second : first - second);
                action.pollFirst();
            }
            if (peekLast == Action.DIVIDE || peekLast == Action.MULTIPLY) {
                second = values.pollLast();
                first = values.pollLast();
                values.add(action.peekLast() == Action.DIVIDE ? first / second : first * second);
                action.pollLast();
            }
        }
    }
}
