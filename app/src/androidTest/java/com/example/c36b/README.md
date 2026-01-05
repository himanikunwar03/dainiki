# Instrumented Tests for C36B App

This directory contains focused instrumented tests for the C36B Android application, covering the most essential functionality.

## Test Files Overview

### 1. `AuthInstrumentedTest.kt`
Tests for login functionality:
- ✅ Login screen UI elements display
- ✅ Valid credentials login
- ✅ Invalid credentials error handling

### 2. `RegisterInstrumentedTest.kt`
Tests for registration functionality:
- ✅ Registration screen UI elements display
- ✅ Valid input successful registration
- ✅ Invalid email validation

### 3. `PostInstrumentedTest.kt`
Tests for post functionality:
- ✅ Home screen UI elements display
- ✅ Create post functionality
- ✅ Delete post functionality

### 4. `TestHelper.kt`
Utility class providing:
- 🔧 Test data management
- 🔧 Firebase authentication setup
- 🔧 Test environment cleanup

### 5. `TestConfig.kt`
Configuration constants including:
- ⚙️ Test credentials
- ⚙️ Test data constants
- ⚙️ UI element test tags
- ⚙️ Test messages

## Setup Instructions

### Prerequisites
1. Android Studio with Android SDK
2. Firebase project configured
3. Test device or emulator

### Firebase Setup
1. Ensure your Firebase project is properly configured
2. Add `google-services.json` to the app directory
3. Configure Firebase Authentication and Realtime Database

## Running the Tests

### Run All Tests
```bash
./gradlew connectedAndroidTest
```

### Run Specific Test Class
```bash
./gradlew connectedAndroidTest --tests "com.example.c36b.AuthInstrumentedTest"
```

### Run Specific Test Method
```bash
./gradlew connectedAndroidTest --tests "com.example.c36b.AuthInstrumentedTest.testLogin_ValidCredentials_SuccessfulLogin"
```

## Test Structure

### Test Naming Convention
- `test[Feature]_[Scenario]_[ExpectedResult]`
- Example: `testLogin_ValidCredentials_SuccessfulLogin`

### Test Setup Pattern
```kotlin
@Before
fun setUp() {
    TestHelper.clearTestData()
}
```

## Adding Test Tags

To make tests more reliable, add test tags to your UI components:

```kotlin
OutlinedTextField(
    modifier = Modifier.testTag("email"),
    // ... other properties
)

OutlinedButton(
    modifier = Modifier.testTag("submit"),
    // ... other properties
) {
    Text("Login")
}
```

## Essential Test Tags

### Login Screen
- `email` - Email input field
- `password` - Password input field
- `submit` - Login button

### Registration Screen
- `name` - Name input field
- `reg_email` - Email input field
- `reg_password` - Password input field
- `signup_button` - Signup button

### Post Screen
- `title_input` - Post title input
- `content_input` - Post content input
- `publish_button` - Publish button
- `delete_button` - Delete button

## Best Practices

### 1. Test Isolation
- Each test should be independent
- Clear test data before tests
- Use unique test data to avoid conflicts

### 2. Test Reliability
- Use proper wait conditions (`waitForIdle()`)
- Handle asynchronous operations properly
- Add appropriate timeouts

### 3. Test Data Management
- Use `TestHelper` for common operations
- Generate unique test data
- Clean up test data after tests

## Troubleshooting

### Common Issues

1. **Firebase Connection Issues**
   - Check Firebase configuration
   - Verify network connectivity

2. **Test Timeouts**
   - Increase timeout values in `TestConfig`
   - Add proper wait conditions

3. **Element Not Found**
   - Verify test tags are added to UI components
   - Check element visibility
   - Ensure proper wait conditions

## Resources

- [Android Testing Documentation](https://developer.android.com/training/testing)
- [Jetpack Compose Testing](https://developer.android.com/jetpack/compose/testing)
- [Firebase Testing](https://firebase.google.com/docs/test-lab) 