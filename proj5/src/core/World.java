package core;

import edu.princeton.cs.algs4.WeightedQuickUnionUF;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.util.*;

public class World {

    static final int WIDTH = 75;
    static final int HEIGHT = 50;


    static int numRooms;
    static int builtRooms;

    public static List<Room> rooms = new ArrayList<>();
    static WeightedQuickUnionUF wqu;
    public static List<Coin> coins = new ArrayList<>();

    static TETile[][] world;
    static int positionX;
    static int positionY;
    static TERenderer ter = new TERenderer();



    public static void createWorld(long seed) {
        rooms.clear();
        coins.clear();
        world = new TETile[WIDTH][HEIGHT];
        Random RANDOM = new Random(seed);
        numRooms = RANDOM.nextInt(25) + 10;
        builtRooms = 0;
        wqu = new WeightedQuickUnionUF(numRooms);

        TERenderer ter = new TERenderer();
        ter.initialize(WIDTH, HEIGHT);

        for (int x = 0; x < WIDTH; x++) {
            for (int y = 0; y < HEIGHT; y++) {
                world[x][y] = Tileset.NOTHING;
            }
        }

        int attempts = 0;
        outerloop:
        while (builtRooms < numRooms) {
            attempts += 1;
            if (attempts > 5000) {
                break;
            }
            int width = RANDOM.nextInt(6) + 6;
            int height = RANDOM.nextInt(6) + 6;
            int x = RANDOM.nextInt(WIDTH - width - 2) + 1;
            int y = RANDOM.nextInt(HEIGHT - height - 2) + 1;

            // check for overlap
            for (int ryan = Math.max(x - 4, 0); ryan < Math.min(x + width + 4, WIDTH); ryan++) {
                for (int angelina = Math.max(y-4, 0); angelina < Math.min(y + height + 4, HEIGHT); angelina++) {
                    if (world[ryan][angelina] != Tileset.NOTHING) {
                        continue outerloop;
                    }
                }
            }

            //build floor
            for (int ryan = x; ryan < x + width; ryan++) {
                for (int angelina = y; angelina < y + height; angelina++) {
                    world[ryan][angelina] = Tileset.FLOWER;
                }
            }

            //build walls
            for (int ryan = x; ryan < x + width; ryan++) {
                world[ryan][y - 1] = Tileset.WALL;
            }
            for (int ryan = x; ryan < x + width; ryan++) {
                world[ryan][y + height] = Tileset.WALL;
            }
            for (int angelina = y; angelina < y + height; angelina++) {
                world[x - 1][angelina] = Tileset.WALL;
            }
            for (int angelina = y; angelina < y + height; angelina++) {
                world[x + width][angelina] = Tileset.WALL;
            }

            //build doors
            world[x + width / 2][y - 1] = Tileset.FLOWER;
            world[x + width / 2][y + height] = Tileset.FLOWER;
            world[x - 1][y + height / 2] = Tileset.FLOWER;
            world[x + width][y + height / 2] = Tileset.FLOWER;

            //build encounters
            int encounterXPos = x + RANDOM.nextInt(width);
            int encounterYPos = y + RANDOM.nextInt((height));
            world[encounterXPos][encounterYPos] = Tileset.MOUNTAIN;

            Room r = new Room(builtRooms, x, y, width, height, encounterXPos, encounterYPos, false);
            rooms.add(r);
            builtRooms += 1;

            //build coins
            int numCoins;
            for (int ryan = x; ryan < x + width; ryan++) {
                for (int angelina = y; angelina < y + height; angelina++) {
                    int randomNum = RANDOM.nextInt(80);
                    if (randomNum == 0) {
                        world[ryan][angelina] = Tileset.COIN;
                        Coin coinPos = new Coin(ryan, angelina, false);
                        coins.add(coinPos);
                    }
                }
            }
        }

        while(!allConnected()) {
            int[] shortestRooms = new int[7];
            shortestRooms[6] = 10000;
            int[] currentRooms = new int[7];
            for(int cannon = 0; cannon<rooms.size(); cannon++) {
                for(int zwigl = 0; zwigl<rooms.size(); zwigl++) {
                    if (wqu.find(cannon) == wqu.find(zwigl)) {
                        continue;
                    }
                    currentRooms = twoRoomsPathQuickest(rooms.get(cannon), rooms.get(zwigl), cannon, zwigl);
                    if (currentRooms[6] < shortestRooms[6]){
                        shortestRooms = currentRooms;
                    }
                }
            }

            buildHallway(world, shortestRooms[2], shortestRooms[4], shortestRooms[3], shortestRooms[5]);

            wqu.union(shortestRooms[0], shortestRooms[1]);
        }

        closeDoors(world, HEIGHT, WIDTH);

        secondOuterLoop:
        for (int ryan = 0; ryan < WIDTH; ryan++) {
            for (int angelina = 0; angelina < HEIGHT; angelina++) {
                if (world[ryan][angelina] == Tileset.FLOWER) {
                    world[ryan][angelina] = Tileset.AVATAR;
                    positionX = ryan;
                    positionY = angelina;
                    break secondOuterLoop;
                }
            }
        }

        ter.renderFrame(world);
    }



    public static void closeDoors(TETile[][]world, int height, int width) {
        for (int i = 0; i < height; i++) {
            if (world[0][i] == Tileset.FLOWER) {
                world[0][i] = Tileset.WALL;
            }
            if (world[width - 1][i] == Tileset.FLOWER) {
                world[width - 1][i] = Tileset.WALL;
            }
        }
        for (int i = 0; i < width; i++) {
            if (world[i][0] == Tileset.FLOWER) {
                world[i][0] = Tileset.WALL;
            }
            if (world[i][height - 1] == Tileset.FLOWER) {
                world[i][height - 1] = Tileset.WALL;
            }
        }

        for (int i = 1; i < width - 1; i++){
            for (int j = 1; j < height - 1; j++) {
                if (world[i][j] == Tileset.FLOWER) {
                    if (world[i+1][j] == Tileset.NOTHING){
                        world[i][j] = Tileset.WALL;
                    }
                    if (world[i-1][j] == Tileset.NOTHING){
                        world[i][j] = Tileset.WALL;
                    }
                    if (world[i][j+1] == Tileset.NOTHING){
                        world[i][j] = Tileset.WALL;
                    }
                    if (world[i][j-1] == Tileset.NOTHING){
                        world[i][j] = Tileset.WALL;
                    }
                }
            }
        }
    }

    public static void buildHallway(TETile[][] world, int x1, int y1, int x2, int y2) {
        int x = x1;
        int y = y1;

        while (x != x2) {
            if (withinWorld(x,y)) {
                world[x][y] = Tileset.FLOWER;
            }

            if (withinWorld(x, y+1) && world[x][y+1] == Tileset.NOTHING) {
                world[x][y+1] = Tileset.WALL;
            }
            if (withinWorld(x, y-1) && world[x][y-1] == Tileset.NOTHING) {
                world[x][y-1] = Tileset.WALL;
            }
            if (withinWorld(x + 1, y) && world[x + 1][y] == Tileset.NOTHING) {
                world[x+1][y] = Tileset.WALL;
            }
            if (withinWorld(x - 1, y) && world[x - 1][y] == Tileset.NOTHING) {
                world[x - 1][y] = Tileset.WALL;
            }
            if (x < x2) {
                x += 1;
            }
            else {
                x -= 1;
            }
        }
        while (y != y2) {
            if (withinWorld(x,y)) {
                world[x][y] = Tileset.FLOWER;
            }

            if (withinWorld(x + 1, y) && world[x + 1][y] == Tileset.NOTHING) {
                world[x+1][y] = Tileset.WALL;
            }
            if (withinWorld(x - 1, y) && world[x - 1][y] == Tileset.NOTHING) {
                world[x - 1][y] = Tileset.WALL;
            }
            if (withinWorld(x, y+1) && world[x][y+1] == Tileset.NOTHING) {
                world[x][y+1] = Tileset.WALL;
            }
            if (withinWorld(x, y-1) && world[x][y-1] == Tileset.NOTHING) {
                world[x][y-1] = Tileset.WALL;
            }

            if (y < y2) {
                y += 1;
            }
            else {
                y -= 1;
            }
        }
    }

    public static boolean withinWorld(int x, int y) {
        return x >= 0 && x < WIDTH && y >= 0 && y < HEIGHT;
    }

    public static boolean allConnected() {
        for (int i = 0; i < builtRooms; i++) {
            for (int j =0; j < builtRooms; j++) {
                if (!wqu.connected(i, j)) {
                    return false;
                }
            }
        }
        return true;
    }

    public static int[] twoRoomsPathQuickest(Room a, Room b, int roomIndexA, int roomIndexB) {
        int shortestDist = 10000;
        int[] shortest = new int[7];
        for (int[] ryan : a.doors) {
            for (int[] angelina : b.doors) {
                int distance  = Math.abs(ryan[0]-angelina[0]) + Math.abs(ryan[1]-angelina[1]);
                if (distance < shortestDist) {
                    shortestDist = distance;
                    shortest[0] = roomIndexA;
                    shortest[1] = roomIndexB;
                    shortest[2] = ryan[0];
                    shortest[3] = angelina[0];
                    shortest[4] = ryan[1];
                    shortest[5] = angelina[1];
                    shortest[6] = shortestDist;
                }
            }
        }
        return shortest;
    }

}
