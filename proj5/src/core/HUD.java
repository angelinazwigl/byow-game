package core;

import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;

import java.awt.*;

public class HUD {
    public static void tileDescription(TETile[][] world) {
        StdDraw.setPenColor(Color.WHITE);
        int cursorX = (int) StdDraw.mouseX();
        int cursorY = (int) StdDraw.mouseY();

        String text;
        if (cursorX >= 0 && cursorX < world.length && cursorY >= 0 && cursorY < world[0].length) {
            text = world[cursorX][cursorY].description();
        } else {
            text = "nothing";
        }

        StdDraw.textLeft(1, world[0].length - 1, text);
    }
}
