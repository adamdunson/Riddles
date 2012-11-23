package us.v0gel.riddles;

import java.util.ArrayList;
import java.util.Random;
import android.app.Application;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

public class RiddlesApplication
extends Application
{
	protected ArrayList<Riddle> riddles;
	private DatabaseHelper databaseHelper;
	protected SQLiteDatabase db;
	protected Cursor cursor;
	protected Riddle currentRiddle;
	protected boolean showQuery;
	private Random generator;

	public RiddlesApplication() {
		super();
	}
	
	@Override
	public void onCreate() {
		generator = new Random();
		databaseHelper = new DatabaseHelper(getApplicationContext());
		db = databaseHelper.getWritableDatabase();
		cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.RIDDLE_TABLE, null);
		initRiddles();
		riddleMeThis();
		showQuery = true;
	}

	/**
	 * Initialize the riddles
	 */
	private void initRiddles() {
		riddles = new ArrayList<Riddle>();

		if(cursor.getCount() == 0) {
			/* 
			 * These riddles are from J.R.R. Tolkien's The Hobbit, if you weren't aware.
			 */
			riddles.add(new Riddle("This thing all things devours:\nBirds, beasts, trees, flowers;\nGnaws iron, bites steel;\nGrinds hard stones to meal;\nSlays king, ruins town,\nAnd beats high mountain down.", "Time"));
			riddles.add(new Riddle("What has roots as nobody sees,\nIs taller than trees\nUp, up it goes,\nAnd yet never grows?", "Mountains"));
			riddles.add(new Riddle("No-legs lay on one-leg, two legs sat near on three legs, four legs got some.", "Fish on a little one-legged table, man at table sitting on a three-legged stool, the cat gets the bones"));
			riddles.add(new Riddle("An eye in a blue face\nSaw an eye in a green face.\n'That eye is like to this eye'\nSaid the first eye,\n'But in low place\nNot in high place.'", "Sun shining on daisies which are growing in a field"));
			riddles.add(new Riddle("Alive without breath,\nAs cold as death;\nNever thirsty, ever drinking,\nAll in mail never clinking", "Fish"));
			riddles.add(new Riddle("It cannot be seen, cannot be felt,\nCannot be heard, cannot be smelt.\nIt lies behind stars and under hills,\nAnd empty holes it fills.\nIt comes first and follows after,\nEnds life, kills laughter.", "Dark"));
			riddles.add(new Riddle("Thirty white horses on a red hill,\nFirst they champ,\nThen they stamp,\nThen they stand still.", "Teeth"));
			riddles.add(new Riddle("A box without hinges, key or lid,\nYet golden treasure inside is hid.", "Egg"));
			riddles.add(new Riddle("Voiceless it cries,\nWingless flutters,\nToothless bites,\nMouthless mutters.", "Wind"));

			for(Riddle riddle : riddles) {
				riddle.saveTo(db);
			}
		} else {
			cursor.moveToFirst();
			for(int i = 0; i < cursor.getCount(); i++) {
				riddles.add(new Riddle(
						cursor.getInt(cursor.getColumnIndex(DatabaseHelper.RIDDLE_ID_COLUMN)),
						cursor.getString(cursor.getColumnIndex(DatabaseHelper.RIDDLE_QUERY_COLUMN)),
						cursor.getString(cursor.getColumnIndex(DatabaseHelper.RIDDLE_RESPONSE_COLUMN)))
						);
				cursor.moveToNext();
			}
		}

		cursor.close();
	}


	/**
	 * Choose a random riddle and assign it to currentRiddle.
	 * 
	 * This requires that generator be a Random object.
	 */
	protected void riddleMeThis() {
		currentRiddle = riddles.get(generator.nextInt(riddles.size()));
	}
}