package com.mouseflow;

import com.sun.jna.Native;

import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.win32.W32APIOptions;

public interface ExtendUser32 extends User32{

    ExtendUser32 INSTANCE = Native.load("user32", ExtendUser32.class, W32APIOptions.DEFAULT_OPTIONS);
    
    HWND WindowFromPoint(WinDef.POINT.ByValue p);
}
