package boiler.com.model.heating;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

public class HeatingDAOimpl implements HeatingDAO {
	
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private final String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private final String user = "test";
	private final String password = "12345678";
	
	
	
	
	public HeatingDAOimpl() {
		System.out.println("HeatingDAOimpl()...");
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			System.out.println("Find Successed");
		} catch (ClassNotFoundException e) {
			System.out.println("Find Failed");
			e.printStackTrace();
		}
	}
	// 제품 추가
	@Override
	public int insert(HeatingVO vo) {
		int flag=0;
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			String SQL_HEATING_INSERT = "update heating set roomname=?, nicname=? where serialnum=?";
			pstmt = conn.prepareStatement(SQL_HEATING_INSERT);
			
			pstmt.setString(1, vo.getRoomName());
			pstmt.setString(2, vo.getNicName());
			pstmt.setString(3, vo.getSerialNum());
			flag = pstmt.executeUpdate();
			
		} catch (SQLException e) {
			e.printStackTrace();
		} finally {
			if (pstmt != null ) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return flag;
	}
	// 제품 번호 조회
	@Override
	public int serialNumCheck(HeatingVO vo) {
		int flag=0;
		System.out.println("SerialNumCheck");
		try {
			conn = DriverManager.getConnection(url, user, password);
			String SQL_AUSER_APARTCHECK = "select count(*) as serialok from heating where serialnum=?";
			pstmt = conn.prepareStatement(SQL_AUSER_APARTCHECK);
			
			pstmt.setString(1, vo.getSerialNum());
			
			rs = pstmt.executeQuery();
			while(rs.next()){
				flag = rs.getInt("serialok");
			}
			
			
		} catch (SQLException e) {
			// TODO: handle exception
			e.printStackTrace();
		} finally {
			if (pstmt != null ) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return flag;
	}
	// 온도 변경
	@Override
	public int update(HeatingVO vo) {
		System.out.println("update ");
		System.out.println(vo.getheatingPower());
		System.out.println(vo.getOutGoingMode());
		System.out.println(vo.getCurrentTemp());
		System.out.println(vo.getDesiredTemp());
		System.out.println(vo.getheatingTime());
		System.out.println(vo.getSerialNum());
		System.out.println(vo.getRoomName());
		
		int flag = 0;
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			
			String SQL_HEATING_UPDATE = "update heating set heatingpower=?, outgoingmode=?,currenttemp=?, desiredtemp=?, heatingtime=?,roomname=? where serialnum=?";
			pstmt = conn.prepareStatement(SQL_HEATING_UPDATE);
			
			pstmt.setInt(1, vo.getheatingPower());
			pstmt.setInt(2, vo.getOutGoingMode());
			pstmt.setDouble(3, vo.getCurrentTemp());
			pstmt.setDouble(4, vo.getDesiredTemp());
			pstmt.setString(5, vo.getheatingTime());
			pstmt.setString(6, vo.getRoomName());
			pstmt.setString(7, vo.getSerialNum());
			
			flag = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (pstmt != null ) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	// 제품 삭제
	@Override
	public int delete(HeatingVO vo) {
		System.out.println(vo.getSerialNum());
		
		int flag = 0;
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			
			String SQL_HEATING_DELETE = "update heating set nicname=? where serialnum=?";
			pstmt = conn.prepareStatement(SQL_HEATING_DELETE);
			
			pstmt.setString(1, vo.getNicName());
			pstmt.setString(2, vo.getSerialNum());
			
			flag = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (pstmt != null ) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	
	@Override
	public HeatingVO searchInfo(HeatingVO vo) {
		HeatingVO vo2 = new HeatingVO();
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			String SQL_HEATING_SELECT = "select * from heating order by heatingtime asc";
			pstmt = conn.prepareStatement(SQL_HEATING_SELECT);
			rs = pstmt.executeQuery();
			while (rs.next()) {
				
				vo2.setheatingPower(rs.getInt("heatingpower"));
				vo2.setOutGoingMode(rs.getInt("outgoingmode"));
				vo2.setCurrentTemp(rs.getDouble("currenttemp"));
				vo2.setDesiredTemp(rs.getDouble("desiredtemp"));
				vo2.setSerialNum(rs.getString("serialnum"));
				vo2.setRoomName(rs.getString("roomname"));
			}
		} catch (SQLException e) {
			e.printStackTrace();
		} finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return vo2;
	}

	@Override
	public ArrayList<HeatingVO> searchList(HeatingVO vo) {
		System.out.println("SearchList");
		ArrayList<HeatingVO> list = new ArrayList<HeatingVO>();
		try {
			conn = DriverManager.getConnection(url, user, password);
			String SQL_HEATING_SEARCHLIST = "select * from heating where nicname=? ";
			pstmt = conn.prepareStatement(SQL_HEATING_SEARCHLIST);
			
			pstmt.setString(1, vo.getNicName());
			System.out.println("getNicName>>"+vo.getNicName());
	
			rs = pstmt.executeQuery();
			while (rs.next()) {
				HeatingVO vo3 = new HeatingVO();
				vo3.setheatingPower(rs.getInt("heatingpower"));
				vo3.setOutGoingMode(rs.getInt("outgoingmode"));
				vo3.setCurrentTemp(rs.getDouble("currenttemp"));
				vo3.setDesiredTemp(rs.getDouble("desiredtemp"));
				vo3.setSerialNum(rs.getString("serialnum"));
				vo3.setRoomName(rs.getString("roomname"));
				list.add(vo3);
			}
			System.out.println(list.toString());
		} catch (SQLException e) {
			e.printStackTrace();
		}finally{
			if(pstmt!=null){
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if(conn!=null){
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		
		return list;
	}

	@Override
	public int updateController(HeatingVO vo) {
		System.out.println("update Controller ");
		System.out.println(vo.getheatingPower());
		System.out.println(vo.getOutGoingMode());
		System.out.println(vo.getDesiredTemp());
		System.out.println(vo.getheatingTime());
		
		int flag = 0;
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			
			String SQL_HEATING_CONTROLLERUPDATE = "update heating set heatingpower=?, outgoingmode=?, desiredtemp=?, heatingtime=? where nicname=?";
			pstmt = conn.prepareStatement(SQL_HEATING_CONTROLLERUPDATE);
			
			pstmt.setInt(1, vo.getheatingPower());
			pstmt.setInt(2, vo.getOutGoingMode());
			pstmt.setDouble(3, vo.getDesiredTemp());
			pstmt.setString(4, vo.getheatingTime());
			pstmt.setString(5, vo.getNicName());
			
			flag = pstmt.executeUpdate();
		} catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (pstmt != null ) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}
	
	@Override
	public int updateName(HeatingVO vo) {
		System.out.println("update Name ");
		System.out.println(vo.getRoomName());
		System.out.println(vo.getSerialNum());
		int flag = 0;
		try {
			conn = DriverManager.getConnection(url, user, password);
			String SQL_HEATING_UPDATENAME = "update heating set roomname=? where serialnum=?";
			pstmt = conn.prepareStatement(SQL_HEATING_UPDATENAME);
			pstmt.setString(1, vo.getRoomName());
			pstmt.setString(2, vo.getSerialNum());
			flag = pstmt.executeUpdate();
		}catch (SQLException e) {
			e.printStackTrace();
		}finally {
			if (pstmt != null ) {
				try {
					pstmt.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
			if (conn != null) {
				try {
					conn.close();
				} catch (SQLException e) {
					e.printStackTrace();
				}
			}
		}
		return flag;
	}

	

}
