package kr.or.ddit.basic;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Scanner;

import kr.or.ddit.util.DBUtil;

/*
 * LPROD 테이블에 새로운 데이터 추가하기
 * 
 * lprod_gu와 lprod_nm은 직접 입력받아 처리하고,
 * lprod_id는 현재의 lprod_id들 중 제일 큰값보다 1증가된 값으로 한다.
 * (기타사항 : lprod_gu도 중복되는지 검사한다.)
 */

public class T04_JdbcTest {

	public static void main(String[] args) {
		
		Scanner scan = new Scanner(System.in);
		Connection conn = null;
		ResultSet rs = null;
		Statement stmt = null;
		PreparedStatement pstmt = null;
		int cnt = 0;
		
		try {
			/*
			//1. 드라이버 로딩
			Class.forName("oracle.jdbc.driver.OracleDriver");
			
			//2. DB에 접속 (Connection객체 생성)
			String url = "jdbc:oracle:thin:@localhost:1521/xe";
			String userId = "WONWOO";
			String password = "java";
			
			conn = DriverManager.getConnection(url, userId, password);
			*/
			conn = DBUtil.getConnection();
			
			stmt = conn.createStatement();
			
			int num = 0;
			String gu = null;
			
			while(true) {
				
				String sql = "select max(lprod_id) from lprod";
				
				rs = stmt.executeQuery(sql);
				
				if(rs.next()) {
					num = rs.getInt(1);
				}
				num++;
				
				System.out.print("lprod_gu 입력 : ");
				gu = scan.nextLine();
				sql= " select * from lprod " + " where lprod_gu = " + "'"+gu+"'";
				
				/*SELECT *
				  FROM lprod
				  WHERE lprod_gu = 'N101';*/
				
				rs = stmt.executeQuery(sql);
				while(rs.next()) {
					System.out.println(rs.getString("lprod_gu"));
					cnt++;
				}
				if(cnt != 0) {
					System.out.println("입력한 lprod_gu 값은 중복됩니다.");
					break;
				} 
				
				System.out.println("입력한 lprod_gu 값은 중복되지 않습니다.");
				
				System.out.print("lprod_nm 입력 : ");
				String nm = scan.nextLine();
				
				String sql1 = " insert into lprod " + " values (?,?,?)";
				pstmt = conn.prepareStatement(sql1);
				
				pstmt.setInt(1, num);
				pstmt.setString(2, gu);
				pstmt.setString(3, nm);
				
				int cnt1 = pstmt.executeUpdate();
				
				
				if(cnt1>0) {
					System.out.println(gu + "를 추가했습니다.");
				}else {
					System.out.println(gu + "를 추가하는데 실패했습니다.");
				}
				
			}
			
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			if(stmt != null) try {stmt.close();}catch(SQLException e) {}
			if(pstmt != null) try {pstmt.close();}catch(SQLException e) {}
			if(conn != null) try {conn.close();}catch(SQLException e) {}
		}
		
		
	}
	
}
