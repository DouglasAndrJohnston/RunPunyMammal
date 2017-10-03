package madcourse.neu.edu.runpunymammal;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;

import static android.content.Context.MODE_PRIVATE;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);

        final SharedPreferences sp = this.getActivity().getSharedPreferences("OURINFO", MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();


        View playButton = rootView.findViewById(R.id.play_button);
        playButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), LevelSelect.class);
                //ed.putBoolean("outside", true);
                //ed.commit();
                getActivity().startActivity(intent);
            }

/*
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.about_title);
                builder.setMessage(R.string.about_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //nothing
                            }
                        });
                mDialog = builder.show();
            }*/


        });

        /*
        View playButton_2 = rootView.findViewById(R.id.play_button_2);
        playButton_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                ed.putBoolean("outside", false);
                ed.commit();
                getActivity().startActivity(intent);
            }
        });*/

        View shopButton = rootView.findViewById(R.id.shop_button);
        shopButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), ShopActivity.class);
                getActivity().startActivity(intent);
            }

/*
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getActivity());
                builder.setTitle(R.string.about_title);
                builder.setMessage(R.string.about_text);
                builder.setCancelable(false);
                builder.setPositiveButton(R.string.ok_label,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //nothing
                            }
                        });
                mDialog = builder.show();
            }*/


        });

        View acknowledgmentsButton = (Button) rootView.findViewById(R.id.acknowledgments_button);
        acknowledgmentsButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                //shows acknowledgments
                AlertDialog acknowledgments;
                AlertDialog.Builder builder =
                        new AlertDialog.Builder(getContext());
                builder.setTitle(R.string.acknowledgments_label);
                builder.setMessage(R.string.acknowledgments_text);
                builder.setCancelable(true);
                builder.setPositiveButton(R.string.ok,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialogInterface, int i) {
                                //nothing
                            }
                        });
                acknowledgments=builder.show();
            }
        });

        return rootView;



    }
}
