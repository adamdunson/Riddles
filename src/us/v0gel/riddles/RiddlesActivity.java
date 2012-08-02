package us.v0gel.riddles;

import java.util.ArrayList;
import java.util.Random;

import us.v0gel.R;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.ContextMenu.ContextMenuInfo;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.TextView;

public class RiddlesActivity
extends Activity
{
	public ArrayList<Riddle> riddles;
	public Riddle currentRiddle;
	private ListView riddlesListView;
	protected DatabaseHelper databaseHelper;
	private static Random generator = new Random();
	private SQLiteDatabase db;
	private Cursor cursor;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		databaseHelper = new DatabaseHelper(this);
		db = databaseHelper.getWritableDatabase();
		cursor = db.rawQuery("SELECT * FROM " + DatabaseHelper.RIDDLE_TABLE, null);
		initRiddles();
		riddleMeThis();
		mainRiddles();
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		MenuInflater inflater = getMenuInflater();
		inflater.inflate(R.menu.riddles_menu, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		switch (item.getItemId()) {
		case R.id.manage_riddles:
			manageRiddles();
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}

	/**
	 * Load the main content view
	 */
	private void mainRiddles() {
		setContentView(R.layout.main);

		final TextView riddleText = (TextView)findViewById(R.id.riddle_text);
		riddleText.setText(currentRiddle.getQuery());

		final Button riddleMe = (Button)findViewById(R.id.riddle_me);
		riddleMe.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				currentRiddle = riddles.get(generator.nextInt(riddles.size()));
				riddleText.setText(currentRiddle.getQuery());
			}
		});

		final Button answerMe = (Button)findViewById(R.id.answer_me);
		answerMe.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				riddleText.setText(currentRiddle.getResponse());
			}
		});
	}

	/**
	 * Load the manage riddles content view
	 * 
	 * TODO: Maybe move this to its own Activity?
	 */
	private void manageRiddles() {
		setContentView(R.layout.manage_riddles);
		riddlesListView = (ListView)findViewById(R.id.riddles_list);
		ArrayAdapter<Riddle> adapter = new ArrayAdapter<Riddle>(this, R.layout.riddle_view, riddles);
		riddlesListView.setAdapter(adapter);
		registerForContextMenu(riddlesListView);

		final Button backButton = (Button)findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mainRiddles();
			}
		});

		final Button addNew = (Button)findViewById(R.id.add_new);
		addNew.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addNewRiddle();
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		if(v.getId()==R.id.riddles_list) {
			String[] menuItems = getResources().getStringArray(R.array.manage_riddles_context_menu);
			for (int i = 0; i<menuItems.length; i++) {
				menu.add(Menu.NONE, i, i, menuItems[i]);
			}
		}
	}

	@Override
	public boolean onContextItemSelected(MenuItem item) {
		AdapterView.AdapterContextMenuInfo info = (AdapterView.AdapterContextMenuInfo)item.getMenuInfo();
		int menuItemIndex = item.getItemId();

		String[] menuItems = getResources().getStringArray(R.array.manage_riddles_context_menu);
		String menuItemName = menuItems[menuItemIndex];

		if(menuItemName.equals(getResources().getString(R.string.edit))) {
			editRiddle();
		} else if(menuItemName.equals(getResources().getString(R.string.delete))) {
			deleteRiddle(riddles.get(info.position));

		}

		return true;
	}
	
	/**
	 * Delete a riddle from the list.
	 * 
	 * TODO: Maybe add a Y/N prompt in the future?
	 */
	private void deleteRiddle(Riddle deadRiddle) {
		deadRiddle.deleteFrom(db);
		riddles.remove(deadRiddle);
		riddleMeThis();
		riddlesListView.invalidateViews();
	}

	/**
	 * Prompt the user to add a new riddle to the list and update the list.
	 * 
	 * TODO: Change addNewRiddle to just addNew (to match the edit, delete methods).
	 */
	private void addNewRiddle() {
		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getResources().getString(R.string.add_new_riddle));

		LayoutInflater factory = LayoutInflater.from(this);
		final View addNewRiddleView = factory.inflate(R.layout.add_new_riddle, null);
		alert.setView(addNewRiddleView);

		alert.setPositiveButton(getResources().getString(R.string.save_button), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				EditText newQuery = (EditText) addNewRiddleView.findViewById(R.id.add_new_query);
				EditText newResponse = (EditText) addNewRiddleView.findViewById(R.id.add_new_response);

				Riddle newRiddle = new Riddle(newQuery.getText().toString(), newResponse.getText().toString());
				riddles.add(newRiddle);
				newRiddle.saveTo(db);
				riddlesListView.invalidateViews();
			}
		});

		alert.setNegativeButton(getResources().getString(R.string.cancel_button), new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				dialog.cancel();
			}
		});

		alert.show();
	}
	
	/**
	 * Prompt the user to edit a riddle.
	 * 
	 * TODO
	 */
	private void editRiddle() {
		riddlesListView.invalidateViews();
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
	private void riddleMeThis() {
		currentRiddle = riddles.get(generator.nextInt(riddles.size()));
	}
}