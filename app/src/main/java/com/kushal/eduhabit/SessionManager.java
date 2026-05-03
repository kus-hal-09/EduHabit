package com.kushal.eduhabit;

import android.content.Context;
import android.content.SharedPreferences;

public class SessionManager {
    private SharedPreferences prefs;
    private SharedPreferences.Editor editor;
    private Context context;
    private static final String PREF_NAME = "EduHabitSession";

    public SessionManager(Context context) {
        this.context = context;
        prefs = context.getSharedPreferences(PREF_NAME, Context.MODE_PRIVATE);
        editor = prefs.edit();
    }

    public void saveSession(String uid, String name, String email,
                            String role, String course, 
                            String semester, String subject) {
        editor.putString("uid", uid);
        editor.putString("name", name);
        editor.putString("email", email);
        editor.putString("role", role);
        editor.putString("course", course);
        editor.putString("semester", semester);
        editor.putString("subject", subject);
        editor.putBoolean("isLoggedIn", true);
        editor.apply();
    }

    public String getUid() { 
        return prefs.getString("uid", ""); 
    }
    public String getName() { 
        return prefs.getString("name", ""); 
    }
    public String getEmail() { 
        return prefs.getString("email", ""); 
    }
    public String getRole() { 
        return prefs.getString("role", ""); 
    }
    public String getCourse() { 
        return prefs.getString("course", ""); 
    }
    public String getSemester() { 
        return prefs.getString("semester", ""); 
    }
    public String getSubject() { 
        return prefs.getString("subject", ""); 
    }
    public boolean isLoggedIn() { 
        return prefs.getBoolean("isLoggedIn", false); 
    }
    public boolean isTeacher() { 
        String role = getRole();
        return role != null && role.equalsIgnoreCase("teacher"); 
    }
    public boolean isStudent() { 
        String role = getRole();
        return role != null && role.equalsIgnoreCase("student"); 
    }
    public void clearSession() { 
        editor.clear(); 
        editor.apply(); 
    }
}
