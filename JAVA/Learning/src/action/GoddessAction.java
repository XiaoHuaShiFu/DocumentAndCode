package action;

import java.sql.SQLException;
import java.util.*;

import dao.GoddessDao;
import model.*;

/**
 * 控制层
 * @author lenovo
 *
 */
public class GoddessAction {
	
	private static GoddessDao goddessDao;
	
	static {
		goddessDao = new GoddessDao();
	}
	
	public GoddessAction() {
		// TODO Auto-generated constructor stub
	}

	public static void add(Goddess goddess) throws SQLException {
		goddessDao.add(goddess);
	}
	
	public static void edit(Goddess goddess) throws SQLException {
		goddessDao.update(goddess);
	}
	
	public static void delete(Integer id) throws SQLException {
		goddessDao.delete(id);
	}
	
	public static List<Goddess> query() throws SQLException {
		List<Goddess> goddesses = goddessDao.query();
		return goddesses;
	}
	
	public static List<Goddess> query(List<Map<String, Object>> params) throws SQLException {
		List<Goddess> goddesses = goddessDao.query(params);
		return goddesses;
	}
	
	public static Goddess get(Integer id) throws SQLException {
		Goddess goddess = goddessDao.get(id);
		return goddess;
	}
	
/*	public static void main(String[] args) throws SQLException {
//		Goddess xiaomei = new Goddess("小美", 1, 26, new Date(), "xioameilalala@qq.com", "22345612345", 
//				"xhsf", "xhsf", 1);
//		GoddessUtils.add(xiaomei);
		Goddess xiaohua = new Goddess(1, "小花", 0, 16, new Date(), "xioahua@qq.com", "12345612345", 
				"xhsf", 1);

//		GoddessUtils.delect(3);
		Goddess xiaoc = GoddessUtils.get(2);
//		System.out.println(xiaoc);
		GoddessUtils.update(xiaohua);

//		List<Goddess> xiaomeis = GoddessUtils.query("小美");
//		for (Goddess xiaomei : xiaomeis) {
//			System.out.println(xiaomei);
//		}
//		
//		List<Goddess> xiaomeiss = GoddessUtils.query("小","12345");
//		for (Goddess xiaomei : xiaomeiss) {
//			System.out.println("mohu" + xiaomei);
//		}
		
		List<Map<String, Object>> params = new ArrayList<>();
		Map<String, Object> param = new HashMap<>();
		param.put("logic", "or");
		param.put("name", "user_name");
		param.put("rela", "=");
		//''表示是字符串
		param.put("value", "'小美'" );
		params.add(param);
		param = new HashMap<>();
		param.put("logic", "or");
		param.put("name", "user_name");
		param.put("rela", "=");
		//''表示是字符串
		param.put("value", "'小花'" );
		params.add(param);
//		param = new HashMap<>();
//		param.put("logic", "and");
//		param.put("name", "sex");
//		param.put("rela", "=");
//		param.put("value", "1" );
//		params.add(param);
//		param = new HashMap<>();
//		param.put("logic", "and");
//		param.put("name", "birthday");
//		param.put("rela", "=");
//		param.put("value", "'2018-08-03'");
//		params.add(param);
		
		
		List<Goddess> goddesses = GoddessUtils.query(params);
		for (Goddess goddess : goddesses) {
			System.out.println(goddess);
		}
		
	}*/
	
}
