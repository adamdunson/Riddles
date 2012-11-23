package us.v0gel.riddles;

import us.v0gel.R;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.ContextMenu;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ContextMenu.ContextMenuInfo;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ListView;

public class ManageActivity
extends Activity
{
	private RiddlesApplication app;
	private ListView riddlesListView;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (RiddlesApplication)getApplicationContext();

		setContentView(R.layout.manage_riddles);
		riddlesListView = (ListView)findViewById(R.id.riddles_list);
		ArrayAdapter<Riddle> adapter = new ArrayAdapter<Riddle>(this, R.layout.riddle_view, app.riddles);
		riddlesListView.setAdapter(adapter);
		registerForContextMenu(riddlesListView);

		final Button addNew = (Button)findViewById(R.id.add_new);
		addNew.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				addNewRiddle();
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
			editRiddle(app.riddles.get(info.position));
		} else if(menuItemName.equals(getResources().getString(R.string.delete))) {
			deleteRiddle(app.riddles.get(info.position));
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
					app.riddles.add(riddle);
					riddle.saveTo(app.db);
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
					riddle.saveTo(app.db);
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
					riddle.deleteFrom(app.db);
					app.riddles.remove(riddle);
					app.riddleMeThis();
					app.showQuery = true;
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
}