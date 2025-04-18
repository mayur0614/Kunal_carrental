package com.example.kunal_carrental


import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.tasks.await

class FirestoreRepository {

    private val db = FirebaseFirestore.getInstance()
    private val notesCollection = db.collection("notes")

    suspend fun addNote(note: Note) {
        val newDocRef = notesCollection.document()
        val noteWithId = note.copy(id = newDocRef.id)
        newDocRef.set(noteWithId).await()
    }

    suspend fun getNotes(): List<Note> {
        return try {
            val snapshot = notesCollection.get().await()
            snapshot.documents.mapNotNull { it.toObject(Note::class.java) }
        } catch (e: Exception) {
            emptyList()
        }
    }
}
