package boiler.com.boilercontrolapp;

import android.graphics.drawable.Drawable;
import android.widget.ImageView;

/**
 * Created by Owner on 2017-10-31.
 */

public class ListView_item {

    private int heatingPower;
    private int outgoingMode;
    private int currentTemp;
    private int desiredTemp;
    private String serialNum;
    private String roomName;
    private int icon;

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public String getSerialNum() {
        return serialNum;
    }

    public void setSerialNum(String serialNum) {
        this.serialNum = serialNum;
    }

    public int getHeatingPower() {
        return heatingPower;
    }

    public void setHeatingPower(int heatingPower) {
        this.heatingPower = heatingPower;
    }

    public int getOutgoingMode() {
        return outgoingMode;
    }

    public void setOutgoingMode(int outgoingMode) {
        this.outgoingMode = outgoingMode;
    }

    public int getCurrentTemp() {
        return currentTemp;
    }

    public void setCurrentTemp(int currentTemp) {
        this.currentTemp = currentTemp;
    }

    public int getDesiredTemp() {
        return desiredTemp;
    }

    public void setDesiredTemp(int desiredTemp) {
        this.desiredTemp = desiredTemp;
    }

    public int getIcon() {
        return icon;
    }

    public void setIcon(int icon) {
        this.icon = icon;
    }
}
