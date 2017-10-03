package madcourse.neu.edu.runpunymammal;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.hardware.SensorManager;
import android.media.Image;
import android.media.MediaPlayer;
import android.net.Uri;
import android.os.Bundle;
import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.os.CountDownTimer;
import android.widget.ImageView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Random;
import java.util.Set;
import java.util.HashMap;



public class PlayActivityFragment extends Fragment{
    DBHandler myHandler;
    SQLiteDatabase db;
    Context context;
    MediaPlayer mediaPlayer;
    CountDownTimer ctimer;
    int tick_length = 1000;
    long starting_time = 100000;
    final Integer[] critters = new Integer[] {5, 40, 75};
    final Set<Integer> creatureTimes = new HashSet<Integer>(Arrays.asList(critters));
    final List<Integer> outsideCreatures = new ArrayList<>(Arrays.asList(1, 2));
    final List<Integer> insideCreatures = new ArrayList<>(Arrays.asList(3, 4));

    View view;
    TextView command_view;
    int pace_start = 0;
    String desired_outside_speed; // = context.getResources().getString(R.string.medium_pace);
    String desired_inside_speed; //= context.getResources().getString(R.string.stand_still);
    String current_desired_pace;
    String desired_default_speed;
    Boolean is_outside = true; //set based on level selected.
    int ticks_passed = 0;
    int new_ticks_passed = 0;


    int paceStart = 0;
    final int paceLength = 2;
    int paceNumber = 0;

    String[] paces;
    TextView test; //only for testing
    int step_number = 4;
    int step_base = 0; //number of steps not being counted

    SharedPreferences sp;
    int steps;

    //these values represent the creature currently active
    String creatureName;
    String creatureSoundfile;
    String creatureRewards;
    String creatureBehavior;

    String walk;
    String jog;
    String run;
    //String feathers;
    //Strin


    HashMap<String, Integer> rewards = new HashMap<>();

    int upperBound = 100; //for testing
    int lowerBound = 0; //for testing
    boolean creatureSelected = false;
    int creatureStart = 0;
    int internal_count =0;
    boolean counting = false;
    boolean measuring = false;
    int measureCount = 1;
    boolean runNow = false;
    int INTERNAL_COUNT = 4;

    ImageView critterView;

    public PlayActivityFragment() {
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        this.context = getActivity();
        walk = context.getResources().getString(R.string.walk);
        jog = context.getResources().getString(R.string.jog);
        run = context.getResources().getString(R.string.run);
        desired_inside_speed = context.getResources().getString(R.string.slow_pace);
        desired_outside_speed = context.getResources().getString(R.string.medium_pace);
        myHandler = new DBHandler(this.context);
        db = myHandler.getReadableDatabase();
        rewards.put("feathers", 0);
        rewards.put("fangs", 0);
        rewards.put("claws", 0);
        rewards.put("furs", 0);
        myHandler.onCreate(db);
        mediaPlayer = MediaPlayer.create(context, R.raw.background);
        mediaPlayer.start();

        sp = this.getActivity().getSharedPreferences("OURINFO", Context.MODE_PRIVATE);
        is_outside = sp.getBoolean("outside", true);

        if (is_outside) {
            desired_default_speed = desired_outside_speed;
        } else {
            desired_default_speed = desired_inside_speed;
        }
        current_desired_pace = desired_default_speed;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        startTimer(starting_time);
        View rootView = inflater.inflate(R.layout.play_fragment, container, false);
        this.view = rootView;
        this.command_view = (TextView) view.findViewById(R.id.command);
        command_view.setText(current_desired_pace);
        //test = (TextView) view.findViewById(R.id.test);
        //test.setText("testing");
        critterView = (ImageView) view.findViewById(R.id.icon);
        critterView.setImageResource(R.drawable.tree);

        return rootView;
    }

    public void startTimer(long time) {

        if (ctimer != null) {
            ctimer.cancel();
        };

        ctimer = new CountDownTimer(time, tick_length) {
            public void onTick(long millis_left) {
                //Integer ints_left = (int) (long) millis_left;
                ticks_passed ++;
                //test.setText(Integer.toString(internal_count));
                int newSteps = sp.getInt("steps", 0);
                steps = newSteps;
                if (counting) {
                    internal_count++;
                }

                new_ticks_passed++;
                int total = steps - step_base;
                int average = total/new_ticks_passed;

                /*
                test.setText("Ave: " + Integer.toString(average) + " Ticks: "

                        + Integer.toString(new_ticks_passed)
                        + " pace: " + current_desired_pace
                        + " upper: " + Integer.toString(upperBound)
                        + " lower: " + Integer.toString(lowerBound)
                );*/

                if (creatureTimes.contains(ticks_passed)) {
                    paceStart = ticks_passed;
                    paceNumber = 0;
                    creatureStart = ticks_passed;
                    internal_count = 0;
                    step_base = steps;
                    beginSprint();
                } else if (creatureSelected
                        && paceNumber < step_number
                        && internal_count == INTERNAL_COUNT
                        || runNow
                        ) {
                    runNow = false;

                    current_desired_pace = paces[paceNumber];
                    command_view.setText(current_desired_pace);
                    paceNumber++;
                    pace_start = ticks_passed; //pace_start keeps track of when the current pace started
                    //ending = true;
                    adjustPace();
                    internal_count = 0;
                    measureCount++;
                    measuring = true;
                    //ending = false;
                } else {
                    if (paceNumber == step_number) {
                        paceNumber = 0;
                        internal_count =0;
                        current_desired_pace = desired_default_speed;
                        command_view.setText(current_desired_pace);
                        creatureSelected = false;
                        counting = false;
                        adjustPace();
                        measuring = false;
                        measureCount = 0;
                        critterView.setImageResource(R.drawable.tree);

                    }
                }
            }

            public void onFinish() {
                try {
                    mediaPlayer.stop();
                } catch (Exception e) {}
                showRewards();
               // test.setText("done");

                for (Map.Entry<String, Integer> entry:  rewards.entrySet()) {
                    String rewardType = entry.getKey();
                    Integer quantity = entry.getValue();
                    myHandler.putRewards(db, quantity, rewardType);
                }
            }
        };
    ctimer.start();
    }


    private void showRewards(){
        String rewardString = "You earned: \n " + rewards.get("feathers") + " feathers"
                + "\n" + rewards.get("fangs") + " fangs"
                + "\n" + rewards.get("claws") + " claws"
                + "\n" + rewards.get("furs") + " furs";
        AlertDialog rewards;
        AlertDialog.Builder builder =
                new AlertDialog.Builder(context);
        builder.setTitle(R.string.rewards_label);
        builder.setMessage(rewardString);
        builder.setCancelable(true);
        builder.setPositiveButton(R.string.ok,
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialogInterface, int i) {
                        ((PlayActivity)getActivity()).finish();
                    }
                });
        try {
            rewards=builder.show();
        } catch (Exception e) {}
    }

    //use this upon end of a pace in a creature
    private void recordRewards() {
       // int newSteps = sp.getInt("steps", 0);
        if (withinBounds()
        //newSteps >= lowerBound
         ){
            String rewardType = creatureRewards;
            Object rewardCount = rewards.get(rewardType);
            int newCount = 1;
            if (rewardCount != null) {
                newCount += (int) rewardCount;
            }
            rewards.put(rewardType, newCount);
            /*
            SharedPreferences.Editor ed = sp.edit();
            ed.putInt("steps", 0);
            ed.commit();*/
        }
    }

    private boolean withinBounds() {
        int average = (steps - step_base)/paceLength;
        return (average <=  upperBound && average >= lowerBound);
    }

    private void setDesiredSpeed() {
        try {
            if (current_desired_pace.equals(run)) {
                ((PlayActivity)getActivity()).setSpeed(run);
                upperBound = 30;
                lowerBound = 12;
            } else if (current_desired_pace.equals(jog)) {
                ((PlayActivity)getActivity()).setSpeed(jog);
                upperBound = 30;
                lowerBound = 8;
            } else if (current_desired_pace.equals(walk)) {
                ((PlayActivity)getActivity()).setSpeed(walk);
                upperBound = 30;
                lowerBound = 0;
            }
        } catch (Exception e) {}
    }

    private void adjustPace() {
        if (internal_count == INTERNAL_COUNT) {

            int indicator;
            if (withinBounds()) {
                indicator = R.raw.success;
                command_view.setText(context.getResources().getString(R.string.success));
            } else {
                indicator = R.raw.failure;
                command_view.setText(context.getResources().getString(R.string.no_luck));
            }
            step_base = steps;
            counting = false;
            new_ticks_passed = 0;
            try {
                mediaPlayer.stop();
                mediaPlayer.reset();
                prepMedia(indicator, mediaPlayer);
                mediaPlayer.prepare();
                mediaPlayer.start();
            } catch (Exception e) {}
            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    counting = true;
                    adjustPaceHelper();
                    command_view.setText(current_desired_pace);
                }
            });
        } else {
            adjustPaceHelper();
        }

    }

    private void adjustPaceHelper() {
        recordRewards();
        setDesiredSpeed();

        try {
            mediaPlayer.stop();
            mediaPlayer.reset();
        } catch (Exception e) {}

        int backgroundNum = 0;

        if (current_desired_pace.equals(jog)) {
            backgroundNum = R.raw.background; //anthem. works.
        } else if (current_desired_pace.equals(run)) {
            backgroundNum = R.raw.background2;
        } else if (current_desired_pace.equals(walk)) {
            backgroundNum = R.raw.background3; //works
        }
        /*
        else if (current_desired_pace.equals(stand)) {
            backgroundNum = R.raw.background4; // works
        }*/
        try {
            prepMedia(backgroundNum, mediaPlayer);
            mediaPlayer.prepare();
            mediaPlayer.start();
        } catch (Exception e) {}
    }

    public String selectCritter() {
        Random generator = new Random();
        if (is_outside) {
            int n = outsideCreatures.size();
            int j = generator.nextInt(n);
            int i = outsideCreatures.get(j);
            return myHandler.getCreature(db, i);
        } else {
            int n = insideCreatures.size();
            int j = generator.nextInt(n);
            int i = insideCreatures.get(j);
            return myHandler.getCreature(db, i);
        }

    }

    private void insertCreature() {
        String creatureData = selectCritter();
        String[] values = creatureData.split(" ");
        creatureName = values[0];
        creatureSoundfile = values[1];
        creatureBehavior = values[2];
        creatureRewards = values[3];
        String[] paces = creatureBehavior.split("-");
    }

    private void beginSprint() {
        insertCreature();

        try {
            mediaPlayer.stop();
            Uri soundUri;
            mediaPlayer.reset();

            if (creatureName.equals(context.getResources().getString(R.string.wolf))) {
                int wolfNum = R.raw.wolf;
                critterView.setImageResource(R.drawable.wolf);
                prepMedia(wolfNum, mediaPlayer);
            } else if (creatureName.equals(context.getResources().getString(R.string.lion))) {
                int lionNum = R.raw.lion;
                critterView.setImageResource(R.drawable.lion);
                prepMedia(lionNum, mediaPlayer);
            } else if (creatureName.equals(context.getResources().getString(R.string.bird))) {
                int birdNum = R.raw.bird;
                critterView.setImageResource(R.drawable.bird);
                prepMedia(birdNum, mediaPlayer);
            } else if (creatureName.equals(context.getResources().getString(R.string.snake))) {
                int snakeNum = R.raw.snake;
                critterView.setImageResource(R.drawable.snake);
                prepMedia(snakeNum, mediaPlayer);
            }

            mediaPlayer.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                @Override
                public void onCompletion(MediaPlayer mp) {
                    counting = true;
                    command_view.setText(current_desired_pace);
                    runNow = true;
                    //ending = false;
                    try {
                        //adjustPaceHelper();
                    } catch (Exception e) {}
                }
            });

            mediaPlayer.prepare();
        } catch (Exception e) {}
        mediaPlayer.start();
        command_view.setText(creatureSoundfile);
        paces = creatureBehavior.split("-");
        current_desired_pace = paces[paceNumber];
        creatureSelected = true;
        setDesiredSpeed();
    }

    private void prepMedia(int i, MediaPlayer mp) throws IOException {
        Uri soundUri = Uri.parse("android.resource://madcourse.neu.edu.runpunymammal/" + i);
        mp.setDataSource(context, soundUri);
    }

    @Override
    public void onPause() {
        super.onPause();
        try {
            mediaPlayer.pause();
        } catch (Exception e) {}
    }

    @Override
    public void onResume() {
        super.onResume();
        try {
            mediaPlayer.start();
        } catch (Exception e) {}
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        try {
            db.endTransaction();
            mediaPlayer.stop();
            mediaPlayer.release();
        } catch (Exception e) {}
    }
}
