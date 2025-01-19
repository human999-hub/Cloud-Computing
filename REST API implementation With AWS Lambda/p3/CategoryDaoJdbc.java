package p3;


import com.mysql.cj.jdbc.MysqlDataSource;

import java.sql.*;
import java.util.ArrayList;
import java.util.List;

public class CategoryDaoJdbc implements CategoryDao {

    private static final String FIND_ALL_SQL =
            "SELECT category_id, name " +
                    "FROM category";

    private static final String FIND_BY_CATEGORY_ID_SQL =
            "SELECT category_id, name " +
                    "FROM category " +
                    "WHERE category_id = ?";

    private static final String FIND_BY_NAME_SQL =
            "SELECT category_id, name " +
                    "FROM category " +
                    "WHERE name = ?";

    final String ADD_CATEGORY_SQL = "INSERT INTO category (category_id, name) VALUES (?, ?)";


    static Statement st = null;
    static PreparedStatement pst = null;
    static Connection con = null;
    static MysqlDataSource source = null;


    static String name=System.getenv("username");
    static String pass=System.getenv("password");



   // static String dbName = "BookStoreTest";

    static String dbName = "p3_HarithaDatabase";

   //static String url = "jdbc:mysql://host.docker.internal:3306/"+ dbName;

   static String url = "jdbc:mysql://p3-haritha-database.cr8mkyai4jms.eu-west-3.rds.amazonaws.com:3306/"+ dbName;

    static
    {
        try{
            source = new MysqlDataSource();
            source.setURL(url);
            source.setPassword(pass);
            source.setUser(name);
            con= source.getConnection();
            st = con.createStatement();

        }
        catch(SQLException e){
            System.out.println(e);
    }

    }

    @Override
    public List<Category> findAll() {
        List<Category> categories = new ArrayList<>();
        try (
             PreparedStatement statement = con.prepareStatement(FIND_ALL_SQL);
             ResultSet resultSet = statement.executeQuery()) {
            while (resultSet.next()) {
                Category category = readCategory(resultSet);
                categories.add(category);
            }
        } catch (SQLException e) {

        }
        return categories;
    }

    @Override
    public Category findByCategoryId(long categoryId) {
        Category category = null;
        try (
             PreparedStatement statement = con.prepareStatement(FIND_BY_CATEGORY_ID_SQL)) {
            statement.setLong(1, categoryId);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    category = readCategory(resultSet);
                }
            }
        } catch (SQLException e) {

        }
        return category;
    }

    @Override
    public Category findByName(String name) {
        Category category = null;
        try (
             PreparedStatement statement = con.prepareStatement(FIND_BY_NAME_SQL)) {
            statement.setString(1, name);
            try (ResultSet resultSet = statement.executeQuery()) {
                if (resultSet.next()) {
                    category = readCategory(resultSet);
                }
            }
        } catch (SQLException e) {

        }
        return category;
    }



    public void addCategory(long categoryId, String name){
        try (PreparedStatement statement = con.prepareStatement(ADD_CATEGORY_SQL)) {
            statement.setLong(1, categoryId);
            statement.setString(2, name);
            int result = statement.executeUpdate();
            //con.commit();
            System.out.println("Rows affected: " + result); // To Check how many rows were actually inserted
        } catch (SQLException e) {
            System.out.println("Error adding category: " + e.getMessage());
            e.printStackTrace(); // To see the full stack trace
        }
    }



    private Category readCategory(ResultSet resultSet) throws SQLException {
        long categoryId = resultSet.getLong("category_id");
        String name = resultSet.getString("name");
        return new Category(categoryId, name);
    }

}
