package com.thinksee.observer;

import java.util.Vector;

public abstract class Subject {
    private Vector<Observer> observers = new Vector<>();

    public void addObserver(Observer observer) {
        this.observers.add(observer);
    }

    public void deleteObserver(Observer observer) {
        this.observers.remove(observer);
    }

    protected void notifyObserver() {
        for(Observer observer : observers) {
            observer.update();
        }
    }

    public abstract void doSomething();
}
