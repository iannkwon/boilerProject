package boiler.com.model.user;

public class AuserVO {
	private int unum;
	private String id;				// ���̵�
	private String password;		// ��ȣ
	private String nicname;			// �г���
	private String joinDate;		// ȸ������ �Ͻ�
	private String serialNum;		// ��Ʈ�ѷ� �ø��� ��ȣ
	public int getUnum() {
		return unum;
	}
	public void setUnum(int unum) {
		this.unum = unum;
	}

	public String getId() {
		return id;
	}
	public void setId(String id) {
		this.id = id;
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
	public String getSerialNum() {
		return serialNum;
	}
	public void setSerialNum(String serialNum) {
		this.serialNum = serialNum;
	}
	
}
