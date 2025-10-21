package com.plcoding.biometricauth

import android.os.Build
import androidx.fragment.app.FragmentActivity
import androidx.biometric.BiometricManager
import androidx.biometric.BiometricManager.Authenticators.BIOMETRIC_STRONG
import androidx.biometric.BiometricManager.Authenticators.DEVICE_CREDENTIAL
import androidx.biometric.BiometricPrompt
import androidx.core.content.ContextCompat
import kotlinx.coroutines.channels.Channel
import kotlinx.coroutines.flow.receiveAsFlow
import java.util.concurrent.Executor

class BiometricPromptManager(
    private val activity: FragmentActivity
) {
    private val resultChannel = Channel<BiometricResult>()
    val promptResults = resultChannel.receiveAsFlow()

    fun showBiometricPrompt(
        title: String,
        description: String
    ) {
        val manager = BiometricManager.from(activity)
        val authenticators = if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
            BIOMETRIC_STRONG or DEVICE_CREDENTIAL
        } else {
            BIOMETRIC_STRONG
        }

        val promptInfo = BiometricPrompt.PromptInfo.Builder()
            .setTitle(title)
            .setDescription(description)
            .setAllowedAuthenticators(authenticators)
            .apply {
                if (Build.VERSION.SDK_INT < Build.VERSION_CODES.R) {
                    setNegativeButtonText("Cancel")
                }
            }
            .build()

        // Controlla la disponibilità del sensore biometrico
        when (manager.canAuthenticate(authenticators)) {
            BiometricManager.BIOMETRIC_ERROR_HW_UNAVAILABLE ->
                resultChannel.trySend(BiometricResult.HardwareUnavailable)
            BiometricManager.BIOMETRIC_ERROR_NO_HARDWARE ->
                resultChannel.trySend(BiometricResult.FeatureUnavailable)
            BiometricManager.BIOMETRIC_ERROR_NONE_ENROLLED ->
                resultChannel.trySend(BiometricResult.AuthenticationNotSet)
            else -> Unit
        }

        val executor: Executor = ContextCompat.getMainExecutor(activity)
        val prompt = BiometricPrompt(
            activity,
            executor,
            object : BiometricPrompt.AuthenticationCallback() {
                override fun onAuthenticationError(errorCode: Int, errString: CharSequence) {
                    resultChannel.trySend(BiometricResult.AuthenticationError(errString.toString()))
                }

                override fun onAuthenticationSucceeded(result: BiometricPrompt.AuthenticationResult) {
                    resultChannel.trySend(BiometricResult.AuthenticationSuccess)
                }

                override fun onAuthenticationFailed() {
                    resultChannel.trySend(BiometricResult.AuthenticationFailed)
                }
            }
        )

        prompt.authenticate(promptInfo)
    }

    sealed interface BiometricResult {
        data object HardwareUnavailable : BiometricResult
        data object FeatureUnavailable : BiometricResult
        data class AuthenticationError(val error: String) : BiometricResult
        data object AuthenticationFailed : BiometricResult
        data object AuthenticationSuccess : BiometricResult
        data object AuthenticationNotSet : BiometricResult
    }
}
