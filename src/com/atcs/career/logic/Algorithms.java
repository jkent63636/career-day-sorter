//Michael Ruberto, Joshua Kent, Bennett Bierman
//Program Description:
//Nov 21, 2018

package com.atcs.career.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.logging.Level;

import javax.swing.JOptionPane;

import com.atcs.career.data.Event;
import com.atcs.career.data.Priority;
import com.atcs.career.data.Room;
import com.atcs.career.data.Session;
import com.atcs.career.data.Student;
import com.atcs.career.program.MainClass;
import com.atcs.career.program.logging.BasicLogger;

public class Algorithms{
    /*Each sub ArrayList (in sessions) corresponds to a period
    * Students who didn't submit a request will be placed into every sub array of "toBeRandomlyAssigned"
    * Students who couldn't get a top 5 choice placed into specific sub array for that period
    * */
   static ArrayList<ArrayList<Student>> toBeRandomlyAssigned = new ArrayList<ArrayList<Student>>();
   private static final BasicLogger log = BasicLogger.getLogger(Algorithms.class.getName());
   
   static {
   	log.setVerbose(false);
   	log.setFilter(Level.FINE);
   }
   
   public static void sort(Event e) {
   	String thingsMissing = "";
   	if (e.getRooms().isEmpty())
   		thingsMissing += "room data.\n";
   	if (e.getSessions().isEmpty())
   		thingsMissing += "session data.\n";
   	if (e.getMasterStudents().isEmpty())
   		thingsMissing  += "master student array\n";
   		
   	if (!thingsMissing.equals("")) {
   		JOptionPane.showMessageDialog(null, "You are missing important data:\n" + thingsMissing
   				 + "Please input this information and try again.", MainClass.APP_NAME + ": Cannot Sort Students",
   				 JOptionPane.WARNING_MESSAGE, null);
   		e.setSorted(false);
   		return;
   	}
   	if (e.isSorted()) {
   		clearAssignments(e.studentsWithRequests(), e.getRooms(), e.getSessions(), e.getNumberOfPeriods());
   		e.setSorted(false);
   	}
   	
   	myBigFatGreekWethod(e.studentsWithRequests(), e.getMasterStudents(), e.getRooms(), e.getSessions());
   	e.setSorted(true);
   }
   
   //BIG METHOD THAT DOES EVERYTHING
   private static void myBigFatGreekWethod(ArrayList<Student> requestStudents, ArrayList<Student> master, ArrayList<Room> rooms, ArrayList<Session> sessions){
      assignRoomsToSessions(requestStudents, rooms, sessions);
      log.info("Method 1 Finished");
      
      rankStudents(requestStudents, master);
      log.info("Method 2 Finished");
      
      assignStudentsToSessions(requestStudents, sessions);
      log.info("Method 3 Finished");
      
      log.fine("Classes under Cap:");
      for(int i = 0; i < sessions.size(); i++) {
         for(int j = 0; j < sessions.get(i).getStudents().size(); j++) {
            if(sessions.get(i).getStudents().get(j).size() < 10) {
            	log.fine(sessions.get(i).getSpeaker() + " PERIOD " + j + " HAS " + sessions.get(i).getStudents().get(j).size());
               for(int k = 0; k < sessions.get(i).getStudents().get(j).size(); k++)
               	log.fine(sessions.get(i).getStudents().get(j).get(k) + "");
            }
         }
      }
      
      log.info("Accuracy:" + getSortingAccuracyAverage(requestStudents));
   }
   
   private static double getSortingAccuracyAverage(ArrayList<Student> students){   //tells you how good the sorting was based on final contentness
      double totalCont = 0;
      for(int i = 0; i < students.size(); i++){
//         System.out.println("Contentness: " + students.get(i).getStudentPriority().getContentness()/50);
         totalCont += ((students.get(i).getStudentPriority().getContentness())/50); //50 is the contentness weight we gave
      }
      return totalCont/students.size();
   }
   
	//ALGORITHM 1. Assigns rooms to sessions by sorting popularity of the sessions/speaker.
   private static void assignRoomsToSessions(ArrayList<Student> students, ArrayList<Room> rooms, ArrayList<Session> sessions){
      
      Collections.sort(rooms);
      
      HashMap<String, Session> sessionHash = new HashMap<String, Session>();
    
      for(int i=0; i<sessions.size(); i++){
         if(sessions.get(i).getSpeaker().charAt(0) == '"') //TEMP FIX FIX IT INFO
            sessionHash.put(sessions.get(i).getSpeaker().substring(1), sessions.get(i));
         else //^^^
            sessionHash.put(sessions.get(i).getSpeaker(), sessions.get(i));
      }
      
//      for(int i=0; i< sessionHash.size(); i++){      //for testing
//         System.out.println("testing: "+i);
//         System.out.println(sessionHash.get(sessions.get(i).getSpeaker()).getSpeaker());
//      }
        
      for(Student stud: students){
         ArrayList<Session> requests = stud.getRequests();
//         System.out.println(stud.toString());
         int requestsSize = requests.size();
         for(int i = 0; i < requestsSize; i++) {
            sessionHash.get(requests.get(i).getSpeaker()).addPopularity(requestsSize-i);   //come back to fix "5-i" if needed
         }
//         System.out.println("Next Student \n");
      }
      
      sessions.clear();
      sessions.addAll(sessionHash.values());
      Collections.sort(sessions);
      
      for(int i = sessions.size() - 1; i >= 0; i--){
         if(rooms.size() > i) { //COME BACK WITH ERROR MANAGER STUFF
            sessions.get(i).setRoom(rooms.get((rooms.size()-1) - ((sessions.size()-1) - i)));
            rooms.get((rooms.size()-1) - ((sessions.size()-1) - i)).setResidentSessions(new Session[] {sessions.get(i)});
         }
      }
      
      //FOR TESTING
//      for(int i = 0; i < rooms.size(); i++){
//         System.out.println(rooms.get(i).toString());
//         System.out.println(sessions.get(i).getSpeaker());
//      }
          
   }
   
	//ALGORITHM 2. Gives students their priority in order for them to be rank so student assignment can be done correctly
   private static void rankStudents(ArrayList<Student> students, ArrayList<Student> master){
    //Creates Array Lists for Random Assignment
      for(int i = 0; i < 3; i++)  //Change 3 later to not be a magic number
         toBeRandomlyAssigned.add(new ArrayList<Student>());
      
      for(int i = 0; i < master.size(); i++) {
         if (master.get(i).getRequests().isEmpty()) {
            for(int j = 0; j < toBeRandomlyAssigned.size(); j++) {
               toBeRandomlyAssigned.get(j).add(master.get(i));
            }
         }
      }
      
      for(int i = 0; i < students.size(); i++) {
         Student currentStud = students.get(i);
         int yearEntered = (currentStud.getTimeEntered()/1000) - Event.startYear;
         int dayEntered = ((yearEntered * 365) + (currentStud.getTimeEntered()%1000)) - Event.startDay;
         if (currentStud.getGrade() >= Priority.classCutOff) {
            currentStud.setStudentPriority(new Priority(dayEntered, Priority.upperClassMagnitudeValue));
         }
         else if (currentStud.getGrade() < Priority.classCutOff) {
            currentStud.setStudentPriority(new Priority(dayEntered, Priority.lowerClassMagnitudeValue));
         }
      }
      Collections.sort(students);
      
      //FOR TESTING
//      for(int i = 0; i < students.size(); i++){
//         System.out.println(students.get(i));
//      }
      
   }
   
	//ALGORITHM 3. With students sorted, we assign them to sessions–trying to give them their highest choices first. We assign by period by period.
   private static void assignStudentsToSessions(ArrayList<Student> students, ArrayList<Session> sessions){
      int numOfPeriods = 3;
      for(int j = 0; j < numOfPeriods; j++) { //For each period
         for(int i = 0; i < students.size(); i++) { //Go through every student
            Student currentStud = students.get(i); //Makes it easier to refer to current students
            assignBasedOnChoice(currentStud, sessions, j);
         }
         Collections.sort(students);  //reranks students
      } 
      
      
      assignRandomsAtEnd(sessions);
      
      // Backfill: remove students from populated sessions and add them to unpopular sessions
      //COMMENT BELOW HERE TO STOP BACKFILL
      int minCap = 10;
      
      for(int i = 0; i < sessions.size(); i++) { //For each session...
         for(int j = 0; j < sessions.get(i).getStudents().size(); j++) { //Check each period of it...
            if(sessions.get(i).getAvailableThisPeriod()[j] && sessions.get(i).getStudents().get(j).size() < minCap) { //If that session doesn't have enough during that period...
               do {
                  for(int k = students.size() - 1; k >= 0; k--) { //Look at every student.
                     Student currentStud = students.get(k);
                     if(currentStud.isSwitchable()) { //If they haven't already been moved during the backfill...
                        if(sessions.indexOf(currentStud.getAssignment(j)) != -1) //This is just because of the issue with quotes before speaker name. shouldn't be an issue otherwise
                           sessions.get(sessions.indexOf(currentStud.getAssignment(j))).getStudents().get(j).remove(currentStud); //Take them out of their old session at that period
                        sessions.get(i).getStudents().get(j).add(currentStud); //Add them to this student
                        currentStud.getAssignments()[j] = sessions.get(i); //Update this in their assignment array
                        currentStud.setSwitchable(false); //Don't let this student be switched anymore
                        break;
                     }
                  }
               }while(sessions.get(i).getStudents().get(j).size() <= minCap); //If they're still under the cap, do it again
            }
         }
      }
      //COMMENT ABOVE HERE TO STOP BACKFILL
   }
      
	//Assigns one students bases on their choices and period
   private static void assignBasedOnChoice(Student currentStud, ArrayList<Session> sessions, int period) {
      log.finer(currentStud);
      log.finer("FINDING AT "+period);
      
      for(int k = 0; k < currentStud.getRequests().size(); k++){ //Check every request the student makes
      	log.finer("REQUEST: " + currentStud.getRequests().get(k));
         int sessionIndex = findIndexOfSession(currentStud.getRequests().get(k), sessions);
         
         Session desiredSession = sessions.get(0);
         if(sessionIndex >= 0) desiredSession = sessions.get(sessionIndex);

         log.finer("FOUND SESSION: " + desiredSession);
			if (desiredSession.getAvailableThisPeriod()[period]
					&& desiredSession.getStudents().get(period)
							.size() < desiredSession.getRoom().getMaxCapacity()
					&& !currentStud.assignmentsContain(desiredSession)) {
				log.finer("SUCCESS!");
            desiredSession.getStudents().get(period).add(currentStud);
            currentStud.getAssignments()[period] =  desiredSession; //Changed from set --> add //took out period - 1
            changeStudentContentness(currentStud); //Deals with contentness
            return;
         }
      }
      log.finer("No, Next!");
      
      
      
      //They couldn't get in any session they chose this period
      //TODO do we need this line
//      currentStud.getAssignments().add(period, new Session()); //Changed from set --> add //took out period - 1
      
      toBeRandomlyAssigned.get(period).add(currentStud); //took out period - 1
      
      
      changeStudentContentness(currentStud); //Deals with contentness
   }
   
	//Checks to make sure all sessions meet minimum capacity (that we define)
   // can we delete this
   @Deprecated
   private static boolean allSessionAreFilledToMin(ArrayList<Session> sessions){ 
      int minCapacity = 10;
      for(int i = 0; i < 3; i++) {
         if(getLeastPopulatedSessionPerPeriod(sessions, i).getStudents().get(i).size() < minCapacity) {
            System.out.println(getLeastPopulatedSessionPerPeriod(sessions, i) + " PERIOD " + i);
            return false;
         }
      }
      return true;
   }
   
	//Takes people who are "randos" (didn't sign up) and assigns to the least populated sessions
   private static void assignRandomsAtEnd(ArrayList<Session> sessions){
      for(int i = 0; i < toBeRandomlyAssigned.size(); i++) {   //toBeRandomlyAssigned.size() is representing the amount of periods
      	for(int j = toBeRandomlyAssigned.get(i).size() - 1; j >= 0; j--){
            Student stud = toBeRandomlyAssigned.get(i).remove(j);
            Session sessionToAssign = getLeastPopulatedSessionPerPeriodStudentConscious(sessions, i, stud);
            sessionToAssign.getStudents().get(i).add(stud);
//            if (stud.getAssignments().length <= i)
//            	stud.getAssignments().add(sessionToAssign);
//            else
            	stud.getAssignments()[i] = sessionToAssign;
         }
      }
   }
   
   //When gives period and all sessions, reutnrs the leas populated session from that period
   private static Session getLeastPopulatedSessionPerPeriod(ArrayList<Session> sessions, int period) {
      Session min = sessions.get(0);
      for(int i = 1; i < sessions.size(); i++){
         if(sessions.get(i).getStudents().get(period).size() < min.getStudents().get(period).size() && sessions.get(i).getAvailableThisPeriod()[period]){
            min = sessions.get(i);
         }
      }
      return min;
   }
   
   //Same as above, but won't return a session the student already has
   private static Session getLeastPopulatedSessionPerPeriodStudentConscious(ArrayList<Session> sessions, int period, Student stud) {
      Session min = sessions.get(0);
      for(int i = 1; i < sessions.size(); i++){
         if(sessions.get(i).getStudents().get(period).size() < min.getStudents().get(period).size()
               && sessions.get(i).getAvailableThisPeriod()[period] && !stud.assignmentsContain(sessions.get(i))){
            min = sessions.get(i);
         }
      }
      return min;
   }
   
   //Changes the student's contentedness (or contentness) based on what requests they have gotten
   private static void changeStudentContentness(Student currentStud){
      int selectionsAlreadyMade = currentStud.getAssignments().length;
      
      double numerator = 0;
      double denominator = 0;
      for(int i = 0; i < selectionsAlreadyMade; i++) {
         int choiceIndex = currentStud.getRequests().indexOf(currentStud.getAssignments()[i]);
         if(choiceIndex != -1)
            numerator += currentStud.getRequests().size() - choiceIndex;
         denominator += currentStud.getRequests().size() - i;
      }
      
      currentStud.getStudentPriority().setContentness(numerator/denominator);
   }
   
   //Gives the index of the session requested in the sessions ArrayList
   private static int findIndexOfSession(Session requestedSession, ArrayList<Session> sessions){
      for(int i = 0; i < sessions.size(); i++){
         if (sessions.get(i).getSpeaker().equals(requestedSession.getSpeaker()))
            return i;
      }
      return -1;     
   }
   
   
   private static void clearAssignments(ArrayList<Student> studentRequestList, ArrayList<Room> rooms, ArrayList<Session> sessions, int numPeriods) {
   	for (Student s : studentRequestList)
   		s.setAssignments(new Session[numPeriods]);
   	for (Room r : rooms) 
   		r.setResidentSessions(new Session[0]);
   	for (Session s : sessions) {
   		s.setStudents(new ArrayList<ArrayList<Student>>());
   		s.setRoom(null);
   		for (int i = 0; i < numPeriods; i++) 
   			s.getStudents().add(new ArrayList<Student>());
   	}
   	toBeRandomlyAssigned = new ArrayList<ArrayList<Student>>();
   	
   }
}