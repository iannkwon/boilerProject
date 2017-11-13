package boiler.com.model.heating;

public class HeatingVO {
	private int num;
	private int heatingPower;		// 보일러 난방
	private int outGoingMode;		// 외출 모드
	private double currentTemp;		// 현재온도
	private double desiredTemp;		// 희망온도
	private String heatingTime;		// 시간
	private String serialNum;	// 제품 번호
	private String roomName;		// 방 이름
	private String nicName;			// 닉네임
	
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
