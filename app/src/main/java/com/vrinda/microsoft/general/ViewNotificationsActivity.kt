package com.vrinda.microsoft.general

import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.view.View
import android.widget.Toast
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.vrinda.microsoft.R
import com.vrinda.microsoft.adapters.MissedNotificationsAdapter
import com.vrinda.microsoft.modals.NotificationData
import com.vrinda.microsoft.utils.AppPreferences
import kotlinx.android.synthetic.main.activity_view_notifications.*

class ViewNotificationsActivity : AppCompatActivity() {

    lateinit var notificationsList: ArrayList<NotificationData>
    lateinit var layoutManager: LinearLayoutManager
    lateinit var notificationAdapter: MissedNotificationsAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_view_notifications)

        notificationsList = ArrayList<NotificationData>()

        layoutManager = LinearLayoutManager(this)
        layoutManager.orientation = LinearLayoutManager.VERTICAL
        layoutManager.stackFromEnd = true
        layoutManager.reverseLayout = true

        animationView.visibility = View.VISIBLE
        recyclerLayout.visibility = View.GONE

        closeButton.setOnClickListener {
            val intent = Intent(this, HomeActivity::class.java)
            intent.flags = Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_SINGLE_TOP
            startActivity(intent)
        }


        retrieveNotificationData()

    }

    private fun retrieveNotificationData() {

        val myRef = FirebaseDatabase.getInstance().getReference(AppPreferences.studentID).child("notifications_data")
        myRef.keepSynced(true)
        val databaseListener = object : ValueEventListener{
            override fun onDataChange(snapshot: DataSnapshot) {
                if(snapshot.exists()) {

                    animationView.visibility = View.GONE
                    recyclerLayout.visibility = View.VISIBLE

                    for (ds in snapshot.children) {
                        val notificationData = ds.getValue(NotificationData::class.java)
                        if(notificationData!=null) {
                            notificationsList.add(notificationData)
                        }
                    }

                    notificationAdapter = MissedNotificationsAdapter(this@ViewNotificationsActivity, notificationsList)
                    notificationRecycler.layoutManager = layoutManager
                    notificationRecycler.setHasFixedSize(true)
                    notificationRecycler.adapter = notificationAdapter

                } else {
                    animationView.visibility = View.GONE
                    recyclerLayout.visibility = View.GONE


                    noDataAnimation.visibility = View.VISIBLE
                }
            }

            override fun onCancelled(error: DatabaseError) {
                animationView.visibility = View.GONE
                recyclerLayout.visibility = View.VISIBLE

                Toast.makeText(this@ViewNotificationsActivity,""+error.message.toString(),Toast.LENGTH_LONG).show()

            }
        }

        myRef.addValueEventListener(databaseListener)

    }
}