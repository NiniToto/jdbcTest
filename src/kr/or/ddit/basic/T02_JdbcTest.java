package kr.or.ddit.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

/**
 * Select 예제 
 */
public class T02_JdbcTest {

	/*
	 * 문제 1) 사용자로부터 lprod_id값을 입력받아 입력한 값보다 lprod_id가 큰 자료들을 출력하시오.
	 * 
	 * 문제 2) lprod_id값을 2개 입력받아서 두 값 중 작은 값부터 큰 값 사이의 자료를 출력하시오
	 */
	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		
		Connection conn = null;
		Statement stmt = null;
		ResultSet rs = null;
		
		try {
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			String url = "jdbc:oracle:thin:@localhost:1521/xe";
			String userId = "WONWOO";
			String password = "java";
			
			conn = DriverManager.getConnection(url, userId, password);
			
			stmt = conn.createStatement();
			System.out.print("lprod_id 입력 : ");
			int input1 = scan.nextInt();
			String sql1 = " select * from lprod "+" where lprod_id > " + input1;
			rs = stmt.executeQuery(sql1);
			
			System.out.println("문제 1");
			while(rs.next()) {
				System.out.println("lprod_id : " + rs.getInt("lprod_id"));
				System.out.println("lprod_gu : " + rs.getString("lprod_gu"));
				System.out.println("lprod_nm : " + rs.getString("lprod_nm"));
				System.out.println("-------------------------------------");
			}
			System.out.println("문제 2");
			System.out.println("lprod_id 추가 입력 : ");
			int input2 = scan.nextInt();
			String sql2 = null;
			if(input1 > input2) {
				sql2 = " select * from lprod " + " where lprod_id > " + input2 + " AND " + " lprod_id < " + input1;
			}else {
				sql2 = " select * from lprod " + " where lprod_id > " + input1 + " AND " + " lprod_id < " + input2;
			}
			rs = stmt.executeQuery(sql2);
			while(rs.next()) {
				System.out.println("lprod_id : " + rs.getInt("lprod_id"));
				System.out.println("lprod_gu : " + rs.getString("lprod_gu"));
				System.out.println("lprod_nm : " + rs.getString("lprod_nm"));
				System.out.println("-------------------------------------");
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}catch(ClassNotFoundException e) {
			e.printStackTrace();
		}finally {
			if(rs != null) try {rs.close();}catch(SQLException e) {}
			if(stmt != null) try {stmt.close();}catch(SQLException e) {}
			if(conn != null) try {conn.close();}catch(SQLException e) {}
		}
		
	}
}
