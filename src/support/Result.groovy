package support

/**
 * Created by b_newyork on 2018-07-03.
 */

class Result{
    int total = 0;
    public int simpleSmartApp = 0;
    public int Event_freq = 0;
    public int Action_freq = 0;
    public int Event_and_Action = 0;
    public int nEvent_and_nAction = 0;
    public int duplicate = 0;
    public int sendingMessage = 0;

    public void Count(){
        total = total +1;
    }

    int getSimpleSmartApp() {
        return simpleSmartApp
    }

    int getEvent_freq() {
        return Event_freq
    }

    int getAction_freq() {
        return Action_freq
    }

    int getEvent_and_Action() {
        return Event_and_Action
    }

    int getnEvent_and_nAction() {
        return nEvent_and_nAction
    }

    int getDuplicate() {
        return duplicate
    }

    int getSendingMessage() {
        return sendingMessage
    }
}