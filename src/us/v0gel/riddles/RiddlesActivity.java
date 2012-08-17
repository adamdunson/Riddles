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
	 * Load the main content view.
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
	 * Load the manage riddles content view.
	 * 
	 * TODO: Move this stuff to its own Activity
	 */
	private void manageRiddles() {
		setContentView(R.layout.manage_riddles);
		riddlesListView = (ListView)findViewById(R.id.riddles_list);
		ArrayAdapter<Riddle> adapter = new ArrayAdapter<Riddle>(this, R.layout.riddle_view, riddles);
		riddlesListView.setAdapter(adapter);
		registerForContextMenu(riddlesListView);

		final Button addNew = (Button)findViewById(R.id.add_new);
		addNew.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addNewRiddle();
			}
		});

		final Button backButton = (Button)findViewById(R.id.back_button);
		backButton.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				mainRiddles();
			}
		});
	}

	@Override
	public void onCreateContextMenu(ContextMenu menu, View v, ContextMenuInfo menuInfo) {
		menu.setHeaderTitle(R.string.manage_riddles);
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
			editRiddle(riddles.get(info.position));
		} else if(menuItemName.equals(getResources().getString(R.string.delete))) {
			deleteRiddle(riddles.get(info.position));
		}

		return true;
	}

	/**
	 * Prompt the user to add a new riddle to the list and update the list.
	 */
	private void addNewRiddle() {
		LayoutInflater factory = LayoutInflater.from(this);
		final View editRiddleView = factory.inflate(R.layout.edit_riddle, null);
		final EditText queryField = (EditText)editRiddleView.findViewById(R.id.edit_query);
		final EditText responseField = (EditText)editRiddleView.findViewById(R.id.edit_response);

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case DialogInterface.BUTTON_POSITIVE:
					Riddle riddle = new Riddle(queryField.getText().toString(), responseField.getText().toString());
					riddles.add(riddle);
					riddle.saveTo(db);
					riddlesListView.invalidateViews();

					break;
				case DialogInterface.BUTTON_NEGATIVE:
					dialog.cancel();
					break;
				}
			}
		};

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getResources().getString(R.string.add_new_riddle));
		alert.setView(editRiddleView);
		alert.setPositiveButton(getResources().getString(R.string.save), listener);
		alert.setNegativeButton(getResources().getString(R.string.cancel), listener);
		alert.show();
	}

	/**
	 * Prompt the user to edit a riddle and update it in the list.
	 */
	private void editRiddle(final Riddle riddle) {
		LayoutInflater factory = LayoutInflater.from(this);
		final View editRiddleView = factory.inflate(R.layout.edit_riddle, null);
		final EditText queryField = (EditText)editRiddleView.findViewById(R.id.edit_query);
		final EditText responseField = (EditText)editRiddleView.findViewById(R.id.edit_response);

		queryField.setText(riddle.getQuery());
		responseField.setText(riddle.getResponse()); 

		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case DialogInterface.BUTTON_POSITIVE:
					riddle.setQuery(queryField.getText().toString());
					riddle.setResponse(responseField.getText().toString());
					riddle.saveTo(db);
					riddlesListView.invalidateViews();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					dialog.cancel();
					break;
				}
			}
		};

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getResources().getString(R.string.edit_riddle));
		alert.setView(editRiddleView);
		alert.setPositiveButton(getResources().getString(R.string.save), listener);
		alert.setNegativeButton(getResources().getString(R.string.cancel), listener);
		alert.show();
	}

	/**
	 * Prompt the user to delete a riddle and delete it).
	 */
	private void deleteRiddle(final Riddle riddle) {
		DialogInterface.OnClickListener listener = new DialogInterface.OnClickListener() {
			@Override
			public void onClick(DialogInterface dialog, int which) {
				switch(which) {
				case DialogInterface.BUTTON_POSITIVE:
					riddle.deleteFrom(db);
					riddles.remove(riddle);
					riddleMeThis();
					riddlesListView.invalidateViews();
					break;
				case DialogInterface.BUTTON_NEGATIVE:
					dialog.cancel();
					break;
				}
			}
		};

		AlertDialog.Builder alert = new AlertDialog.Builder(this);
		alert.setTitle(getResources().getString(R.string.delete_riddle));
		alert.setMessage(getResources().getString(R.string.are_you_sure));
		alert.setPositiveButton(getResources().getString(R.string.yes), listener);
		alert.setNegativeButton(getResources().getString(R.string.no), listener);
		alert.show();
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