package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TERenderer;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;

public class Menu {
    Random RANDOM = new Random();

    public void start() {
        StdDraw.setXscale(0, World.WIDTH);
        StdDraw.setYscale(0, World.HEIGHT);

        StdDraw.enableDoubleBuffering();
        menuScreen();

        char c = userMenuInput();

        while (true) {
            if (c == 'N' || c == 'n') {
                String seed = userSeed();
                startGame(Integer.parseInt(seed));
            } else if (c == 'Q' || c =='q') {
                System.exit(0);
            }
            else if (c == 'L' || c == 'l') {
                StdDraw.clear(Color.BLACK);
                StdDraw.setPenColor(Color.WHITE);
                StdDraw.text(35, 35, "Please Select a Game to Open");

                StdDraw.text(35, 20, "(1) Slot One");
                StdDraw.text(35, 15, "(2) Slot Two");
                StdDraw.text(35, 10, "(3) Slot Three");
                StdDraw.show();

                char gameLoad;
                loadLoop:
                while (true) {
                    if (StdDraw.hasNextKeyTyped()) {
                        char loadC = StdDraw.nextKeyTyped();
                        if (loadC == '1' || loadC == '2' || loadC == '3') {
                            gameLoad = loadC;
                            break loadLoop;
                        }
                    }
                }

                loadGame("Save" + gameLoad + ".txt");
            }
        }
    }

    private void saveGame(TETile[][] world) {
        String[][] worldArray = new String[World.WIDTH][World.HEIGHT];
        String text;

        for (int ryan = 0; ryan < World.WIDTH; ryan++) {
            for (int angelina = 0; angelina < World.HEIGHT; angelina++) {
                text = world[ryan][angelina].description();
                worldArray[ryan][angelina] = text;
            }
        }

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 35, "Please Select a Save Slot");

        StdDraw.text(35, 20, "(1) Slot One");
        StdDraw.text(35, 15, "(2) Slot Two");
        StdDraw.text(35, 10, "(3) Slot Three");
        StdDraw.show();

        char saveSlot;
        saveLoop:
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == '1' || c == '2' || c == '3') {
                    saveSlot = c;
                    break saveLoop;
                }
            }
        }

        Out out = new Out("Save" + saveSlot + ".txt");

        for (int y = 0; y < World.HEIGHT; y++) {
            StringBuilder worldString = new StringBuilder();
            for (int x = 0; x < World.WIDTH; x++) {
                worldString.append(worldArray[x][y]);
                if (x < World.WIDTH - 1) {
                    worldString.append(" ");
                }
            }
           out.println(worldString.toString());
        }
        out.close();
    }

    private void loadGame(String file) {
        World.createWorld(1);
        int row = 0;
        int col = 0;
        String filename = file;
        In in = new In(filename);

        while (!in.isEmpty()) {
            String line = in.readLine();
            String[] rows = line.split(" ");
            for (String word : rows) {
                if (word.equals("nothing")) {
                    World.world[col][row] = Tileset.NOTHING;
                    col += 1;
                } else if (word.equals("wall")) {
                    World.world[col][row] = Tileset.WALL;
                    col += 1;
                } else if (word.equals("flower")) {
                    World.world[col][row] = Tileset.FLOWER;
                    col += 1;
                } else if (word.equals("you")) {
                    World.world[col][row] = Tileset.AVATAR;
                    World.positionX = col;
                    World.positionY = row;
                    col += 1;
                } else if (word.equals("mountain")) {
                    World.world[col][row] = Tileset.MOUNTAIN;
                    col += 1;
                } else if (word.equals("coin")) {
                    World.world[col][row] = Tileset.COIN;
                    col += 1;
                } else {
                    World.world[col][row] = Tileset.NOTHING;
                    col += 1;
                }
            }
            col = 0;
            row += 1;
        }
        World.rooms.clear();
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                if (World.world[x][y] == Tileset.MOUNTAIN) {
                    Room room = new Room(1,1,1, World.WIDTH, World.HEIGHT, x, y, false);
                    room.encounterX = x;
                    room.encounterY = y;
                    room.playedGame = false;
                    World.rooms.add(room);
                }
            }
        }
        World.coins.clear();
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                if (World.world[x][y] == Tileset.COIN) {
                    Coin savedCoin = new Coin(x, y, false);
                    savedCoin.x = x;
                    savedCoin.y = y;
                    savedCoin.collected = false;
                    World.coins.add(savedCoin);
                }
            }
        }
        World.ter.renderFrame(World.world);
        moveCharacter();
    }

    private void startGame(int seed) {
        World.createWorld(seed);
        moveCharacter();
    }
    private void encounterGame() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 30, "Catch Some Waves");
        StdDraw.show();
        StdDraw.pause(1500);

        int gameWidth = 20;
        int gameHeight = 20;

        TETile[][] miniWorld = new TETile[World.WIDTH][World.HEIGHT];
        for (int x = 0; x < World.WIDTH; x++) {
            for (int y = 0; y < World.HEIGHT; y++) {
                miniWorld[x][y] = Tileset.NOTHING;
            }
        }

        for (int x = 0; x < gameWidth; x++) {
            for (int y = 0; y < gameHeight; y++) {
                if (x == 0 || y == 0 || x == gameWidth - 1 || y == gameHeight - 1) {
                    miniWorld[x][y] = Tileset.WALL;
                } else {
                    miniWorld[x][y] = Tileset.FLOWER;
                }
            }
        }

        //avatar
        int startingX = 1 + RANDOM.nextInt(gameWidth - 2);
        int startingY = 1 + RANDOM.nextInt(gameHeight - 2);
        miniWorld[startingX][startingY] = Tileset.AVATAR;

        //make waves
        int numWaves = 0;
        List<Coin> waves = new ArrayList<>();

        while (numWaves != 2) {
            int waveX = 1 + RANDOM.nextInt(gameWidth - 2);
            int waveY = 1 + RANDOM.nextInt(gameHeight - 2);
            if (miniWorld[waveX][waveY] == Tileset.FLOWER) {
                miniWorld[waveX][waveY] = Tileset.WATER;
                Coin wavePos = new Coin(waveX, waveY, false);
                waves.add(wavePos);
                numWaves += 1;
            }
        }

        TERenderer gameTer = new TERenderer();
        gameTer.initialize(World.WIDTH, World.HEIGHT);

        encounterLoop:
        while (true) {
            gameTer.renderFrame(miniWorld);
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                int oldX = startingX;
                int oldY = startingY;
                int posX = startingX;
                int posY = startingY;

                if (c == 'w' || c == 'W') {
                    posY += 1;
                }
                if (c == 's' || c == 'S') {
                    posY -= 1;
                }
                if (c == 'a' || c == 'A') {
                    posX -= 1;
                }
                if (c == 'd' || c == 'D') {
                    posX += 1;
                }

                if (miniWorld[posX][posY] != Tileset.WALL) {
                    miniWorld[oldX][oldY] = Tileset.FLOWER;

                    startingX = posX;
                    startingY = posY;
                    miniWorld[posX][posY] = Tileset.AVATAR;
                } else {
                    posX = oldX;
                    posY = oldY;
                }
                for (Coin wavePos : waves) {
                    if (!wavePos.collected && posX == wavePos.x && posY == wavePos.y) {
                        wavePos.collected = true;
                    }
                }
                for (Coin wavePos : waves) {
                    if (!wavePos.collected) {
                        boolean waveMoved = false;
                        int wavePosX = wavePos.x;
                        int wavePosY = wavePos.y;
                        int waveOldX = wavePos.x;
                        int waveOldY = wavePos.y;
                        waveLoop:
                        while (!waveMoved) {
                            int waveDirection = RANDOM.nextInt(8);
                            if (waveDirection == 0) {
                                wavePosY += 1;
                            } else if (waveDirection == 1) {
                                wavePosY -= 1;
                            } else if (waveDirection == 2) {
                                wavePosX -= 1;
                            } else if (waveDirection == 4) {
                                wavePosX += 1;
                            } else {
                                break waveLoop;
                            }

                            if (miniWorld[wavePosX][wavePosY] != Tileset.WALL &&
                                    miniWorld[wavePosX][wavePosY] != Tileset.AVATAR &&
                                    miniWorld[wavePosX][wavePosY] != Tileset.WATER) {
                                miniWorld[waveOldX][waveOldY] = Tileset.FLOWER;
                                miniWorld[wavePosX][wavePosY] = Tileset.WATER;
                                waveMoved = true;
                                wavePos.x = wavePosX;
                                wavePos.y = wavePosY;
                            } else {
                                wavePosX = waveOldX;
                                wavePosY = waveOldY;
                            }
                        }
                    }
                }
                boolean allWavesCollected = true;
                for (Coin hello : waves) {
                    if (hello.collected == false) {
                        allWavesCollected = false;
                        break;
                    }
                }
                if (allWavesCollected) {
                    break encounterLoop;
                }
            }
        }
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 30, "You caught the waves!");
        StdDraw.show();
        StdDraw.pause(1500);
        World.ter.renderFrame(World.world);
    }

    private void moveCharacter() {
        boolean colon = false;
        while (true) {
            World.ter.renderFrame(World.world);
            HUD.tileDescription(World.world);
            StdDraw.show();
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (colon) {
                    if (c == 'q' || c == 'Q') {
                        saveGame(World.world);
                        System.exit(0);
                    }
                } else if (c ==':') {
                    colon = true;
                } else {

                    int oldX = World.positionX;
                    int oldY = World.positionY;
                    int posX = World.positionX;
                    int posY = World.positionY;

                    if (c == 'w' || c == 'W') {
                        posY += 1;
                    }
                    if (c == 's' || c == 'S') {
                        posY -= 1;
                    }
                    if (c == 'a' || c == 'A') {
                        posX -= 1;
                    }
                    if (c == 'd' || c == 'D') {
                        posX += 1;
                    }

                    if (World.world[posX][posY] != Tileset.WALL) {
                        World.world[oldX][oldY] = Tileset.FLOWER;

                        World.positionX = posX;
                        World.positionY = posY;
                        World.world[posX][posY] = Tileset.AVATAR;
                    } else {
                        posX = oldX;
                        posY = oldY;
                    }
                    for (Room r : World.rooms) {
                        if (!r.playedGame && posX == r.encounterX && posY == r.encounterY) {
//                            int randomNum = RANDOM.nextInt(5);
//                                if (randomNum == 0) {
//                                    rockPaperScissors();
//                                } else if (randomNum == 1) {
//                                    flipACoin();
//                                } else if (randomNum == 2) {
//                                    rollADie();
//                                } else if (randomNum == 3) {
//                                    additionGame();
//                                } else {
//                                    multiplicationGame();
//                                }
                            encounterGame();
                            r.playedGame = true;
                        }
                    }

                    for (Coin coinPos : World.coins) {
                        if(!coinPos.collected && posX == coinPos.x && posY == coinPos.y) {
                            coinPos.collected = true;
                        }

                        boolean allCoinsCollected = true;
                        for (Coin hello : World.coins) {
                            if (hello.collected == false) {
                                allCoinsCollected = false;
                                break;
                            }
                        }
                        if (allCoinsCollected) {
                            StdDraw.clear(Color.BLACK);
                            StdDraw.setPenColor(Color.WHITE);
                            StdDraw.text(35, 30, "You collected all the coins! Returning to main menu");
                            StdDraw.show();
                            StdDraw.pause(3000);
                            Menu menu = new Menu();
                            menu.start();
                        }
                    }

                    colon = false;
                }
            }
        }
    }

    int encounters = 0;
    private void rollADie() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 35, "Let's roll a die");

        StdDraw.text(35, 20, "(O) Odd");
        StdDraw.text(35, 15, "(E) Even");

        StdDraw.show();

        char playersPick;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'e' || c == 'E' || c == 'o' || c == 'O') {
                    playersPick = Character.toUpperCase(c);
                    break;
                }
            }
        }

        char computersPick;
        String[] wordOptions = {"even", "odd"};
        int randomNum = RANDOM.nextInt(6) + 1;

        int remainder = randomNum % 2;

        String typeOfNum;

        String result;
        if (remainder == 0) {
            typeOfNum = "even";
        } else {
            typeOfNum = "odd";
        }
        if ((playersPick == 'E' &&  remainder == 0 ) ||
                (playersPick == 'O' && remainder == 1)) {
            result = "You won!";
        } else {
            result = "You lost!";
        }

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 30, "The die rolled " + randomNum + ", which is " + typeOfNum + ". " + result);
        StdDraw.show();
        StdDraw.pause(3000);
    }

    private void multiplicationGame() {
        int numOne = RANDOM.nextInt(20) + 1;
        int numTwo = RANDOM.nextInt(20) + 1;
        int answer = numOne * numTwo;

        String userAnswer = "";
            mathScreen("*", userAnswer, numOne, numTwo);
            userAnswer = userMathAnswer("*", userAnswer, numOne, numTwo);
            int playerInt = Integer.parseInt(userAnswer);
            StdDraw.clear(Color.BLACK);
            if (playerInt == numOne * numTwo) {
                StdDraw.text(35, 30, "You're right! " + answer + " is the answer!");
            } else {
                StdDraw.text(35, 30, "You're wrong. " + answer + " is the correct answer.");
            }
            StdDraw.show();
            StdDraw.pause(3000);
    }

    private void additionGame() {
        int numOne = RANDOM.nextInt(999) + 1;
        int numTwo = RANDOM.nextInt(999) + 1;
        int answer = numOne + numTwo;

        String userAnswer = "";
        mathScreen("+", userAnswer, numOne, numTwo);
        userAnswer = userMathAnswer("+", userAnswer, numOne, numTwo);
        int playerInt = Integer.parseInt(userAnswer);
        StdDraw.clear(Color.BLACK);
        if (playerInt == numOne + numTwo) {
            StdDraw.text(35, 30, "You're right! " + answer + " is the answer!");
        } else {
            StdDraw.text(35, 30, "You're wrong. " + answer + " is the correct answer.");
        }
        StdDraw.show();
        StdDraw.pause(3000);
    }

    private void rockPaperScissors() {

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 35, "Let's play rock paper scissors!");

        StdDraw.text(35, 20, "(R) Rock");
        StdDraw.text(35, 15, "(P) Paper");
        StdDraw.text(35, 10, "(S) Scissors");

        StdDraw.show();

        char playersPick;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'r' || c == 'R' || c == 'p' || c == 'P' || c == 's' || c == 'S') {
                    playersPick = Character.toUpperCase(c);
                    break;
                }
            }
        }

        char computersPick;
        char[] options = {'R', 'P', 'S'};
        String[] wordOptions = {"Rock", "Paper", "Scissors"};
        int randomNum = RANDOM.nextInt(3);
        computersPick = options[randomNum];

        String result;
        if (playersPick == computersPick) {
            result = "We tied";
        } else if ((playersPick == 'R' && computersPick == 'S') ||
                        (playersPick == 'S' && computersPick == 'P') ||
                        (playersPick == 'P' && computersPick == 'R')
        ) {
            result = "You won!";
        } else {
            result = "I won!";
        }
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 30, "I played " + wordOptions[randomNum] + ". " + result);
        StdDraw.show();
        StdDraw.pause(3000);
    }

    private void flipACoin() {
        Random RANDOM = new Random();

        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 35, "I'm flipping a coin");

        StdDraw.text(35, 20, "(H) Heads");
        StdDraw.text(35, 15, "(T) Tails");

        StdDraw.show();

        char playersPick;
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'h' || c == 'H' || c == 't' || c == 'T') {
                    playersPick = Character.toUpperCase(c);
                    break;
                }
            }
        }

        char computersPick;
        char[] options = {'H', 'T'};
        String[] wordOptions = {"Heads", "Tails"};
        int randomNum = RANDOM.nextInt(2);
        computersPick = options[randomNum];

        String result;
        if (playersPick == computersPick) {
            result = "You won!";
        } else {
            result = "You lost!";
        }
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 30, "The coin was " + wordOptions[randomNum] + ". " + result);
        StdDraw.show();
        StdDraw.pause(3000);
    }

    private void menuScreen() {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 35, "CS61B: BYOW");

        StdDraw.text(35, 20, "(N) New Game");
        StdDraw.text(35, 15, "(L) Load Game");
        StdDraw.text(35, 10, "(Q) Quit");

        StdDraw.show();
    }

    private void mathScreen(String operation, String userAnswer, int numOne, int numTwo) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 35, "Are you good at math?");
        StdDraw.text(35, 30, "What is " + numTwo + " " + operation + " " + numOne + "?");

        StdDraw.text(35, 20, "Enter your answer followed by M");
        StdDraw.text(35, 15, userAnswer);

        StdDraw.show();
    }

    private String userMathAnswer(String operation, String userAnswer, int numOne, int numTwo) {
        StringBuilder seed = new StringBuilder("");
        mathScreen(operation, String.valueOf(seed), numOne, numTwo);
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char num = StdDraw.nextKeyTyped();
                if (num == 'M' || num == 'm') {
                    break;
                } else if (num != '0' && num != '1' && num != '2' && num != '3' && num != '4' &&  num != '5' &&
                        num != '6' &&  num != '7' &&  num != '8' &&  num != '9') {
                    continue;
                }
                seed.append(num);
                mathScreen(operation, String.valueOf(seed), numOne, numTwo);
            }
        }
        return String.valueOf(seed);
    }
    private void seedScreen(String seed) {
        StdDraw.clear(Color.BLACK);
        StdDraw.setPenColor(Color.WHITE);
        StdDraw.text(35, 35, "CS61B: BYOW");

        StdDraw.text(35, 20, "Enter seed followed by S");
        StdDraw.text(35, 15, seed);

        StdDraw.show();
    }

    private char userMenuInput() {
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char c = StdDraw.nextKeyTyped();
                if (c == 'N' || c == 'L' || c == 'Q'
                || c == 'n' || c == 'l' || c == 'q') {
                    return c;
                }
            }
        }
    }

    private String userSeed() {
        StringBuilder seed = new StringBuilder("");
        seedScreen(String.valueOf(seed));
        while (true) {
            if (StdDraw.hasNextKeyTyped()) {
                char num = StdDraw.nextKeyTyped();
                if (num == 'S' || num == 's') {
                    break;
                } else if (num != '0' && num != '1' && num != '2' && num != '3' && num != '4' &&  num != '5' &&
                        num != '6' &&  num != '7' &&  num != '8' &&  num != '9') {
                    continue;
                }
                seed.append(num);
                seedScreen(String.valueOf(seed));
            }
        }
        return String.valueOf(seed);
    }
}
