import java.sql.*;
import java.util.Scanner;

public class TermProject {
    public static void main(String[] args) {
        try {
            Class.forName("com.mysql.cj.jdbc.Driver");
            Connection con = DriverManager.getConnection("jdbc:mysql://192.168.82.3:4567/access_control", "username", "password");
            Scanner scanner = new Scanner(System.in);

            while (true) {
                System.out.println("\n1. 사용자 추가");
                System.out.println("2. 사용자 삭제");
                System.out.println("3. 출입 기록 보기");
                System.out.println("4. 종료");
                System.out.print("메뉴를 선택하세요: ");

                int choice = scanner.nextInt();
                scanner.nextLine(); // Consume newline

                switch (choice) {
                    case 1:
                        addUser(con, scanner);
                        break;
                    case 2:
                        deleteUser(con, scanner);
                        break;
                    case 3:
                        showAccessLogs(con);
                        break;
                    case 4:
                        con.close();
                        scanner.close();
                        System.out.println("프로그램을 종료합니다.");
                        System.exit(0);
                    default:
                        System.out.println("올바른 메뉴를 선택하세요.");
                        break;
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static void addUser(Connection con, Scanner scanner) {
        try {
            System.out.print("사용자 이름 입력: ");
            String name = scanner.nextLine();
            System.out.print("사용자 ID 입력: ");
            String id = scanner.nextLine();

            PreparedStatement pstmt = con.prepareStatement("INSERT INTO Users (name, id) VALUES (?, ?)");
            pstmt.setString(1, name);
            pstmt.setString(2, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("사용자가 성공적으로 추가되었습니다.");
            } else {
                System.out.println("사용자 추가 실패");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void deleteUser(Connection con, Scanner scanner) {
        try {
            System.out.print("삭제할 사용자 ID 입력: ");
            String id = scanner.nextLine();

            PreparedStatement pstmt = con.prepareStatement("DELETE FROM Users WHERE id = ?");
            pstmt.setString(1, id);

            int rowsAffected = pstmt.executeUpdate();
            if (rowsAffected > 0) {
                System.out.println("사용자가 성공적으로 삭제되었습니다.");
            } else {
                System.out.println("사용자 삭제 실패");
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }

    public static void showAccessLogs(Connection con) {
        try {
            Statement stmt = con.createStatement();
            ResultSet rs = stmt.executeQuery("SELECT * FROM AccessLog");

            while (rs.next()) {
                System.out.println(rs.getString("username") + " - " + rs.getString("access_time"));
            }
        } catch (SQLException e) {
            System.out.println(e);
        }
    }
}
