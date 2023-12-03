import java.sql.*;
import java.util.Scanner;

public class TermProject {
	public static void main(String[] args) {
		try {
			Class.forName("com.mysql.cj.jdbc.Driver");
			Connection con = DriverManager.getConnection("jdbc:mysql://192.168.82.3:4567/termdb", "root", "1234");
			Scanner scanner = new Scanner(System.in);

			while (true) {
				System.out.println("\n1. 사원 로그인");
				System.out.println("2. 관리자 로그인");
				System.out.println("3. 종료");
				System.out.print("메뉴를 선택하세요: ");

				int loginChoice = scanner.nextInt();
				scanner.nextLine(); // Consume newline

				if (loginChoice == 1) {
					employeeLogin(con, scanner);
				} else if (loginChoice == 2) {
					adminLogin(con, scanner);
				} else if (loginChoice == 3) {
					con.close();
					scanner.close();
					System.out.println("프로그램을 종료합니다.");
					System.exit(0);
				} else {
					System.out.println("올바른 메뉴를 선택하세요.");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public static void employeeLogin(Connection con, Scanner scanner) {
		try {
			System.out.print("사원 이름 입력: ");
			String employeeName = scanner.nextLine();

			PreparedStatement pstmt = con.prepareStatement("SELECT * FROM Employee WHERE name = ?");
			pstmt.setString(1, employeeName);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
		        System.out.println("사원 로그인 성공");
		        while (true) {
		            System.out.println("\n1. 들어감");
		            System.out.println("2. 나옴");
		            System.out.println("3. 나의 출입 기록 조회");
		            System.out.println("4. 로그아웃");
		            System.out.print("메뉴를 선택하세요: ");

		            int choice = scanner.nextInt();
		            scanner.nextLine(); // Consume newline

		            if (choice == 1) {
		                recordAccess(con, employeeName, "입"); // '들어감' 선택 시 출입 기록 추가
		            } else if (choice == 2) {
		                // 나옴 기능 구현
		            } else if (choice == 3) {
		                showEmployeeAccessLogs(con, employeeName);
		            } else if (choice == 4) {
		                System.out.println("로그아웃합니다.");
		                break;
		            } else {
		                System.out.println("올바른 메뉴를 선택하세요.");
		            }
		        }
		    } else {
		        System.out.println("사원 이름이 올바르지 않습니다.");
		    }
		
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public static void adminLogin(Connection con, Scanner scanner) {
		try {
			System.out.print("관리자 이름 입력: ");
			String adminName = scanner.nextLine();

			PreparedStatement pstmt = con.prepareStatement("SELECT * FROM Admin WHERE name = ?");
			pstmt.setString(1, adminName);

			ResultSet rs = pstmt.executeQuery();
			if (rs.next()) {
				System.out.println("관리자 로그인 성공");
				while (true) {
					System.out.println("\n1. 사원 조회");
					System.out.println("2. 모든 사원 출입기록 확인");
					System.out.println("3. 사원 추가");
					System.out.println("4. 사원 정보 변경");
					System.out.println("5. 사원 삭제");
					System.out.println("6. 로그아웃");
					System.out.print("메뉴를 선택하세요: ");

					int choice = scanner.nextInt();
					scanner.nextLine(); // Consume newline

					switch (choice) {
					case 1:
						showEmployees(con);
						break;
					case 2:
						showAllEmployeeAccessLogs(con);
						break;
					case 3:
						addEmployee(con, scanner);
						break;
					case 4:
						updateEmployee(con, scanner);
						break;
					case 5:
						deleteEmployee(con, scanner);
						break;
					case 6:
						System.out.println("로그아웃합니다.");
						return;
					default:
						System.out.println("올바른 메뉴를 선택하세요.");
						break;
					}
				}
			} else {
				System.out.println("관리자 이름이 올바르지 않습니다.");
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public static void recordAccess(Connection con, String employeeName) {
		try {
			PreparedStatement pstmt = con
					.prepareStatement("INSERT INTO AccessLog (username, access_time) VALUES (?, NOW())");
			pstmt.setString(1, employeeName);

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("출입 기록이 성공적으로 저장되었습니다.");
			} else {
				System.out.println("출입 기록 저장 실패");
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	public static void showEmployeeAccessLogs(Connection con, String employeeName) {
		try {
			PreparedStatement pstmt = con.prepareStatement("SELECT * FROM AccessLog WHERE username = ?");
			pstmt.setString(1, employeeName);
			ResultSet rs = pstmt.executeQuery();

			while (rs.next()) {
				System.out.println("사원: " + rs.getString("username") + " - 출입 시간: " + rs.getString("access_time"));
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	// showEmployees: 모든 사원 조회
	public static void showEmployees(Connection con) {
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM Employee");

			while (rs.next()) {
				System.out.println("사원 이름: " + rs.getString("name") + " - 사원 ID: " + rs.getString("id"));
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	// showAllEmployeeAccessLogs: 모든 사원의 출입 기록 확인
	public static void showAllEmployeeAccessLogs(Connection con) {
		try {
			Statement stmt = con.createStatement();
			ResultSet rs = stmt.executeQuery("SELECT * FROM AccessLog");

			while (rs.next()) {
				System.out.println("사원: " + rs.getString("username") + " - 출입 시간: " + rs.getString("access_time"));
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	// addEmployee: 사원 추가
	public static void addEmployee(Connection con, Scanner scanner) {
		try {
			System.out.print("추가할 사원 이름: ");
			String name = scanner.nextLine();
			System.out.print("추가할 사원 ID: ");
			String id = scanner.nextLine();

			PreparedStatement pstmt = con.prepareStatement("INSERT INTO Employee (name, id) VALUES (?, ?)");
			pstmt.setString(1, name);
			pstmt.setString(2, id);

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("사원이 성공적으로 추가되었습니다.");
			} else {
				System.out.println("사원 추가 실패");
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	// updateEmployee: 사원 정보 변경
	public static void updateEmployee(Connection con, Scanner scanner) {
		try {
			System.out.print("변경할 사원 ID 입력: ");
			String id = scanner.nextLine();
			System.out.print("변경할 이름 입력: ");
			String newName = scanner.nextLine();

			PreparedStatement pstmt = con.prepareStatement("UPDATE Employee SET name = ? WHERE id = ?");
			pstmt.setString(1, newName);
			pstmt.setString(2, id);

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("사원 정보가 성공적으로 변경되었습니다.");
			} else {
				System.out.println("사원 정보 변경 실패");
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}

	// deleteEmployee: 사원 삭제
	public static void deleteEmployee(Connection con, Scanner scanner) {
		try {
			System.out.print("삭제할 사원 ID 입력: ");
			String id = scanner.nextLine();

			PreparedStatement pstmt = con.prepareStatement("DELETE FROM Employee WHERE id = ?");
			pstmt.setString(1, id);

			int rowsAffected = pstmt.executeUpdate();
			if (rowsAffected > 0) {
				System.out.println("사원이 성공적으로 삭제되었습니다.");
			} else {
				System.out.println("사원 삭제 실패");
			}
		} catch (SQLException e) {
			System.out.println(e);
		}
	}
	public static void recordAccess(Connection con, String employeeName, String inOut) {
	    try {
	        PreparedStatement pstmt = con.prepareStatement("INSERT INTO AccessLog (username, access_time, in_out) VALUES (?, NOW(), ?)");
	        pstmt.setString(1, employeeName);
	        pstmt.setString(2, inOut);

	        int rowsAffected = pstmt.executeUpdate();
	        if (rowsAffected > 0) {
	            System.out.println("출입 기록이 성공적으로 저장되었습니다.");
	        } else {
	            System.out.println("출입 기록 저장 실패");
	        }
	    } catch (SQLException e) {
	        System.out.println(e);
	    }
	}

}
