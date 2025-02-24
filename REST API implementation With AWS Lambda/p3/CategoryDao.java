package p3;

import java.util.List;


public interface CategoryDao {

    public List<Category> findAll();

    public Category findByCategoryId(long categoryId);

    public Category findByName(String categoryName);

   // void addCategory(Category category);
   public void addCategory(long categoryId, String name);


}