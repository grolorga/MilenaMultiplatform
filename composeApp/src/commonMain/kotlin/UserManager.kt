object UserManager {
    private var userPreferences: UserPreferences? = null

    fun initialize(userPreferences: UserPreferences) {
        this.userPreferences = userPreferences
    }

    fun getUserPreferences(): UserPreferences? {
        return userPreferences
    }

    fun isUserLoggedIn(): Boolean {
        val login = userPreferences?.getUserLogin()
        val password = userPreferences?.getUserPassword()
        return !login.isNullOrEmpty() && !password.isNullOrEmpty()
    }
}

