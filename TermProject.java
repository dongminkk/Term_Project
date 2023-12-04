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
                scanner.nextLine(); 

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

            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM 사원 WHERE 이름 = ?");
            pstmt.setString(1, employeeName);

            ResultSet rs = pstmt.executeQuery();
            if (rs.next()) {
                System.out.println("사원 로그인 성공");
                String employeeNumber = rs.getString("사원번호"); 
                while (true) {
                    System.out.println("\n1. 들어감");
                    System.out.println("2. 나옴");
                    System.out.println("3. 나의 출입 기록 조회");
                    System.out.println("4. 로그아웃");
                    System.out.print("메뉴를 선택하세요: ");

                    int choice = scanner.nextInt();
                    scanner.nextLine(); 

                    if (choice == 1) {
                        recordAccess(con, employeeNumber, "in"); // '들어감' 선택 시 출입 기록 추가
                    } else if (choice == 2) {
                    	recordAccess(con, employeeNumber, "out"); // 나옴
                    } else if (choice == 3) {
                        showEmployeeAccessLogs(con, employeeNumber);
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

            PreparedStatement pstmt = con.prepareStatement("SELECT * FROM 관리자 WHERE 이름 = ?");
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
                    scanner.nextLine(); 

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

    public static void recordAccess(Connection con, String employeeNumber, String inOut) {
        try {
            PreparedStatement pstmt = con.prepareStatement("INSERT INTO 출입기록 (Eno, 출입시간, in_out) VALUES (?, NOW(), ?)");
            pstmt.setString(1, employeeNumber);
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



    public static void showEmployeeAccessLogs(Connection con, String employeeNumber) {
        try {
            PreparedStatement pstmt = con.prepareStatement("SELECT e.이름 AS 사원이름, e.성별, d.부서명, a.출입시간 FROM 출입기록 a " +
                                                            "INNER JOIN 사원 e ON a.Eno = e.사원번호 " +
                                                            "INNER JOIN 부서 d ON e.Dno = d.부서번호 " +
                                                            "WHERE a.Eno = ? ORDER BY a.출입시간 ASC");
            pstmt.setString(1, employeeNumber);
            ResultSet rs = pstmt.executeQuery();

            while (rs.next()) {
                System.out.println("사원 이름: " + rs.getString("사원이름") + " - 성별: " + rs.getString("성별") + 
                                   " - 부서명: " + rs.getString("부서명") + " - 출입 시간: " + rs.getString("출입시간"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }



    public static void showEmployees(Connection con) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT s.사원번호, s.이름, s.성별, d.부서명 FROM 사원 s JOIN 부서 d ON s.Dno = d.부서번호");

            while (rs.next()) {
                System.out.println("사원 이름: " + rs.getString("이름") + " - 사원 ID: " + rs.getString("사원번호") + 
                    " - 성별: " + rs.getString("성별") + " - 부서명: " + rs.getString("부서명"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
    // showAllEmployeeAccessLogs: 모든 사원의 출입 기록 확인 시간순
    public static void showAllEmployeeAccessLogs(Connection con) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT e.이름 AS 사원이름, a.출입시간 FROM 출입기록 a " +
                                             "INNER JOIN 사원 e ON a.Eno = e.사원번호 " +
                                             "ORDER BY a.출입시간 ASC");

            while (rs.next()) {
                System.out.println("사원: " + rs.getString("사원이름") + " - 출입 시간: " + rs.getString("출입시간"));
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
            System.out.print("추가할 부서 번호: ");
            String departmentId = scanner.nextLine();
            System.out.print("추가할 성별: ");
            String gender = scanner.nextLine();

            PreparedStatement pstmt = con.prepareStatement("INSERT INTO 사원 (이름, 사원번호, Dno, 성별) VALUES (?, ?, ?, ?)");
            pstmt.setString(1, name);
            pstmt.setString(2, id);
            pstmt.setString(3, departmentId);
            pstmt.setString(4, gender);

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
            System.out.print("변경할 부서 번호: ");
            String newDepartmentId = scanner.nextLine();
            System.out.print("변경할 성별: ");
            String newGender = scanner.nextLine();

            PreparedStatement pstmt = con.prepareStatement("UPDATE 사원 SET 이름 = ?, Dno = ?, 성별 = ? WHERE 사원번호 = ?");
            pstmt.setString(1, newName);
            pstmt.setString(2, newDepartmentId);
            pstmt.setString(3, newGender);
            pstmt.setString(4, id);

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

            PreparedStatement pstmt = con.prepareStatement("DELETE FROM 사원 WHERE 사원번호 = ?");
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
}