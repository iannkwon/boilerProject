package boiler.com.model.user;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

public class AuserDAOimpl implements AuserDAO {
	
	private Connection conn;
	private PreparedStatement pstmt;
	private ResultSet rs;
	
	private final String url = "jdbc:oracle:thin:@localhost:1521:xe";
	private final String user = "test";
	private final String password = "12345678";
	
	
	
	
	public AuserDAOimpl() {
		// TODO Auto-generated constructor stub
		try {
			Class.forName("oracle.jdbc.OracleDriver");
			System.out.println("Find Successed");
		} catch (ClassNotFoundException e) {
			System.out.println("Find Failed");
			e.printStackTrace();
		}
	}
	@Override
	public int insert(AuserVO vo) {
		System.out.println("insert ID>>>"+vo.getId());
		System.out.println("insert password>>>"+vo.getPassword());
		System.out.println("insert nicname>>>"+vo.getNicname());
		System.out.println("insert joindate>>>"+vo.getJoinDate());
		int flag=0;
		
		try {
			conn = DriverManager.getConnection(url, user, password);
			String SQL_AUSER_INSERT = "insert into auser(unum, id, password, nicname, joindate)"
					+ "values(seq_auser_num.nextval,?,?,?,?)";
			pstmt = conn.prepareStatement(SQL_AUSER_INSERT);
			pstmt.setString(1, vo.getId());
			pstmt.setString(2, vo.getPassword());
			pstmt.setString(3, vo.getNicname());
			pstmt.setString(4, vo.getJoinDate());
			
			flag = pstmt.executeUpdate();
			
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
	
@Override
public AuserVO loginSearch(AuserVO vo) {
	AuserVO vo2 = new AuserVO();
	String nic="";
	try {
		conn = DriverManager.getConnection(url, user, password);
		String SQL_AUSER_LOGIN = "select nicname from auser "
				+ "where id = ? and password=?";
		pstmt = conn.prepareStatement(SQL_AUSER_LOGIN);
		pstmt.setString(1, vo.getId());
		pstmt.setString(2, vo.getPassword());
		
		rs = pstmt.executeQuery();
		while(rs.next()){
			nic = rs.getString("nicname");
		}
		
		if (!nic.equals("")) {
			vo2.setId(vo.getId());
			vo2.setNicname(nic);
		}else{
			vo2.setId("fail");
		}
		
		
		System.out.println(vo2.getId());
		System.out.println(vo2.getPassword());
		System.out.println(nic);
		
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
	return vo2;
	}

@Override
public int loginIdCheck(AuserVO vo) {
	int flag=0;
	System.out.println("loginApartCheck");
	try {
		conn = DriverManager.getConnection(url, user, password);
		String SQL_AUSER_APARTCHECK = "select count(*) as userok from auser where id =? ";
		pstmt = conn.prepareStatement(SQL_AUSER_APARTCHECK);
		
		pstmt.setString(1, vo.getId());
		
		rs = pstmt.executeQuery();
		while(rs.next()){
			flag = rs.getInt("userok");
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

@Override
public int loginNicCheck(AuserVO vo) {
	int flag=0;
	System.out.println("loginNicCheck");
	try {
		conn = DriverManager.getConnection(url, user, password);
		String SQL_AUSER_NICCHECK = "select count(*) as userok from auser where nicname=?";
		pstmt = conn.prepareStatement(SQL_AUSER_NICCHECK);
		
		pstmt.setString(1, vo.getNicname());
		
		rs = pstmt.executeQuery();
		while(rs.next()){
			flag = rs.getInt("userok");
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

}
