package application;


public class Settings {

    public static double SCENE_WIDTH = 1280;
    public static double SCENE_HEIGHT = 720;

    public static int ATTRACTOR_COUNT = 3;
    public static int VEHICLE_COUNT = 5;

    public static double SPRITE_MAX_SPEED = 1;
    public static double SPRITE_MAX_FORCE = 0.05;

    // distance at which the sprite moves slower towards the target
    public static double SPRITE_SLOW_DOWN_DISTANCE = 300;

    //kill limit to stop the game
    public static int KILL_LIMIT = 100;

    public static String scoreStyle = "" +
            "-fx-font: 25px Tahoma;" +
            "-fx-fill: linear-gradient(from 0% 0% to 100% 200%, repeat, aqua 0%, red 50%);" +
            "-fx-stroke: black;" +
            "-fx-stroke-width: 1;";

    public static String GAME_NAME = "Balls'n'Arrows";
}