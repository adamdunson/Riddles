package us.v0gel.riddles;

import us.v0gel.R;
import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RiddlesActivity
extends Activity
{
	private RiddlesApplication app;
	private TextView riddleText;

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		app = (RiddlesApplication)getApplicationContext();
		
		app.showQuery = true;
		setContentView(R.layout.main);
		
		riddleText = (TextView)findViewById(R.id.riddle_text);

		final Button riddleMe = (Button)findViewById(R.id.riddle_me);
		riddleMe.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				app.showQuery = true;
				app.riddleMeThis();
				riddleText.setText(app.currentRiddle.getQuery());
			}
		});

		final Button answerMe = (Button)findViewById(R.id.answer_me);
		answerMe.setOnClickListener(new View.OnClickListener() {
			public void onClick(View v) {
				app.showQuery = false;
				riddleText.setText(app.currentRiddle.getResponse());
			}
		});
	}

	@Override
	public void onResume() {
		super.onResume();
		if(app.showQuery) riddleText.setText(app.currentRiddle.getQuery());
		else riddleText.setText(app.currentRiddle.getResponse());
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
			Intent manageIntent = new Intent(this, ManageActivity.class);
			startActivity(manageIntent);
			return true;
		default:
			return super.onOptionsItemSelected(item);
		}
	}
}