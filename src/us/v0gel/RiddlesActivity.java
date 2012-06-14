package us.v0gel;

import java.util.ArrayList;
import java.util.Random;

import android.app.Activity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;

public class RiddlesActivity
extends Activity
{
	public ArrayList<Riddle> riddles;
	public Riddle currentRiddle;
	private TextView riddleText;
	private static Random generator = new Random();

	/** Called when the activity is first created. */
	@Override
	public void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		setContentView(R.layout.main);

		initRiddles();
		currentRiddle = riddles.get(generator.nextInt(riddles.size()));

		riddleText = (TextView)findViewById(R.id.riddle_text);
		riddleText.setText(currentRiddle.getQuery());

		final Button riddleMe = (Button)findViewById(R.id.riddle_me);
		riddleMe.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				currentRiddle = riddles.get(generator.nextInt(riddles.size()));
				riddleText.setText(currentRiddle.getQuery());
			}
		});

		final Button answerMe = (Button)findViewById(R.id.answer_me);
		answerMe.setOnClickListener(new View.OnClickListener()
		{
			public void onClick(View v)
			{
				riddleText.setText(currentRiddle.getResponse());
			}
		});
	}

	/**
	 * Initialize the riddles
	 */
	private void initRiddles() {
		riddles = new ArrayList<Riddle>();
		riddles.add(new Riddle("This thing all things devours:\nBirds, beasts, trees, flowers;\nGnaws iron, bites steel;\nGrinds hard stones to meal;\nSlays king, ruins town,\nAnd beats high mountain down.", "Time"));
		riddles.add(new Riddle("What has roots as nobody sees,\nIs taller than trees\nUp, up it goes,\nAnd yet never grows?", "Mountains"));
		riddles.add(new Riddle("No-legs lay on one-leg, two legs sat near on three legs, four legs got some.", "Fish on a little one-legged table, man at table sitting on a three-legged stool, the cat gets the bones"));
		riddles.add(new Riddle("An eye in a blue face\nSaw an eye in a green face.\n'That eye is like to this eye'\nSaid the first eye,\n'But in low place\nNot in high place.'", "Sun shining on daisies which are growing in a field"));
		riddles.add(new Riddle("Alive without breath,\nAs cold as death;\nNever thirsty, ever drinking,\nAll in mail never clinking", "Fish"));
		riddles.add(new Riddle("It cannot be seen, cannot be felt,\nCannot be heard, cannot be smelt.\nIt lies behind stars and under hills,\nAnd empty holes it fills.\nIt comes first and follows after,\nEnds life, kills laughter.", "Dark"));
		riddles.add(new Riddle("Thirty white horses on a red hill,\nFirst they champ,\nThen they stamp,\nThen they stand still.", "Teeth"));
		riddles.add(new Riddle("A box without hinges, key or lid,\nYet golden treasure inside is hid.", "Egg"));
		riddles.add(new Riddle("Voiceless it cries,\nWingless flutters,\nToothless bites,\nMouthless mutters.", "Wind"));
	}
}