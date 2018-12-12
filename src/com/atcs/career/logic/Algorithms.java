//Michael Ruberto
//Program Description:
//Nov 21, 2018
/*TODO
 * - Implement ability to randomly assign sessions to students who didn't answer/couldn't get in a selected session
 * - give proper weight to time
 */


package com.atcs.career.logic;

import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;

import com.atcs.career.data.Priority;
import com.atcs.career.data.Room;
import com.atcs.career.data.Session;
import com.atcs.career.data.Student;

public class Algorithms{
    /*Each sub ArrayList corresponds to a period
    * Students who didn't submit a request will be placed into every sub array
    * Students who couldn't get a top 5 choice placed into specific sub array for that period
    * */
   static ArrayList<ArrayList<Student>> toBeRandomlyAssigned = new ArrayList<ArrayList<Student>>();
   
   //BIG METHOD THAT DOES EVERYTHING
   public static void myBigFatGreekWethod(ArrayList<Student> students, ArrayList<Room> rooms, ArrayList<Session> sessions){
      int classCutOffForGroupLevel = 0;
      int upperClassmanLevelMag = 0;
      int lowerClassmanLevelMag = 0;
      
      assignRoomsToSessions(students, rooms, sessions);
      rankStudents(students, classCutOffForGroupLevel, upperClassmanLevelMag, lowerClassmanLevelMag);
      assignStudentsToSessions(students, sessions);
   }
   
   
   //ALGORITHM 1
   public static void assignRoomsToSessions(ArrayList<Student> students, ArrayList<Room> rooms, ArrayList<Session> sessions){
      
      Collections.sort(rooms);
      
      HashMap<String, Session> sessionHash = new HashMap<String, Session>();
      for(Session s: sessions){
         sessionHash.put(s.getSpeaker(), s);
      }
 
      for(Student stud: students){
         ArrayList<Session> requests = stud.getRequests();
         int requestsSize = requests.size();
         for(int i = 0; i < requestsSize; i++) {
            sessionHash.get(requests.get(i)).addPopularity(requestsSize-i);   //come back to fix "5-i" if needed
         }
      }
      
      sessions = (ArrayList<Session>) sessionHash.values();
      Collections.sort(sessions);
      
      for(int i = 0; i < sessions.size(); i++){
         if(rooms.size() < i) { //COME BACK WITH ERROR MANAGER STUFF
            sessions.get(i).setRoom(rooms.get(i));
         }
      }
   }
   
   //ALGORITHM 2
   public static void rankStudents(ArrayList<Student> students, int classCutOffForGroupLevel, int upperClassmanLevelMag, int lowerClassmanLevelMag){
      for(int i = 0; i < students.size(); i++) {
         Student currentStud = students.get(i);
         int timeMagForPriority = currentStud.getTimeEntered();     //**Fix how we are getting value for "timeMagForPriority" so its not just a time
         if (currentStud.getGrade() >= classCutOffForGroupLevel) {
            currentStud.setStudentPriority(new Priority(timeMagForPriority, upperClassmanLevelMag));
         }
         else if (currentStud.getGrade() < classCutOffForGroupLevel) {
            currentStud.setStudentPriority(new Priority(timeMagForPriority, lowerClassmanLevelMag));
         }
      }
      Collections.sort(students);
   }
   
   //ALGORITHM 3
   public static void assignStudentsToSessions(ArrayList<Student> students, ArrayList<Session> sessions){
      //Creates Array Lists for Random Assignment
      for(int i = 0; i < 3; i++) { //Change 3 later to not be a magic number
         toBeRandomlyAssigned.add(new ArrayList<Student>());
      }
      
      int numOfPeriods = 3;
      for(int j = 0; j < numOfPeriods; j++) { //For each period
         for(int i = 0; i < students.size(); i++) { //Go through every student
            Student currentStud = students.get(i); //Makes it easier to refer to current students
            assignBasedOnChoice(currentStud, sessions, j);
         }
         Collections.sort(students);  //reranks students
      } 
      
      assignRandomsAtEnd(sessions);
      
      while(!allSessionAreFilledToMin(sessions)){
         int period = getLeastPopulatedSessionIndex(sessions, 3); //CHANGE
         Session minSession = getLeastPopulatedSessionPerPeriod(sessions, period);
         
         boolean successfullyChangedSomeone = false;
         for(int i = students.size() - 1; i >= 0; i--){
            if(students.get(i).getPeriodOfLeastDesired() == getLeastPopulatedSessionIndex(sessions, 3) && students.get(i).isSwitchable()){
               Session oldSession = students.get(i).getRequests().remove(period); //Take Away Their Old Session
               sessions.get(sessions.indexOf(oldSession)).getStudents().get(period).remove(students.get(i)); //Take them out of their old session
               minSession.getStudents().get(period).add(students.get(i)); //Add to new session
               students.get(i).getRequests().set(period, minSession); //Tell them they're in the new session
               students.get(i).setSwitchable(false);
               successfullyChangedSomeone = true;
            }
            break;
         }
         
         if(!successfullyChangedSomeone){
            for(int i = students.size() - 1; i >= 0; i--){
               if(students.get(i).isSwitchable()){
                  Session oldSession = students.get(i).getRequests().remove(period); //Take Away Their Old Session
                  sessions.get(sessions.indexOf(oldSession)).getStudents().get(period).remove(students.get(i)); //Take them out
                  minSession.getStudents().get(period).add(students.get(i)); //Add to new session
                  students.get(i).getRequests().set(period, minSession); //Tell them they're in the new session
                  students.get(i).setSwitchable(false);
               }
            }
         }
      }
   }
   
   
   
   
   public static void assignBasedOnChoice(Student currentStud, ArrayList<Session> sessions, int period) {
      for(int k = 0; k < currentStud.getRequests().size(); k++){ //Check every request the student makes
         Session desiredSession = sessions.get(findIndexOfSession(currentStud.getRequests().get(k), sessions));
         if(desiredSession.getStudents().get(period).size() < desiredSession.getRoom().getMaxCapacity() &&
           !currentStud.getAssignments().contains(desiredSession)){
            desiredSession.getStudents().get(period).add(currentStud);
            currentStud.getAssignments().set(period - 1, desiredSession);
            changeStudentContentness(currentStud); //Deals with contentness
            return;
         }
      }
      
      //They couldn't get in any session they chose this period
      currentStud.getAssignments().set(period - 1, new Session());
      toBeRandomlyAssigned.get(period - 1).add(currentStud);
      
      
      changeStudentContentness(currentStud); //Deals with contentness
   }
   
   public static boolean allSessionAreFilledToMin(ArrayList<Session> sessions){ 
      int minCapacity = 10;
      for(int i = 0; i < sessions.size(); i++) {
         for(int j=0; j < sessions.get(i).getStudents().size(); j++){
            if(sessions.get(i).getStudents().get(i).size() < minCapacity)
               return false;
         }        
      }
      return true;
   }
   
   
   public static void assignRandomsAtEnd(ArrayList<Session> sessions){
      for(int i = 0; i < toBeRandomlyAssigned.size(); i++) {   //toBeRandomlyAssigned.size() is representing the amount of periods
         for(int j = 0; j < toBeRandomlyAssigned.get(i).size(); j++){
            Session session = getLeastPopulatedSessionPerPeriod(sessions, i);
            Student stud = toBeRandomlyAssigned.get(i).remove(j);
            session.getStudents().get(i).add(stud);
            stud.getAssignments().set(j, session);
         }
      }
   }
   
   private static Session getLeastPopulatedSessionPerPeriod(ArrayList<Session> sessions, int period) {
      Session min = sessions.get(0);
      for(int i = 0; i < sessions.size(); i++){
         if(sessions.get(i).getStudents().get(period).size() < min.getStudents().get(period).size()){
            min = sessions.get(i);
         }
      }
      return min;
   }
   
   private static int getLeastPopulatedSessionIndex(ArrayList<Session> sessions, int numberOfPeriods){
      ArrayList<Session> leastPopulatedSessions = new ArrayList<Session>();
      for(int i = 0; i < numberOfPeriods; i++){
         leastPopulatedSessions.add(getLeastPopulatedSessionPerPeriod(sessions, i));  
      }
      
      Session min = leastPopulatedSessions.get(0);
      for(int i = 0; i < leastPopulatedSessions.size(); i++){
         if(leastPopulatedSessions.get(i).getStudents().get(i).size() < min.getStudents().get(leastPopulatedSessions.indexOf(min)).size()){
            min = leastPopulatedSessions.get(i);
         }
      }
      return leastPopulatedSessions.indexOf(min);
   }
   
   public static void changeStudentContentness(Student currentStud){
      int selectionsAlreadyMade = currentStud.getAssignments().size();
      
      double numerator = 0;
      double denominator = 0;
      for(int i = 0; i < selectionsAlreadyMade; i++) {
         int choiceIndex = currentStud.getRequests().indexOf(currentStud.getAssignments().get(i));
         if(choiceIndex != -1)
            numerator += currentStud.getRequests().size() - choiceIndex;
         denominator += currentStud.getRequests().size() - i;
      }
      
      currentStud.getStudentPriority().setContentness(numerator/denominator);
   }
   
   public static int findIndexOfSession(Session requestedSession, ArrayList<Session> sessions){
      for(int i = 0; i < sessions.size(); i++){
         if (sessions.get(i).getSpeaker().equals(requestedSession.getSpeaker()));
            return i;
      }
      return -1;     
   }
}