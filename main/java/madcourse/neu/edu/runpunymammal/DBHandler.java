package madcourse.neu.edu.runpunymammal;


import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.io.BufferedReader;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.ArrayList;
import java.util.List;


public class DBHandler extends SQLiteOpenHelper {
    private static final String CREATURE_TABLE = "dictionary";
    private static final String REWARDS_TABLE = "rewards";
    private static final String id = "_ID";
    private static final String REWARD_QUANTITY = "reward_data";
    private static final String REWARD_TYPE = "reward_type";

    private static final String TROPHY_TABLE = "trophies";
    //private static final String TROPHY_QUANTITY = "trophy_data";
    private static final String TROPHY_TYPE = "trophy_type";
    private static final String TROPHY_PURCHASED = "trophy_purchased";
    private static final String TROPHY_DATA = "trophy_data";

    private static final String CREATURE_DATA = "lotsOfLetters";
    private static final String DB_NAME = "MainDB.db";


    private static final String CREATE_TROPHIES = "CREATE TABLE IF NOT EXISTS " +
            TROPHY_TABLE + " (" +
            id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            TROPHY_TYPE + " TEXT, " +
            TROPHY_PURCHASED + " TEXT, " +
            TROPHY_DATA + " TEXT)";


    private static final String CREATE_REWARDS = "CREATE TABLE IF NOT EXISTS " +
            REWARDS_TABLE + " (" +
            id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            REWARD_TYPE + " TEXT, " +
            REWARD_QUANTITY + " INTEGER)";

    private static final String CREATE_CREATURES = "CREATE TABLE IF NOT EXISTS " +
            CREATURE_TABLE + " (" +
            id + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
            CREATURE_DATA + " TEXT)";
    private Context context;
    private Boolean table_finished = false;

    public DBHandler(Context applicationcontext) {
        super(applicationcontext, DB_NAME, null, 1);
        this.context = applicationcontext;
    }

    @Override
    public void onCreate(SQLiteDatabase database) {
        database.execSQL("DROP TABLE IF EXISTS " + CREATURE_TABLE);
        //database.execSQL("DROP TABLE IF EXISTS " + TROPHY_TABLE);
        //database.execSQL("DROP TABLE IF EXISTS " + REWARDS_TABLE);
        buildBigWords(database);
        buildRewards(database);
        buildTrophies(database);
    }

    private void buildBigWords(SQLiteDatabase database) {
        if (tableExists(database, CREATURE_TABLE)) {
            Log.d(null, "database already exists");
        } else {
            Log.d(null, "about to create table");
            database.execSQL(CREATE_CREATURES);
            Log.d(null, CREATE_CREATURES);

            InputStream inputStream = context.getResources().openRawResource(R.raw.creaturelist);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader newbuffer = new BufferedReader(inputreader);
            String line;
            ContentValues values = new ContentValues();
            try {
                while ((line = newbuffer.readLine()) != null) {
                    values.put(CREATURE_DATA, line);
                    database.insert(CREATURE_TABLE, null, values);
                    values.clear();
                    Log.d(null, "just inserted: " + line);
                }
                this.table_finished = true;

            } catch (Exception e) {
                Log.d(null, "error in inserting values into database");
            }
            if (newbuffer != null) {
                try {
                    newbuffer.close();
                } catch (Exception e) {
                    Log.d(null, "could not close buffer");
                }
            }
        }
    }

    private void buildTrophies(SQLiteDatabase database) {
        if (tableExists(database, TROPHY_TABLE)) {
        } else {
            database.execSQL(CREATE_TROPHIES);
            InputStream inputStream = context.getResources().openRawResource(R.raw.trophylist);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader newbuffer = new BufferedReader(inputreader);
            String line;
            ContentValues values = new ContentValues();
            try {
                while ((line = newbuffer.readLine()) != null) {
                    values.put(TROPHY_DATA, line);
                    values.put(TROPHY_PURCHASED, "false");
                    String[] data = line.split(" ");
                    values.put(TROPHY_TYPE, data[0]);
                    database.insert(TROPHY_TABLE, null, values);
                    values.clear();
                    Log.d(null, "just inserted: " + line);
                }
                this.table_finished = true;
            } catch (Exception e) {
                Log.d(null, "error in inserting values into database");
            }
            if (newbuffer != null) {
                try {
                    newbuffer.close();
                } catch (Exception e) {
                    Log.d(null, "could not close buffer");
                }
            }
        }
    }

    private void buildRewards(SQLiteDatabase database) {
        if (tableExists(database, REWARDS_TABLE)) {
        } else {
            database.execSQL(CREATE_REWARDS);
            InputStream inputStream = context.getResources().openRawResource(R.raw.rewardslist);
            InputStreamReader inputreader = new InputStreamReader(inputStream);
            BufferedReader newbuffer = new BufferedReader(inputreader);
            String line;
            ContentValues values = new ContentValues();
            try {
                while ((line = newbuffer.readLine()) != null) {
                    values.put(REWARD_TYPE, line);
                    values.put(REWARD_QUANTITY, 0);
                    database.insert(REWARDS_TABLE, null, values);
                    values.clear();
                    Log.d(null, "just inserted: " + line);
                }
                this.table_finished = true;
            } catch (Exception e) {
                Log.d(null, "error in inserting values into database");
            }
            if (newbuffer != null) {
                try {
                    newbuffer.close();
                } catch (Exception e) {
                    Log.d(null, "could not close buffer");
                }
            }
        }
    }


    private boolean tableExists(SQLiteDatabase database, String table_name) {
        try {
            Cursor cursor = database.rawQuery("select * from " + table_name + " where " + id + " = 0;", null);
            return true;
        } catch (Exception e) {
            return false;
        }
    }


    @Override
    public void onUpgrade(SQLiteDatabase database, int version_old,
                          int current_version) {
        database.execSQL("DROP TABLE IF EXISTS " + TROPHY_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + REWARDS_TABLE);
        database.execSQL("DROP TABLE IF EXISTS " + CREATURE_TABLE);
        onCreate(database);
    }


    public String getCreature(SQLiteDatabase database, int num) {
        String[] projection = {CREATURE_DATA};
        String selection = id + " = ?";
        String[] selectionArgs = {Integer.toString(num)};
        Cursor cursor = database.query(CREATURE_TABLE, projection, selection, selectionArgs,
                null, null, null);
        cursor.moveToFirst();
        String result = cursor.getString(0);
        cursor.close();
        return result;
    }


    public List<String> getPurchasedTrophies (SQLiteDatabase database) {
        List<String> results = new ArrayList<>();
        String[] projection = {TROPHY_DATA};
        String selection = TROPHY_PURCHASED + " = ?";
        String[] selectionArgs = {"true"};
        Cursor cursor = database.query(TROPHY_TABLE, projection, selection, selectionArgs,
                null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
                results.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return results;
    }

    public List<String> getAvailableTrophies (SQLiteDatabase database) {
        List<String> results = new ArrayList<>();
        String[] projection = {TROPHY_DATA};
        String selection = TROPHY_PURCHASED + " = ?";
        String[] selectionArgs = {"false"};
        Cursor cursor = database.query(TROPHY_TABLE, projection, selection, selectionArgs,
                null, null, null);
        if (cursor.moveToFirst()) {
            while (!cursor.isAfterLast()) {
               results.add(cursor.getString(0));
                cursor.moveToNext();
            }
        }
        cursor.close();
        return results;
    }

    public int getReward (SQLiteDatabase database, String rewardType) {
        int result = 0;
        String[] projection = {REWARD_QUANTITY};
        String selection = REWARD_TYPE + " = ?";
        String[] selectionArgs = {rewardType};
        Cursor cursor = database.query(REWARDS_TABLE, projection, selection, selectionArgs,
                null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() >0){
            result = cursor.getInt(0);
        }
        cursor.close();
        return result;
    }

    public void putRewards (SQLiteDatabase database, int quantity, String rewardType) {
        int count = getReward(database, rewardType);
        int newCount = count + quantity;
        ContentValues vals = new ContentValues();
        vals.put(REWARD_TYPE, rewardType);
        vals.put(REWARD_QUANTITY, newCount);
        database.update(REWARDS_TABLE, vals, REWARD_TYPE + "= '" + rewardType + "'", null);
    }

    public void setTrophyPurchased(SQLiteDatabase database, String name) {
        String[] projection = {TROPHY_DATA};
        String selection = TROPHY_TYPE + " = ?";
        String[] selectionArgs = {name};
        Cursor cursor = database.query(TROPHY_TABLE, projection, selection, selectionArgs,
                null, null, null);
        cursor.moveToFirst();
        if (cursor.getCount() >0){
            String data = cursor.getString(0);
            String[] dataParts = data.split(" ");
            int feathercost = 0 -  Integer.parseInt(dataParts[1]);
            int fangcost = 0 - Integer.parseInt(dataParts[2]);
            int clawcost = 0 - Integer.parseInt(dataParts[3]);
            int furcost = 0 - Integer.parseInt(dataParts[4]);

            putRewards(database, feathercost, "feathers");
            putRewards(database, fangcost, "fangs");
            putRewards(database, clawcost, "claws");
            putRewards(database, furcost, "furs");

            ContentValues vals = new ContentValues();
            vals.put(TROPHY_TYPE, name);
            vals.put(TROPHY_PURCHASED, "true");
            vals.put(TROPHY_DATA, data);
            database.update(TROPHY_TABLE, vals, TROPHY_TYPE + "= '" + name + "'", null);
        }
        cursor.close();
    }
}
