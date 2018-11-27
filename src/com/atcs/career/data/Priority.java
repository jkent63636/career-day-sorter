//Joshua Kent
//Program description:
//Nov 20, 2018

package com.atcs.career.data;

public class Priority
{
   /*Priority is made up of three factors:
    * -Time
    * -Group
    * -Contentness
    * 
    * The "Magnitude" value represents the actual value (What time, what group, etc.)
    * The "Weight" value is user inputted and controls how much the algorithm factors in that value
    * Magnitude and Weight are used to compute the overall Value for each factor, which in turn calculates their priority
    */
   private double contentnessMagnitude, timeMagnitude, groupMagnitude;
   private static double contentnessWeight, timeWeight, groupWeight;
   
   public Priority(double tMag, double gMag)
   {
      contentnessMagnitude = 0;
      this.timeMagnitude = tMag;
      this.groupMagnitude = gMag;
   }
   
   public double getContentness(){
      return contentnessMagnitude*contentnessWeight;
   }
   
   public void setContentness(double cMag){
      this.contentnessMagnitude = cMag;
   }
   
   public double getTime(){
      return timeMagnitude*timeWeight;
   }
   
   public double getGroup(){
      return groupMagnitude*groupWeight;
   }
   
   public double getPriority(){
      return getContentness() + getTime() + getGroup();
   }

   public static void setContentnessWeight(double cWeight){
      Priority.contentnessWeight = cWeight;
   }

   public static void setTimeWeight(double tWeight){
      Priority.timeWeight = tWeight;
   }

   public static void setGroupWeight(double gWeight){
      Priority.groupWeight = gWeight;
   }

   
}