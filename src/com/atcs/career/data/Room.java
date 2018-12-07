//Information Team
//Nov 7, 2018

package com.atcs.career.data;

import java.io.Serializable;

public class Room implements Comparable<Room>, Serializable {
	private String roomNumber;
	private int maxCapacity;
	private Session[] residentSessions;
	

   public Room(String roomNumber, int maxCapacity){
      super();
      this.roomNumber = roomNumber;
      this.maxCapacity = maxCapacity;
   }
	
   @Override
   public int compareTo(Room o){
      return this.maxCapacity - o.getMaxCapacity();
   }

   public Session[] getResidentSessions(){
      return residentSessions;
   }

   public void setResidentSessions(Session[] residentSessions){
      this.residentSessions = residentSessions;
   }

   public String getRoomNumber(){
      return roomNumber;
   }
   
   public void setRoomNumber(String roomNumber){
	   this.roomNumber = roomNumber;
   }

   public int getMaxCapacity(){
      return maxCapacity;
   }
   
   public String toString(){
      return "Room: " + "[Room Number: " + roomNumber +"] [Capacity: " + maxCapacity +"]" ; 
   }
}
