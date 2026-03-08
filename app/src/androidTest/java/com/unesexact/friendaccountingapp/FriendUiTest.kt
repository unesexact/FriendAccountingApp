package com.unesexact.friendaccountingapp

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createAndroidComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import androidx.compose.ui.test.performTextInput
import androidx.test.ext.junit.runners.AndroidJUnit4
import com.unesexact.friendaccountingapp.ui.main.MainActivity
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith

@RunWith(AndroidJUnit4::class)
class FriendUiTest {

    @get:Rule
    val composeTestRule = createAndroidComposeRule<MainActivity>()

    @Test
    fun addFriend_displaysInList() {

        composeTestRule.onNodeWithText("+").performClick()

        composeTestRule.onNodeWithText("Name").performTextInput("John")

        composeTestRule.onNodeWithText("Add").performClick()

        composeTestRule.onNodeWithText("John").assertIsDisplayed()
    }
}