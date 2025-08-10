class NoteService {
    private val notes = mutableListOf<Note>()
    private val comments = mutableListOf<Comment>()
    private var currentUserId: Int = 1 // Текущий пользователь (для упрощения)

    // Добавление новой заметки
    fun add(title: String, text: String): Int {
        val note = Note(
            id = generateId(notes),
            ownerId = currentUserId,
            title = title,
            text = text,
            date = System.currentTimeMillis(),
            isDeleted = false
        )
        notes.add(note)
        return note.id
    }

    // Создание комментария к заметке
    fun createComment(noteId: Int, message: String): Int {
        val note = notes.find { it.id == noteId && !it.isDeleted }
            ?: throw NoteNotFoundException("Note with id $noteId not found or deleted")

        val comment = Comment(
            id = generateId(comments),
            noteId = noteId,
            ownerId = currentUserId,
            message = message,
            date = System.currentTimeMillis(),
            isDeleted = false
        )
        comments.add(comment)
        return comment.id
    }

    // Удаление заметки
    fun delete(noteId: Int): Boolean {
        val note = notes.find { it.id == noteId && it.ownerId == currentUserId && !it.isDeleted }
            ?: return false

        note.isDeleted = true
        // Помечаем все комментарии к заметке как удалённые
        comments.filter { it.noteId == noteId }.forEach { it.isDeleted = true }
        return true
    }

    // Удаление комментария
    fun deleteComment(commentId: Int): Boolean {
        val comment = comments.find { it.id == commentId && it.ownerId == currentUserId && !it.isDeleted }
            ?: return false

        comment.isDeleted = true
        return true
    }

    // Редактирование заметки
    fun edit(noteId: Int, title: String, text: String): Boolean {
        val note = notes.find { it.id == noteId && it.ownerId == currentUserId && !it.isDeleted }
            ?: return false

        note.title = title
        note.text = text
        return true
    }

    // Редактирование комментария
    fun editComment(commentId: Int, message: String): Boolean {
        val comment = comments.find { it.id == commentId && it.ownerId == currentUserId && !it.isDeleted }
            ?: return false

        comment.message = message
        return true
    }

    // Получение списка заметок пользователя
    fun get(): List<Note> {
        return notes.filter { it.ownerId == currentUserId && !it.isDeleted }
    }

    // Получение заметки по ID
    fun getById(noteId: Int): Note? {
        return notes.find { it.id == noteId && it.ownerId == currentUserId && !it.isDeleted }
    }

    // Получение комментариев к заметке
    fun getComments(noteId: Int): List<Comment> {
        if (notes.none { it.id == noteId && !it.isDeleted }) {
            throw NoteNotFoundException("Note with id $noteId not found or deleted")
        }
        return comments.filter { it.noteId == noteId && !it.isDeleted }
    }

    // Восстановление комментария
    fun restoreComment(commentId: Int): Boolean {
        val comment = comments.find { it.id == commentId && it.ownerId == currentUserId && it.isDeleted }
            ?: return false

        // Проверяем, что заметка, к которой относится комментарий, не удалена
        if (notes.none { it.id == comment.noteId && !it.isDeleted }) {
            throw NoteNotFoundException("Cannot restore comment - parent note is deleted")
        }

        comment.isDeleted = false
        return true
    }

    private fun <T : Deletable> generateId(items: List<T>): Int {
        return (items.maxOfOrNull { it.id } ?: 0) + 1
    }
}

// Базовый интерфейс для сущностей, которые могут быть "удалены"
interface Deletable {
    val id: Int
    var isDeleted: Boolean
}

// Класс заметки
data class Note(
    override val id: Int,
    val ownerId: Int,
    var title: String,
    var text: String,
    val date: Long,
    override var isDeleted: Boolean
) : Deletable

// Класс комментария
data class Comment(
    override val id: Int,
    val noteId: Int,
    val ownerId: Int,
    var message: String,
    val date: Long,
    override var isDeleted: Boolean
) : Deletable

// Исключение для случая, когда заметка не найдена
class NoteNotFoundException(message: String) : RuntimeException(message)