package com.example.acexproyecto.fragment

import android.app.Dialog
import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.fragment.app.DialogFragment
import com.example.acexproyecto.R
import com.example.acexproyecto.model.Usuario
import com.example.acexproyecto.views.MsalAppHolder
import com.microsoft.graph.requests.GraphServiceClient
import com.microsoft.identity.client.AcquireTokenParameters
import com.microsoft.identity.client.AuthenticationCallback
import com.microsoft.identity.client.IAuthenticationResult
import com.microsoft.identity.client.Prompt
import com.microsoft.identity.client.exception.MsalException
import java.util.concurrent.CompletableFuture
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import com.microsoft.graph.http.GraphErrorResponse
import com.microsoft.graph.http.GraphServiceException
import java.io.File
import java.io.FileOutputStream

class LoginDialogFragment(private val onSuccess: (IAuthenticationResult, String, String) -> Unit) : DialogFragment() {

    private var isSuccessCalled = false
    private lateinit var listener: LoginDialogListener
    private var isLoading = mutableStateOf(false)

    override fun onAttach(context: Context) {
        super.onAttach(context)
        listener = context as LoginDialogListener
    }

    override fun onCreateDialog(savedInstanceState: Bundle?): Dialog {
        return Dialog(requireContext(), R.style.Theme_AppACEX_Dialog)
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_login_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        isLoading.value = true

        val graphScopes = listOf(
            "User.Read",
            "Calendars.Read"
        )

        val apiScopes = listOf(
            "api://7c80ff29-dc1d-47a3-9cc3-78997d1de943/access_as_user"
        )


        val parameters = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(requireActivity())
            .withScopes(graphScopes) // Add the required permissions
            .withPrompt(Prompt.SELECT_ACCOUNT)
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    Usuario.msalToken = authenticationResult.accessToken
                    isLoading.value = false
                    if (!isSuccessCalled) {
                        isSuccessCalled = true
                        Log.d("LoginDialogFragment", "Authentication successful")
                        dismissAllowingStateLoss()
                    }
                }

                override fun onError(exception: MsalException) {
                    isLoading.value = false
                    Log.e("LoginDialogFragment", "Authentication error", exception)
                    dismissAllowingStateLoss()
                }

                override fun onCancel() {
                    isLoading.value = false
                    Log.d("LoginDialogFragment", "Authentication canceled")
                    dismissAllowingStateLoss()
                }
            })
            .build()

        MsalAppHolder.msalApp?.acquireToken(parameters)

        val apiParameters = AcquireTokenParameters.Builder()
            .startAuthorizationFromActivity(requireActivity())
            .withScopes(apiScopes)
            .withPrompt(Prompt.SELECT_ACCOUNT)
            .withCallback(object : AuthenticationCallback {
                override fun onSuccess(authenticationResult: IAuthenticationResult) {
                    Usuario.apiToken = authenticationResult.accessToken
                }

                override fun onError(exception: MsalException) {
                    // Handle authentication error
                    Log.e("Authentication", "Custom API Error: ${exception.message}")
                }

                override fun onCancel() {
                    // Handle user canceling the authentication
                    Log.d("Authentication", "Custom API Authentication canceled")
                }
            })
            .build()

        MsalAppHolder.msalApp?.acquireToken(apiParameters)
    }




    interface LoginDialogListener {
        fun onLoginSuccess(authenticationResult: IAuthenticationResult, displayName: String, photoPath: String)
    }
}