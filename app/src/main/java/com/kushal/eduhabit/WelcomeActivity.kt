package com.kushal.eduhabit

import android.content.Intent
import android.os.Bundle
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.*
import androidx.compose.animation.core.*
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyRow
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material.icons.automirrored.filled.Assignment
import androidx.compose.material.icons.filled.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.blur
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.shadow
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.drawscope.DrawScope
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.PasswordVisualTransformation
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.FieldValue

class WelcomeActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            EduHabitTheme {
                MainNavigation()
            }
        }
    }
}

@Composable
fun MainNavigation() {
    var currentScreen by remember { mutableStateOf("Splash") }
    var isSignUpMode by remember { mutableStateOf(false) }
    val context = LocalContext.current
    val mAuth = FirebaseAuth.getInstance()
    val session = remember { SessionManager(context) }

    LaunchedEffect(Unit) {
        if (mAuth.currentUser != null && session.isLoggedIn()) {
            val role = session.getRole() ?: "student"
            val intent = if (role.equals("teacher", true)) {
                Intent(context, TeacherDashboardActivity::class.java)
            } else {
                Intent(context, StudentDashboardActivity::class.java)
            }
            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
            context.startActivity(intent)
            (context as? ComponentActivity)?.finish()
        } else {
            currentScreen = "Welcome"
        }
    }

    Box(modifier = Modifier.fillMaxSize().background(Brush.verticalGradient(listOf(Color(0xFF0F172A), Color(0xFF1E1B4B))))) {
        AnimatedGeometricBackground()

        if (currentScreen == "Splash") {
            Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
                CircularProgressIndicator(color = Color.White)
            }
        } else {
            AnimatedContent(targetState = currentScreen, label = "screen_transition") { screen ->
                when (screen) {
                    "Welcome" -> WelcomeContent(
                        onLogin = { isSignUpMode = false; currentScreen = "Auth" },
                        onSignUp = { isSignUpMode = true; currentScreen = "Auth" }
                    )
                    "Auth" -> AuthDashboard(
                        isSignUpModeInitial = isSignUpMode,
                        onBack = { currentScreen = "Welcome" }
                    )
                }
            }
        }
    }
}

@Composable
fun WelcomeContent(onLogin: () -> Unit, onSignUp: () -> Unit) {
    Column(
        modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(24.dp),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.SpaceBetween
    ) {
        Column(horizontalAlignment = Alignment.CenterHorizontally) {
            Spacer(modifier = Modifier.height(24.dp))
            Surface(modifier = Modifier.size(80.dp).shadow(12.dp, RoundedCornerShape(24.dp)), shape = RoundedCornerShape(24.dp), color = Color.White) {
                Box(contentAlignment = Alignment.Center) { Icon(Icons.Default.AutoStories, null, modifier = Modifier.size(40.dp), tint = Color(0xFF4F46E5)) }
            }
            Spacer(modifier = Modifier.height(16.dp))
            Text("EduHabit", fontSize = 36.sp, fontWeight = FontWeight.Black, color = Color.White)
            Text("Your academic growth, automated.", fontSize = 14.sp, color = Color.White.copy(0.6f))
        }

        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            FeatureGlassCard(Icons.Default.AutoStories, "Curriculum", "Track all subjects", Color(0xFF3B82F6))
            FeatureGlassCard(Icons.AutoMirrored.Filled.Assignment, "Tasks", "Smart management", Color(0xFF10B981))
            FeatureGlassCard(Icons.Default.BarChart, "Growth", "Visualize activity", Color(0xFFFACC15))
        }

        Surface(modifier = Modifier.fillMaxWidth().shadow(24.dp, RoundedCornerShape(32.dp)), shape = RoundedCornerShape(32.dp), color = Color.White.copy(0.05f), border = BorderStroke(1.dp, Color.White.copy(0.1f))) {
            Column(modifier = Modifier.padding(24.dp), horizontalAlignment = Alignment.CenterHorizontally) {
                Button(onClick = onLogin, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), colors = ButtonDefaults.buttonColors(containerColor = Color(0xFF4F46E5))) {
                    Text("LOG IN", fontWeight = FontWeight.Bold)
                }
                Spacer(modifier = Modifier.height(12.dp))
                OutlinedButton(onClick = onSignUp, modifier = Modifier.fillMaxWidth().height(56.dp), shape = RoundedCornerShape(16.dp), border = BorderStroke(2.dp, Color(0xFF4F46E5).copy(0.5f))) {
                    Text("SIGN UP", color = Color.White)
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AuthDashboard(isSignUpModeInitial: Boolean, onBack: () -> Unit) {
    var isSignUpMode by remember { mutableStateOf(isSignUpModeInitial) }
    
    // Form State
    var selectedRole by remember { mutableStateOf("Student") }
    var selectedSemester by remember { mutableStateOf("1st") }
    var fullName by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var password by remember { mutableStateOf("") }
    var subject by remember { mutableStateOf("") }
    var isLoading by remember { mutableStateOf(false) }

    val context = LocalContext.current
    val mAuth = FirebaseAuth.getInstance()
    val db = FirebaseFirestore.getInstance()
    val session = remember { SessionManager(context) }
    val semesters = listOf("1st", "2nd", "3rd", "4th", "5th", "6th", "7th", "8th")

    Column(modifier = Modifier.fillMaxSize().statusBarsPadding().navigationBarsPadding().padding(24.dp).verticalScroll(rememberScrollState()), horizontalAlignment = Alignment.CenterHorizontally) {
        Row(modifier = Modifier.fillMaxWidth(), verticalAlignment = Alignment.CenterVertically) {
            IconButton(onClick = onBack) { Icon(Icons.AutoMirrored.Filled.ArrowBack, null, tint = Color.White) }
            Text("EduHabit", fontWeight = FontWeight.Black, color = Color.White, modifier = Modifier.padding(start = 16.dp))
        }
        
        Spacer(modifier = Modifier.height(24.dp))
        RoleSegmentedToggle(selectedRole) { selectedRole = it }
        
        Spacer(modifier = Modifier.height(32.dp))
        Column(verticalArrangement = Arrangement.spacedBy(16.dp)) {
            
            GlassDropdown(selected = selectedSemester, options = semesters, label = "Semester", onSelect = { selectedSemester = it })

            if (isSignUpMode) {
                GlassInputField(fullName, { fullName = it }, "Full Name", Icons.Default.Person)
            }
            GlassInputField(email, { email = it }, "Email", Icons.Default.Email)
            GlassInputField(password, { password = it }, "Password", Icons.Default.Lock, true)
            
            if (isSignUpMode && selectedRole == "Teacher") {
                GlassInputField(subject, { subject = it }, "Teaching Subject", Icons.Default.Book)
            }

            GradientSubmitButton(if (isSignUpMode) "Create Account" else "Log In") {
                if (email.isEmpty() || password.isEmpty()) return@GradientSubmitButton
                isLoading = true
                
                if (isSignUpMode) {
                    mAuth.createUserWithEmailAndPassword(email, password).addOnSuccessListener { res ->
                        val uid = res.user!!.uid
                        val finalRole = selectedRole.lowercase()
                        val user = hashMapOf(
                            "uid" to uid, "name" to fullName, "email" to email,
                            "role" to finalRole, "course" to "BCA",
                            "semester" to selectedSemester,
                            "createdAt" to FieldValue.serverTimestamp()
                        )
                        if (selectedRole == "Student") {
                            user["xp"] = 0
                            user["streak"] = 0
                        } else user["subject"] = subject

                        db.collection("users").document(uid).set(user).addOnSuccessListener {
                            session.saveSession(uid, fullName, email, finalRole, "BCA", selectedSemester, subject)
                            val intent = if (finalRole == "teacher") Intent(context, TeacherDashboardActivity::class.java) else Intent(context, StudentDashboardActivity::class.java)
                            intent.flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK
                            context.startActivity(intent)
                            (context as? ComponentActivity)?.finish()
                        }.addOnFailureListener { isLoading = false }
                    }.addOnFailureListener { isLoading = false; Toast.makeText(context, it.message, Toast.LENGTH_SHORT).show() }
                } else {
                    mAuth.signInWithEmailAndPassword(email, password).addOnSuccessListener { res ->
                        db.collection("users").document(res.user!!.uid).get().addOnSuccessListener { doc ->
                            if (doc.exists()) {
                                val role = doc.getString("role") ?: "student"
                                val name = doc.getString("name") ?: ""
                                val course = doc.getString("course") ?: "BCA"
                                val semester = doc.getString("semester") ?: selectedSemester
                                val subjectVal = doc.getString("subject") ?: ""
                                
                                session.saveSession(res.user!!.uid, name, email, role, course, semester, subjectVal)
                                
                                if (role.equals("teacher", true)) {
                                    context.startActivity(Intent(context, TeacherDashboardActivity::class.java))
                                } else {
                                    context.startActivity(Intent(context, StudentDashboardActivity::class.java))
                                }
                                (context as? ComponentActivity)?.finish()
                            } else {
                                isLoading = false
                                Toast.makeText(context, "User profile not found", Toast.LENGTH_SHORT).show()
                            }
                        }.addOnFailureListener { isLoading = false }
                    }.addOnFailureListener { isLoading = false; Toast.makeText(context, "Login Failed", Toast.LENGTH_SHORT).show() }
                }
            }
            TextButton(onClick = { isSignUpMode = !isSignUpMode }, modifier = Modifier.align(Alignment.CenterHorizontally)) {
                Text(if (isSignUpMode) "Already have an account? Log In" else "New here? Sign Up", color = Color.White.copy(0.7f))
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GlassDropdown(selected: String, options: List<String>, label: String, onSelect: (String) -> Unit) {
    var expanded by remember { mutableStateOf(false) }
    ExposedDropdownMenuBox(expanded = expanded, onExpandedChange = { expanded = !expanded }, modifier = Modifier.fillMaxWidth()) {
        TextField(
            value = "$selected Semester", onValueChange = {}, readOnly = true,
            modifier = Modifier.menuAnchor(MenuAnchorType.PrimaryNotEditable, true).fillMaxWidth().height(60.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.05f)).border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(16.dp)),
            placeholder = { Text(label, color = Color.White.copy(0.4f)) },
            leadingIcon = { Icon(Icons.Default.School, null, tint = Color.White.copy(0.6f)) },
            trailingIcon = { Icon(if (expanded) Icons.Default.ArrowDropUp else Icons.Default.ArrowDropDown, null, tint = Color.White) },
            colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent)
        )
        ExposedDropdownMenu(expanded = expanded, onDismissRequest = { expanded = false }, modifier = Modifier.background(Color(0xFF1E293B))) {
            options.forEach { DropdownMenuItem(text = { Text(it, color = Color.White) }, onClick = { onSelect(it); expanded = false }) }
        }
    }
}

@Composable
fun FeatureGlassCard(icon: ImageVector, title: String, desc: String, glowColor: Color) {
    Surface(modifier = Modifier.fillMaxWidth().height(80.dp), shape = RoundedCornerShape(20.dp), color = Color.White.copy(0.08f), border = BorderStroke(1.dp, Color.White.copy(0.1f))) {
        Row(modifier = Modifier.padding(12.dp), verticalAlignment = Alignment.CenterVertically) {
            Box(contentAlignment = Alignment.Center) {
                Box(modifier = Modifier.size(44.dp).blur(15.dp).background(glowColor.copy(0.4f), CircleShape))
                Icon(icon, null, modifier = Modifier.size(28.dp), tint = Color.White)
            }
            Spacer(modifier = Modifier.width(16.dp))
            Column {
                Text(title, color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
                Text(desc, color = Color.White.copy(0.6f), fontSize = 12.sp)
            }
        }
    }
}

@Composable
fun RoleSegmentedToggle(selected: String, onSelect: (String) -> Unit) {
    Row(modifier = Modifier.width(260.dp).height(50.dp).clip(CircleShape).background(Color.White.copy(0.05f)).border(1.dp, Color.White.copy(0.1f), CircleShape)) {
        Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(CircleShape).background(if(selected == "Student") Color.White.copy(0.15f) else Color.Transparent).clickable { onSelect("Student") }, contentAlignment = Alignment.Center) {
            Text("Student", color = Color.White)
        }
        Box(modifier = Modifier.weight(1f).fillMaxHeight().clip(CircleShape).background(if(selected == "Teacher") Color.White.copy(0.15f) else Color.Transparent).clickable { onSelect("Teacher") }, contentAlignment = Alignment.Center) {
            Text("Teacher", color = Color.White)
        }
    }
}

@Composable
fun GlassInputField(v: String, onV: (String) -> Unit, label: String, icon: ImageVector, isP: Boolean = false) {
    TextField(
        value = v, onValueChange = onV, modifier = Modifier.fillMaxWidth().height(60.dp).clip(RoundedCornerShape(16.dp)).background(Color.White.copy(0.05f)).border(1.dp, Color.White.copy(0.1f), RoundedCornerShape(16.dp)),
        placeholder = { Text(label, color = Color.White.copy(0.4f)) }, leadingIcon = { Icon(icon, null, tint = Color.White.copy(0.6f)) },
        visualTransformation = if (isP) PasswordVisualTransformation() else androidx.compose.ui.text.input.VisualTransformation.None,
        colors = TextFieldDefaults.colors(focusedTextColor = Color.White, unfocusedTextColor = Color.White, focusedContainerColor = Color.Transparent, unfocusedContainerColor = Color.Transparent, focusedIndicatorColor = Color.Transparent, unfocusedIndicatorColor = Color.Transparent),
        singleLine = true
    )
}

@Composable
fun GradientSubmitButton(text: String, onClick: () -> Unit) {
    Box(modifier = Modifier.fillMaxWidth().height(60.dp).background(Brush.horizontalGradient(listOf(Color(0xFF6366F1), Color(0xFF4F46E5))), RoundedCornerShape(20.dp)).clickable { onClick() }, contentAlignment = Alignment.Center) {
        Text(text = text, color = Color.White, fontWeight = FontWeight.Bold)
    }
}

@Composable
fun AnimatedGeometricBackground() {
    val infiniteTransition = rememberInfiniteTransition(label = "bg")
    val animOffset by infiniteTransition.animateFloat(0f, 100f, infiniteRepeatable(tween(8000, easing = LinearEasing), RepeatMode.Reverse), label = "offset")
    Canvas(modifier = Modifier.fillMaxSize()) { drawGeometricShapes(animOffset) }
}

fun DrawScope.drawGeometricShapes(offset: Float) {
    drawCircle(color = Color(0xFF6366F1).copy(alpha = 0.05f), radius = 350f, center = Offset(size.width * 0.1f + offset, size.height * 0.2f - offset))
    drawCircle(color = Color(0xFF4F46E5).copy(alpha = 0.03f), radius = 550f, center = Offset(size.width * 0.9f - offset, size.height * 0.8f + offset))
}

@Composable
fun EduHabitTheme(content: @Composable () -> Unit) { MaterialTheme(content = content) }
