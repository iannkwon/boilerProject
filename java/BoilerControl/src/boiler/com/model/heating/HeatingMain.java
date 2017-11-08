package boiler.com.model.heating;

import java.util.ArrayList;

public class HeatingMain {

	public static void main(String[] args) {
		// TODO Auto-generated method stub
		System.out.println("Boiler Main()");
		
		HeatingVO vo = new HeatingVO();
		HeatingDAO dao = new HeatingDAOimpl();
		
		//insert
		
//		vo.setheatingPower(0);
//		vo.setOutGoingMode(1);
//		vo.setCurrentTemp(20);
//		vo.setDesiredTemp(24);
//		vo.setheatingTime("11:11");
//		vo.setSerialNum("17F10010");
//		vo.setRoomName("Children");
//		
//		int result = dao.insert(vo);
//		
//		if (result == 1) {
//			System.out.println("Insert Successed");
//		}else{
//			System.out.println("Insert Failed");
//		}
		
		// update
		
//		vo.setSerialNum("Deo_1234");
//		vo.setheatingPower(0);
//		vo.setOutGoingMode(1);
//		vo.setCurrentTemp(20);
//		vo.setDesiredTemp(24);
//		vo.setheatingTime("11:11");
//		vo.setRoomName("Parent");
//		
//		int result = dao.update(vo);
//		if (result == 1) {
//			System.out.println("update Successed");
//		}else{
//			System.out.println("update Failed");
//		}
		
		// delete
		
		vo.setSerialNum("17F10010");
		
		int result = dao.delete(vo);
		if (result==1) {
			System.out.println("delete Successed");
		
		} else{
			System.out.println("delete failed");
		}
		
		// Controller update
		
//		vo.setheatingPower(0);
//		vo.setOutGoingMode(1);
//		vo.setDesiredTemp(24);
//		vo.setheatingTime("11:11");
//		
//		int result = dao.updateController(vo);
//		if (result == 1) {
//			System.out.println("Controller update Successed");
//		}else{
//			System.out.println("Controller update Failed");
//		}
		
		// Heating Info Select
		//vo.setheatingTime("2017/10/24 17:12:22");
//		vo.setSerialNum("deo_1234");
		
//		HeatingVO vo2 = dao.searchInfo(vo);
//		
//		System.out.println(vo2.getheatingPower());
//		System.out.println(vo2.getOutGoingMode());
//		System.out.println(vo2.getCurrentTemp());
//		System.out.println(vo2.getDesiredTemp());
//		System.out.println(vo2.getheatingTime());
//		System.out.println(vo2.getSerialNum());
//		System.out.println(vo2.getRoomName());
		
		// Search
		
//		ArrayList<HeatingVO> searchResult = dao.searchList();
//		System.out.println(searchResult);
//		for (int i = 0; i < searchResult.size(); i++) {
//			System.out.println(searchResult.get(i).getheatingPower()+"");
//			System.out.println(searchResult.get(i).getOutGoingMode()+"");
//			System.out.println(searchResult.get(i).getCurrentTemp()+"");
//			System.out.println(searchResult.get(i).getDesiredTemp()+"");
//			System.out.println(searchResult.get(i).getSerialNum()+"");
//			System.out.println(searchResult.get(i).getRoomName()+"");
//			
//		}
		
		
		
		
		
		
	}

}
