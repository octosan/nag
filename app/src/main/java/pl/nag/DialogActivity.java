package pl.nag;

import android.app.Activity;
import android.content.Intent;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import pl.nag.model.Answer;

public class DialogActivity extends Activity {

    private static final List<Integer> ANSWERS_IDS = Arrays.asList(R.id.answer0, R.id.answer1, R.id.answer2, R.id.answer3);
    public static ScriptManager scriptManager = null;

    private Map<Integer, Double> pointsMap = new HashMap<Integer, Double>();
    private double points;
    private int index;
    private int episode;
    private List<Button> buttons = new ArrayList<Button>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dialog);

        Intent incomingIntent = this.getIntent();

        index = incomingIntent.getIntExtra(ExtraKey.INDEX.name(), 0);
        points = incomingIntent.getDoubleExtra(ExtraKey.POINTS.name(), 0);
        episode = incomingIntent.getIntExtra(ExtraKey.EPISODE.name(), 0);

        if (scriptManager == null) {
            scriptManager = new ScriptManager(this);
        }
        scriptManager.setNodeIndex(index);

        Log.d("StateLog", "Application index: " + index);
        Log.d("StateLog", "Points: " + points);

        // View setup
        TextView description = (TextView) findViewById(R.id.description);
        description.setText(scriptManager.getDescription());
        TextView question = (TextView) findViewById(R.id.question);
        question.setText(scriptManager.getQuestion());
        updateImage(scriptManager.getImageName());

        ProgressBar timeLeftBar = (ProgressBar) findViewById(R.id.timeLeft);
        new TimeLeftTimer(timeLeftBar, new OnFinishCallback() {
            @Override
            public void onFinish() {
                for (Button b : buttons) {
                    if (b.getId() == R.id.answer0) {
                        onClick(b);
                    } else {
                        b.setEnabled(false);
                    }
                }
            }
        }).start();

        // Shuffling of buttons
        Answer answer = scriptManager.getNextAnswer();
        Collections.shuffle(ANSWERS_IDS);
        for (int i = 0; i < ANSWERS_IDS.size(); i++) {
            Button button = (Button) findViewById(ANSWERS_IDS.get(i));
            if (answer != null && answer.getOptions() != null && answer.getOptions().get(i) != null) {
                button.setText(answer.getOptions().get(i).getText());
            }
            pointsMap.put(ANSWERS_IDS.get(i), answer.getOptions().get(i).getValue());
            buttons.add(button);
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        return id == R.id.action_settings ? true : super.onOptionsItemSelected(item);
    }

    public void onClick(View view) {
        Intent nextIntent;

        if (pointsMap.containsKey(view.getId())) {
            nextIntent = new Intent(this, ((NakApp) getApplication()).getNextActivityClass());
            nextIntent.putExtra(ExtraKey.VIDEOID.name(), scriptManager.getMovie());
            nextIntent.putExtra(ExtraKey.POINTS.name(), points + pointsMap.get(view.getId()));
            nextIntent.putExtra(ExtraKey.INDEX.name(), index + 1);
        } else {
            return;
        }
        startActivity(nextIntent);
    }

    private void updateImage(String imageName) {
        try {
            Bitmap bitmap = new ImageHelper(this).getRawByFileName(imageName);
            if (bitmap != null) {
                ImageView imageView = (ImageView) findViewById(R.id.imageView);
                imageView.setImageBitmap(bitmap);
            }
        } catch (Resources.NotFoundException e) {
            GuiHelper.toast(this, imageName + " Not Found");
        }
    }
}
