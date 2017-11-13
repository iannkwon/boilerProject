package boiler.com.model.heating;

public class HeatingVO {
	private int num;
	private int heatingPower;		// ���Ϸ� ����
	private int outGoingMode;		// ���� ���
	private double currentTemp;		// ����µ�
	private double desiredTemp;		// ����µ�
	private String heatingTime;		// �ð�
	private String serialNum;	// ��ǰ ��ȣ
	private String roomName;		// �� �̸�
	private String nicName;			// �г���
	
	public int getNum() {
		return num;
	}
	public void setNum(int num) {
		this.num = num;
	}
	public int getheatingPower() {
		return heatingPower;
	}
	public void setheatingPower(int heatingPower) {
		this.heatingPower = heatingPower;
	}
	
	public int getOutGoingMode() {
		return outGoingMode;
	}
	public void setOutGoingMode(int outGoingMode) {
		this.outGoingMode = outGoingMode;
	}
	public double getCurrentTemp() {
		return currentTemp;
	}
	public void setCurrentTemp(double currentTemp) {
		this.currentTemp = currentTemp;
	}
	public double getDesiredTemp() {
		return desiredTemp;
	}
	public void setDesiredTemp(double desiredTemp) {
		this.desiredTemp = desiredTemp;
	}
	public String getheatingTime() {
		return heatingTime;
	}
	public void setheatingTime(String heatingTime) {
		this.heatingTime = heatingTime;
	}
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	public String getRoomName() {
		return roomName;
	}
	public void setRoomName(String roomName) {
		this.roomName = roomName;
	}
	public String getNicName() {
		return nicName;
	}
	public void setNicName(String nicName) {
		this.nicName = nicName;
	}
	
	
	
}
