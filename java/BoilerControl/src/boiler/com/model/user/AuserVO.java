package boiler.com.model.user;

public class AuserVO {
	private int unum;
	private int apartComplex;		// ����Ʈ ����
	private int apartNumber;		// ����Ʈ ȣ��
	private String password;		// ��й�ȣ
	private String nicname;
	private String joinDate;		// �α��� �ð�
	public int getUnum() {
		return unum;
	}
	public void setUnum(int unum) {
		this.unum = unum;
	}
	public int getApartComplex() {
		return apartComplex;
	}
	public void setApartComplex(int apartComplex) {
		this.apartComplex = apartComplex;
	}
	public int getApartNumber() {
		return apartNumber;
	}
	public void setApartNumber(int apartNumber) {
		this.apartNumber = apartNumber;
	}
	public String getPassword() {
		return password;
	}
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getNicname() {
		return nicname;
	}
	public void setNicname(String nicname) {
		this.nicname = nicname;
	}
	public String getJoinDate() {
		return joinDate;
	}
	public void setJoinDate(String joinDate) {
		this.joinDate = joinDate;
	}
	
	
}
