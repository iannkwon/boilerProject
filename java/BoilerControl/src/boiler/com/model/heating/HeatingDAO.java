package boiler.com.model.heating;

import java.util.ArrayList;
import java.util.List;

import boiler.com.model.user.AuserVO;

public interface HeatingDAO {
	
	public int insert(HeatingVO vo);
	
	public int update(HeatingVO vo);
	
	public int updateController(HeatingVO vo);
	
	public int delete(HeatingVO vo);
	
	public int serialNumCheck(HeatingVO vo);
	
	public HeatingVO searchInfo(HeatingVO vo);
	
	
	public ArrayList<HeatingVO> searchList(HeatingVO vo);
}
