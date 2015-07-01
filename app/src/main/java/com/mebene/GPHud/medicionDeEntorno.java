package com.mebene.GPHud;

import java.util.Calendar;
import java.util.Date;

/**
 * Created by Martin on 01/07/2015.
 */
public class medicionDeEntorno {

    Calendar fechaYhora;
    float velocidad, velocidadMaxima, velocidadPromedio;
    long cantMediciiones;
    Aceleracion acelecarion;
    Giro giro;
    CampoMagnetico campoMagnetico;
    Clima clima;
    Cronometro cronometro;


    public medicionDeEntorno() {
        fechaYhora = Calendar.getInstance();
        velocidad=velocidadMaxima=velocidadPromedio=0;
        cantMediciiones=0;
        acelecarion= new Aceleracion(false);
        giro=new Giro(false);
        campoMagnetico=new CampoMagnetico(false);
        clima=new Clima(false);
        cronometro=new Cronometro(false);
    }

    @Override
    public String toString() {
        return "medicionDeEntorno{" +
                "fechaYhora=" + fechaYhora +
                ", velocidad=" + velocidad +
                ", velocidadMaxima=" + velocidadMaxima +
                ", velocidadPromedio=" + velocidadPromedio +
                ", cantMediciiones=" + cantMediciiones +
                ", acelecarion=" + acelecarion +
                ", giro=" + giro +
                ", campoMagnetico=" + campoMagnetico +
                ", clima=" + clima +
                ", cronometro=" + cronometro +
                '}';
    }
}




class Aceleracion {

    public long timestamp;
    boolean activo;

    float x,y,z,ax,ay,az,maxX,maxY,maxZ,minx,minY,minZ;

    Aceleracion(boolean lactivo) {
        activo = lactivo;
        x=y=z=ax=ay=az=maxX=maxY=maxZ=minx=minY=minZ=0;
    }

    @Override
    public String toString() {
        return "Aceleracion{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

class Giro {

    public long timestamp;
    boolean activo;

    float x,y,z,ax,ay,az,maxX,maxY,maxZ,minx,minY,minZ;

    Giro(boolean lactivo) {
        activo = lactivo;
        x=y=z=ax=ay=az=maxX=maxY=maxZ=minx=minY=minZ=0;
    }

    @Override
    public String toString() {
        return "Giro{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

class CampoMagnetico {

    public long timestamp;
    boolean activo;

    float x,y,z,ax,ay,az,maxX,maxY,maxZ,minx,minY,minZ;

    CampoMagnetico(boolean lactivo) {
        activo = lactivo;
        x=y=z=ax=ay=az=maxX=maxY=maxZ=minx=minY=minZ=0;
    }

    @Override
    public String toString() {
        return "CampoMagnetico{" +
                "x=" + x +
                ", y=" + y +
                ", z=" + z +
                '}';
    }
}

class Clima {

    public long timestamp;
    boolean activo;

    String temp, presion, humedad, intensidadViento, direViento, locacion;

    Clima(boolean lactivo) {
        activo = lactivo;
        temp = presion = humedad = intensidadViento = direViento = locacion = "";
    }

    @Override
    public String toString() {
        return "Clima{" +
                "temp='" + temp + '\'' +
                ", presion='" + presion + '\'' +
                ", humedad='" + humedad + '\'' +
                ", intensidadViento='" + intensidadViento + '\'' +
                ", direViento='" + direViento + '\'' +
                ", locacion='" + locacion + '\'' +
                '}';
    }
}

class Cronometro {

    boolean activo;
    public long t0;


    Cronometro(boolean lactivo) {
        activo = lactivo;
        t0=0;
    }

    void iniciar(long lt0){
        t0=lt0;
    }

    long getTranscurrido(long t1) {
        return t1-t0;
    }

    @Override
    public String toString() {
        return "Cronometro{" +
                "t0=" + t0 +
                '}';
    }
}