package com.mouseflow;

import org.jnativehook.GlobalScreen;
import org.jnativehook.NativeHookException;
import org.jnativehook.mouse.NativeMouseEvent;
import org.jnativehook.mouse.NativeMouseMotionListener;

import java.util.concurrent.Executors;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;
import java.util.logging.Level;
import java.util.logging.Logger;

import com.sun.jna.Native;
import com.sun.jna.platform.win32.User32;
import com.sun.jna.platform.win32.WinDef;
import com.sun.jna.platform.win32.WinDef.HWND;

import javafx.application.Platform;

import java.io.FileWriter;
import java.io.PrintWriter;
import java.io.IOException;
import java.io.File;

public class App implements NativeMouseMotionListener
{
    private PrintWriter writer;
    private char[] buffer = new char[2048];

    private volatile int currentX = 0;
    private volatile int currentY = 0;
    private int lastLoggedX = 0;
    private int lastLoggedY = 0;
    private long accumulatedTime = 0;
    private String lastActiveW = "";

    static ScheduledExecutorService scheduler;

    TrackerUI ui;

    public App(TrackerUI ui){
        try {
            this.ui = ui;
            new File("logs").mkdir();
            String filename = "logs/mouse_log_" + System.currentTimeMillis() + ".csv";
            FileWriter fw = new FileWriter(filename, true);
            writer = new PrintWriter(fw);

            writer.println("timestamp,ActiveWindow,HoveredWindow,x,y,durationMS");
            writer.flush();
            System.out.println("Logging to: " + filename);
        } catch (IOException e){
            e.printStackTrace();
        }

        scheduler = Executors.newSingleThreadScheduledExecutor();
        scheduler.scheduleAtFixedRate(() -> {
            logPulse(false);
            Platform.runLater(() -> {
                ui.updatePath(currentX,currentY);
            });
        }, 0, 40, TimeUnit.MILLISECONDS);

        Runtime.getRuntime().addShutdownHook(new Thread(() -> {
            System.out.println("Closing..Save final data.");
            logPulse(true);
            writer.close();
            }
        ));
    }

    private String getActiveWindow(){
        HWND hwnd = User32.INSTANCE.GetForegroundWindow();
        User32.INSTANCE.GetWindowText(hwnd, buffer, 1024);
        String title = Native.toString(buffer);
        return title.isEmpty() ? "Desktop" : title.replace(",", "");
    }

    private String getHoverWindow(int x, int y){
        WinDef.POINT.ByValue pValue = new WinDef.POINT.ByValue();
        pValue.x = x;
        pValue.y = y;
        HWND hwnd = ExtendUser32.INSTANCE.WindowFromPoint(pValue);
        if (hwnd == null) return "Desktop";
        HWND root = User32.INSTANCE.GetAncestor(hwnd, 2);
        User32.INSTANCE.GetWindowText(root, buffer, 1024);
        String title = Native.toString(buffer);
        return title.isEmpty() ? "Desktop" : title.replace(",", "");
    }

    @Override 
    public void nativeMouseMoved(NativeMouseEvent e){
        currentX = e.getX();
        currentY = e.getY();

    }

    @Override
    public void nativeMouseDragged(NativeMouseEvent e){
        //empty
    }

    private void logPulse(boolean force){
        try{
            String activeW = getActiveWindow();
            String hoverW = getHoverWindow(currentX, currentY);

            long ts = System.currentTimeMillis();
            if(Math.abs(currentX - lastLoggedX) < 5 && Math.abs(currentY - lastLoggedY) < 5 && activeW.equals(lastActiveW) && !force){
                accumulatedTime += 40;
            } else {
                writer.println(ts + "," + activeW + "," + hoverW + "," + lastLoggedX + "," + lastLoggedY +"," + accumulatedTime);
                writer.flush();
                accumulatedTime = 0;
                lastActiveW = activeW;
                lastLoggedX = currentX;
                lastLoggedY = currentY;
            }

            if (ui != null){
                ui.updateLiveStats(currentX, currentY, activeW, hoverW);
            }
        } catch(Exception e) {
            e.printStackTrace();
        }

    }

    public void startTracking(){
        Logger logger = Logger.getLogger(GlobalScreen.class.getPackageName());
        logger.setLevel(Level.OFF);

        try {
            GlobalScreen.registerNativeHook();
            GlobalScreen.addNativeMouseMotionListener(this);
            System.out.println("--Mouse Track Active--");
        }
        catch(NativeHookException ex) {
            System.err.println("There was a problem registering the native hook");
            ex.printStackTrace();
            System.exit(1);
        }
    }

    public void shutdown(){
        scheduler.shutdownNow();
        try {
            GlobalScreen.unregisterNativeHook();
        } catch (NativeHookException e) {
            e.printStackTrace();
        }
        writer.close();
    }
}
