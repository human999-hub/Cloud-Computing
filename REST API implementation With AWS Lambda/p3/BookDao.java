package p3;

import java.util.List;
import java.util.Optional;

public interface BookDao {

    public List<Book> findAll();
    Optional<Book> findById(long bookId);
    List<Book> findByCategoryId(long categoryId);
    List<Book> findByCategoryName(String categoryName);
    public List<Book> findRandomBook();
    public void addBook(long bookId, String title, String author, String description, double price, int rating, boolean isPublic, boolean isFeatured, long categoryId);
}

