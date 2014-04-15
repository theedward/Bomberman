package com.cmov.bomberman.model;

import android.content.res.Resources;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.LinkedList;
import java.util.List;

// This class provides utility functions that can be used by other classes.
// The init method must be called before using the readBitmapFromResource.
public class GameUtils {
	private static Resources resources;

	public static void init(Resources res) {
		resources = res;
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

	// Used on Android
	public static Bitmap readBitmapFromResource(int resourceId) {
		return BitmapFactory.decodeResource(resources, resourceId);
	}
}
