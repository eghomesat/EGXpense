# Implementation Plan: Master PIN Integration & Universal Privacy Mode

This plan focuses on making the PIN Protection's PIN the master PIN for all app interactions and ensuring sensitive amounts are hidden by default when PIN protection is active.

## Proposed Changes

### 1. Settings Screen Cleanup [MODIFY]
- **File**: [SettingsScreen.kt](file:///E:/AndroidApps/EGExpensesV2/app/src/main/java/com/example/egexpensesv2/screens/SettingsScreen.kt)
- **Change**: Ensure "Reset PIN" label is completely removed. (Verified: Already removed in latest version).

### 2. Universal Master PIN Enforcement [MODIFY]
- **File**: [ExpenseViewModel.kt](file:///E:/AndroidApps/EGExpensesV2/app/src/main/java/com/example/egexpensesv2/viewmodel/ExpenseViewModel.kt)
- **Change**: Refine `verifyPin` to strictly use the hardware-encrypted PIN from `SecurityUtils`.
- **Change**: Ensure `isPrivacyMode` is initialized based on the `isPinEnabled` flag.

### 3. Privacy Mode Implementation in Expense List [MODIFY]
- **File**: [ViewExpensesScreen.kt](file:///E:/AndroidApps/EGExpensesV2/app/src/main/java/com/example/egexpensesv2/screens/ViewExpensesScreen.kt)
- **Change**: Add an Eye icon button to the TopAppBar.
- **Change**: Respect `isPrivacyMode` for all expense cards, hiding amounts as `****` by default.
- **Change**: Tapping the Eye icon will trigger the same Master PIN verification flow.

### 4. Recent Expenses Refresh [MODIFY]
- **File**: [HomeScreen.kt](file:///E:/AndroidApps/EGExpensesV2/app/src/main/java/com/example/egexpensesv2/screens/HomeScreen.kt)
- **Change**: Ensure the list items also show the amount (obscured when privacy is ON) alongside the category and time.

## Verification Plan

### Manual Verification
- **Default State**: Enable PIN protection -> Restart App -> Verify all amounts in Home, Reports, and View Expenses are `****`.
- **Master PIN Usage**: Tap any eye icon -> Enter the Master PIN -> Verify amounts are revealed across all screens.
- **Security**: Verify that entering a wrong PIN still blocks visibility and shows the professional error dialog.
