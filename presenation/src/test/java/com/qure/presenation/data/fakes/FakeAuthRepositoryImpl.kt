package com.qure.presenation.data.fakes

import android.app.Activity
import com.facebook.AccessToken
import com.google.android.gms.tasks.OnFailureListener
import com.google.android.gms.tasks.OnSuccessListener
import com.google.android.gms.tasks.Task
import com.google.firebase.auth.AuthCredential
import com.google.firebase.auth.AuthResult
import com.google.firebase.auth.FirebaseUser
import com.qure.domain.repository.AuthRepository
import java.lang.Exception
import java.util.concurrent.Executor

class FakeAuthRepositoryImpl : AuthRepository {

    private lateinit var successTask: Task<AuthResult>
    private lateinit var failureTask: Task<AuthResult>
    private lateinit var firebaseUser: FirebaseUser
    private lateinit var messageTokenTask: Task<String>

    override fun signInWithGoogle(credential: AuthCredential): Task<AuthResult> {
        if (String.format(credential.provider) == "google.com") {
            successTask = object : Task<AuthResult>() {
                override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> {
                    TODO("Not yet implemented")
                }

                override fun addOnFailureListener(
                    p0: Activity,
                    p1: OnFailureListener
                ): Task<AuthResult> {
                    TODO("Not yet implemented")
                }

                override fun addOnFailureListener(
                    p0: Executor,
                    p1: OnFailureListener
                ): Task<AuthResult> {
                    TODO("Not yet implemented")
                }

                override fun getException(): Exception? {
                    TODO("Not yet implemented")
                }

                override fun getResult(): AuthResult {
                    TODO("Not yet implemented")
                }

                override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult {
                    TODO("Not yet implemented")
                }

                override fun isCanceled(): Boolean {
                    TODO("Not yet implemented")
                }

                override fun isComplete(): Boolean = true

                override fun isSuccessful(): Boolean = true
                override fun addOnSuccessListener(
                    p0: Executor,
                    p1: OnSuccessListener<in AuthResult>
                ): Task<AuthResult> {
                    TODO("Not yet implemented")
                }

                override fun addOnSuccessListener(
                    p0: Activity,
                    p1: OnSuccessListener<in AuthResult>
                ): Task<AuthResult> {
                    TODO("Not yet implemented")
                }

                override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {
                    return successTask
                }

            }
        }
        failureTask = object : Task<AuthResult>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<AuthResult> {
                TODO("Not yet implemented")
            }

            override fun addOnFailureListener(
                p0: Activity,
                p1: OnFailureListener
            ): Task<AuthResult> {
                return failureTask
            }

            override fun addOnFailureListener(
                p0: Executor,
                p1: OnFailureListener
            ): Task<AuthResult> {
                TODO("Not yet implemented")
            }

            override fun getException(): Exception? {
                TODO("Not yet implemented")
            }

            override fun getResult(): AuthResult {
                TODO("Not yet implemented")
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): AuthResult {
                TODO("Not yet implemented")
            }

            override fun isCanceled(): Boolean {
                TODO("Not yet implemented")
            }

            override fun isComplete(): Boolean = true
            override fun isSuccessful(): Boolean = false
            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in AuthResult>
            ): Task<AuthResult> {
                TODO("Not yet implemented")
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in AuthResult>
            ): Task<AuthResult> {
                TODO("Not yet implemented")
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in AuthResult>): Task<AuthResult> {
                TODO("Not yet implemented")
            }

        }
        return failureTask
    }

    override fun signInWithFacebook(token: AccessToken): Task<AuthResult> {
        if (token != null) {
            return successTask
        }
        return failureTask
    }

    override fun signOutUser() {
        TODO("Not yet implemented")
    }

    override suspend fun geMessageToken(): Task<String> {
        messageTokenTask = object : Task<String>() {
            override fun addOnFailureListener(p0: OnFailureListener): Task<String> {
                TODO("Not yet implemented")
            }

            override fun addOnFailureListener(p0: Activity, p1: OnFailureListener): Task<String> {
                TODO("Not yet implemented")
            }

            override fun addOnFailureListener(p0: Executor, p1: OnFailureListener): Task<String> {
                TODO("Not yet implemented")
            }

            override fun getException(): Exception? {
                TODO("Not yet implemented")
            }

            override fun getResult(): String {
                TODO("Not yet implemented")
            }

            override fun <X : Throwable?> getResult(p0: Class<X>): String {
                TODO("Not yet implemented")
            }

            override fun isCanceled(): Boolean {
                TODO("Not yet implemented")
            }

            override fun isComplete(): Boolean =true
            override fun isSuccessful(): Boolean = true

            override fun addOnSuccessListener(
                p0: Executor,
                p1: OnSuccessListener<in String>
            ): Task<String> {
                return messageTokenTask
            }

            override fun addOnSuccessListener(
                p0: Activity,
                p1: OnSuccessListener<in String>
            ): Task<String> {
                return messageTokenTask
            }

            override fun addOnSuccessListener(p0: OnSuccessListener<in String>): Task<String> {
                return messageTokenTask
            }
        }
        return messageTokenTask
    }
}