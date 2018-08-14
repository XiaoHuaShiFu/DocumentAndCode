package dao;

import java.sql.SQLException;
import java.util.*;

import model.Goddess;

public class ProduceDaoTest {

	public static void main(String[] args) throws SQLException {
		List<Goddess> goddesses = ProduceDao.select_filter("");
		
		for (Goddess goddess : goddesses) {
			System.out.println(goddess);
		}
		
		int count = ProduceDao.select_count();
		System.out.println(count);
	}

}
