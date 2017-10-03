package madcourse.neu.edu.runpunymammal;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by douglas on 3/22/17.
 */

public class ShopActivityFragment extends Fragment {
    Context context;// = getActivity();
    static List<String> purchase_labels = new ArrayList<>();
    static List<String> wares = new ArrayList<>();
    static List<String> purchasedItems = new ArrayList<>();

    DBHandler myHandler;
    SQLiteDatabase db;
    TextView feathers;
    TextView fangs;
    TextView claws;
    TextView furs;
    int featherCount;
    int fangCount;
    int clawCount;
    int furCount;

    List<String> trophyNames = new ArrayList<>();

    ArrayAdapter<String> available;
    ArrayAdapter<String> purchased;


    public ShopActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.shop_fragment, container, false);

        context = getActivity();

        feathers = (TextView) rootView.findViewById(R.id.shop_feathers);
        fangs = (TextView) rootView.findViewById(R.id.shop_fangs);
        furs = (TextView) rootView.findViewById(R.id.shop_fur);
        claws = (TextView) rootView.findViewById(R.id.shop_claws);
        myHandler = new DBHandler(this.context);
        db = myHandler.getReadableDatabase();
        myHandler.onCreate(db);

        setItems();

        purchase_labels.add("test_label1");
        purchase_labels.add("test_label2");


        available = new ArrayAdapter<String>(
                context, android.R.layout.simple_list_item_1, wares);
        final ListView listView = (ListView) rootView.findViewById(R.id.available_list);
        listView.setAdapter(available);
        listView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override

            public void onItemClick(AdapterView adapterView, View view, final int position, long id) {
                AlertDialog purchase;
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle(trophyNames.get(position));
                builder.setMessage(context.getResources().getString(R.string.purchase_offer));
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.accept,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public synchronized void onClick(DialogInterface dialogInterface, int i) {
                                purchaseItem(position);
                            }
                        });
                builder.setNegativeButton(R.string.decline, new DialogInterface.OnClickListener(){
                    @Override
                    public synchronized  void onClick(DialogInterface dialogInterface, int i) {}
                });
                purchase=builder.show();
            }
        });

        purchased = new ArrayAdapter<String>(
                context, android.R.layout.simple_list_item_1, purchasedItems);
        final ListView listViewPurchased = (ListView) rootView.findViewById(R.id.purchased_list);
        listViewPurchased.setAdapter(purchased);

        if (purchasedItems.size() ==0) {
            purchasedItems.add("you don't have any trophies yet");
            purchased.notifyDataSetChanged();
        }

        return rootView;
    }

    private void setItems() {
        featherCount = myHandler.getReward(db, "feathers");
        fangCount = myHandler.getReward(db, "fangs");
        clawCount = myHandler.getReward(db, "claws");
        furCount = myHandler.getReward(db, "furs");
        String featherText = "Feathers: " + Integer.toString(featherCount);
        String fangText = "Fangs: " + Integer.toString(fangCount);
        String clawText = "Claws: " + Integer.toString(clawCount);
        String furText = "Furs: " + Integer.toString(furCount);
        feathers.setText(featherText);
        fangs.setText(fangText);
        claws.setText(clawText);
        furs.setText(furText);

        List<String> bought = myHandler.getPurchasedTrophies(db);
        purchasedItems.clear();
        for(String purchase : bought) {
            String[] items = purchase.split(" ");
            purchasedItems.add(items[0]);
        }

        List<String> availableTrophies = myHandler.getAvailableTrophies(db);

        wares.clear();
        for (String trophy : availableTrophies) {
            String[] data = trophy.split(" ");
            String format = data[0] + " : Costs " + data[1] + " feathers, " + data[2] + " fangs, "
                    + data[3] + " claws, " + data[4] + " furs";
            wares.add(format);
            trophyNames.add(data[0]);
        }



    }

    public void purchaseItem(int position) {
/*
        myHandler.putRewards(db, 10, "feathers");
        myHandler.putRewards(db, 10, "fangs");
        myHandler.putRewards(db, 10, "claws");
        myHandler.putRewards(db, 10, "furs");
        setItems();
        available.notifyDataSetChanged();
        purchased.notifyDataSetChanged();
*/

        String entry = wares.get(position);
        String[] values = entry.split(" ");
        int featherDemand = Integer.parseInt(values[3]);
        int fangDemand = Integer.parseInt(values[5]);
        int clawDemand = Integer.parseInt(values[7]);
        int furDemand = Integer.parseInt(values[9]);

        if (featherDemand <= featherCount && fangDemand <= fangCount
                && clawDemand <= clawCount && furDemand <= furCount) {

            myHandler.setTrophyPurchased(db, values[0]);
            setItems();
            available.notifyDataSetChanged();
            purchased.notifyDataSetChanged();
        } else {
            AlertDialog sorry;
            AlertDialog.Builder builder =
                    new AlertDialog.Builder(getActivity());
            builder.setTitle(trophyNames.get(position));
            builder.setMessage(context.getResources().getString(R.string.not_enough_resources));
            builder.setCancelable(false);
            builder.setPositiveButton(R.string.accept,
                    new DialogInterface.OnClickListener() {
                        @Override
                        public synchronized void onClick(DialogInterface dialogInterface, int i) {
                        }
                    });

            sorry=builder.show();
        }

    }

    public void onDestroy() {
        super.onDestroy();
        wares.clear();
        try {
            db.endTransaction();
        } catch (Exception e) {}
    }
}
