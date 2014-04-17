#Game Architecture

##Description

This game supports multiplayer. To minimize the amount of processing, there
is a centralized server where the game loop will be executed. When changes occur,
the server will notify every player that will just update their screen.
Also, everytime a player presses a key, this information is sent to the server.

##Architecture

	// Every object that will be printed on the screen must implement this
	// interface.
	interface Drawable {
		void draw(Canvas);
	}

	// Represents a position in the map.
	class Position {
		int x, y;
		public Position(int, int);
	}

	class Maze implements Drawable {}

	// All the state of the game should be in this class. It's this class that is
	// passed to the objects on their play method.
	class State {
		public char[][] map;
		private List<Playable> objects;

		// This method will call the method play of each player.
		void playAll();
		
		// This method will remove all the given objects from the state.
		void removeAll(List<Playable> objects);
	}

	// This class is responsible to draw everything in the canvas.
	class Screen {
		Canvas canvas;
		List<Drawable> objects;

		// Calls the method draw for all the objects
		public void drawAll(Canvas);
	}

	// This class allows extensibility on the choice of player character.
	// Ex: By using this class, a player can be any character (Bomberman, Robot,
	// Bomb).
	class GameConfiguration {
		// Maps player ids into their initial position in the map.
		Map<int, Position> initialPositions;
		int level;
		int numUpdatesPerSecond;
		int numPlayers;
		int timeLeft;
	}

	// This class provides utility functions that can be used by other classes.
	class GameUtils {
		static char[][] readLevelFromFile(String filename);

		// Used on Android
		static Bitmap readBitmapFromResource(int resourceId);
	}

	// This class represents a player in the game. This is needed to support
	// multiplayer.
	class Player {
		String username;
		int currentScore;
		Screen myScreen;
		List<Playable> objects;

		public Player(String username);

		// This method will create all the characters and all the drawables
		void onGameStart(GameConfiguration initialConfig);

		// This method will update all the state of the Player.
		// Ex: This will be called when any player pauses the game.
		void onGameUpdate(GameConfiguration currentConfig);

		// This method will be called when the game finishes. This can be useful to
		// tell each player who is the winner. (if there's any winner)
		void onGameEnd(GameConfiguration finalConfig);

		// This method will be called every round. This will update all the object's positions.
		void onUpdate(Position[] objectsPositions);
	}

	// To support a player in the network, this class will serve as proxy in the
	// server-side.
	class NetworkPlayer extends Player {
		// Override all methods to work in the network
	}

	// This is where all the game will be processed.
	class Game {
		Map<String, Player> players;
		State gameState;
		GameConfiguration currentConfig;

		static void readConfigurationFile(String filename);

		void addPlayer(Player p);
		void removePlayer(Player p);

		// Calls onGameStart on each player. Creates the first GameConfiguration.
		void start();
		void end();

		// Updates the state of the map. This is, moves players and detects collisions.
		void update();

		// Calls onGameUpdate
		void pause(String playerUsername);
		void unpause(String playerUsername);
		void stop();
		void restart();
	}

	// All objects that are updated during the game must implement this interface
	interface Playable {
		void play(State);
	}

	/**
	  * This class allows to create algorithms or even to control any object that
	  * depends on this interface.
	  * Ex: Bomberman, Robot
	  */
	interface Algorithm {
		public String getNextActionName();
	}

	// This class enables an algorithm to be provided by a user. (By controlling)
	// Must provide a keymap.
	class Controllable implements Algorithm {
		Map<Character, String> keymap;
		char lastKeyPressed;

		public Controllable(Map<Character, String> keymap);

		// Returns true if the key is valid, false otherwise
		public boolean keyPressed(char c);
	}

	// With this class, it's possible to use a controller in the network. This
	// will serve as proxy in the client-side.
	class NetworkControllable extends Controllable {
	}

	// An Agent is an object that can play (has an algorithm) and can be printed on the screen.
	abstract class Agent implements Drawable, Playable {
		Position currentPos;
		Algorithm algorithm;

		public Character(Position, Algorithm);
	}

	class Bomberman extends Agent {
		public enum Actions {
			MOVE_TOP, MOVE_BOTTOM, MOVE_LEFT, MOVE_RIGHT, PUT_BOMB
		};

		int bombSize;

		public Bomberman(Position, Algorithm, int bombSize);
	}

	class Robot extends Agent {
		public enum Actions {
			MOVE_TOP, MOVE_BOTTOM, MOVE_LEFT, MOVE_RIGHT
		};

		public Robot(Position, Algorithm);
	}

	class Bomb implements Agent {
		int timeout;

		public Bomb(Position, Algorithm);
		void explode(State);
	}

	class Wall implements Drawable {}

	class Obstacle implements Agent {}
