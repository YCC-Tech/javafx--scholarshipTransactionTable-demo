package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;

import database.DBConnection;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class ScholarTransactionUtil {
	
	
	
	
	private Connection connection;
	private PreparedStatement pStmt;
	private Statement stmt;
	private ResultSet rs;
	
	
	
	
	//get academic year list from db to major combo box filtered by university id
		public ObservableList<String> getAcademicYearList(String sql) throws SQLException{
			ObservableList<String> majorList = FXCollections.observableArrayList();
			connection = DBConnection.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery(sql);
			
			while(rs.next()) {
				majorList.add(rs.getString("name")
						);
			}
			System.out.println(majorList);
			return majorList;
			
		}
		//uni short name to show in scholar transaction page
		public String getUniversityShortName(Integer universityId) throws SQLException {
			String uniShortName = "";
			connection = DBConnection.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select short_name from universities where university_id='"+universityId+"';");
			while(rs.next()) {
				uniShortName=rs.getString("short_name");
				
			}
			return uniShortName;
		}
		
		
		//uni long name to show in scholar transaction page
		public String getUniversityLongName(Integer universityId) throws SQLException {
			String uniLongName = "";
			connection = DBConnection.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("select name from universities where university_id='"+universityId+"';");
			while(rs.next()) {
				uniLongName=rs.getString("name");
				
			}
			return uniLongName;
		}
		
		//get scholar amount filtered by university id and attendance year name
		public Integer getScholarAmount(Integer universityId, String attendanceYearName) throws SQLException {
			Integer scholarAmt = 0;
			Integer attendanceYearId= 0;
			connection = DBConnection.getConnection();
			stmt = connection.createStatement();
			
			//first get year id by academic year name and uni Id
			
			rs = stmt.executeQuery("select attendance_year_id from attendance_years where name='"+attendanceYearName+"' and university_id='"+universityId+"';");
			while(rs.next()) {
				attendanceYearId=rs.getInt("attendance_year_id");
				
			}

			//System.out.println("attendanceYearId"+attendanceYearId+"\nUniversityId"+universityId);
			
			//get scholar amount of with uni+year
			rs = stmt.executeQuery("select amount from scholarship_amounts where university_id='"+universityId+"' and attendance_year_id='"+attendanceYearId+"';");
			while(rs.next()) {
				scholarAmt=rs.getInt("amount");
				
			}
			return scholarAmt;
		}
		
		
		
		//get student list of unchecked payment
		// it is still needed to check condition
	public ObservableList<ScholarTransaction> getScholarTransactionList(String sql) throws SQLException{
		ObservableList<ScholarTransaction> scholarTransactionList = FXCollections.observableArrayList();
		connection = DBConnection.getConnection();
		stmt = connection.createStatement();
		rs = stmt.executeQuery(sql);
		
		while(rs.next()) {
			
			
			/*check condition here
			 * -that student is in transaction tb
			 * -
			*/
			
			
				scholarTransactionList.add(new ScholarTransaction(
						rs.getInt("student_id"), 
						rs.getString("name"), 
						rs.getString("nrc"), 
						new TextField(), 
						new CheckBox()
						));
			
					
			
		}
		
		System.out.println(scholarTransactionList);
		return scholarTransactionList;
		
	}
	
	//get academic year id from db according its name and university id
		public int getAcademicYearId(String academicYearName, Integer universityId) throws SQLException {
			int academicYearId= 0;
			connection = DBConnection.getConnection();
			stmt = connection.createStatement();
			rs = stmt.executeQuery("SELECT attendance_year_id FROM attendance_years where university_id='"+universityId+"' and name='"+academicYearName+"';");

			while(rs.next()) {
				academicYearId=rs.getInt("attendance_year_id");
				
			}
			return academicYearId;
		}
		
		
		//update date of after checked payment
		//(after paying scholarship)
		public int updateScholarTransactionDate(ScholarTransaction scholarTransaction) throws SQLException {
			connection = DBConnection.getConnection();

			var rowUpdated = 0;
			pStmt = connection.prepareStatement("UPDATE `transactions` SET "
					
					+ "`taken_out_at` = ? "
					+ "WHERE (`student_id` = ?);"
					);
			
			LocalDate date=LocalDate.now();
			Date publishedDate = Date.valueOf(date);
			pStmt.setDate(1, publishedDate);
			pStmt.setInt(2, scholarTransaction.getStudentId());
			
		
			rowUpdated =pStmt.executeUpdate();
			return rowUpdated;
		}
		
		
		//update scholarship description for each student
		public int updateScholarTransactionDescription(ScholarTransaction scholarTransaction) throws SQLException {
			connection = DBConnection.getConnection();

			var rowUpdated = 0;
			pStmt = connection.prepareStatement("UPDATE `transactions` SET "
					
					+ "`remark` = ? "
					+ "WHERE (`student_id` = ?);"
					);
			
			
			pStmt.setString(1, scholarTransaction.getRemark().getText());
			pStmt.setInt(2, scholarTransaction.getStudentId());
			
		
			rowUpdated =pStmt.executeUpdate();
			return rowUpdated;
		}
		
}
