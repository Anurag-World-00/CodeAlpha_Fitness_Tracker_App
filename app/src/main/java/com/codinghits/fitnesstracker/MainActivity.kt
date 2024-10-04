package com.codinghits.fitnesstracker
import android.os.Bundle
import android.text.TextUtils
import android.util.Log
import android.view.View
import android.widget.Button
import android.widget.EditText
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.firebase.database.*

class MainActivity : AppCompatActivity() {

    private lateinit var editTextType: EditText
    private lateinit var editTextDuration: EditText
    private lateinit var buttonAddWorkout: Button
    private lateinit var recyclerView: RecyclerView
    private lateinit var workoutAdapter: WorkoutAdapter
    private lateinit var database: DatabaseReference
    private val workoutList = mutableListOf<Workout>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        editTextType = findViewById(R.id.editTextType)
        editTextDuration = findViewById(R.id.editTextDuration)
        buttonAddWorkout = findViewById(R.id.buttonAddWorkout)
        recyclerView = findViewById(R.id.recyclerViewWorkouts)

        recyclerView.layoutManager = LinearLayoutManager(this)
        workoutAdapter = WorkoutAdapter(workoutList)
        recyclerView.adapter = workoutAdapter

        database = FirebaseDatabase.getInstance().getReference("workouts")

        buttonAddWorkout.setOnClickListener {
            addWorkout()
        }

        fetchWorkouts()
    }

    private fun fetchWorkouts() {
        database.addValueEventListener(object : ValueEventListener {
            override fun onDataChange(snapshot: DataSnapshot) {
                workoutList.clear()
                for (data in snapshot.children) {
                    val workout = data.getValue(Workout::class.java)
                    if (workout != null) {
                        workoutList.add(workout)
                    }
                }
                workoutAdapter.notifyDataSetChanged()
            }

            override fun onCancelled(error: DatabaseError) {
                Log.e("MainActivity", "Failed to read workouts.", error.toException())
            }
        })
    }

    private fun addWorkout() {
        val type = editTextType.text.toString().trim()
        val duration = editTextDuration.text.toString().trim()

        if (TextUtils.isEmpty(type) || TextUtils.isEmpty(duration)) {
            return
        }

        val newWorkout = Workout(
            id = database.push().key ?: "",
            type = type,
            duration = duration.toInt()
        )

        database.child(newWorkout.id).setValue(newWorkout)

        editTextType.text.clear()
        editTextDuration.text.clear()
    }
}
