package com.piedpiper1337.pickwhich.callbacks;

/**
 * Created by cary on 1/24/16.
 */
public interface NavigationCallback {

    void goFullScreen();

    void returnFromFullScreen();

    void startNewPick();

    void goToInbox();

    void nextPhoto();
}
