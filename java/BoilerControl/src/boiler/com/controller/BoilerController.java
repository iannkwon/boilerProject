package boiler.com.controller;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.List;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.json.JSONArray;
import org.json.JSONObject;

import boiler.com.model.heating.HeatingDAO;
import boiler.com.model.heating.HeatingDAOimpl;
import boiler.com.model.heating.HeatingVO;
import boiler.com.model.user.AuserDAO;
import boiler.com.model.user.AuserDAOimpl;
import boiler.com.model.user.AuserVO;

/**
 * Servlet implementation class BoilerController
 */
@WebServlet({"/heatingInfo.do","/heatingSearch.do","/loginGo.do"
	,"/heatingInsert.do","/heatingUpdate.do","/heatingControllerUpdate.do"
	,"/heatingDelete.do","/memberGo.do","/updateName.do"})
public class BoilerController extends HttpServlet {
	private static final long serialVersionUID = 1L;

    /**
     * Default constructor. 
     */
    public BoilerController() {
        // TODO Auto-generated constructor stub
    	super();
    }

	/**
	 * @see HttpServlet#doGet(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		doProcess(request, response);
	}

	/**
	 * @see HttpServlet#doPost(HttpServletRequest request, HttpServletResponse response)
	 */
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		// TODO Auto-generated method stub
		request.setCharacterEncoding("UTF-8");
		doProcess(request, response);
	}
	
	protected void doProcess(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
		response.setContentType("application/json");
		response.setHeader("Cache-Control", "nocache");
		response.setCharacterEncoding("utf-8");
        PrintWriter out = response.getWriter();
        
        if(request.getServletPath().equals("/memberGo.do")){
			// 회원가입
        	String id = request.getParameter("id");
			String password = request.getParameter("password");
			String nicName = request.getParameter("nicName");
			String joinDate = request.getParameter("joinDate");
			String serialNum = request.getParameter("serialNum");
			
			System.out.println("Android >> java id :"+id);
			System.out.println("Android >> java Password :"+password);
			System.out.println("Android >> java Nicname :"+nicName);
			System.out.println("Android >> java Joindate :"+joinDate);
			System.out.println("Android >> java serialNum :"+serialNum);
			
			if (!id.equals("")  && !nicName.equals("") 
					&&!password.equals("") && !joinDate.equals("")
					&&!serialNum.equals("")) {
				AuserVO vo = new AuserVO();
				AuserDAO dao = new AuserDAOimpl();
				
				vo.setId(id);
				vo.setPassword(password);
				vo.setNicname(nicName);
				vo.setJoinDate(joinDate);
				vo.setSerialNum(serialNum);
				int idResult = dao.loginIdCheck(vo);
				int nicResult = dao.loginNicCheck(vo);
				int serialResult = dao.serialCheck(vo);
				System.out.println("idResult>>"+idResult);
				System.out.println("nicResult>>"+nicResult);
				System.out.println("serialResult>>"+serialResult);
				if (idResult == 1) {
					out.append("idFail");
				}if (nicResult ==1) {
					out.append("nicFail");
				}if ( serialResult == 0 ){
					out.append("serialFail");
				}
				else if (idResult == 0 && nicResult ==0 && serialResult == 1) {
					int insertResult = dao.insert(vo);
					System.out.println("insertResult:"+insertResult);
					if (insertResult == 1) {
						out.append("joinSuccess");
						System.out.println("member Successed");
					}else{
						out.append("joinFail");
						System.out.println("member failed");
					}
				}
				
			}
			
		}else if (request.getServletPath().equals("/loginGo.do")) {
			// 로그인
			String id = request.getParameter("id");
			String password = request.getParameter("password");
			
			
			System.out.println(id);
			System.out.println(password);
			
			if (!id.equals("") && !password.equals("")){
				
			AuserVO vo = new AuserVO();
			AuserDAO dao = new AuserDAOimpl();
			
			vo.setId(id);
			vo.setPassword(password);
			
			System.out.println(vo.getId());
			
			AuserVO vo2 = dao.loginSearch(vo);
			
			System.out.println("vo2.getID : "+vo2.getId());
			if (vo2.getId()=="fail") {
				out.append("fail");
			}else{
				JSONObject jobj = new JSONObject();
				jobj.put("id", vo2.getId());
				jobj.put("nicName", vo2.getNicname());
				
				out.append(jobj.toString());
			}
			
		}else{
				out.append("fail");
		}
			
		} else if (request.getServletPath().equals("/heatingInsert.do")) {
        	// 장치 추가
			System.out.println("Servlet boilerInsert.do...");
        	String serialNum = request.getParameter("serialNum");
        	String roomName = request.getParameter("roomName");
        	String nicName = request.getParameter("nicName");
        	
        	System.out.println("serialNum :"+serialNum);
        	System.out.println("roomName :"+roomName);
        	System.out.println("nicName :"+nicName);
        	
        		if (!serialNum.equals("") && !roomName.equals("")) 
        		{
				
        		HeatingVO vo = new HeatingVO();
        		HeatingDAO dao = new HeatingDAOimpl();
        		
        		vo.setSerialNum(serialNum);
        		vo.setRoomName(roomName);
        		vo.setNicName(nicName);
        		
        		System.out.println("serialNum DB->"+serialNum);
        		System.out.println("roomName DB->"+roomName);
        		System.out.println("nicName DB->"+nicName);
        		
        		int result2 = dao.serialNumCheck(vo);
        		System.out.println("result2>>"+result2);
        		
        		int result = dao.insert(vo);
        		System.out.println("result>>"+result);
  
        		if (result2 == 0) {
					out.append("serialFailed");
				}
        		if (result == 1 && result2 == 1) {
					System.out.println("heatingInsert.do Successed");
					out.append("serialOk");
				} else {
					System.out.println("heatingInsert.do Failed");
				}
        		
			}
        	
			
		}else if (request.getServletPath().equals("/heatingInfo.do")) {
			// 해당 방 온도 정보
			HeatingVO vo = new HeatingVO();
			HeatingDAO dao = new HeatingDAOimpl();
			
			HeatingVO vo2 = dao.searchInfo(vo);
			
			System.out.println(vo2.getheatingPower());
			System.out.println(vo2.getOutGoingMode());
			System.out.println(vo2.getCurrentTemp());
			System.out.println(vo2.getDesiredTemp());
			System.out.println(vo2.getSerialNum());
			System.out.println(vo2.getRoomName());
			
			JSONObject object = new JSONObject(vo2);
			object.put("heatingPower", vo2.getheatingPower());
			object.put("outGoingMode", vo2.getOutGoingMode());
			object.put("currentTemp", vo2.getCurrentTemp());
			object.put("desiredTemp", vo2.getDesiredTemp());
			object.put("serialNum", vo2.getSerialNum());
			object.put("roomName", vo2.getRoomName());
			out.append(object.toString());
			
		} else if(request.getServletPath().equals("/heatingUpdate.do")) {
			// 온도 입력
			String heatingPower= request.getParameter("heatingPower");
        	String outGoingMode= request.getParameter("outGoingMode");
        	String currentTemp = request.getParameter("currentTemp");
        	String desiredTemp = request.getParameter("desiredTemp");
        	String heatingTime = request.getParameter("heatingTime");
        	String serialNum = request.getParameter("serialNum");
        	String roomName = request.getParameter("roomName");
        	
        	System.out.println("heatingPower :"+heatingPower);
        	System.out.println("outGoingMode :"+outGoingMode);
        	System.out.println("currentTemp : "+currentTemp);
        	System.out.println("desiredTemp :"+desiredTemp);
        	System.out.println("heatingTime :"+heatingTime);
        	System.out.println("serialNum :"+serialNum);
        	System.out.println("roomName :"+roomName);
        	
        	if (!heatingPower.equals("") && !currentTemp.equals("") 
        		&& !desiredTemp.equals("") && !heatingTime.equals("")) {
				
        		HeatingVO vo = new HeatingVO();
        		HeatingDAO dao = new HeatingDAOimpl();
        		
        		vo.setheatingPower(Integer.parseInt(heatingPower));
        		vo.setOutGoingMode(Integer.parseInt(outGoingMode));
        		vo.setCurrentTemp(Double.parseDouble(currentTemp));
        		vo.setDesiredTemp(Double.parseDouble(desiredTemp));
        		vo.setheatingTime(heatingTime);
        		vo.setSerialNum(serialNum);
        		vo.setRoomName(roomName);
        		
        		
        		System.out.println("heatingPower DB->"+Integer.parseInt(heatingPower));
        		System.out.println("outGoingMode DB->"+Integer.parseInt(outGoingMode));
        		System.out.println("currentTemp DB->"+Double.parseDouble(currentTemp));
        		System.out.println("desiredTemp DB->"+Double.parseDouble(desiredTemp));
        		System.out.println("heatingTime DB->"+heatingTime);
        		System.out.println("serialNum DB->"+serialNum);
        		System.out.println("roomName DB->"+roomName);
        		
        		int result = dao.update(vo);
        		
        		if (result == 1) {
					System.out.println("heatingSet.do Successed");
				} else {
					System.out.println("heatingSet.do Failed");
				}
		}
	}else if (request.getServletPath().equals("/heatingSearch.do")) {
		// 해당 닉네임 각 방 마다 온도 가져오기
		String nicName = request.getParameter("nicName");
		
		HeatingVO vo = new HeatingVO();
		HeatingDAO dao = new HeatingDAOimpl();
		
		vo.setNicName(nicName);
		
		System.out.println("Nicname:"+nicName);
		System.out.println("vo.getNic"+vo.getNicName());
		
		if (!nicName.equals("")) {
			List<HeatingVO> list = dao.searchList(vo);
			JSONArray array = new JSONArray();
			JSONObject object = new JSONObject(list);
			
			for (int i = 0; i < list.size(); i++) {
				JSONObject object2 = new JSONObject();
				object2.put("heatingPower", list.get(i).getheatingPower());
				object2.put("outGoingMode", list.get(i).getOutGoingMode());
				object2.put("currentTemp", list.get(i).getCurrentTemp());
				object2.put("desiredTemp", list.get(i).getDesiredTemp());
				object2.put("serialNum", list.get(i).getSerialNum());
				object2.put("roomName", list.get(i).getRoomName());
				array.put(object2);
			}
			
			object.put("list", array);
			out.append(object.toString());
			System.out.println("servlet"+object.toString());
		}
		
		
	}else if(request.getServletPath().equals("/heatingControllerUpdate.do")){
		// 전체 방 온도 처리
		String heatingPower= request.getParameter("heatingPower");
    	String outGoingMode= request.getParameter("outGoingMode");
    	String desiredTemp = request.getParameter("desiredTemp");
    	String heatingTime = request.getParameter("heatingTime");
    	String nicName = request.getParameter("nicName");
    	
    	System.out.println("heatingPower :"+heatingPower);
    	System.out.println("outGoingMode :"+outGoingMode);
    	System.out.println("desiredTemp :"+desiredTemp);
    	System.out.println("heatingTime :"+heatingTime);
    	System.out.println("nicName :"+nicName);
    	
    	if (!heatingPower.equals("") && !outGoingMode.equals("") 
    		&& !desiredTemp.equals("") && !heatingTime.equals("")) {
			
    		HeatingVO vo = new HeatingVO();
    		HeatingDAO dao = new HeatingDAOimpl();
    		
    		vo.setheatingPower(Integer.parseInt(heatingPower));
    		vo.setOutGoingMode(Integer.parseInt(outGoingMode));
    		vo.setDesiredTemp(Double.parseDouble(desiredTemp));
    		vo.setheatingTime(heatingTime);
    		vo.setNicName(nicName);
    		
    		
    		System.out.println("heatingPower DB->"+Integer.parseInt(heatingPower));
    		System.out.println("outGoingMode DB->"+Integer.parseInt(outGoingMode));
    		System.out.println("desiredTemp DB->"+Double.parseDouble(desiredTemp));
    		System.out.println("heatingTime DB->"+heatingTime);
    		
    		int result = dao.updateController(vo);
    		
    		if (result == 1) {
				System.out.println("heatingUpdateController.do Successed");
			} else {
				System.out.println("heatingUpdateController.do Failed");
			}
	}
        
	}else if (request.getServletPath().equals("/heatingDelete.do")) {
		// 장치 삭제
		String serialNum = request.getParameter("serialNum");
		String nicName = "";
			
		if (!serialNum.equals("")) {
			HeatingVO vo = new HeatingVO();
    		HeatingDAO dao = new HeatingDAOimpl();
    		
    		vo.setSerialNum(serialNum);
    		vo.setNicName(nicName);
    		
    		int result = dao.delete(vo);
    		
    		if (result == 1) {
				System.out.println("heatingDelete Successed");
			} else {
				System.out.println("heatingDelete Failed");
			}
		}
		
	
	}else if (request.getServletPath().equals("/updateName.do")) {
		// 방 이름 변경
		String serialNum = request.getParameter("serialNum");
		String roomName = request.getParameter("roomName");
			
		System.out.println(serialNum);
		System.out.println(roomName);
		
		if (!serialNum.equals("") && !roomName.equals("") ) {
			HeatingVO vo = new HeatingVO();
    		HeatingDAO dao = new HeatingDAOimpl();
    		
    		vo.setSerialNum(serialNum);
    		vo.setRoomName(roomName);
    		
    		int result = dao.updateName(vo);
    		
    		if (result == 1) {
				System.out.println("updateName Successed");
			} else {
				System.out.println("updateName Failed");
			}
		}
		
	
	}
        
  }
	
}

