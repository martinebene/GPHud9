package com.mebene.GPHud;

import android.app.AlertDialog;
import android.app.Fragment;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.net.Uri;
import android.net.wifi.WifiManager;
import android.os.AsyncTask;
import android.os.Bundle;
import android.provider.Settings;
import android.text.Layout;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
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

import apigopro.core.CameraMode;
import apigopro.core.GoProHelper;
import apigopro.core.GoProStatus;
import apigopro.core.model.CamFields;


public class Fm_control extends Fragment implements SensorEventListener {

    private static final long TIEMPO_RECONEXION = 5000;
    private static final long INTERVALO_ACTUALIZACION_UI = 2000;
    private Spinner spinner;
    public GoProHelper gp_helper;
    GoProStatus gPStatus;
    TextView tV_status_conexion, tV_status_bateria, tV_sin_conexion,tV_videos_en_camara,tV_fotos_en_camara;
    LinearLayout LOsb_gps, LOsb_videosEnCamara, LOsb_fotosEnCamara, LOsd_bateriaCamara;


    EditText tf_output_console;
    VideoView videoView;
    ImageButton ib_rec,ib_stop,ib_OnOff;

    private AsyncUpdateGUI asyncUpdateGUI;
    boolean conectado, usarSinConexion, camOn, adquiriendo;
    String SsId;

    int cameraModeGui;

    //provisorios
    public int i=0;


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fm_control_layout, container, false);


        return rootView;
    }


    //**********************************************************************************************************************//
        @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        gp_helper = new GoProHelper("10.5.5.9", 80, "martin123456");
        conectado=false;
        camOn=false;
        adquiriendo = false;
        SsId = null;
        Log.i("tag", "cree helper");
        asyncUpdateGUI = new AsyncUpdateGUI();
        gPStatus= new GoProStatus();
        cameraModeGui=0;



            SensorManager sensorManager = (SensorManager)getActivity().getSystemService(Context.SENSOR_SERVICE);
            List<Sensor> listaSensores = sensorManager.
                    getSensorList(Sensor.TYPE_ALL);
            for(Sensor sensor: listaSensores) {
                Log.i("tag", sensor.getName());
            }







    }

    //**********************************************************************************************************************//
    @Override
    public void onResume () {
        super.onResume();
        setGui();
        Log.i("tag", "On Resume con hilo muerto");

        try {
            if(conectado = startUpConexion()){
                Log.i("tag2", "On Resume conectado");
                if(camOn = gp_helper.isOn()){
                    Log.i("tag2", "On Resume conectada y prendida");
                    setGuiConectado(SsId);}
                else{
                    Log.i("tag2", "On Resume conectada y apagada");
                    setGuiApagado(SsId);}
            }else{
                Log.i("tag2", "On Resume desconectada");
                setGuiDesconectado();}
        } catch (Exception e) {
            Log.i("tag2", "On Resume cayo en excepcion");
            e.printStackTrace();
        }

    }


    //**********************************************************************************************************************//
    @Override
    public void onDestroyView () {
        super.onDestroyView();
        Log.i("tag", "onDestroyView");
        asyncUpdateGUI.cancel(true);
    }

    //**********************************************************************************************************************//
    @Override
    public void onDestroy () {
        super.onDestroy();
        Log.i("tag", "onDestroy");
        asyncUpdateGUI.cancel(true);
    }

    //**********************************************************************************************************************//
    @Override
    public void onDetach () {
        super.onDetach();
        Log.i("tag", "onDetach");
        asyncUpdateGUI.cancel(true);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setGui() {

        videoView = (VideoView)getView().findViewById(R.id.videoView);
        tV_status_conexion = (TextView)getView().findViewById(R.id.tV_status_conexion);
        tV_status_bateria = (TextView)getView().findViewById(R.id.tV_status_bateria);
        tV_sin_conexion = (TextView)getView().findViewById(R.id.tV_SinConexion);
        tV_videos_en_camara = (TextView)getView().findViewById(R.id.tV_videos_en_camara);
        tV_fotos_en_camara = (TextView)getView().findViewById(R.id.tV_fotos_en_camara);

        tf_output_console = (EditText)getView().findViewById(R.id.tf_output_console);

        LOsb_gps = (LinearLayout)getView().findViewById(R.id.LOsb_gps);
        LOsb_videosEnCamara = (LinearLayout)getView().findViewById(R.id.LOsb_videosEnCamara);
        LOsb_fotosEnCamara = (LinearLayout)getView().findViewById(R.id.LOsb_fotosEnCamara);
        LOsd_bateriaCamara = (LinearLayout)getView().findViewById(R.id.LOsd_bateriaCamara);

        ib_rec = (ImageButton) getView().findViewById(R.id.ib_rec);
        ib_stop = (ImageButton) getView().findViewById(R.id.ib_stop);
        ib_OnOff = (ImageButton) getView().findViewById(R.id.ib_OnOff);

        spinner = (Spinner) getView().findViewById(R.id.spinner_gopro_mod);


        tV_sin_conexion.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                startUpConexion();
            }
        });

        ib_rec.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                if (cameraModeGui == CameraMode.CAM_MODE_VIDEO) {
                    try {
                        gp_helper.startRecord();
                        adquiriendo = true;
                        ib_rec.setEnabled(false);
                        ib_rec.setVisibility(View.GONE);
                        ib_stop.setEnabled(true);
                        ib_stop.setVisibility(View.VISIBLE);
                        spinner.setEnabled(false);
                        spinner.setVisibility(View.INVISIBLE);
                    } catch (Exception e) {
                        setGuiConectado(SsId);
                        e.printStackTrace();
                        adquiriendo = false;
                    }
                } else {
                    try {
                        gp_helper.startRecord();
                    } catch (Exception e) {
                        setGuiConectado(SsId);
                        e.printStackTrace();
                        adquiriendo = false;
                    }
                }
            }
        });

        ib_stop.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                try {
                    gp_helper.stopRecord();
                    adquiriendo = false;
                    ib_rec.setEnabled(true);
                    ib_rec.setVisibility(View.VISIBLE);
                    ib_stop.setEnabled(false);
                    ib_stop.setVisibility(View.GONE);
                    spinner.setEnabled(true);
                    spinner.setVisibility(View.VISIBLE);
                } catch (Exception e) {
                    setGuiConectado(SsId);
                    adquiriendo = false;
                    e.printStackTrace();
                }
            }
        });

        ib_OnOff.setOnClickListener(new View.OnClickListener() {
            public void onClick(View v) {
                //si enciendo buscar como volver al onResume para lanzar async y guiconectado
                try {
                    if ((!gp_helper.isOn())) {
                        Log.i("tag", "OnOff button con cam apagada");
                        gp_helper.turnOnCamera();
                        setGuiConectado(SsId);
                    } else {
                        Log.i("tag", "OnOff button con cam encendida");
                        asyncUpdateGUI.cancel(true);
                        gp_helper.turnOffCamera();
                        adquiriendo = false;
                        setGuiApagado(SsId);
                    }
                } catch (Exception e) {
                    setGuiDesconectado();
                    e.printStackTrace();
                }
            }
        });


    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void setGuiDesconectado() {
        Log.i("tag", "Entre a setGuiDesconectado");
        Toast.makeText(getView().getContext(), "Sin conexion", Toast.LENGTH_SHORT).show();
        tV_sin_conexion.setText("Buscar Conexion");
        tV_sin_conexion.setEnabled(true);
        tV_sin_conexion.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        spinner.setEnabled(false);
        spinner.setVisibility(View.INVISIBLE);
        ib_rec.setEnabled(false);
        ib_rec.setVisibility(View.INVISIBLE);
        ib_stop.setEnabled(false);
        ib_stop.setVisibility(View.INVISIBLE);
        ib_OnOff.setEnabled(false);
        ib_OnOff.setVisibility(View.INVISIBLE);
        tV_status_conexion.setText("Sin conexion");
        LOsb_videosEnCamara.setVisibility(View.GONE);
        tV_videos_en_camara.setText("");
        LOsb_fotosEnCamara.setVisibility(View.GONE);
        tV_fotos_en_camara.setText("");
        LOsd_bateriaCamara.setVisibility(View.GONE);
        tV_status_bateria.setText("");
        limpiarConsola();
        stopVideo();
        adquiriendo=false;
    }


    //**********************************************************************************************************************//
    private void setGuiConectado(String localSsId) {
        Log.i("tag", "Entre a setGuiConectado");
        Toast.makeText(getView().getContext(), "conectada y encendida", Toast.LENGTH_SHORT).show();
        tV_sin_conexion.setText("");
        tV_sin_conexion.setEnabled(false);
        tV_sin_conexion.setVisibility(View.GONE);
        videoView.setVisibility(View.VISIBLE);
        spinner.setEnabled(true);
        spinner.setVisibility(View.VISIBLE);
        ib_rec.setEnabled(true);
        ib_rec.setVisibility(View.VISIBLE);
        ib_OnOff.setEnabled(true);
        ib_OnOff.setVisibility(View.VISIBLE);
        tV_status_conexion.setText(localSsId);
        LOsb_videosEnCamara.setVisibility(View.VISIBLE);
        LOsb_fotosEnCamara.setVisibility(View.VISIBLE);
        LOsd_bateriaCamara.setVisibility(View.VISIBLE);

        List<GoproMode> items = new ArrayList<GoproMode>(4);
        items.add(new GoproMode(getString(R.string.gpMode_video), R.drawable.ic_action_video, CameraMode.CAM_MODE_VIDEO));
        items.add(new GoproMode(getString(R.string.gpMode_foto), R.drawable.ic_action_camera, CameraMode.CAM_MODE_PHOTO));
        items.add(new GoproMode(getString(R.string.gpMode_rafaga), R.drawable.ic_foto_rafaga, CameraMode.CAM_MODE_BURST));
        items.add(new GoproMode(getString(R.string.gpMode_fotoTemp), R.drawable.ic_foto_temp, CameraMode.CAM_MODE_TIMELAPSE));

        GoproModSpinnerAdapter adapter = new GoproModSpinnerAdapter(getActivity().getBaseContext(), items); // Create an ArrayAdapter using the string array and a default spinner layout
        spinner.setAdapter(adapter);    // Apply the adapter to the spinner
        spinner.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> adapterView, View view, int position, long id) {
                GoproMode localGpMode;

                localGpMode = (GoproMode) adapterView.getItemAtPosition(position);
                Toast.makeText(adapterView.getContext(), (localGpMode.getNombre()), Toast.LENGTH_SHORT).show();
                switch (cameraModeGui = localGpMode.getCodeMode()) {
                    case (CameraMode.CAM_MODE_VIDEO):
                        try {
                            stopVideo();
                            gp_helper.modeCamera();
                            if(adquiriendo){
                                ib_rec.setEnabled(false);
                                ib_rec.setVisibility(View.GONE);
                                ib_stop.setEnabled(true);
                                ib_stop.setVisibility(View.VISIBLE);}
                            else {
                                ib_rec.setEnabled(true);
                                ib_rec.setVisibility(View.VISIBLE);
                                ib_stop.setEnabled(false);
                                ib_stop.setVisibility(View.GONE);}
                            //Thread.sleep(1000);
                            playVideo();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case (CameraMode.CAM_MODE_PHOTO):
                        try {
                            stopVideo();
                            gp_helper.modePhoto();
                            ib_stop.setEnabled(false);
                            ib_stop.setVisibility(View.INVISIBLE);
                            //Thread.sleep(1000);
                            playVideo();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case (CameraMode.CAM_MODE_BURST):
                        try {
                            stopVideo();
                            gp_helper.modeBurst();
                            ib_stop.setEnabled(false);
                            ib_stop.setVisibility(View.INVISIBLE);
                            //Thread.sleep(1000);
                            playVideo();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                    case (CameraMode.CAM_MODE_TIMELAPSE):
                        try {
                            stopVideo();
                            Thread.sleep(1000);
                            gp_helper.modeTimeLapse();
                            ib_stop.setEnabled(false);
                            ib_stop.setVisibility(View.INVISIBLE);
                            playVideo();
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                        break;
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> adapterViewICS) {
            }
        });
        actualizarGpStatus();
        playVideo();
        if(!asyncUpdateGUI.isRunning()) {
            asyncUpdateGUI = new AsyncUpdateGUI();
            asyncUpdateGUI.execute();
        }
        else{
            Log.i("tag", "ejecute SetGUI conectado con hilo vivo");
        }
    }


    //**********************************************************************************************************************//
    private void setGuiApagado(String localSsId) {
        Log.i("tag", "Entre a setGuiApagado");
        Toast.makeText(getView().getContext(), "conectada y apagada", Toast.LENGTH_SHORT).show();
        try {
            asyncUpdateGUI.cancel(true);
            Thread.sleep(200);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        tV_sin_conexion.setText("Camara Apagada");
        tV_sin_conexion.setEnabled(false);
        tV_sin_conexion.setVisibility(View.VISIBLE);
        videoView.setVisibility(View.GONE);
        spinner.setEnabled(false);
        spinner.setVisibility(View.INVISIBLE);
        ib_rec.setEnabled(false);
        ib_rec.setVisibility(View.INVISIBLE);
        ib_stop.setEnabled(false);
        ib_stop.setVisibility(View.INVISIBLE);
        ib_OnOff.setEnabled(true);
        tV_status_conexion.setText(localSsId);
        LOsb_videosEnCamara.setVisibility(View.GONE);
        tV_videos_en_camara.setText("");
        LOsb_fotosEnCamara.setVisibility(View.GONE);
        tV_fotos_en_camara.setText("");
        LOsd_bateriaCamara.setVisibility(View.GONE);
        tV_status_bateria.setText("");
        limpiarConsola();
        stopVideo();
        adquiriendo=false;
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private boolean startUpConexion() {
        i++;
        Log.i("tag", "Entre a conexion " + i + "veces");
        String TituloDialog="", textoDialog="", textoPositive="", textoNegative="";

        SsId = null;
        WifiManager wifiMan = (WifiManager) getActivity().getSystemService(Context.WIFI_SERVICE);

        if (wifiMan.isWifiEnabled()) {
            TituloDialog="Sin Conexion a Camara";
            textoDialog = "El dispositivo movil no se encuentra conectado a una camara GoPro compatible";
            textoPositive = "Buscar Camara WiFi";
            try {
                SsId = gp_helper.getBackPackInfo().getSSID();
                Log.i("tag", "desopues de StartUp Conexion con SSID:" + SsId);
            } catch (Exception e) {
                e.printStackTrace();
            }

        }else{
            TituloDialog="WiFi desactivado";
            textoDialog="No se encuentra activa la conexion WiFi";
            textoPositive = "Activar WiFi";
        }
        textoNegative="Continuar sin conexion";

        if (SsId == null){
            conectado=false;
            Log.i("tag", "entro por ssid null");
            tV_status_conexion.setText("Sin Conexion");//R.string.status_sinconexion);
            AlertDialog.Builder dialog = new AlertDialog.Builder(getActivity());
            dialog.setTitle(TituloDialog);
            dialog.setMessage(textoDialog);
            dialog.setCancelable(false);
            dialog.setPositiveButton(textoPositive, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    Intent i = new Intent(Settings.ACTION_WIFI_SETTINGS);
                    startActivity(i);
                }
            });
            dialog.setNegativeButton(textoNegative, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    setGuiDesconectado();
                    dialog.cancel();
                }
            });
            dialog.show();
            return false;
        }
        else{
            return true;
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    void agregarTextoAConsola(String t){
        tf_output_console.setText(tf_output_console.getText() + t);
    }


    //**********************************************************************************************************************//
    void agregarLineaAConsola(String t) {
        tf_output_console.setText(tf_output_console.getText() + "\n" + t);
    }


    //**********************************************************************************************************************//
    void limpiarConsola(){
        tf_output_console.setText("");
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void playVideo() {
        //Log.i("tag", "en play video");

        try {
            gp_helper.setCamLivePreview(true);
            videoView.setBackgroundResource(0);
            //MediaController mediaController = new MediaController(getActivity());
            //mediaController.setAnchorView(videoView);
            Uri video = Uri.parse("http://10.5.5.9:8080/live/amba.m3u8");
            //videoView.setMediaController(mediaController);
            videoView.setMediaController(null);
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
            //Log.i("tag", "saliendo de play video");
        } catch (Exception e) {
            System.out.println("Video Play Error :" + e.toString());
        }
    }


    //**********************************************************************************************************************//
    private void stopVideo() {
        videoView.stopPlayback();
        videoView.setMediaController(null);
        videoView.setBackgroundResource(R.drawable.viewer_background);
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    private void actualizarGpStatus() {
        try {
            gPStatus.setBacPacStatus(gp_helper.getBacpacStatus());
            gPStatus.setBacPacInfo(gp_helper.getBackPackInfo());
            gPStatus.setCamFields(gp_helper.getCameraSettings());
            gPStatus.setPassword(gp_helper.getPassword());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////

    @Override
    public void onSensorChanged(SensorEvent evento) {

        synchronized (this) {
            switch(evento.sensor.getType()) {
                case Sensor.TYPE_ORIENTATION:
                    for (int i=0 ; i<3 ; i++) {
                        Log.i("Sensor", "Orientación "+i+": "+evento.values[i]);
                    }
                    break;
                case Sensor.TYPE_ACCELEROMETER:
                    for (int i=0 ; i<3 ; i++) {
                        Log.i("Sensor","Acelerómetro "+i+": "+evento.values[i]);
                    }
                    break;
                case Sensor.TYPE_MAGNETIC_FIELD:
                    for (int i=0 ; i<3 ; i++) {
                        Log.i("Sensor","Magnetismo "+i+": "+evento.values[i]);
                    }
                    break;
                default:
                    for (int i=0 ; i<evento.values.length ; i++) {
                        Log.i("Sensor","Temperatura "+i+": "+evento.values[i]);
                    }
            }
        }



    }



    @Override
    public void onAccuracyChanged(Sensor sensor, int i) {
        return;
    }







    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
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
            GoProStatus local_gPStatus= new GoProStatus();

            Log.i("tag", "Async iniciando");
            while (running){
                try {
                    Thread.sleep(INTERVALO_ACTUALIZACION_UI);

                    local_gPStatus.setBacPacStatus(local_gp_helper.getBacpacStatus());
                    local_gPStatus.setBacPacInfo(local_gp_helper.getBackPackInfo());
                    local_gPStatus.setCamFields(local_gp_helper.getCameraSettings());
                    local_gPStatus.setCameraSettingsExtended(local_gp_helper.getCameraSettingsExtended());
                    local_gPStatus.setCameraInfo(local_gp_helper.getCameraInfo());
                    local_gPStatus.setPassword(local_gp_helper.getPassword());
                    local_gPStatus.setCname(local_gp_helper.getCameraNameCN());

                    if(local_gPStatus.isFieldComplete()){
                        publishProgress(local_gPStatus);
                    } else {
                        running = false;
                    }

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
        protected void onProgressUpdate (GoProStatus... local_gPStatus) {

            Log.i("tag", "onProgressUpdate: publishing");

            gPStatus = local_gPStatus[0];

            tV_status_conexion.setText(local_gPStatus[0].getBacPacInfo().getSSID());
            tV_status_bateria.setText(Integer.toString(local_gPStatus[0].getCamFields().getBattery()));
            tV_videos_en_camara.setText(Long.toString(local_gPStatus[0].getCamFields().getVideoOncard()));
            tV_fotos_en_camara.setText(Long.toString(local_gPStatus[0].getCamFields().getPhotosOncard()));

            limpiarConsola();

            /*
            agregarTextoAConsola(
                    "CamFields:\n" +
                            "Cname: " + local_gPStatus[0].getCamFields().getCamname() + "\n" +
                            "Version: " + local_gPStatus[0].getCamFields().getVersion() + "\n" +
                            "Batery: " + local_gPStatus[0].getCamFields().getBattery() + "\n" +
                            "BeepSound: " + local_gPStatus[0].getCamFields().getBeepSound() + "\n" +
                            "BurstMode: " + local_gPStatus[0].getCamFields().getBurstMode() + "\n" +
                            "Continuous shot: " + local_gPStatus[0].getCamFields().getContinuousShot() + "\n" +
                            "Frames per second: " + local_gPStatus[0].getCamFields().getFramesPerSecond() + "\n" +
                            "Locate: " + local_gPStatus[0].getCamFields().getLocate() + "\n" +
                            "Mode: " + local_gPStatus[0].getCamFields().getMode() + "\n" +
                            "Model: " + local_gPStatus[0].getCamFields().getModel() + "\n" +
                            "Photo Resolution: " + local_gPStatus[0].getCamFields().getPhotoResolution() + "\n" +
                            "UpDown: " + local_gPStatus[0].getCamFields().getUpdown() + "\n" +
                            "Video On Card: " + local_gPStatus[0].getCamFields().getVideoOncard() + "\n" +
                            "Photos On Card: " + local_gPStatus[0].getCamFields().getPhotosOncard() + "\n"
            );
*/
          //  agregarTextoAConsola("\n" + local_gPStatus[0].getCamFields().toString());
            //agregarTextoAConsola("\n" + local_gPStatus[0].getBacPacInfo().toString());
            //agregarTextoAConsola("\n" + );
            agregarTextoAConsola(local_gPStatus[0].toString());
            Log.i("tag1", "Info que traigo: \n" + local_gPStatus[0].toString());

        }

        @Override
        protected void onPostExecute(Object o) {
            Log.i("tag", "hay post execute del async");
            try {
                if(startUpConexion()){
                    setGuiApagado(SsId);
                }
                else{
                    setGuiDesconectado();
                }
            } catch (Exception e) {
                e.printStackTrace();
            }
            conectado=false;
            running=false;
        }

        public boolean isRunning(){
            return running;
        }

    }


    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    //**********************************************************************************************************************//
    //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
    public class GoproMode
    {
        private String name;

        private int icon;

        public int getCodeMode() {
            return codeMode;
        }

        public void setCodeMode(int codeMode) {
            this.codeMode = codeMode;
        }

        public int codeMode;

        public GoproMode(String nombre, int icono, int code)
        {
            super();
            this.name = nombre;
            this.icon = icono;
            this.codeMode = code;
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
