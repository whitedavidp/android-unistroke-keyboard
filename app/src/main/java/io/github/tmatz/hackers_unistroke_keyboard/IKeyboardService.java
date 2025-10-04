package com.whitedavidp.unistroke_keyboard;

interface IKeyboardService
{
    void updateView();

    void sendText(String text);

    void sendKey(int action, int keyCode, int metaState);

    void sendKeyRepeat(int keyCode, int metaState);

    boolean isEditorActionRequested();

    void performEditorAction();
}

