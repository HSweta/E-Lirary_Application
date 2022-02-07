package com.project.demo.controller

import com.project.demo.data.Book
import com.project.demo.service.BookService
import org.slf4j.LoggerFactory
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.data.domain.Page
import org.springframework.http.HttpStatus
import org.springframework.http.ResponseEntity
import org.springframework.web.bind.annotation.*
import org.springframework.web.multipart.MultipartFile

@RestController
@RequestMapping("/book")
class BooksController {

    @ExceptionHandler(NoSuchElementException::class)
    fun handleNotFound(e:NoSuchElementException): ResponseEntity<String> =
            ResponseEntity(e.message, HttpStatus.NOT_FOUND)

    private val logger = LoggerFactory.getLogger(Book::class.java)

    @Autowired //dependency injection
    private lateinit var service: BookService

    @GetMapping
    fun getBooks() : Collection<Book> = service.getBooks()

    @GetMapping("/{id}")
    fun getbookByID(@PathVariable id:Int) : Book {
        logger.info("From cache")
        return service.getBookByID(id)
    }

//    @GetMapping("/Book/{name}")
//    fun getBookByName(@PathVariable name:String):Book{
//        return service.getBookByName(name)
//    }

    @GetMapping("/author/{aname}")
    fun getBookbyAuthor(@PathVariable aname:String) : Collection<Book> {
        return service.getBookbyAuthor(aname)
    }

    @GetMapping("/category/{category}")
    fun getBookByCategory(@PathVariable category: String) : Collection<Book> {
        return service.getBookbyCategory(category)
    }

    @GetMapping("/Authorbygenre/{authorname}/{genre}")
    fun getAuthorByCategory(@PathVariable authorname: String ,@PathVariable genre:String) : Collection<Book> = service.getBookbyAuthorgenre(authorname,genre)

    @GetMapping("/sorting/{startpage}/{pagesize}")
    fun getSort(@PathVariable startpage:Int,@PathVariable pagesize:Int) : Page<Book> = service.getSort(startpage,pagesize)

    @PostMapping
    fun addBooks(@RequestBody book: Book) : Book = service.addBooks(book)

    @DeleteMapping("/{id}")
    fun deleteBooks(@PathVariable id:Int) : String = service.deleteBooks(id)

}



