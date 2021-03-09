package application;

import java.net.URL;
import java.sql.SQLException;
import java.util.ResourceBundle;

import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.CheckBox;
import javafx.scene.control.ComboBox;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.input.MouseEvent;

public class ScholarTransactionController implements Initializable{
	
	
    @FXML
    private TableView<ScholarTransaction> scholarTransactionTable;
    
    @FXML
    private TableColumn<ScholarTransaction, Integer> studentId;

    @FXML
    private TableColumn<ScholarTransaction, String> name;

    @FXML
    private TableColumn<ScholarTransaction, String>  nrc;

    @FXML
    private TableColumn<ScholarTransaction, TextField> remark;

    @FXML
    private TableColumn<ScholarTransaction, CheckBox> withdrawStatus;

    @FXML
    private Button btnConfirm;

    @FXML
    private Label lblUniShortName;

    @FXML
    private ComboBox<String> cobAcademicYear;


    @FXML
    private Label lblUniLongName;

    @FXML
    private Label lblAmount;
    
    String year;
    String month;
    
    
    ScholarTransactionUtil scholarTransactionUtil = new ScholarTransactionUtil();

    //process in combo box is slow ( additional click is needed to get combo value)
    
    
    @FXML
    void processConfirm(ActionEvent event) throws SQLException {
    	//change date;
    	//update description
    	
		ObservableList<ScholarTransaction> checkedScholarTransactionList = FXCollections.observableArrayList();

		for (ScholarTransaction scholarTransaction : scholarTransactionTable.getItems()) {
			if (scholarTransaction.getWithdrawStatus().isSelected()) {
				//insert scholar transaction to transaction tb
				scholarTransactionUtil.insertScholarTransactionDate(scholarTransaction,year, month);
				// update text field
			//	scholarTransactionUtil.insertScholarTransactionDescription(scholarTransaction,year,month);
				//update 
				
				
				checkedScholarTransactionList.add(scholarTransaction);

			}

		}

		scholarTransactionTable.getItems().removeAll(checkedScholarTransactionList);

    }
    
    
    
    
    
  //  private void showScholarTransactions(String sql) {
    
    
    //--change sql to parameter
        private void showScholarTransactions(Integer universityId, String year, String month) {

    	
    	studentId.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, Integer>("studentId"));
    	
    	name.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, String>("name"));
    	
    	nrc.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, String>("nrc"));
    	
    	remark.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, TextField>("remark"));
    	
    	withdrawStatus.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, CheckBox>("withdrawStatus"));
		
		
			
			try {
				scholarTransactionTable.setItems(scholarTransactionUtil.getScholarTransactionList(universityId,year,month));
			} catch (SQLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		

    }
        
       //new add no1 
		private void showScholarTransactions(Integer universityId, String year, String month,Integer attendanceYearId) {

        	
        	studentId.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, Integer>("studentId"));
        	
        	name.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, String>("name"));
        	
        	nrc.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, String>("nrc"));
        	
        	remark.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, TextField>("remark"));
        	
        	withdrawStatus.setCellValueFactory(new PropertyValueFactory<ScholarTransaction, CheckBox>("withdrawStatus"));
    		
    		
    			
    			try {
    				scholarTransactionTable.setItems(scholarTransactionUtil.getScholarTransactionList(universityId, year, month, attendanceYearId));
    			} catch (SQLException e) {
    				// TODO Auto-generated catch block
    				e.printStackTrace();
    			}
    		

        }
	@Override
	public void initialize(URL arg0, ResourceBundle arg1) {
		
		//please change university Id carried from previous page
		int universityId = 1;
		 year = "2015";
		 month = "March";
		
		//show data before chosing academic year;
		//showScholarTransactions("select * from enrollments where university_id='"+universityId+"' and is_active='1'; ");
		
		//carry year and date parameter
		//showScholarTransactions("select s.student_id, s.name, s.nrc ,e.university_id FROM students s,enrollments e where e.university_id='"+universityId+"' and e.is_active='1' and s.student_id= e.student_id;");
	
		showScholarTransactions(universityId, year, month);

		//showScholarTransactions("SELECT * FROM students;");
		
		
		//setting short name of uni
		try {
			lblUniShortName.setText(scholarTransactionUtil.getUniversityShortName(universityId));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//setting long name of uni
		try {
			lblUniLongName.setText(scholarTransactionUtil.getUniversityLongName(universityId));
		} catch (SQLException e1) {
			// TODO Auto-generated catch block
			e1.printStackTrace();
		}
		
		
		//setting major according to uniId carried from previous page
		try {
			cobAcademicYear.setItems(scholarTransactionUtil.getAcademicYearList("select name from attendance_years where university_id='"+ universityId +"';"));
		} catch (SQLException e) {			
			e.printStackTrace();
		}
		
		
		cobAcademicYear.valueProperty().addListener(new ChangeListener<String>() {

			@Override
			public void changed(ObservableValue<? extends String> observableValue, String oldAcademicYear, String newAcademicYear) {
				
				try {
					int scholarAmt = scholarTransactionUtil.getScholarAmount(universityId, newAcademicYear);
					int academicYearId = scholarTransactionUtil.getAcademicYearId(newAcademicYear,universityId);
		
					System.out.println("academic year id"+academicYearId);
					lblAmount.setText(String.valueOf(scholarAmt));
					
					
					//show data after  choosing academic year;
					
//--change query to parameter
					
					//	showScholarTransactions("select s.student_id, s.name, s.nrc ,e.university_id FROM students s,enrollments e where e.university_id='"+universityId+"' and e.is_active='1' and attendance_year_id='"+academicYearId+"' and s.student_id= e.student_id; ");
					
					
					showScholarTransactions(universityId, year, month, academicYearId);
				} catch (SQLException e) {
					// TODO Auto-generated catch block
					e.printStackTrace();
				}
				
				
				
			}
		});
		
		
		//show scholar transaction
		
		
		
		
		
	}

}
