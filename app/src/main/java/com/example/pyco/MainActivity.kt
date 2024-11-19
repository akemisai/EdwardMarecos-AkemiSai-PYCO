package com.example.pyco

import android.app.Application
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.Button
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.viewModelScope
import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.Room
import androidx.room.RoomDatabase
import com.example.pyco.ui.theme.PycoTheme
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            PycoTheme {
                // Obtain the UserViewModel
                val userViewModel: UserViewModel = UserViewModel(application = application)
                // Display the UserScreen
                UserScreen(viewModel = userViewModel)
            }
        }
    }
}

@Entity(tableName = "user")
data class User(
    @PrimaryKey val userId: String, // Unique identifier
    val username: String, // Username
    val email: String, // Email
    val profilePicture: String? = null, // URL for profile picture (optional)
    val bio: String? = null, // Bio (optional)
    val followerCount: Int = 0, // Number of followers
    val followingCount: Int = 0, // Number of people the user follows
    val totalLikes: Int = 0, // Total likes received
    val totalBookmarks: Int = 0, // Total bookmarks received
    val totalAwards: Int = 0, // Total awards won
    val dateJoined: Long = System.currentTimeMillis() // Timestamp for joining date
)

@Dao    //data access object
interface UserDao {
    // Insert a new user into the database or replace if there is a conflict (e.g., same primary key)
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertUser(user: User)

    // Query to get a specific user by their unique ID; returns LiveData so UI can observe changes
    @Query("SELECT * FROM user WHERE userId = :id")
    fun getUserById(id: String): LiveData<User>

    // Query to get all users from the database; returns LiveData to observe data changes
    @Query("SELECT * FROM user")
    fun getAllUsers(): LiveData<List<User>>
}

// Marks this as a Room database with an entity (User) and version 1
@Database(entities = [User::class], version = 1, exportSchema = false)
abstract class AppDatabase : RoomDatabase() {
    // Abstract method to get the UserDao instance
    abstract fun userDao(): UserDao

    companion object {
        // ensures that the INSTANCE variable is always up-to-date and visible to all threads
        @Volatile
        private var INSTANCE: AppDatabase? = null

        // Returns the singleton instance of the Room database
        fun getDatabase(context: Context): AppDatabase {
            // Synchronized block ensures only one instance is created
            return INSTANCE ?: synchronized(this) {
                // Create the database instance if it doesn't exist
                val instance = Room.databaseBuilder(
                    context.applicationContext,
                    AppDatabase::class.java,
                    "app_database" // Name of the database file
                ).build()
                INSTANCE = instance // Assigns the instance to the variable
                instance // Returns the instance
            }
        }
    }
}

// Repository class handles data operations and abstracts them from the ViewModel
class UserRepository(private val userDao: UserDao) {
    // LiveData list of all users observed by the ViewModel
    val allUsers: LiveData<List<User>> = userDao.getAllUsers()

    // Coroutine function to insert a user into the database
    suspend fun insert(user: User) {
        userDao.insertUser(user)
    }

    // Function to get a user by their ID; returns LiveData
    fun getUserById(id: String): LiveData<User> {
        return userDao.getUserById(id)
    }
}

// ViewModel connects the UI to the repository and holds app data
class UserViewModel(application: Application) : AndroidViewModel(application) {
    private val repository: UserRepository // Instance of the repository
    val allUsers: LiveData<List<User>> // LiveData list of all users

    init {
        // Initialize the repository and DAO instance
        val userDao = AppDatabase.getDatabase(application).userDao()
        repository = UserRepository(userDao)
        allUsers = repository.allUsers // Assigns all users LiveData
    }

    // Function to insert a user; uses ViewModel scope for coroutines
    fun insert(user: User) = viewModelScope.launch(Dispatchers.IO) {
        repository.insert(user)
    }

    // Function to get a user by ID; returns LiveData
    fun getUserById(id: String): LiveData<User> {
        return repository.getUserById(id)
    }
}

@Composable
fun UserScreen(viewModel: UserViewModel) {
    // Observe the LiveData list of all users
    val allUsers by viewModel.allUsers.observeAsState(emptyList())

    // Variables to hold input for user details
    var username by remember { mutableStateOf("") }
    var email by remember { mutableStateOf("") }
    var bio by remember { mutableStateOf("") }

    Column(modifier = Modifier.padding(16.dp)) {
        // Text fields for user input
        OutlinedTextField(
            value = username,
            onValueChange = { username = it },
            label = { Text("Username") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = email,
            onValueChange = { email = it },
            label = { Text("Email") },
            modifier = Modifier.fillMaxWidth()
        )
        OutlinedTextField(
            value = bio,
            onValueChange = { bio = it },
            label = { Text("Bio") },
            modifier = Modifier.fillMaxWidth()
        )

        // Button to insert a new user
        Button(
            onClick = {
                if (username.isNotEmpty() && email.isNotEmpty()) {
                    val newUser = User(
                        userId = System.currentTimeMillis().toString(), // Simple unique ID for demo
                        username = username,
                        email = email,
                        bio = bio
                    )
                    viewModel.insert(newUser)
                    username = ""
                    email = ""
                    bio = ""
                }
            },
            modifier = Modifier.padding(top = 8.dp)
        ) {
            Text("Add User")
        }

        // Display all users
        LazyColumn(modifier = Modifier.padding(top = 16.dp)) {
            items(allUsers) { user ->
                Text(text = "Username: ${user.username}, Email: ${user.email}", modifier = Modifier.padding(4.dp))
            }
        }
    }
}

