package boiler.com.model.user;

public interface AuserDAO {
	
	public int insert(AuserVO vo);
	
	public AuserVO loginSearch(AuserVO vo);
	
	public int loginIdCheck(AuserVO vo);
	
	public int loginNicCheck(AuserVO vo);
	
	public int serialCheck(AuserVO vo);

	
}
