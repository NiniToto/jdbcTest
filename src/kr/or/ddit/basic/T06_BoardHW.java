package kr.or.ddit.basic;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.SQLSyntaxErrorException;
import java.sql.Statement;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Scanner;

import kr.or.ddit.util.DBUtil;

/*
 * 기능 구현하기 ==> 전체 목록 출력, 새글작성, 수정, 삭제, 검색 
 * 시퀀스의 다음 값 구하기
 * 시퀀스이름.nextVal
 */
public class T06_BoardHW {

	private Connection conn;
	private Statement stmt;
	private ResultSet rs;
	private PreparedStatement pstmt;
	
	private Scanner scan;
	
	public T06_BoardHW() {
		conn = null;
		stmt = null;
		rs = null;
		pstmt = null;
		
		scan = new Scanner(System.in);
	}
	
	private void showMenu() {
		System.out.println("============================================");
		System.out.println("================== 게 시 판 ====================");
		System.out.println("--------------------------------------------");
		System.out.println("1. 새 글 작성");
		System.out.println("2. 글 수정");
		System.out.println("3. 글 삭제");
		System.out.println("4. 글 검색");
		System.out.println("5. 전체 목록 출력");
		System.out.println("0. 시스템 종료");
		System.out.println("============================================");
		System.out.print("원하는 작업 선택>> ");
		System.out.println();
	}
	
	
	public static void main(String[] args) {
		new T06_BoardHW().start();
	
	}
	
	public void start() {
		
		while(true) {
			showMenu(); //메뉴 출력
			
			int input = scan.nextInt();
			
			switch(input) {
				case 1 : 
				write();//새 글 작성
				break;
				
				case 2 : 
				modify(); //글 수정
				break;
				
				case 3 : 
				delete();	//글 삭제
				break;
				
				case 4 : 
				search();	//글 검색
				break;
				
				case 5 : 
				printOut();	//전체 목록 출력
				break;
				
				case 0 : 
				System.out.println("시스템을 종료합니다.");
				System.out.println("감사합니다.");
				System.exit(0);
				break;
				
				default : 
					System.out.println("잘못된 번호를 입력하셨습니다.");
					System.out.println("다시 입력하세요.");
			}
		}	
	}
	public void write() {
		
		boolean chk = false;
		String title = null;
		String name = null;
		String content = null;
		
		System.out.println("새로운 게시글을 작성해주세요! ");
		
		System.out.println("작성할 게시글 제목을 정해주세요>> ");
		title = scan.next();
		scan.nextLine();
		
		System.out.println("작성자의 이름을 정해주세요>> ");
		name = scan.next();
		scan.nextLine();
		
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
		
		System.out.println("작성할 게시글 내용을 입력해주세요>> ");
		content = scan.next();
		scan.nextLine();
		try {
			conn = DBUtil.getConnection();
			
			String sql = "insert into jdbc_board " + " values (board_seq.NEXTVAL,?,?," + "?,?)";
			/*TO_CHAR(SYSDATE, 'YYYY/MM/DD HH24:MI:SS')*/	
			pstmt = conn.prepareStatement(sql);
				
			pstmt.setString(1, title);
			pstmt.setString(2, name);
			pstmt.setString(3, sdf.format(date));
			pstmt.setString(4, content);

			int cnt = pstmt.executeUpdate();
				
			if(cnt > 0) {
				System.out.println(title + " 제목의 게시글이 입력되었습니다.");
			}else {
				System.out.println(title + " 제목의 게시글 입력 실패!!!");
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			disConnect();
		}
	}

	public void modify() {
		boolean flag = false;
		try {
			conn = DBUtil.getConnection();
			
			String sql = "select * from jdbc_board " + " where board_no in (?)";
			String sqlSet = " update jdbc_board set board_title = ?, board_content = ? where board_no = ?";

			while(true) {
				System.out.print("수정할 글의 번호를 입력해주세요>> ");
				//나가기
				int num = scan.nextInt();
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				
				rs = pstmt.executeQuery();
				
				while(rs.next()) {
					System.out.println("게시글 번호 \t:\t " + rs.getInt("BOARD_NO"));
					System.out.println("기존 게시글 제목\t:\t" + rs.getString("BOARD_TITLE"));
					System.out.println("게시글 작성자\t:\t" + rs.getString("BOARD_WRITER"));
					System.out.println("게시글 작성일자\t:\t" + rs.getString("BOARD_DATE"));
					System.out.println("게시글 내용\t:\t" + rs.getString("BOARD_CONTENT"));
					System.out.println();
				}
			
					pstmt = null;
					
					System.out.print("수정할 게시글 제목을 입력해주세요>> ");
					String title = scan.next();
					scan.nextLine();
					
					System.out.print("수정할 게시글 내용을 입력해주세요>> ");
					String content = scan.nextLine();
					
					pstmt = conn.prepareStatement(sqlSet);
					pstmt.setString(1, title);
					pstmt.setString(2, content);
					pstmt.setInt(3, num);
					
					int check = pstmt.executeUpdate();
				
					System.out.println("수정이 완료되었습니다.");
					flag = true;
					if(flag) {break;}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			disConnect();
		}
	}

	/**
	 * 글 삭제
	 */
	public void delete() {
		
		try {
			conn = DBUtil.getConnection();
			
			String sql = "select * from jdbc_board " + " where board_no in (?)";
			String sqlDel = " delete from jdbc_board " + " where board_no = ? ";
			while(true) {
				System.out.print("삭제할 글의 번호를 입력해주세요>> ");
				int num = scan.nextInt();
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				
				rs = pstmt.executeQuery();
				
			
				while(rs.next()) {
					System.out.println("게시글 번호\t:\t" + rs.getInt("BOARD_NO"));
					System.out.println("게시글 제목\t:\t" + rs.getString("BOARD_TITLE"));
					System.out.println("게시글 작성자\t:\t" + rs.getString("BOARD_WRITER"));
					System.out.println("게시글 작성일자\t:\t" + rs.getString("BOARD_DATE"));
					System.out.println("게시글 내용\t:\t" + rs.getString("BOARD_CONTENT"));
					System.out.println();
					System.out.println("해당 글을 정말 삭제하시겠습니까?");
					System.out.println("1.YES\t2.NO");
					int input1 = scan.nextInt();
					
					if(input1 == 1) {
						pstmt = conn.prepareStatement(sqlDel);
						pstmt.setInt(1, num);
						
						int check = pstmt.executeUpdate();
						System.out.println(num + "번의 게시글이 삭제 되었습니다.");
					}else{
						break;
					}
				}
				break;
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			disConnect();
		}
	}

	/**
	 * 글 검색
	 */
	public void search() {
		
		try {
			conn = DBUtil.getConnection();
			
			String sql = "select * from jdbc_board " + "where board_no in (?)";

			while(true) {
				System.out.print("검색할 글의 번호를 입력해주세요>> ");
				int num = scan.nextInt();
				
				pstmt = conn.prepareStatement(sql);
				pstmt.setInt(1, num);
				
				rs = pstmt.executeQuery();
				boolean flag = false;
				
				while(rs.next()) {
					System.out.println("게시글 번호 \t" + rs.getInt("BOARD_NO"));
					System.out.println("게시글 제목:\t" + rs.getString("BOARD_TITLE"));
					System.out.println("게시글 작성자\t" + rs.getString("BOARD_WRITER"));
					System.out.println("게시글 작성일자\t" + rs.getString("BOARD_DATE"));
					System.out.println("게시글 내용\t" + rs.getString("BOARD_CONTENT"));
					flag = true;
				}
				if(flag) {break;}
			}
		}catch(SQLException e) {
			e.printStackTrace();
		}finally {
			disConnect();
		}
		
	}

	/**
	 * 전체 목록 출력
	 */
	public void printOut() {
		
		try {
			conn = DBUtil.getConnection();
			
			stmt = conn.createStatement();
			
			String sql = "select * from jdbc_board";
			
			rs = stmt.executeQuery(sql);
			boolean flag = false;
			
			while(rs.next()) {
				System.out.println("==========================================");
				System.out.println("게시글 번호 : " + rs.getInt("BOARD_NO"));
				System.out.println("게시글 제목 : " + rs.getString("BOARD_TITLE"));
				System.out.println("게시글 작성자 : " + rs.getString("BOARD_WRITER"));
				System.out.println("게시글 작성일자 : " + rs.getString("BOARD_DATE"));
				System.out.println("게시글 내용 : " + rs.getString("BOARD_CONTENT"));
				System.out.println("==========================================");
				flag = true;
			}
			if(!flag) {
				System.out.println("해당 게시글이 없습니다.");
			}
			
		}catch(SQLException e){
			e.printStackTrace();
		}finally {
			disConnect();//자원 반납
		}
	}
	public void disConnect() {
		
		if(rs != null) try {rs.close();}catch(SQLException e) {}
		if(stmt != null) try {stmt.close();}catch(SQLException e) {}
		if(pstmt != null) try {pstmt.close();}catch(SQLException e) {}
		if(conn != null) try {conn.close();}catch(SQLException e) {}
		
	}
}
