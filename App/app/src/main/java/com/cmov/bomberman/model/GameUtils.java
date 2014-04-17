package com.cmov.bomberman.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.JsonReader;
import com.cmov.bomberman.R;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

// This class provides utility functions that can be used by other classes.
// The init method must be called before using the readBitmapFromResource.
public class GameUtils {
	/**
	 * The image files have lots of sprites. The block size is the size of each image in the sprite.
	 */
	private static final int IMAGE_BLOCK_SIZE = 10;

	/**
	 * The number of blocks that fit in the canvas.
	 */
	private static final int NUM_BLOCK_CANVAS = 20;
	private static int canvasWidth;
	private static int canvasHeight;
	private static Resources resources;

	public static void init(Resources resources, int canvasWidth, int canvasHeight) {
		GameUtils.resources = resources;
		GameUtils.canvasWidth = canvasWidth;
		GameUtils.canvasHeight = canvasHeight;
	}

	public static char[][] readLevelFromFile(String filename) {
		List<char[]> map = new LinkedList<char[]>();
		try {
			BufferedReader rd = new BufferedReader(new FileReader(filename));
			String line;
			while ((line = rd.readLine()) != null) {
				map.add(line.toCharArray());
			}
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
		}
		catch (IOException e) {
			System.out.println("Error while reading file: " + filename);
		}

		return (char[][]) map.toArray();
	}

	public static GameConfiguration readConfigurationFile(String filename) {
		GameConfiguration config = new GameConfiguration();
		try {
			JsonReader rd = new JsonReader(new FileReader(filename));
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
				} else if (msg.equals("NumUpdatesPerSecond")) {
					config.setNumUpdatesPerSecond(rd.nextInt());
				} else if (msg.equals("PointRobot")) {
					config.setPointRobot(rd.nextInt());
				} else if (msg.equals("PointOpponent")) {
					config.setPointOpponent(rd.nextInt());
				} else if (msg.equals("BombermanInitialPositions")) {
					List<Position> bombermanPositions = new LinkedList<Position>();
					// Read array of positions
					rd.beginArray();
					while (rd.hasNext()) {
						Position p = new Position();
						// Read position object
						rd.beginObject();
						msg = rd.nextName();
						if (msg.equals("X")) {
							p.setX((float) rd.nextDouble());
						} else if (msg.equals("Y")) {
							p.setY((float) rd.nextDouble());
						}
						rd.endObject();
						bombermanPositions.add(p);
					}
					rd.endArray();
					config.setBombermanInitialPositions((Position[]) bombermanPositions.toArray());
				} else if (msg.equals("RobotInitialPositions")) {
					List<Position> robotPositions = new LinkedList<Position>();
					// Read array of positions
					rd.beginArray();
					while (rd.hasNext()) {
						Position p = new Position();
						// Read position object
						rd.beginObject();
						msg = rd.nextName();
						if (msg.equals("X")) {
							p.setX((float) rd.nextDouble());
						} else if (msg.equals("Y")) {
							p.setY((float) rd.nextDouble());
						}
						rd.endObject();
						robotPositions.add(p);
					}
					rd.endArray();
					config.setRobotInitialPositions((Position[]) robotPositions.toArray());
				}
			}
			rd.endObject();
			rd.close();
		}
		catch (FileNotFoundException e) {
			System.out.println("File not found: " + filename);
		}
		catch (IOException e) {
			System.out.println("Error while reading file: " + filename);
		}

		return config;
	}

	// Used on Android
	public static Bitmap readLevelBitmap(int row, int col, int width, int height) {
		Bitmap level = BitmapFactory.decodeResource(resources, R.drawable.levels);
		level = Bitmap.createBitmap(level, row * IMAGE_BLOCK_SIZE,
									col * IMAGE_BLOCK_SIZE, width, height);
		return Bitmap.createScaledBitmap(level, canvasWidth, canvasHeight, true);

	}

	public static Bitmap[] readCharacterSprite(int row, int col, int num) {
		Bitmap characters = BitmapFactory
				.decodeResource(resources, R.drawable.characters);
		Bitmap[] sprite = new Bitmap[num];
		for (int i = 0; i < num; i++) {
			sprite[i] = Bitmap.createBitmap(characters, row * IMAGE_BLOCK_SIZE,
											(col + i) * IMAGE_BLOCK_SIZE,
											IMAGE_BLOCK_SIZE,
											IMAGE_BLOCK_SIZE);
			sprite[i] = Bitmap.createScaledBitmap(sprite[i],
												  canvasWidth / NUM_BLOCK_CANVAS,
												  canvasHeight / NUM_BLOCK_CANVAS,
												  true);
		}
		return sprite;
	}
}
