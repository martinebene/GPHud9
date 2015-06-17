package com.mebene.GPHud;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.MediaController;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.VideoView;

import android.net.wifi.WifiInfo;
import android.net.wifi.WifiManager;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.List;

import apigopro.core.GoProHelper;
import apigopro.core.GoProStatus;
import apigopro.core.model.CamFields;


public class Fm_control extends Fragment {

    private static final long TIEMPO_RECONEXION = 5000;
    private static final long INTERVALO_ACTUALIZACION_UI = 2000;
    private Spinner spinner;
    public GoProHelper gp_helper;
    TextView tV_status_conexion, tV_status_bateria, tV_status_signal;
    EditText tf_output_console;
    VideoView videoView;
    private AsyncUpdateGUI asyncUpdateGUI;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fm_control_layout, container, false);


        return rootView;
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gp_helper = new GoProHelper("10.5.5.9", 80, "martin123456");
        Log.i("tag", "cree helper");
        asyncUpdateGUI = new AsyncUpdateGUI();

    }

    @Override
    public void onResume () {
        super.onResume();
        setGui();
        Log.i("tag", "On Resume con hilo muerto");
        startUpConexion();
        if(!asyncUpdateGUI.isRunning()) {
            asyncUpdateGUI.execute();
        }
        else{
            Log.i("tag", "On Resume con hilo vivo");
        }
        playVideo();
    }

    @Override
    public void onDestroyView () {
        super.onDestroyView();
        Log.i("tag", "onDestroyView");
        asyncUpdateGUI.cancel(true);
    }

    @Override
    public void onDestroy () {
        super.onDestroy();
        Log.i("tag", "onDestroy");
        asyncUpdateGUI.cancel(true);
    }

    @Override
    public void onDetach () {
        super.onDetach();
        Log.i("tag", "onDetach");
        asyncUpdateGUI.cancel(true);
    }


    private void setGui() {
        List<GoproMode> items = new ArrayList<GoproMode>(4);
        items.add(new GoproMode(getString(R.string.gpMode_video), R.drawable.ic_action_video));
        items.add(new GoproMode(getString(R.string.gpMode_foto), R.drawable.ic_action_camera));
        items.add(new GoproMode(getString(R.string.gpMode_rafaga), R.drawable.ic_foto_rafaga));
        items.add(new GoproMode(getString(R.string.gpMode_fotoTemp), R.drawable.ic_foto_temp));

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


        videoView = (VideoView)getView().findViewById(R.id.videoView);
        tV_status_conexion = (TextView)getView().findViewById(R.id.tV_status_conexion);
        tV_status_bateria = (TextView)getView().findViewById(R.id.tV_status_bateria);
        tV_status_signal = (TextView)getView().findViewById(R.id.tV_status_signal);
        tf_output_console = (EditText)getView().findViewById(R.id.tf_output_console);
    }


    private void startUpConexion() {
        Log.i("tag", "Entre a conexion");
        String SsId = null;

        WifiManager wifiMan = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        //if (!wifiMan.isWifiEnabled())
            //aca cambiar el texto del dialogo si no poner otro texto

        try {
            SsId = gp_helper.getBackPackInfo().getSSID();
            Log.i("tag", "desopues del getback con SSID:" + SsId);
        } catch (Exception e) {
            e.printStackTrace();
        }
            if (SsId == null){
                Log.i("tag", "entro por ssid null");
                tV_status_conexion.setText("no conectado");//R.string.status_sinconexion);
                AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
                dialog.setTitle("Sin Conexion");
                dialog.setMessage("El dispositivo movil no se encuentra conectado a una camara GoPro compatible");
                dialog.setCancelable(false);
                dialog.setPositiveButton("Buscar Camara", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                        startActivity(i);
                        //dialog.
                       /* try {
                            Thread.sleep(TIEMPO_RECONEXION);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        startUpConexion();*/
                    }
                });
                dialog.setNegativeButton("Continuar sin conexion", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.cancel();
                    }
                });
                dialog.show();
            }
            else tV_status_conexion.setText(SsId);

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

    void agregarTextoAConsola(String t){
        tf_output_console.setText(tf_output_console.getText() + t);
    }

    void agregarLineaAConsola(String t) {
        tf_output_console.setText(tf_output_console.getText() +"\n"+ t);
    }

    void limpiarConsola(){
        tf_output_console.setText("");
    }

    private void playVideo() {
        Log.i("tag", "en play video");
        try {
            MediaController mediaController = new MediaController(getActivity());
            mediaController.setAnchorView(videoView);
            Uri video = Uri.parse("http://10.5.5.9:8080/live/amba.m3u8");
            videoView.setMediaController(mediaController);
            videoView.setVideoURI(video);
            videoView.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                public void onCompletion(MediaPlayer mp) {
                    mp.stop();
                    playVideo();
                }
            });
            videoView.setOnErrorListener(new MediaPlayer.OnErrorListener() {
                public boolean onError(MediaPlayer mp, int what, int extra) {
                    return true;
                }
            });
            videoView.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
                public void onPrepared(MediaPlayer mp) {
                    videoView.start();
                }
            });
            Log.i("tag", "saliendo de play video");
        } catch (Exception e) {
            System.out.println("Video Play Error :" + e.toString());
        }

    }

    private class AsyncUpdateGUI extends AsyncTask <Object, GoProStatus, Object>{

        public boolean running=false;
        @Override
        protected void onPreExecute() {

        }

        @Override
        protected Object doInBackground(Object... params) {
            String msg;
            running = true;
            //GoProHelper local_gp_helper = new GoProHelper("10.5.5.9", 80, "martin123456");
            GoProHelper local_gp_helper = new GoProHelper();
            GoProStatus gPStatus= new GoProStatus();

            Log.i("Async", "en el hilo antes del while");
            while (running){
                try {
                    Thread.sleep(INTERVALO_ACTUALIZACION_UI);

                    gPStatus.setBacPacStatus(local_gp_helper.getBacpacStatus());
                    gPStatus.setBacPacInfo(local_gp_helper.getBackPackInfo());
                    gPStatus.setCamFields(local_gp_helper.getCameraSettings());
                    gPStatus.setPassword(local_gp_helper.getPassword());

                    publishProgress(gPStatus);
                    if (isCancelled()){
                        running=false;
                    }

                } catch (Exception e) {
                    e.printStackTrace();
                    running=false;
                }
            }

            return null;
        }


        @Override
        protected void onProgressUpdate (GoProStatus... gPStatus) {

            Log.i("onProgressUpdate", "publishing");

            tV_status_conexion.setText(gPStatus[0].getBacPacInfo().getSSID());
            tV_status_bateria.setText(Integer.toString(gPStatus[0].getCamFields().getBattery()));
            tV_status_signal.setText(gPStatus[0].getBacPacInfo().getSignal());

            limpiarConsola();

            agregarTextoAConsola(
                    "CamFields:\n" +
                            "Cname: " + gPStatus[0].getCamFields().getCamname() + "\n" +
                            "Version: " + gPStatus[0].getCamFields().getVersion() + "\n" +
                            "Batery: " + gPStatus[0].getCamFields().getBattery() + "\n" +
                            "BeepSound: " + gPStatus[0].getCamFields().getBeepSound() + "\n" +
                            "BurstMode: " + gPStatus[0].getCamFields().getBurstMode() + "\n" +
                            "Continuous shot: " + gPStatus[0].getCamFields().getContinuousShot() + "\n" +
                            "Frames per second: " + gPStatus[0].getCamFields().getFramesPerSecond() + "\n" +
                            "Locate: " + gPStatus[0].getCamFields().getLocate() + "\n" +
                            "Mode: " + gPStatus[0].getCamFields().getMode() + "\n" +
                            "Model: " + gPStatus[0].getCamFields().getModel() + "\n" +
                            "Photo Resolution: " + gPStatus[0].getCamFields().getPhotoResolution() + "\n" +
                            "UpDown: " + gPStatus[0].getCamFields().getUpdown() + "\n" +
                            "Video On Card: " + gPStatus[0].getCamFields().getVideoOncard() + "\n" +
                            "Photos On Card: " + gPStatus[0].getCamFields().getPhotosOncard() + "\n"
            );




        }

        @Override
        protected void onPostExecute(Object o) {
            Log.i("Async", "hay post execute del async");
            running=false;
        }

        public boolean isRunning(){
            return running;
        }


    }


}
