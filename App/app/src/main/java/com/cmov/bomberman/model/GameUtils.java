package com.cmov.bomberman.model;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import com.cmov.bomberman.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.LinkedList;

/**
 * This class provides utility functions that can be used by other classes.
 * The init method must be called before using any method related to sprites.
 */
public class GameUtils {
	/**
	 * The width any bitmap has on the resource file.
	 */
	public static final int IMG_WIDTH = 30;

	/**
	 * The height any bitmap has on the resource file.
	 */
	public static final int IMG_HEIGHT = 30;

	/**
	 * The width any bitmap will occupy in the canvas.
	 */
	public static int IMG_CANVAS_WIDTH;

	/**
	 * The height any bitmap will occupy in the canvas.
	 */
	public static int IMG_CANVAS_HEIGHT;

	/**
	 * This is needed to access all the project resources like files.
	 */
	public static Context CONTEXT;

	/**
	 * @param level the game level
	 * @return the file name of this level.
	 */
	private static String levelFilename(final int level) { return "level_" + level + ".txt"; }

	/**
	 * Reads the map correspondent to the level from a resource file.
	 * @param level the game level
	 * @return the map
	 */
	public static char[][] readLevelFromFile(final int level) {
		final String filename = GameUtils.levelFilename(level);

		try {
			BufferedReader rd = new BufferedReader(new InputStreamReader(CONTEXT.getAssets().open(filename)));
			LinkedList<String> list = new LinkedList<String>();

			String line;
			int width = 0;
			while ((line = rd.readLine()) != null) {
				list.add(line);
				width = line.length();
			}

			String[] array = list.toArray(new String[list.size()]);
			char[][] map = new char[array.length][width];
			for (int i = 0; i < array.length; i++) {
				map[i] = array[i].toCharArray();
			}

			return map;
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
		}
		catch (IOException e) {
			System.out.println("Error while reading file: " + filename);
		}

		return null;
	}

	/**
	 * @param level the game level
	 * @return the file name of the configuration file for this level.
	 */
	private static String configFilename(final int level) {
		return "configuration_" + level + ".txt";
	}

	/**
	 * Reads the configuration file correspondent to the level from a resource file.
	 * @param level the game level
	 * @return the game configuration
	 */
	public static GameConfiguration readConfigurationFile(final int level) {
		final String filename = configFilename(level);
		final GameConfiguration config = new GameConfiguration();
		try {
			JsonReader rd = new JsonReader(new InputStreamReader(CONTEXT.getAssets().open(filename)));
			rd.beginObject();
			while (rd.hasNext()) {
				String msg = rd.nextName();
				if (msg.equals("NumUpdatesPerSecond")) {
					config.setNumUpdatesPerSecond(rd.nextInt());
				} else if (msg.equals("MaxNumPlayers")) {
					config.setMaxNumPlayers(rd.nextInt());
				} else if (msg.equals("TimeLimit")) {
					config.setTimeLimit(rd.nextInt());
				} else if (msg.equals("BombermanSpeed")) {
					config.setbSpeed(rd.nextInt());
				} else if (msg.equals("RobotSpeed")) {
					config.setrSpeed(rd.nextInt());
				} else if (msg.equals("TimeToExplode")) {
					config.setTimeToExplode(rd.nextInt());
				} else if (msg.equals("ExplosionDuration")) {
					config.setExplosionDuration(rd.nextInt());
				} else if (msg.equals("ExplosionRange")) {
					config.setExplosionRange(rd.nextInt());
				} else if (msg.equals("PointRobot")) {
					config.setPointRobot(rd.nextInt());
				} else if (msg.equals("PointOpponent")) {
					config.setPointOpponent(rd.nextInt());
				} else if (msg.equals("MapWidth")) {
					config.setMapWidth(rd.nextInt());
				} else if (msg.equals("MapHeight")) {
					config.setMapHeight(rd.nextInt());
				} else {
					rd.skipValue();
				}
			}
			rd.endObject();
			rd.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
		}
		catch (IOException e) {
			e.printStackTrace();
		}

		return config;
	}

	/**
	 * Gets the bitmap in the nth row and the nth column.
	 * This only works in the bomberman_*_sheet sprites.
	 *
	 * @param src the initial resource file
	 * @param row the row of the destiny bitmap
	 * @param column the column of the destiny bitmap
	 * @return the bitmap in the specified row and column in the src bitmap assuming every image
	 * has 30px width and 30px height.
	 */
	private static Bitmap getBitmap(final Bitmap src, final int row, final int column) {
		Bitmap bm = Bitmap.createBitmap(src, (column - 1) * IMG_WIDTH, (row - 1) * IMG_HEIGHT, IMG_WIDTH, IMG_HEIGHT);
		return Bitmap.createScaledBitmap(bm, IMG_CANVAS_WIDTH, IMG_CANVAS_HEIGHT, true);
	}

	/**
	 * Reads the wall sprite from the resource file.
	 * @return the wall image
	 */
	public static Bitmap readWallSprite() {
		// Wall: 3rd row, 1st column
		return getBitmap(BitmapFactory.decodeResource(CONTEXT.getResources(), R.drawable.bomberman_tiles_sheet), 1, 3);
	}

	/**
	 * Reads the bomb sprite from the resource file
	 * @return the bomb image on the normal mode
	 */
	public static Bitmap[] readBombSprite() {
		final Bitmap bombSprite = BitmapFactory.decodeResource(CONTEXT.getResources(), R.drawable.bomberman_bomb_sheet);
		final int numBitmaps = 3;
		final Bitmap[] bombImg = new Bitmap[numBitmaps];

		// These images are all in the first column and are separated by an empty position
		for (int i = 0; i < numBitmaps; i++) {
			// 1 + 2*i row, 1st column
			bombImg[i] = getBitmap(bombSprite, 1 + 2*i, 1);
		}

		return bombImg;
	}

	/**
	 * Reads the bomb explosion sprite from the resource file.
	 * The order in which the bitmaps are in the array is:
	 * (Left, Up, Right, Down, Center, Vertical, Horizontal)
	 *
	 * @return a bomb img for each step (there are 4 steps)
	 */
	public static Bitmap[][] readBombExplosionSprite() {
		Bitmap bombSprite = BitmapFactory.decodeResource(CONTEXT.getResources(), R.drawable.bomberman_bomb_sheet);

		final int numSteps = 4;
		final int numBitmaps = 7;

		Bitmap[][] bombImg = new Bitmap[numSteps][numBitmaps];
		final int distanceBetweenSteps = 6;
		for (int i = 0; i < numSteps; i++) {
			// Left
			bombImg[i][0] = getBitmap(bombSprite, 3, 3 + i * distanceBetweenSteps);
			// Up
			bombImg[i][1] = getBitmap(bombSprite, 1, 5 + i * distanceBetweenSteps);
			// Right
			bombImg[i][2] = getBitmap(bombSprite, 3, 7 + i * distanceBetweenSteps);
			// Down
			bombImg[i][3] = getBitmap(bombSprite, 5, 5 + i * distanceBetweenSteps);
			// Center
			bombImg[i][4] = getBitmap(bombSprite, 3, 5 + i * distanceBetweenSteps);
			// Vertical
			bombImg[i][5] = getBitmap(bombSprite, 1, 7 + i * distanceBetweenSteps);
			// Horizontal
			bombImg[i][6] = getBitmap(bombSprite, 5, 5 + i * distanceBetweenSteps);
		}

		return bombImg;
	}

	/**
	 * Reads the bomberman sprite from the resource file.
	 * The order in which the bitmaps are in the array is:
	 * (Down, Left, Up, Right, PowerUp?, Destroyed)
	 * @return the bomberman img for each possible action (there are 6 different actions) and for
	 * each step (there are 3 steps)
	 */
	public static Bitmap[][] readBombermanSprite() {
		Bitmap bombermanSprite = BitmapFactory.decodeResource(CONTEXT.getResources(), R.drawable.bomberman_bomberman_sheet);
		final int numActions = 6;
		final int numSteps = 3;

		Bitmap[][] bombermanImg = new Bitmap[numActions][numSteps];
		final int distanceBetweenActions = 2;
		final int distanceBetweenSteps = 2;
		for (int actionIdx = 0; actionIdx < numActions; actionIdx++) {
			for (int stepIdx = 0; stepIdx < numSteps; stepIdx++) {
				bombermanImg[actionIdx][stepIdx] = getBitmap(bombermanSprite, 1 + distanceBetweenSteps * stepIdx,
															 1 + distanceBetweenActions * actionIdx);
			}
		}
		return bombermanImg;
	}

	/**
	 * Reads the obstacle sprite from the resource file.
	 * @return the obstacle img for each step (there are 7 steps)
	 */
	public static Bitmap[] readObstacleSprite() {
		Bitmap obstacleSprite = BitmapFactory.decodeResource(CONTEXT.getResources(), R.drawable.bomberman_tiles_sheet);
		final int numSteps = 7;

		Bitmap[] obstacleImg = new Bitmap[numSteps];
		final int firstColumn = 5;
		final int distanceBetweenSteps = 2;
		for (int stepIdx = 0; stepIdx < numSteps; stepIdx++) {
			obstacleImg[stepIdx] = getBitmap(obstacleSprite, 1, firstColumn + distanceBetweenSteps * stepIdx);
		}
		return obstacleImg;
	}

	/**
	 * Reads the robot sprite from the resource file.
	 * @return the robot img for each action (MOVE_LEFT and MOVE_RIGHT) and for each step (there are 3 steps).
	 */
	public static Bitmap[][] readRobotSprite() {
		Bitmap robotSprite = BitmapFactory.decodeResource(CONTEXT.getResources(), R.drawable.bomberman_enemies_sheet);
		// moving left or moving right
		final int numActions = 2;
		final int numSteps = 3;

		Bitmap[][] robotImg = new Bitmap[numActions][numSteps];
		final int distanceBetweenSteps = 2;
		final int distanceBetweenActions = 6;
		for (int actionIdx = 0; actionIdx < numActions; actionIdx++) {
			for (int stepIdx = 0; stepIdx < numSteps; stepIdx++) {
				robotImg[actionIdx][stepIdx] = getBitmap(robotSprite, 1, 1 + distanceBetweenActions * actionIdx +
																		 distanceBetweenSteps * stepIdx);
			}
		}
		return robotImg;
	}

	/**
	 * Reads the robot sprite from the resource file.
	 * @return the robot img for each step (there are 5 steps) of the robot in the destroyed mode.
	 */
	public static Bitmap[] readRobotDestroyedSprite() {
		Bitmap robotSprite = BitmapFactory.decodeResource(CONTEXT.getResources(), R.drawable.bomberman_enemies_sheet);
		final int numSteps = 5;

		Bitmap[] robotImg = new Bitmap[numSteps];
		final int firstColumn = 6;
		final int distanceBetweenSteps = 2;
		for (int stepIdx = 0; stepIdx < numSteps; stepIdx++) {
			robotImg[stepIdx] = getBitmap(robotSprite, 1, 1 + (firstColumn + stepIdx) * distanceBetweenSteps);
		}
		return robotImg;
	}
}
