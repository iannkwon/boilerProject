package boiler.com.model.hotwater;

public class HotWaterVO {

	private int hnum;
	private int hotWaterPower;
	private int hotWaterCurrentTemp;
	private int hotWaterDesiredTemp;
	private String hTime;
	public int getHnum() {
		return hnum;
	}
	public void setHnum(int hnum) {
		this.hnum = hnum;
	}
	public int getHotWaterPower() {
		return hotWaterPower;
	}
	public void setHotWaterPower(int hotWaterPower) {
		this.hotWaterPower = hotWaterPower;
	}
	public int getHotWaterCurrentTemp() {
		return hotWaterCurrentTemp;
	}
	public void setHotWaterCurrentTemp(int hotWaterCurrentTemp) {
		this.hotWaterCurrentTemp = hotWaterCurrentTemp;
	}
	public int getHotWaterDesiredTemp() {
		return hotWaterDesiredTemp;
	}
	public void setHotWaterDesiredTemp(int hotWaterDesiredTemp) {
		this.hotWaterDesiredTemp = hotWaterDesiredTemp;
	}
	public String gethTime() {
		return hTime;
	}
	public void sethTime(String hTime) {
		this.hTime = hTime;
	}
	
	
	
}
