package core;

import edu.princeton.cs.algs4.In;
import edu.princeton.cs.algs4.Out;
import edu.princeton.cs.algs4.StdDraw;
import tileengine.TETile;
import tileengine.Tileset;

import java.awt.*;
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
                loadGame();
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

        Out out = new Out("Save.txt");

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

    private void loadGame() {
        World.createWorld(1);
        int row = 0;
        int col = 0;
        String filename = "Save.txt";
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
                            encounters += 1;
                            int randomNum = RANDOM.nextInt(5);
                                if (randomNum == 0) {
                                    rockPaperScissors();
                                } else if (randomNum == 1) {
                                    flipACoin();
                                } else if (randomNum == 2) {
                                    rollADie();
                                } else if (randomNum == 3) {
                                    additionGame();
                                } else {
                                    multiplicationGame();
                                }
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
