import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.nio.charset.StandardCharsets;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.Statement;

public class MainClass {
	static BufferedWriter fw;
	
	public static void main(String[] str) {
		try {
			Connection con = null;
			Statement stmt = null;
			Class.forName("org.sqlite.JDBC");
			con = DriverManager.getConnection("jdbc:sqlite:assets/stub1");
			con.setAutoCommit(false);
			System.out.println("Opened database successfully");
			
			stmt = con.createStatement();
			PreparedStatement deleteStmt = con.prepareStatement("DELETE FROM TAMILPROVERB");
			deleteStmt.execute();
			con.commit();
			deleteStmt.close();
			openLogFile();
			BufferedReader reader = new BufferedReader(new InputStreamReader(new FileInputStream("resource/TamilProverbs.txt"),
					StandardCharsets.UTF_8));
			String line = null;
			int sno = 1;
			while ((line = reader.readLine()) != null) {
				String proverb = line.substring(0,line.indexOf("#")-1);
				String proverbMeaning = line.substring(line.indexOf("#")+2,line.length());
				
				PreparedStatement insertStmt = con
						.prepareStatement("INSERT INTO TAMILPROVERB(_ID,PROVERB,MEANING) VALUES(?, ?, ?)");
				insertStmt.setInt(1, sno);
				insertStmt.setString(2, proverb);
				insertStmt.setString(3, proverbMeaning);
				insertStmt.execute();
				sno++;
			}
			reader.close();
			con.commit();
			
			//checking what is inserted
			stmt = con.createStatement();
			
			ResultSet rs = stmt.executeQuery( "select * from TAMILPROVERB;" );
			while ( rs.next() )
			{
				sno = rs.getInt("_id");
				String  word = rs.getString("proverb");
				String  meaning = rs.getString("meaning");
				writeLogToFile( sno +" " + word + "-->" + meaning);
			}
			closeLogFile();
			
			rs.close();
			stmt.close();
			con.commit();
			con.close();
		} catch (Exception e) {
			System.out.println("Exception: " + e.getMessage());
		}
	}
	
	public static void openLogFile() {
		try {
			fw = new BufferedWriter(new OutputStreamWriter(new FileOutputStream("logs/ProverbsFromDb.txt"), StandardCharsets.UTF_8));
		} catch (Exception e) {
			System.out.println("exception: " + e.getMessage());
		}
	}
	
	public static void closeLogFile() {
		try {
			fw.close();
		} catch (Exception e) {
			System.out.println("exception: " + e.getMessage());
		}
	}
	
	public static void writeLogToFile(String str) {
		try {
			fw.write(str);
			fw.newLine();
		} catch (Exception e) {
			// TODO: handle exception
		}
	}
}