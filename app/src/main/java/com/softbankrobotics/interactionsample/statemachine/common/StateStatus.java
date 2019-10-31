package com.softbankrobotics.interactionsample.statemachine.common;

/* A data class to communicate state status to watchers (is this state active ? etc.)
 */
public class StateStatus {
    public String name;
    public Boolean active;
    public Boolean isPrevious;
    public Boolean isNext;
    StateStatus(String name, Boolean active, boolean isPrevious, boolean isNext) {
        this.name = name;
        this.active = active;
        this.isPrevious = isPrevious;
        this.isNext = isNext;
    }
}
