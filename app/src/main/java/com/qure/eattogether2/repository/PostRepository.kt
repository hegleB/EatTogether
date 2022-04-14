package com.qure.eattogether2.repository

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore
import com.google.firebase.firestore.Query
import com.google.firebase.firestore.ktx.getField
import com.qure.eattogether2.data.Comments
import com.qure.eattogether2.data.Post
import com.qure.eattogether2.data.User
import kotlinx.coroutines.channels.awaitClose
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.callbackFlow
import kotlinx.coroutines.tasks.await
import java.lang.Exception
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class PostRepository @Inject constructor(
    private val firestore: FirebaseFirestore
) {

    fun getAllPosts() :Flow<List<Post>> {

        return callbackFlow {
             val callback = firestore.collection("posts").orderBy("timestamp",Query.Direction.DESCENDING)
                 .addSnapshotListener { snapshots, e ->
                if (e != null) {
                    close(e)
                } else {

                    offer(snapshots!!.toObjects(Post::class.java))

                }
            }
            awaitClose{
                callback.remove()
            }
        }
    }

    fun getCategoryPosts(category : String) : Flow<List<Post>> {

        return callbackFlow {

            val callback = firestore.collection("posts").whereEqualTo("category",category).addSnapshotListener {
                snapshot, e ->

                if(e!=null){
                    close(e)
                } else {
                    offer(snapshot!!.toObjects(Post::class.java))
                }
            }
            awaitClose{
                callback.remove()
            }
        }
    }


    fun getAllComments(postkey: String) : Flow<List<Comments>> {

        return callbackFlow {
            val callback = firestore.collection("comments").whereEqualTo("comments_postkey",postkey)
                .whereEqualTo("comments_depth",0)
                .orderBy("comments_timestamp",Query.Direction.ASCENDING).orderBy("comments_replyTimeStamp",Query.Direction.ASCENDING)
                .addSnapshotListener{ snapshots, e ->

                    if(e!=null){
                        close(e)
                    } else {
                        offer(snapshots!!.toObjects(Comments::class.java))
                    }

                }
            awaitClose{
                callback.remove()
            }
        }

    }


    fun getReply(postkey: String,commentsId : String) : Flow<List<Comments>>{

        return callbackFlow {

            val callback =  firestore.collectionGroup("reply")
                .whereEqualTo("comments_postkey", postkey)
                .whereEqualTo("comments_depth", 1)
                .whereEqualTo("comments_commentskey",commentsId)
                .orderBy("comments_timestamp", Query.Direction.ASCENDING)
                .orderBy("comments_replyTimeStamp", Query.Direction.ASCENDING)
                .addSnapshotListener { snapshots, e ->

                    if(e!=null){
                        close(e)
                    } else {
                        offer(snapshots!!.toObjects(Comments::class.java))
                    }

                }
            awaitClose{
                callback.remove()
            }
        }
    }

    companion object {
        const val TAG = "Post Repository"
    }

}