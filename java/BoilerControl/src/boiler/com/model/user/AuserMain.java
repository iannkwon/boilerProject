package boiler.com.model.user;

public class AuserMain {

	public static void main(String[] args) {
		System.out.println("User Main");
		
		AuserVO vo = new AuserVO();
		AuserDAO dao = new AuserDAOimpl();
		
		//Insert
//		vo.setApartComplex(101);
//		vo.setApartNumber(201);
//		vo.setPassword("123456");
//		vo.setJoinDate("08:11");
//		
//		int result = dao.insert(vo);
//		
//		if (result == 1) {
//			System.out.println("Login insert Successed");
//		}
		
		// login search
//		vo.setApartComplex(101);
//		vo.setApartNumber(201);
//		vo.setPassword("123456");
//		
//		AuserVO vo2 = dao.loginSearch(vo);
//		
//		
//		System.out.println("MAIN ->"+vo2.getApartComplex());
//		System.out.println("MAIN ->"+vo2.getApartNumber());
//		System.out.println("MAIN ->"+vo2.getNicname());
	}

}
