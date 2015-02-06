package com.mebene.GPHud;

import android.app.Fragment;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Spinner;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;


public class Fm_control extends Fragment {

    private Spinner spinner;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.seccion1, container, false);

        List<GoproMode> items = new ArrayList<GoproMode>(3);
        items.add(new GoproMode(getString(R.string.app_name), R.drawable.ic_action_android));
        items.add(new GoproMode(getString(R.string.app_subname), R.drawable.ic_action_attachment));
        items.add(new GoproMode(getString(R.string.app_name), R.drawable.ic_action_bluetooth));

        spinner = (Spinner) getView().findViewById(R.id.spinner_gopro_mod);

        GoproModSpinnerAdapter adapter = new GoproModSpinnerAdapter(getActivity().getBaseContext(), items); // Create an ArrayAdapter using the string array and a default spinner layout
        spinner.setAdapter(adapter);    // Apply the adapter to the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener()
        {
            @Override
            public void onItemSelected(AdapterView<?> adapterView,View view, int position, long id) {
                Toast.makeText(adapterView.getContext(), ((GoproMode) adapterView.getItemAtPosition(position)).getNombre(), Toast.LENGTH_SHORT).show();
            }
            @Override
            public void onNothingSelected(AdapterView<?> adapterViewICS) {

            }

    });
        return rootView;
    }


        public class GoproMode
        {
            private String name;

            private int icon;

            public GoproMode(String nombre, int icono)
            {
                super();
                this.name = nombre;
                this.icon = icono;
            }

            public String getNombre()
            {
                return name;
            }

            public void setNombre(String nombre)
            {
                this.name = nombre;
            }

            public int getIcono()
            {
                return icon;
            }

            public void setIcono(int icono)
            {
                this.icon = icono;
            }
        }



}
