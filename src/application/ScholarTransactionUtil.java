package application;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;

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
	private ResultSet rss;
	
	
	
	
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
		
		
		//passed as parameter how???//it may be two same methods in Utils 
		
		
		ObservableList<ScholarTransaction> scholarTransactionList = FXCollections.observableArrayList();
		connection = DBConnection.getConnection();
		stmt = connection.createStatement();
		rs = stmt.executeQuery(sql);
		
		String year = "2025";
		String month = "December" ;
		
		//universityId, year, Month
		
		//query hmar tan yay
		while(rs.next()) {
			
			
			/*check condition here
			 * -get student id and date and month passed
			 * -checked in transaction tb
			 * -that student is in transaction tb of Specified date
			 * -
			*/
		
			//if(!checkInTransactionDatabase(rs.getInt("student_id"),year,month)) {//check with greater than 
		
				System.out.println(rs.getInt("student_id"));
				scholarTransactionList.add(new ScholarTransaction(
						rs.getInt("student_id"), 
						rs.getString("name"), 
						rs.getString("nrc"), 
						new TextField(), 
						new CheckBox()
						));
		//	}//end of if
			
			
			
					
			
		}
		
		System.out.println(scholarTransactionList);
		return scholarTransactionList;
		
	}
	
	//newly added no-2
	public ObservableList<ScholarTransaction> getScholarTransactionList(Integer universtiyId,String year, String month) throws SQLException {

		// passed as parameter how???//it may be two same methods in Utils

		ObservableList<ScholarTransaction> scholarTransactionList = FXCollections.observableArrayList();
		connection = DBConnection.getConnection();
		stmt = connection.createStatement();
		ArrayList<Integer> arraylist = new ArrayList<Integer>();
		ArrayList<Integer> idList = new ArrayList<Integer>();
		
		
		//loop
		rs = stmt.executeQuery(
				"select  ss.student_id,ss.name from students ss where ss.student_id not in( select s.student_id FROM students s,enrollments e, transactions t  where e.university_id='"+universtiyId+"' and e.is_active='1' and taken_out_at  like'"+year+"-"+getMonthNumber(month)+"-%'  and s.student_id= e.student_id and s.student_id= t.student_id);");
		
		while(rs.next()) {
			arraylist.add(rs.getInt("student_id"));
		}
		
		
		for(int i =0;i<arraylist.size();i++) {
			rs =stmt.executeQuery("SELECT student_id FROM enrollments where university_id='"+universtiyId+"' and student_id='"+arraylist.get(i)+"';");
			if(rs.next()) {
				idList.add(rs.getInt("student_id"));
			}
		}
		
		
		for(int i=0;i<idList.size();i++) {
			rs= stmt.executeQuery("SELECT * FROM students where student_id='"+idList.get(i)+"';");
			if(rs.next()) {
				scholarTransactionList.add(
						new ScholarTransaction(
								rs.getInt("student_id"),
								rs.getString("name"),
								rs.getString("nrc"),
								new TextField(),
								new CheckBox()
								));
			}
		}
		
		System.out.println(arraylist);
		System.out.println(idList);

		System.out.println(scholarTransactionList);
		return scholarTransactionList;

	}
	
	
	
	//newly added no-3
	public ObservableList<ScholarTransaction> getScholarTransactionList(Integer universtiyId,String year, String month,Integer attendanceYearId) throws SQLException {

		ObservableList<ScholarTransaction> scholarTransactionList = FXCollections.observableArrayList();
		connection = DBConnection.getConnection();
		stmt = connection.createStatement();
		ArrayList<Integer> arraylist = new ArrayList<Integer>();
		ArrayList<Integer> idList = new ArrayList<Integer>();
		
		
		//step-1 get
		rs = stmt.executeQuery(
				"select  ss.student_id,ss.name from students ss where ss.student_id not in( select s.student_id FROM students s,enrollments e, transactions t  where e.university_id='"+universtiyId+"' and e.is_active='1' and taken_out_at  like'"+year+"-"+getMonthNumber(month)+"-%'  and s.student_id= e.student_id and s.student_id= t.student_id );");
		
		while(rs.next()) {
			arraylist.add(rs.getInt("student_id"));
		}
		
		
		for(int i =0;i<arraylist.size();i++) {
			rs =stmt.executeQuery("SELECT student_id FROM enrollments where university_id='"+universtiyId+"' and student_id='"+arraylist.get(i)+"' and attendance_year_id='"+attendanceYearId+"';");
			if(rs.next()) {
				idList.add(rs.getInt("student_id"));
			}
		}
		
		
		for(int i=0;i<idList.size();i++) {
			rs= stmt.executeQuery("SELECT * FROM students where student_id='"+idList.get(i)+"';");
			if(rs.next()) {
				scholarTransactionList.add(
						new ScholarTransaction(
								rs.getInt("student_id"),
								rs.getString("name"),
								rs.getString("nrc"),
								new TextField(),
								new CheckBox()
								));
			}
		}
		
		System.out.println(arraylist);
		System.out.println(idList);

		System.out.println(scholarTransactionList);
		return scholarTransactionList;

	}
	
	
	
	
	public boolean checkInTransactionDatabase(Integer studentId, String year, String month) throws SQLException {

	//	boolean isExist = false;
		connection = DBConnection.getConnection();
		stmt = connection.createStatement();
		rs = stmt.executeQuery("select * from transactions where student_id='"+studentId+"' and taken_out_at LIKE '"+year+"-"+getMonthNumber(month)+"-%';");
		
		return rs.next();
	
//		if(rs.next()==false) {
//			isExist = true;
//		}
//	//	SELECT taken_out_at FROM edulink.transactions WHERE taken_out_at LIKE '2015-12-%';
//		
//		//connection.close();
//		return isExist;
		
	}
	
	//newly added no 1
	public String getMonthNumber(String month) {
		
		
	//	String dateString="";
		
		switch (month) {
		case "January":
			month = "01";
			break;
		case "February":
			month = "02";
			break;
		case "March":
			month = "03";
			break;
		case "April":
			month = "04";
			break;
		case "May":
			month = "05";
			break;
		case "Jun":
			month = "06";
			break;
		case "July":
			month = "07";
			break;
		case "August":
			month = "08";
			break;
		case "September":
			month = "09";
			break;
		case "October":
			month = "10";
			break;
		case "November":
			month = "11";
			break;
		case "December":
			month = "12";
			break;
	}
		
		
		return month;
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
		
		//insert scholartransaction of after checked payment
		//(after paying scholarship)
		public int insertScholarTransactionDate(ScholarTransaction scholarTransaction,String year, String month) throws SQLException {
			connection = DBConnection.getConnection();

			var rowUpdated = 0;
			pStmt = connection.prepareStatement("INSERT INTO `transactions` (`student_id`, `taken_out_at`, `remark`) VALUES (?, ?, ?);"

					);
			
			//LocalDate date=LocalDate.now();
			//Date publishedDate = Date.valueOf(date);
			
			DateTimeFormatter dateFormat = DateTimeFormatter.ofPattern("d/MM/yyyy");
			
			LocalDate local_date_1 = LocalDate.parse("01/"+getMonthNumber(month)+"/"+year, dateFormat);
			Date payDate =Date.valueOf(local_date_1);
			 
			pStmt.setInt(1,  scholarTransaction.getStudentId());
			pStmt.setDate(2, payDate);
			pStmt.setNString(3, scholarTransaction.getRemark().getText());
			
		
			rowUpdated =pStmt.executeUpdate();
			return rowUpdated;
		}
		
		
		
				
		
}
