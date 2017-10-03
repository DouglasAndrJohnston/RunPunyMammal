package madcourse.neu.edu.runpunymammal;

import android.app.Fragment;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import static android.content.Context.MODE_PRIVATE;

/**
 * Created by douglas on 4/27/17.
 */

public class LevelSelectFragment extends Fragment {
    public LevelSelectFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.level_fragment, container, false);

        final SharedPreferences sp = this.getActivity().getSharedPreferences("OURINFO", MODE_PRIVATE);
        final SharedPreferences.Editor ed = sp.edit();


        View playPlains = rootView.findViewById(R.id.plains_button);
        playPlains.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                ed.putBoolean("outside", true);
                ed.commit();
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

        View playForrest = rootView.findViewById(R.id.forrest_button);
        playForrest.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(getActivity(), PlayActivity.class);
                ed.putBoolean("outside", false);
                ed.commit();
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

        return rootView;
    }
}
