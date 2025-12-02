package core;

import java.lang.reflect.Array;
import java.util.ArrayList;
import java.util.List;

public class Room {
    int id;
    int x;
    int y;
    int width;
    int height;
    int[][] doors = new int[4][2];

    int encounterX;
    int encounterY;
    public boolean playedGame;


    public Room(int id, int x, int y, int width, int height, int encounterX, int encounterY, boolean playedGame) {
        this.id = id;
        this.x = x;
        this.y = y;
        this.width = width;
        this.height = height;

        doors[0] = new int[]{x + width/2, y - 1};
        doors[1] = new int[]{x + width/2, y + height};
        doors[2] = new int[]{x - 1, y + height/2};
        doors[3] = new int[]{x + width, y + height/2};

        this.encounterX = encounterX;
        this.encounterY = encounterY;
        this.playedGame = false;
    }
}
