package application;

import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.scene.control.CheckBox;
import javafx.scene.control.TextField;

public class ScholarTransaction {
	
	private SimpleIntegerProperty studentId;
	private SimpleStringProperty name;
	private SimpleStringProperty nrc;
	private TextField remark;
	private CheckBox withdrawStatus;
	
	
	public ScholarTransaction(int studentId, String name, String nrc, TextField remark, CheckBox withdrawStatus) {
		super();
		this.studentId 	=new SimpleIntegerProperty(studentId);
		this.name	= new SimpleStringProperty(name);
		this.nrc 	= new SimpleStringProperty(nrc);
		this.remark = remark;
		this.withdrawStatus = withdrawStatus;
	}


	


	public Integer getStudentId() {
		return studentId.get();
	}





	public String getName() {
		return name.get();
	}


	public String getNrc() {
		return nrc.get();
	}


	public TextField getRemark() {
		return remark;
	}


	public CheckBox getWithdrawStatus() {
		return withdrawStatus;
	}

	
	
}
