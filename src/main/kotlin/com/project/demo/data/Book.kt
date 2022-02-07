package com.project.demo.data
import org.springframework.data.jpa.repository.JpaRepository
import org.springframework.stereotype.Repository
import java.io.Serializable
import javax.persistence.*

@Repository
interface BookRepository : JpaRepository<Book, Int>
@Entity
    data class Book(
        @Id
        @GeneratedValue(strategy=GenerationType.IDENTITY)
        var id:Int?=1,
        var noOfPage:Int = 200,
        var bookName : String = "New",
        var isBnNo : String ="123456789",
        var pdf:String ="Book",
        @ManyToOne(optional = false,fetch = FetchType.LAZY,cascade = [CascadeType.PERSIST,CascadeType.REMOVE, CascadeType.REFRESH,CascadeType.MERGE])
        var author : Author= Author(1,"Anna"),

        //@JsonIgnore
        @ManyToMany(fetch = FetchType.EAGER,cascade = [CascadeType.PERSIST,CascadeType.MERGE])

        var categories:MutableCollection<Category> = arrayListOf(),

        ) : Serializable


interface AuthorRepository : JpaRepository<Author, Int>
  @Entity
  data class Author(
          @Id
          @GeneratedValue(strategy = GenerationType.IDENTITY)
          var id: Int= 1,
          var name: String = "Anna"
  ) : Serializable




interface CategoryRepository : JpaRepository<Category, Int>
@Entity
data class Category(
        @Id
        @GeneratedValue(strategy = GenerationType.IDENTITY)
        var id:Int= 1,
        var c_name:String = "Comedy"
      ) : Serializable




