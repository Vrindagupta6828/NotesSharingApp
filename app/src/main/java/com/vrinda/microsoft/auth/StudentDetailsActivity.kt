package com.vrinda.microsoft.auth

import android.content.Intent
import android.os.Bundle
import android.widget.ArrayAdapter
import android.widget.AutoCompleteTextView
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.vrinda.microsoft.R
import com.vrinda.microsoft.general.HomeActivity
import com.vrinda.microsoft.modals.StudentData
import com.vrinda.microsoft.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_student_details.*
import java.util.regex.Matcher
import java.util.regex.Pattern

class StudentDetailsActivity : AppCompatActivity() {
    private lateinit var database: DatabaseReference

    private var COLLEGE_NAME_GMRIT = Pair("GMR Institute of Technology", "GMRIT")

    private var CSE_DEPT = Pair("Computer Science Engineering", "CSE")
    private var IT_DEPT = Pair("Information Technology", "IT")
    private var ECE_DEPT = Pair("Electronics and Communication Engineering", "ECE")
    private var EEE_DEPT = Pair("Electrical and Electronics Engineering","EEE")
    private var CIV_DEPT = Pair("Civil Engineering","CIV")
    private var MECH_DEPT = Pair("Mechanical Engineering","MECH")
    private var CHEM_DEPT = Pair("Chemical Engineering","CHEM")

    private var FIRST_YEAR = Pair("1st Year", "1")
    private var SECOND_YEAR = Pair("2nd Year", "2")
    private var THIRD_YEAR = Pair("3rd Year", "3")
    private var FOURTH_YEAR = Pair("4th Year", "4")

    private var A_SECTION = Pair("A Section", "A")
    private var B_SECTION = Pair("B Section", "B")
    private var C_SECTION = Pair("C Section", "C")

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_student_details)
        setUpCollegeList()
        setUpDepartmentList()
        setUpSectionList()
        setUpYearList()

        database = Firebase.database.reference

        AppPreferences.init(this)

        btnSubmit.setOnClickListener {
            val studentName = editName.text.toString().trim()
            val studentPhoneNumber = editPhone.text.toString().trim()
            val collegeName = collegeList.text.toString().trim()
            val graduationYear = yearsList.text.toString().trim()
            val studentDept = deptSpinner.text.toString().trim()
            val studentSection = sectionSpinner.text.toString().trim()

            //Phone Number (Only 10 Digits length!=10) and Name (only Characters allowed) Validation
            if(isNullOrEmpty(studentName)) {
                edtName.error = "Please enter Name"
            } else if(!isValidName(studentName.toString())) {
                edtName.error = null
                edtName.error = "Name should be only Alphabets"
            }else if(isNullOrEmpty(studentPhoneNumber)) {
                edtName.error = null
                edtPhone.error = "Please enter Phone Number"
            } else if(editPhone.length() !=10) {
                edtPhone.error = null
                edtPhone.error = "Please enter Valid Phone Number "
            } else if(isNullOrEmpty(collegeName)) {
                edtPhone.error = null
                edtCollegeName.error = "Please choose College Name"
            } else if(isNullOrEmpty(graduationYear)) {
                edtCollegeName.error = null
                edtGraduationYear.error = "Please choose current Studying Year"
            } else if(isNullOrEmpty(studentDept)) {
                edtGraduationYear.error = null
                edtDepartment.error = "Please choose Department"
            } else if(isNullOrEmpty(studentSection)) {
                edtDepartment.error = null
                edtSection.error = "Please choose Section"
            } else {
                edtSection.error = null
                btnSubmit.isEnabled = false

                val intent=intent
                val email=intent.getStringExtra("Email")
                val provider = intent.getStringExtra("provider")

                val collegeID: String
                val deptID: String
                val yearID: String
                val sectionID: String

                if(studentDept.equals("Computer Science Engineering")) {
                    deptID = "CSE"
                } else if(studentDept.equals("Electronics and Communication Engineering")) {
                    deptID = "ECE"
                } else if(studentDept.equals("Electrical and Electronics Engineering")) {
                    deptID = "EEE"
                }  else if(studentDept.equals("Civil Engineering")) {
                    deptID = "CIV"
                }  else if(studentDept.equals("Mechanical Engineering")) {
                    deptID = "MECH"
                }  else if(studentDept.equals("Chemical Engineering")) {
                    deptID = "CHEM"
                }
                else {
                    deptID = IT_DEPT.second
                }

                if(graduationYear.toString().equals("1st Year")){
                    yearID = FIRST_YEAR.second
                }
                else if(graduationYear.toString().equals("2nd Year")){
                    yearID = SECOND_YEAR.second
                }
                else if(graduationYear.toString().equals("3rd Year")){
                    yearID = THIRD_YEAR.second
                }
                else {
                    yearID = FOURTH_YEAR.second
                }

                if(studentSection.toString().equals("A Section")){
                    sectionID = A_SECTION.second
                }
                else if(studentSection.toString().equals("B Section")){
                    sectionID = B_SECTION.second
                }
                else {
                    sectionID = C_SECTION.second
                }

                var userID = "GMRIT"+"_"+deptID + "_"+ yearID+"_"+sectionID


                //Should send Email ID from Authentication Activity and pass Parameter to Database
                writeNewUser(
                   userID,
                   studentName,
                   email,
                   studentPhoneNumber,
                   collegeName,
                   graduationYear,
                   studentDept,
                   studentSection,
                   provider
                )

            }
        }
    }

    private fun writeNewUser(
        userId: String,
        name: String,
        email: String?,
        phone: String,
        collegeName: String,
        graduationYear: String,
        studentDept: String,
        studentSection: String,
        provider: String?
    ) {
        val user = StudentData(userId, name, email.toString(), phone, collegeName, graduationYear,
                                 studentDept, studentSection, provider.toString())
        database.child("students_data").push().setValue(user)

        AppPreferences.isLogin = true
        AppPreferences.studentName = name
        AppPreferences.studentID = userId
        AppPreferences.studentEmailID = email.toString()

        // subscribing the student to his class topic
        FirebaseMessaging.getInstance().subscribeToTopic("/topics/" + AppPreferences.studentID)

        showToast("Details Submitted Successfully !")

        val intent = Intent(this, HomeActivity::class.java)
        intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
        startActivity(intent)
        finish()


    }

    private fun isNullOrEmpty(str: String): Boolean {
        if (str != null && str.trim().isNotEmpty())
            return false
        return true
    }

    private fun showToast(message: String) {
        Toast.makeText(this, message, Toast.LENGTH_SHORT).show()
    }

    private fun setUpCollegeList() {
        val collegeNames = listOf(COLLEGE_NAME_GMRIT.first)
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item, collegeNames
        )
        (collegeList as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setUpYearList() {
        val yearNumbers = listOf(
            FIRST_YEAR.first,
            SECOND_YEAR.first,
            THIRD_YEAR.first,
            FOURTH_YEAR.first
        )
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item, yearNumbers
        )
        (yearsList as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setUpDepartmentList() {
        val deptNames = listOf(CSE_DEPT.first, IT_DEPT.first,ECE_DEPT.first,EEE_DEPT.first,CIV_DEPT.first,MECH_DEPT.first,CHEM_DEPT.first)
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item, deptNames
        )
        (deptSpinner as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun setUpSectionList() {
        val sectionNames = listOf(A_SECTION.first, B_SECTION.first, C_SECTION.first)
        val adapter = ArrayAdapter(
            this,
            R.layout.list_item, sectionNames
        )
        (sectionSpinner as? AutoCompleteTextView)?.setAdapter(adapter)
    }

    private fun isValidName(name: String): Boolean {
        val NAME_PATTERN = ("^[A-Z a-z]+$")
        val pattern: Pattern = Pattern.compile(NAME_PATTERN)
        val matcher: Matcher = pattern.matcher(name)
        return matcher.matches()
    }

    private infix fun Boolean.and(length: Int): Boolean {
        return length != 10
    }
}