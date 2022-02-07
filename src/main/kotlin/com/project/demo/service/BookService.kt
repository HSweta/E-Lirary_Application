package com.project.demo.service

import com.project.demo.data.*
import org.springframework.cache.annotation.CacheEvict
import org.springframework.cache.annotation.CachePut
import org.springframework.cache.annotation.Cacheable

import org.springframework.data.domain.Page
import org.springframework.data.domain.PageRequest
import org.springframework.data.domain.Sort
import org.springframework.data.repository.findByIdOrNull
import org.springframework.stereotype.Service
import org.springframework.transaction.annotation.Transactional
import kotlin.NoSuchElementException

@Service

class BookService(private val bookRepository: BookRepository, private val authorRepository: AuthorRepository, private val categoryRepository: CategoryRepository) {

    @Transactional
    @CachePut(value = arrayOf("book"), key = "#book.id")
    fun addBooks(book: Book): Book {

        var k = bookRepository.findAll().filter { it.bookName == book.bookName };

        if (k.isNotEmpty())
            throw NoSuchElementException("Book is already present with this name")

        var a = authorRepository.findAll().filter { it.name == book.author.name }

        if (a.isEmpty()) {
            try {
                authorRepository.save(book.author)
            } catch (e: Exception) {
                throw NoSuchElementException("This Author is not present and not saved  $e")
            }
        } else {
            try {
                book.author = a[0]
            } catch (e: Exception) {
                throw NoSuchElementException("This Author is present but not assigned to given book  $e")
            }

        }

        val book_c = book.categories

        for (cate in book_c) {

            var c = categoryRepository.findAll().filter { book.categories.any { it.c_name == cate.c_name } }
            if (c.isEmpty()) {
                try {
                    categoryRepository.save(cate)
                } catch (e: Exception) {
                    throw NoSuchElementException("This Category is not present and not saved $e")
                }
            } else {
                book.categories.remove(cate)
                book.categories.add(c[0])
            }
        }


        var x: Int? = book.id

        if (x?.let { bookRepository.existsById(it) } == true) {
                throw NoSuchElementException("id exist so this book is not saved")
            }
        else {
            try {
                bookRepository.save(book)
            } catch (e: Exception) {
                throw NoSuchElementException("new entry book but not saved  $e")
            }
        }

        return book
    }


    fun getBooks(): Collection<Book> {
        return bookRepository.findAll()
    }

    @Cacheable(value = arrayOf("bookbyID"), key = "#id")
    @Transactional
    fun getBookByID(id: Int): Book {
        //return bookRepository.findById(id)
        println("From Database")
        val currbook = bookRepository.findByIdOrNull(id)
                ?: throw NoSuchElementException("Could not find a book with these details")
        return currbook

    }


    @Cacheable(value = arrayOf("bookbyAuthor"), key = "#name")
    @Transactional
    fun getBookbyAuthor(name: String): Collection<Book> {
        val output = bookRepository.findAll().filter { it.author.name == name }
        if (output.isNotEmpty())
            return output
        else
            throw NoSuchElementException("No book available with this author name")
    }


    @Cacheable(value = arrayOf("bookbyGenre"), key = "#category")
    @Transactional
    fun getBookbyCategory(category: String): List<Book> {

        var output = bookRepository.findAll().filter { it.categories.any { it.c_name == category } }
        if (output.isNotEmpty())
            return output
        else
            throw NoSuchElementException("No book available with this Category")

    }

    fun getSort(startpage: Int, pagesize: Int): Page<Book> {
        var x: PageRequest = PageRequest.of(startpage, pagesize, Sort.by("bookName"))
        return bookRepository.findAll(x)
    }

    @CacheEvict(value = arrayOf("Remove"), key = "#id")
    fun deleteBooks(id: Int): String {

        if (bookRepository.existsById(id)) {
            var b = getBookByID(id)
            var au_id = bookRepository.findAll().filter { it.author == b.author }
            bookRepository.deleteById(id)
            if (au_id.size == 1) {
                b.author.id?.let { authorRepository.deleteById(it) }
            }
            return "Successfully Deleted"
        } else {
            throw NoSuchElementException("Book is not Present with this id")
        }
    }


    fun getBookbyAuthorgenre(authorname: String, genre: String): Collection<Book> {
        var c = bookRepository.findAll().filter { it.author.name == authorname && it.categories.any { it.c_name == genre } }
        if (c.isEmpty()) {
            throw NoSuchElementException("Book is not available with given details")
        } else {
            return c
        }

    }


}















