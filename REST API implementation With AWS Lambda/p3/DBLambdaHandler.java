package p3;

import com.amazonaws.services.lambda.runtime.Context;
import com.amazonaws.services.lambda.runtime.RequestHandler;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyRequestEvent;
import com.amazonaws.services.lambda.runtime.events.APIGatewayProxyResponseEvent;
import org.json.JSONObject;
import com.google.gson.Gson;


import java.util.List;
import java.util.Map;

public class DBLambdaHandler implements RequestHandler<APIGatewayProxyRequestEvent, APIGatewayProxyResponseEvent> {

    private CategoryDaoJdbc categoryDb = new CategoryDaoJdbc();
    private BookDaoJdbc bookDb = new BookDaoJdbc();

    @Override
    public APIGatewayProxyResponseEvent handleRequest(APIGatewayProxyRequestEvent req, Context context) {
        Map<String, String> CORS = Map.of("access-control-allow-origin", "*");
        String path = req.getPath();
        System.out.println("Received path: " + path);
        switch (path) {
            case "/api/getAllCategory":
                return getAllCategories(CORS);
            case "/api/getCategoryName":
                return getCategoryName(req, CORS);
            case "/api/getCategoryId":
                return getCategoryId(req, CORS);
            case "/api/addCategory":
                return addCategory(req, CORS);
            case "/api/getAllBook":
                return getAllBook(CORS);
            case "/api/addBook":
                return addNewBook(req, CORS);
            case "/api/getBookById":
                return getBookById(req, CORS);
            case "/api/getBookByCategoryId":
                return getBookByCategoryId(req, CORS);
            case "/api/getBookByCategoryName":
                return getBookByCategoryName(req, CORS);
            case "/api/getRandomBook":
                return getRandomBooks(CORS);
            default:
                return new APIGatewayProxyResponseEvent()
                        .withBody("Invalid request")
                        .withHeaders(CORS)
                        .withStatusCode(404);
        }
    }



    private APIGatewayProxyResponseEvent getAllCategories(Map<String, String> CORS) {
        List<Category> categories = categoryDb.findAll();
        Gson gson = new Gson();
        String json = gson.toJson(categories);

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }



    private APIGatewayProxyResponseEvent getCategoryName(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        Map<String, String> queryParams = req.getQueryStringParameters();
        if (queryParams == null || !queryParams.containsKey("categoryId")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"categoryId query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        long categoryId;
        try {
            categoryId = Long.parseLong(queryParams.get("categoryId"));
        } catch (NumberFormatException e) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Invalid categoryId format\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        Category category = categoryDb.findByCategoryId(categoryId);
        if (category == null) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Category not found\"}")
                    .withHeaders(CORS)
                    .withStatusCode(404);
        }

        Gson gson = new Gson();
        String json = gson.toJson(Map.of("name", category.name()));

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }


    private APIGatewayProxyResponseEvent getCategoryId(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        Map<String, String> queryParams = req.getQueryStringParameters();
        if (queryParams == null || !queryParams.containsKey("categoryName")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Category name query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        String categoryName = queryParams.get("categoryName");
        Category category = categoryDb.findByName(categoryName);
        if (category == null) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Category not found\"}")
                    .withHeaders(CORS)
                    .withStatusCode(404);
        }

        Gson gson = new Gson();
        String json = gson.toJson(Map.of("categoryId", category.CategoryId()));

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }

    private APIGatewayProxyResponseEvent addCategory(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        String addedCategory = req.getBody();
        JSONObject obj = new JSONObject(addedCategory);
        String categoryName = obj.getString("name");
        long categoryId = obj.getLong("CategoryId");

        Category newCategory = new Category(categoryId, categoryName);
        categoryDb.addCategory(newCategory.CategoryId(), newCategory.name());

        Gson gson = new Gson();
        String json = gson.toJson(Map.of("message", "Category added: " + newCategory.name()));

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(201);
    }




    private APIGatewayProxyResponseEvent getAllBook(Map<String, String> CORS) {
        List<Book> books = bookDb.findAll();
        Gson gson = new Gson();
        String json = gson.toJson(books);

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }




    private APIGatewayProxyResponseEvent addNewBook(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        String addedBook = req.getBody();
        JSONObject obj = new JSONObject(addedBook);

        Book newBook = new Book(
                obj.getLong("bookId"),
                obj.getString("title"),
                obj.getString("author"),
                obj.getString("description"),
                obj.getDouble("price"),
                obj.getInt("rating"),
                obj.getBoolean("isPublic"),
                obj.getBoolean("isFeatured"),
                obj.getLong("categoryId"));

        bookDb.addBook(
                newBook.bookId(),
                newBook.title(),
                newBook.author(),
                newBook.description(),
                newBook.price(),
                newBook.rating(),
                newBook.isPublic(),
                newBook.isFeatured(),
                newBook.categoryId());

        Gson gson = new Gson();
        String json = gson.toJson(Map.of("message", "Book added: " + newBook.title()));

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(201);
    }

    private APIGatewayProxyResponseEvent getBookById(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        Map<String, String> queryParams = req.getQueryStringParameters();
        if (queryParams == null || !queryParams.containsKey("bookId")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"bookId query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        long bookId;
        try {
            bookId = Long.parseLong(queryParams.get("bookId"));
        } catch (NumberFormatException e) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Invalid bookId format\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        return bookDb.findById(bookId)
                .map(book -> {
                    Gson gson = new Gson();
                    String json = gson.toJson(book);
                    return new APIGatewayProxyResponseEvent()
                            .withBody(json)
                            .withHeaders(CORS)
                            .withStatusCode(200);
                })
                .orElseGet(() -> new APIGatewayProxyResponseEvent()
                        .withBody("{\"message\":\"Book not found\"}")
                        .withHeaders(CORS)
                        .withStatusCode(404));
    }

    private APIGatewayProxyResponseEvent getBookByCategoryId(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        Map<String, String> queryParams = req.getQueryStringParameters();
        if (queryParams == null || !queryParams.containsKey("categoryId")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"categoryId query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        long categoryId;
        try {
            categoryId = Long.parseLong(queryParams.get("categoryId"));
        } catch (NumberFormatException e) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"Invalid categoryId format\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        List<Book> books = bookDb.findByCategoryId(categoryId);
        if (books == null || books.isEmpty()) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"No books found for this category\"}")
                    .withHeaders(CORS)
                    .withStatusCode(404);
        }

        Gson gson = new Gson();
        String json = gson.toJson(books);

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }


    private APIGatewayProxyResponseEvent getBookByCategoryName(APIGatewayProxyRequestEvent req, Map<String, String> CORS) {
        Map<String, String> queryParams = req.getQueryStringParameters();
        if (queryParams == null || !queryParams.containsKey("categoryName")) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"categoryName query parameter is missing\"}")
                    .withHeaders(CORS)
                    .withStatusCode(400);
        }

        String categoryName = queryParams.get("categoryName");
        List<Book> books = bookDb.findByCategoryName(categoryName);
        Gson gson = new Gson();
        String json = gson.toJson(books);

        return new APIGatewayProxyResponseEvent()
                .withBody(json)
                .withHeaders(CORS)
                .withStatusCode(200);
    }

    private APIGatewayProxyResponseEvent getRandomBooks(Map<String, String> CORS) {
        List<Book> books = bookDb.findRandomBook();
        if (books.isEmpty()) {
            return new APIGatewayProxyResponseEvent()
                    .withBody("{\"message\":\"No books available\"}")
                    .withHeaders(CORS)
                    .withStatusCode(404);
        } else {
            Gson gson = new Gson();
            String json = gson.toJson(books);
            return new APIGatewayProxyResponseEvent()
                    .withBody(json)
                    .withHeaders(CORS)
                    .withStatusCode(200);
        }
    }
}



