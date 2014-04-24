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

/**
 * This class provides utility functions that can be used by other classes.
 * The init method must be called before using any method related to sprites.
 */
public class GameUtils {
	/**
	 * The width any sprite will have when drawn in the screen.
	 */
	public static final int IMG_WIDTH = 60;

	/**
	 * The height any sprite will have when drawn in the screen.
	 */
	public static final int IMG_HEIGHT = 60;

	/**
	 * This is needed to access all the project resources like files.
	 */
	private static Context context;

	public static void init(final Context context) {
		GameUtils.context = context;
	}

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
			BufferedReader rd = new BufferedReader(new InputStreamReader(context.getAssets().open(filename)));
			final int width = Integer.parseInt(rd.readLine());
			final int height = Integer.parseInt(rd.readLine());

			char[][] map = new char[height][width];

			for (int i = 0; i < height; i++) {
				String line = rd.readLine();
				for (int j = 0; j < width; j++) {
					map[i][j] = line.charAt(j);
				}
			}
			return map;
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
		}
		catch (IOException e) {
			System.out.println("Error while reading file: " + filename);
		}

		return new char[0][0];
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
			JsonReader rd = new JsonReader(new InputStreamReader(context.getAssets().open(filename)));
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
	 * Reads the wall sprite from the resource file.
	 * @return the wall image
	 */
	public static Bitmap readWallSprite() {
		Bitmap tilesSprite = BitmapFactory.decodeResource(context.getResources(), R.drawable.bomberman_tiles_sheet);

		// Each image has this width and height
		final int imgWidth = 30;
		final int imgHeight = 32;

		// Wall image is the third one
		Bitmap wallImg = Bitmap.createBitmap(tilesSprite, 2 * imgWidth, 0, imgWidth, imgHeight);
		return Bitmap.createScaledBitmap(wallImg, IMG_WIDTH, IMG_HEIGHT, true);
	}
}
