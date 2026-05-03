package com.kushal.eduhabit;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

public class CourseData {

    public static final String[] SEMESTERS = {
        "1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th"
    };

    private static final Map<String, List<String>> BCA_SUBJECTS = new LinkedHashMap<>();

    static {
        BCA_SUBJECTS.put("1", Arrays.asList(
            "Computer Fundamentals & Applications", 
            "Society and Technology", 
            "English I", 
            "Mathematics I", 
            "Programming in C", 
            "C Programming Lab"));
            
        BCA_SUBJECTS.put("2", Arrays.asList(
            "Discrete Mathematics", 
            "English II", 
            "Mathematics II", 
            "Data Structures and Algorithms", 
            "Data Structures Lab", 
            "Accounting for IT"));
            
        BCA_SUBJECTS.put("3", Arrays.asList(
            "Web Technology", 
            "Web Technology Lab", 
            "Statistics I", 
            "Java Programming", 
            "Java Programming Lab", 
            "Organizational Behavior"));
            
        BCA_SUBJECTS.put("4", Arrays.asList(
            "Operating System", 
            "Numerical Methods", 
            "Software Engineering", 
            "Scripting Language", 
            "Database Management System", 
            "Project I"));
            
        BCA_SUBJECTS.put("5", Arrays.asList(
            "MIS and E-Business", 
            "Dot Net Technology", 
            "Computer Networking", 
            "Introduction to Management", 
            "Computer Graphics and Animation"));
            
        BCA_SUBJECTS.put("6", Arrays.asList(
            "Mobile Programming", 
            "Distributed System", 
            "Applied Economics", 
            "Advanced Java Programming", 
            "Network Programming"));

        BCA_SUBJECTS.put("7", Arrays.asList(
            "Cyber Law and Professional Ethics",
            "Cloud Computing",
            "Internship",
            "Elective I (AI/InfoSec)",
            "Elective II (Advanced DB/E-Gov)"));

        BCA_SUBJECTS.put("8", Arrays.asList(
            "Operations Research",
            "Project II (Final Year Project)",
            "Elective III (ML/IoT/Big Data)",
            "Elective IV (Digital Marketing)"));
    }

    public static List<String> getSubjects(String semester) {
        if (semester == null) return new ArrayList<>();
        String semNum = semester.replace("st","").replace("nd","")
                               .replace("rd","").replace("th","");
        return BCA_SUBJECTS.getOrDefault(semNum, new ArrayList<>());
    }

    public static List<String> getSubjects(String course, String semester) {
        return getSubjects(semester);
    }

    public static String getCourseFullName(String course) {
        return "Bachelor of Computer Applications";
    }
}
