import android.content.Context
import android.content.SharedPreferences
import com.example.spendwise.Auth.SupabaseClientProvider
import io.github.jan.supabase.auth.auth
import io.github.jan.supabase.auth.providers.builtin.Email
import io.github.jan.supabase.exceptions.BadRequestRestException
import io.github.jan.supabase.postgrest.postgrest
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.serialization.json.JsonPrimitive
import kotlinx.serialization.json.buildJsonObject

class AuthRepository(private val context: Context) {

    private val sharedPreferences: SharedPreferences =
        context.getSharedPreferences("UserPrefs", Context.MODE_PRIVATE)

    suspend fun login(email: String, password: String): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            SupabaseClientProvider.supabase.auth.signInWith(Email) {
                this.email = email
                this.password = password
            }
            Result.success(Unit)
        } catch (e: BadRequestRestException) {
            Result.failure(Exception("Invalid email or password"))
        } catch (e: Exception) {
            Result.failure(e)
        }
    }

    suspend fun signup(
        firstName: String,
        lastName: String,
        email: String,
        password: String
    ): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            // 1. Create user authentication
            SupabaseClientProvider.supabase.auth.signUpWith(Email) {
                this.email = email
                this.password = password
                data = buildJsonObject {
                    put("first_name", JsonPrimitive(firstName))
                    put("last_name", JsonPrimitive(lastName))
                }
            }

            // 2. Get the current user ID after signup
            val userId = SupabaseClientProvider.supabase.auth.currentUserOrNull()?.id
                ?: throw Exception("User creation failed - no user ID available")

            // 3. Insert user profile into "profiles" table
            SupabaseClientProvider.supabase.postgrest["profiles"].insert(
                value = mapOf(
                    "id" to userId,
                    "first_name" to firstName,
                    "last_name" to lastName,
                    "email" to email
                )
            )

            // 4. Save signup completion flag
            setSignupCompletedFlag(true)

            Result.success(Unit)
        } catch (e: BadRequestRestException) {
            Result.failure(Exception("Registration failed: ${e.message}"))
        } catch (e: Exception) {
            Result.failure(Exception("Registration failed: ${e.message ?: "Unknown error"}"))
        }
    }

    // Save signup completion flag in SharedPreferences
    private fun setSignupCompletedFlag(isSignupCompleted: Boolean) {
        val editor = sharedPreferences.edit()
        editor.putBoolean("isSignupCompleted", isSignupCompleted)
        editor.apply()
    }

    // Check if the user has completed signup
    fun isSignupCompleted(): Boolean {
        return sharedPreferences.getBoolean("isSignupCompleted", false)
    }

    suspend fun signOut(): Result<Unit> = withContext(Dispatchers.IO) {
        try {
            SupabaseClientProvider.supabase.auth.signOut()
            Result.success(Unit)
        } catch (e: Exception) {
            Result.failure(e)
        }
    }
}
