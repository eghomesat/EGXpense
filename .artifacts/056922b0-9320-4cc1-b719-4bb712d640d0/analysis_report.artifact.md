# EGExpensesV2 Project Analysis Report

## 1. Architecture Overview
- **Pattern**: MVVM (Model-View-ViewModel)
- **UI Framework**: Jetpack Compose
- **Navigation**: Single Activity with `androidx.navigation.compose`
- **Dependency Injection**: Manual injection via `AppModule` object
- **Data Layer**: Room Database for local persistence of expenses and categories
- **Settings Layer**: Preferences DataStore for configuration (PIN, Dark Mode)

## 2. Existing PIN Implementation
- **Storage**: Plain text string in DataStore (`user_pin`)
- **State**: Boolean in DataStore (`is_pin_enabled`)
- **Validation**: Performed in `ExpenseViewModel#verifyPin`
- **UI**: Handled via `AlertDialog` in `HomeScreen` and `SettingsScreen`

## 3. Current Settings Implementation
- **File**: [SettingsScreen.kt](file:///E:/AndroidApps/EGExpensesV2/app/src/main/java/com/example/egexpensesv2/screens/SettingsScreen.kt)
- **Component**: Reusable `SettingsItem` composable
- **Sections**: Security (PIN toggle), Appearance (Dark Mode), Data Management (Excel, Backup/Restore)

## 4. Firebase Status
- **Status**: **Not Integrated**
- **Requirements**:
    - Add `google-services` plugin and JSON
    - Add `firebase-auth` and `firebase-bom` dependencies
    - Configure SHA-1/SHA-256 in Firebase Console

## 5. Security Evaluation
- **Weakness**: PIN is stored in plain text.
- **Weakness**: No rate limiting or brute-force protection implemented yet.
- **Improvement**: Migrate to encrypted storage for the PIN.
- **Improvement**: Implement a secure Installation ID for device binding.

## 6. Recommended Integration Points
- **Settings**: Add "Reset PIN" item under "Security".
- **Navigation**: Define new routes for phone verification and new PIN setup.
- **DI**: Update `AppModule` to provide Firebase instances and secure storage.

## 7. Files to be Modified
- `gradle/libs.versions.toml`: Add Firebase and Security libs.
- `build.gradle.kts` (app/root): Apply Firebase plugins.
- `AppScreen.kt` & `NavGraph.kt`: Add recovery routes.
- `SettingsScreen.kt`: UI for Reset PIN.
- `DataStoreManager.kt`: Support for secure storage and recovery metadata.
- `ExpenseViewModel.kt`: Integration with recovery logic.

## 8. New Files to be Created
- `RecoveryViewModel.kt`: Core logic for Firebase Auth and Reset flow.
- `PhoneVerificationScreen.kt`: UI for OTP verification.
- `PinResetScreen.kt`: UI for creating a new secure PIN.
- `SecurityUtils.kt`: Installation ID generation and encryption helpers.

## 9. Potential Risks
- **Data Migration**: Ensuring existing plain-text PINs are handled safely.
- **Firebase Config**: Correct SHA-1 setup is critical for Phone Auth.
- **State Management**: Ensuring OTP session persists through rotation/backgrounding.
