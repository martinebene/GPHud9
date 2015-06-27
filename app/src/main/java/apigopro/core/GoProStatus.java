package apigopro.core;

import apigopro.core.model.BacPacStatus;
import apigopro.core.model.BackPack;
import apigopro.core.model.CamFields;
import apigopro.core.model.CameraSettings;

/**
 * Created by Martin on 15/06/2015.
 */
public class GoProStatus {

    public BacPacStatus bacPacStatus;
    public BackPack bacPacInfo;
    public CamFields camFields;
    public String password;

    public  GoProStatus() {

        bacPacStatus=null;
        bacPacInfo=null;
        camFields=null;
        password=null;

    }

    public boolean isFieldComplete(){

        if(bacPacStatus==null)return false;
        if(bacPacInfo==null)return false;
        if(camFields==null)return false;
        if(password==null)return false;
        else return true;
    }

    public BackPack getBacPacInfo() {
        return bacPacInfo;
    }

    public void setBacPacInfo(BackPack bacPacInfo) {
        this.bacPacInfo = bacPacInfo;
    }

    public String getPassword() {
        return password;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public BacPacStatus getBacPacStatus() {
        return bacPacStatus;
    }

    public void setBacPacStatus(BacPacStatus bacPacStatus) {
        this.bacPacStatus = bacPacStatus;
    }

    public CamFields getCamFields() {
        return camFields;
    }

    public void setCamFields(CamFields camFields) {
        this.camFields = camFields;
    }
}
