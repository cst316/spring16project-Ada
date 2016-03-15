package net.sf.memoranda;

import net.sf.memoranda.date.CalendarDate;

public interface Template {
    
    public static final int PRIORITY_LOWEST = 0;
    public static final int PRIORITY_LOW = 1;
    public static final int PRIORITY_NORMAL = 2;
    public static final int PRIORITY_HIGH = 3;
    public static final int PRIORITY_HIGHEST = 4;
    
    /**
     * Gets difference of time from the start date to the end date.
     * The result will be applied to the current date in a new task
     * to get the end date.
     * 
     * @return an int array for the difference of days, months, and years 
     */
    int[] getDateDifference();
    void setDateDifference(CalendarDate startDate, CalendarDate endDate);
    
    /**
     * @return the ID for the Template
     */
    String getId();
    
    /**
     * Task Template title
     * 
     * @return the Template's title
     */
    String getTitle();
    void setTitle(String title);
    
    /**
     * Task Template type
     * 
     * @return the Template's type
     */
    String getType();
    void setType(String type);
    
    /**
     * Task Template description
     * 
     * @return the Template's description
     */
    String getDescription();
    void setDescription(String description);
    
    /**
     * Task Template effort
     * 
     * @return the Template's effort
     */
    long getEffort();
    void setEffort(long effort);
    
    /**
     * Task Template's priority
     * 
     * @return the Template's priority
     */
    int getPriority();
    void setPriority(int priority);
    
    /**
     * The Template as XML
     * 
     * @return the XML element of the Template
     */
    nu.xom.Element getContent();
}
