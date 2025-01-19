package p3;

import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

public class BookDaoJdbc implements BookDao {

    private Connection con;

    final String ADD_BOOK_SQL = "INSERT INTO book (book_id, title, author, description, price, rating, is_public, is_featured, category_id) VALUES (?,?,?,?,?,?,?,?,?)";

    static String name=System.getenv("username");
    static String pass=System.getenv("password");


    public BookDaoJdbc() {
        MysqlDataSource dataSource = new MysqlDataSource();
        //dataSource.setURL("jdbc:mysql://host.docker.internal:3306/BookStoreTest");
        dataSource.setURL("jdbc:mysql://p3-haritha-database.cr8mkyai4jms.eu-west-3.rds.amazonaws.com:3306/p3_HarithaDatabase");
        dataSource.setUser(name);
        dataSource.setPassword(pass);
        try {
            this.con = dataSource.getConnection();
        } catch (SQLException e) {
            System.out.println("Connection failed: " + e.getMessage());
        }
    }

    @Override
    public List<Book> findAll() {
        List<Book> books = new ArrayList<>();
        String query = "SELECT book_id, title, author, description, price, rating, is_public, is_featured, category_id FROM book";
        try (PreparedStatement stmt = con.prepareStatement(query);
             ResultSet rs = stmt.executeQuery()) {
            while (rs.next()) {
                books.add(new Book(
                        rs.getLong("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("rating"),
                        rs.getBoolean("is_public"),
                        rs.getBoolean("is_featured"),
                        rs.getLong("category_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        return books;
    }

    @Override
    public Optional<Book> findById(long bookId) {
        String query = "SELECT book_id, title, author, description, price, rating, is_public, is_featured, category_id FROM book WHERE book_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setLong(1, bookId);
            ResultSet rs = stmt.executeQuery();
            if (rs.next()) {
                return Optional.of(new Book(
                        rs.getLong("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("rating"),
                        rs.getBoolean("is_public"),
                        rs.getBoolean("is_featured"),
                        rs.getLong("category_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        return Optional.empty();
    }

    @Override
    public List<Book> findByCategoryId(long categoryId) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT book_id, title, author, description, price, rating, is_public, is_featured, category_id FROM book WHERE category_id = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setLong(1, categoryId);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getLong("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("rating"),
                        rs.getBoolean("is_public"),
                        rs.getBoolean("is_featured"),
                        rs.getLong("category_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> findByCategoryName(String categoryName) {
        List<Book> books = new ArrayList<>();
        String query = "SELECT b.book_id, b.title, b.author, b.description, b.price, b.rating, b.is_public, b.is_featured, b.category_id FROM book b JOIN category c ON b.category_id = c.category_id WHERE c.name = ?";
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            stmt.setString(1, categoryName);
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {
                books.add(new Book(
                        rs.getLong("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("rating"),
                        rs.getBoolean("is_public"),
                        rs.getBoolean("is_featured"),
                        rs.getLong("category_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        return books;
    }

    @Override
    public List<Book> findRandomBook() {
        List<Book> randomBooks = new ArrayList<>();
        String query = "SELECT book_id, title, author, description, price, rating, is_public, is_featured, category_id FROM book ORDER BY RAND() LIMIT 5";  // Changed LIMIT to 5
        try (PreparedStatement stmt = con.prepareStatement(query)) {
            ResultSet rs = stmt.executeQuery();
            while (rs.next()) {  // Used while to handle multiple books
                randomBooks.add(new Book(
                        rs.getLong("book_id"),
                        rs.getString("title"),
                        rs.getString("author"),
                        rs.getString("description"),
                        rs.getDouble("price"),
                        rs.getInt("rating"),
                        rs.getBoolean("is_public"),
                        rs.getBoolean("is_featured"),
                        rs.getLong("category_id")
                ));
            }
        } catch (SQLException e) {
            System.out.println("Query failed: " + e.getMessage());
        }
        return randomBooks;  // Return the list of books
    }

    @Override
    public void addBook(long bookId, String title, String author, String description, double price, int rating, boolean isPublic, boolean isFeatured, long categoryId){
        try (PreparedStatement statement = con.prepareStatement(ADD_BOOK_SQL)) {


                    statement.setLong(1,bookId);
                    statement.setString(2,title);
                    statement.setString(3,author);
                    statement.setString(4,description);
                    statement.setDouble(5,price);
                    statement.setInt(6,rating);
                    statement.setBoolean(7,isPublic);
                    statement.setBoolean(8,isFeatured);
                    statement.setLong(9,categoryId);

            int result = statement.executeUpdate();
            //con.commit();
            System.out.println("Rows affected: " + result); // Check how many rows were actually inserted
        } catch (SQLException e) {
            System.out.println("Error adding category: " + e.getMessage());
            e.printStackTrace(); // This will help us see the full stack trace
        }
    }
}

