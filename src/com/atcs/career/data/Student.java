//Information Team
//Nov 7, 2018

package com.atcs.career.data;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Calendar;

public class Student implements Comparable<Student>, Serializable  {
	private String lName, fName, email;
	private ArrayList<Session> requests, assignments;
	private int grade, timeEntered;
	private Priority priority;

	public static void main(String[] args) {
		Student s = new Student("Reineke", "Michael", "mreineke20@pascack.org", null, 100);
		System.out.println(s.getGrade());
		System.out.println(s);
	}

	public Student(String lName, String fName, String email,
			ArrayList<Session> requests, int timeEntered) {
		this.lName = lName;
		this.fName = fName;
		this.email = email;
		this.requests = requests;
		grade = getGradeFromEmail();
		priority = getStudentPriority();
		this.timeEntered = timeEntered;
	}

	/**
	 * Returns 0 if an invalid email is used
	 * 
	 * @return Grade Level 9-12
	 */

	public int getGradeFromEmail() {
		int thisYear = Calendar.getInstance().get(Calendar.YEAR);
		if (Calendar.getInstance().get(Calendar.MONTH) >= 0
				&& Calendar.getInstance().get(Calendar.MONTH) <= 6)
			thisYear--;
		int gradYear = 0;
		for (int c = 0; c < email.substring(0, email.indexOf('@'))
				.length(); c++) {
			if (email.toCharArray()[c] >= '0' && email.toCharArray()[c] <= '9') {
				if (email.substring(c + 1, c + 2).equals("@")) return 12;
				gradYear = Integer.parseInt("20" + email.substring(c, c + 2));
				break;
			}
		}
		if ((gradYear + "").length() < 4) return 12;

		switch (gradYear - thisYear) {
			case 1 :
				return 12;
			case 2 :
				return 11;
			case 3 :
				return 10;
			case 4 :
				return 9;
		}
		return 0;
	}

	public String getlName() {
		return lName;
	}

	public void setlName(String lName) {
		this.lName = lName;
	}

	public String getfName() {
		return fName;
	}

	public void setfName(String fName) {
		this.fName = fName;
	}

	public String getFullName() {
		return fName + " " + lName;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public ArrayList<Session> getRequests() {
		return requests;
	}

	public void setRequests(ArrayList<Session> requests) {
		this.requests = requests;
	}

	public ArrayList<Session> getAssignments() {
		return assignments;
	}

	public void setAssignments(ArrayList<Session> assignments) {
		this.assignments = assignments;
	}

	public int getTimeEntered() {
		return timeEntered;
	}

	public void setTimeEntered(int timeEntered) {
		this.timeEntered = timeEntered;
	}

	public int getGrade() {
		return grade;
	}

	public void setGrade(int grade) {
		this.grade = grade;
	}

	public Priority getStudentPriority() {
		return priority;
	}

   
   public void setStudentPriority(Priority newPriority) {
      this.priority = newPriority;
   }
   
   @Override
  public String toString()
  {
      return "Student: [" + fName +" " + lName + "] [email: " + email + 
            "] [grade: " + grade + "] [timeEntered: " + timeEntered + "] [priority: " + priority+"]";  
  }
   
   @Override
   public int compareTo(Student o){
      return (int)(this.getStudentPriority().getPriority() - o.getStudentPriority().getPriority());
   }
}
