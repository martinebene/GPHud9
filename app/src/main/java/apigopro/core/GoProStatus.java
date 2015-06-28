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
    public String cname;
    public CamFields cameraInfo;
    public CamFields cameraSettingsExtended;

    @Override
    public String toString() {
        return "GoProStatus{" + "\n" +
                "bacPacStatus=" + bacPacStatus + "\n" + "\n" +
                ", bacPacInfo=" + bacPacInfo + "\n" + "\n" +
                ", camFields=" + camFields + "\n" + "\n" +
                ", password='" + password + '\'' + "\n" + "\n" +
                ", cname='" + cname + '\'' + "\n" + "\n" +
                ", cameraInfo=" + cameraInfo + "\n" + "\n" +
                ", cameraSettingsExtended=" + cameraSettingsExtended + "\n" + "\n" +
                '}';
    }

    public String getCname() {
        return cname;
    }

    public void setCname(String cname) {
        this.cname = cname;
    }

    public CamFields getCameraInfo() {
        return cameraInfo;
    }

    public void setCameraInfo(CamFields cameraInfo) {
        this.cameraInfo = cameraInfo;
    }

    public CamFields getCameraSettingsExtended() {
        return cameraSettingsExtended;
    }

    public void setCameraSettingsExtended(CamFields cameraSettingsExtended) {
        this.cameraSettingsExtended = cameraSettingsExtended;
    }

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
