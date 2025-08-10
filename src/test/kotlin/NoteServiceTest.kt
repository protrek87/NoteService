import org.junit.Test
import kotlin.test.*

class NoteServiceTest {
    private val service = NoteService()

    @Test
    fun addNote() {
        val noteId = service.add("Test title", "Test text")
        assertTrue(noteId > 0)
    }

    @Test
    fun getNoteById() {
        val noteId = service.add("Test title", "Test text")
        val note = service.getById(noteId)
        assertNotNull(note)
        assertEquals("Test title", note.title)
    }

    @Test
    fun editNote() {
        val noteId = service.add("Test title", "Test text")
        assertTrue(service.edit(noteId, "New title", "New text"))
        val note = service.getById(noteId)
        assertEquals("New title", note?.title)
    }

    @Test
    fun deleteNote() {
        val noteId = service.add("Test title", "Test text")
        assertTrue(service.delete(noteId))
        assertNull(service.getById(noteId))
    }

    @Test
    fun createComment() {
        val noteId = service.add("Test title", "Test text")
        val commentId = service.createComment(noteId, "Test comment")
        assertTrue(commentId > 0)
    }

    @Test(expected = NoteNotFoundException::class)
    fun createCommentToDeletedNote() {
        val noteId = service.add("Test title", "Test text")
        service.delete(noteId)
        service.createComment(noteId, "Test comment")
    }

    @Test
    fun getComments() {
        val noteId = service.add("Test title", "Test text")
        service.createComment(noteId, "Comment 1")
        service.createComment(noteId, "Comment 2")
        val comments = service.getComments(noteId)
        assertEquals(2, comments.size)
    }

    @Test
    fun deleteComment() {
        val noteId = service.add("Test title", "Test text")
        val commentId = service.createComment(noteId, "Test comment")
        assertTrue(service.deleteComment(commentId))
        assertEquals(0, service.getComments(noteId).size)
    }

    @Test
    fun restoreComment() {
        val noteId = service.add("Test title", "Test text")
        val commentId = service.createComment(noteId, "Test comment")
        service.deleteComment(commentId)
        assertTrue(service.restoreComment(commentId))
        assertEquals(1, service.getComments(noteId).size)
    }

    @Test(expected = NoteNotFoundException::class)
    fun restoreCommentToDeletedNote() {
        val noteId = service.add("Test title", "Test text")
        val commentId = service.createComment(noteId, "Test comment")
        service.delete(noteId)
        service.restoreComment(commentId)
    }

    @Test
    fun editComment() {
        val noteId = service.add("Test title", "Test text")
        val commentId = service.createComment(noteId, "Test comment")
        assertTrue(service.editComment(commentId, "Updated comment"))
        val comments = service.getComments(noteId)
        assertEquals("Updated comment", comments[0].message)
    }

    @Test
    fun getNotes() {
        service.add("Note 1", "Text 1")
        service.add("Note 2", "Text 2")
        val notes = service.get()
        assertEquals(2, notes.size)
    }
}