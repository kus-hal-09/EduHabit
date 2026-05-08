package com.kushal.eduhabit;

import com.google.firebase.Timestamp;
import com.google.firebase.firestore.Exclude;
import java.util.Date;

public class Submission {
    private String id;
    private String studentName;
    private String studentEmail;
    private String assignmentTitle;
    private String status; // "submitted" or "graded"
    private String grade;
    private String feedback;
    private String content;
    private Object submittedAt; // Using Object to handle both Long and Timestamp

    public Submission() {}

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getStudentName() { return studentName != null ? studentName : "Unknown Student"; }
    public void setStudentName(String studentName) { this.studentName = studentName; }

    public String getStudentEmail() { return studentEmail != null ? studentEmail : ""; }
    public void setStudentEmail(String studentEmail) { this.studentEmail = studentEmail; }

    public String getAssignmentTitle() { return assignmentTitle != null ? assignmentTitle : "Untitled Assignment"; }
    public void setAssignmentTitle(String assignmentTitle) { this.assignmentTitle = assignmentTitle; }

    public String getStatus() { return status != null ? status : "submitted"; }
    public void setStatus(String status) { this.status = status; }

    public String getGrade() { return grade; }
    public void setGrade(String grade) { this.grade = grade; }

    public String getFeedback() { return feedback; }
    public void setFeedback(String feedback) { this.feedback = feedback; }

    public String getContent() { return content; }
    public void setContent(String content) { this.content = content; }

    public Object getSubmittedAt() { return submittedAt; }
    public void setSubmittedAt(Object submittedAt) { this.submittedAt = submittedAt; }

    @Exclude
    public Date getSubmittedDate() {
        if (submittedAt instanceof Timestamp) {
            return ((Timestamp) submittedAt).toDate();
        } else if (submittedAt instanceof Long) {
            return new Date((Long) submittedAt);
        } else if (submittedAt instanceof com.google.firebase.Timestamp) {
             return ((com.google.firebase.Timestamp) submittedAt).toDate();
        }
        return new Date();
    }
}
