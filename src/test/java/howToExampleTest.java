import api.RegisterRequest;
import api.RegisterResponseSuccess;
import api.UserData;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.http.Header;
import io.restassured.http.Headers;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import io.restassured.response.ResponseBody;
import io.restassured.specification.RequestSpecification;
import org.json.simple.JSONObject;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;

//Lesson here:  https://www.youtube.com/watch?v=gxzXOMxIt4w

public class howToExampleTest {

    private static String BASE_URL = "https://reqres.in";

    /**
     * вариант работы БЕЗ использования спецификаций и с pojo
     */
    @Test
    public void noSpecificationWithPOJO() {
        //GET---------------------
        //отправляем запрос дергем, из ответа объекты согласно pojo ContentType.JSON
        List<UserData> users1 = given()
                .when()//условия запроса
                .contentType(ContentType.JSON)//тип контента
                .get(BASE_URL + "/api/users?page=2") //method + endpoint
                .then()//что делать с ответом
                .statusCode(200)//ожидаемый статус код
                .log().all() //вывести в консоль
                .extract().body().jsonPath().getList("data", UserData.class); //извлечение из ответа
        //Проверка что у каждого пользователя в имени файла автара есть айди
        users1.stream().forEach(x -> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
        //проверка что емейл оканчивается на reqres.in
        Assert.assertTrue(users1.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));
        //POST---------------------
        //готовим боди пост запроса - pojo класс
        String email = "eve.holt@reqres.in";
        String password = "pistol";
        RegisterRequest req = new RegisterRequest(email, password);
        //реквест
        RegisterResponseSuccess resp = given()
                .body(req)
                .when()
                .contentType(ContentType.JSON)
                .post(BASE_URL + "/api/register")
                .then()
                .statusCode(200)
                .log().all()
                .extract().as(RegisterResponseSuccess.class);//ответ в pojo класс
        //проверки
        int expectedId = 4;
        String expectedToken = "QpwL5tke4Pnpja7X4";
        Assert.assertEquals(expectedId, resp.getId());
        Assert.assertEquals(expectedToken, resp.getToken());
    }

    /**
     * Вариант работы того же теста но с использованиесм спецификаций и с pojo
     */
    @Test
    public void withSpecificationWithPOJO() {
        //GET---------------------
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
        List<UserData> users2 = given()
                .when()//условия запроса
                .get("/api/users?page=2") //method + endpoint
                .then()//что делать с ответом
                .log().all() //вывести в консоль
                .extract().body().jsonPath().getList("data", UserData.class); //извлечение из ответа
        //POST---------------------
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
        //готовим боди пост запроса - pojo класс
        String email = "eve.holt@reqres.in";
        String password = "pistol";
        RegisterRequest req = new RegisterRequest(email, password);
        //реквест
        RegisterResponseSuccess resp = given()
                .body(req)
                .when()
                .post("/api/register")
                .then()
                .log().all()
                .extract().as(RegisterResponseSuccess.class);//ответ в pojo класс
        //проверки
        int expectedId = 4;
        String expectedToken = "QpwL5tke4Pnpja7X4";
        Assert.assertEquals(expectedId, resp.getId());
        Assert.assertEquals(expectedToken, resp.getToken());
    }

    /**
     * Далее вариант работы с использованиесм спецификаций но без pojo
     */
    @Test
    public void withSpecificationNoPOJO() {
        //GET---------------------
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
        Response response = RestAssured.given()
                .when()//условия запроса
                .get("/api/users?page=2") //method + endpoint
                .then()//что делать с ответом
                .log().all() //вывести в консоль
                .body("page", equalTo(2))//проверки
                .body("data.id", notNullValue())//проверки
                .body("data.first_name", notNullValue())//проверки при помощи hemcrest
                .body("data.last_name", notNullValue())//проверки
                .extract().response(); //извлечение ответа
        //проверка
        JsonPath jsonPath = response.jsonPath();
        List<String> emails = jsonPath.get("data.email");
        for (String emailg : emails) {
            emailg.endsWith("@reqres.in");
        }
        //POST---------------------
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
        Map<String, String> user = new HashMap<>();//вместо body в постзапросе можно вкинуть мапу
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");
        Response response2 = given()
                .body(user)
                .when()
                .post("api/register")
                .then().log().all()
                .body("id", equalTo(4))
                .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .extract().response();
        //проверка
        JsonPath jsonPath1 = response2.jsonPath();
        String tokenh = jsonPath1.get("token");
        Assert.assertEquals("Неверный токен", "QpwL5tke4Pnpja7X4", tokenh);
    }


    /**
     * AllMethods всякие полезные методы
     */
    @Test
    public void allMethods() {
        //GET---------------------
        Response response = RestAssured.given()
                //.header("Authorization", "Bearer " + "dfgdfgdfgdfgdfgdf") // добавить хедер
                .when()//условия запроса
                .contentType(ContentType.JSON)//тип контента
                .get(BASE_URL + "/api/users?page=2") //method + endpoint
                .then()//что делать с ответом
                .statusCode(200)//ожидаемый статус код
                .log().all() //вывести в консоль
                //проверки прямо в потоке вызовов
                .body("page", equalTo(2))//проверки
                .body("data.id", notNullValue())//проверки
                .body("data.first_name", notNullValue())//проверки при помощи hemcrest
                .body("data.last_name", notNullValue())//проверки
                .time(lessThan(5000L)) // проверить что время запроса меньше чем 5000 мс
                .extract().response(); //извлечение ответа


        System.out.println("response.getStatusCode(): " + response.getStatusCode()); //возвращает статус код
        System.out.println("response.getStatusLine(): " + response.getStatusLine()); //возвращает HTTP/1.1 200 OK
        Headers allHeaders = response.headers(); // возвращает все хедеры
        for(Header header : allHeaders) {
            System.out.println("Key: " + header.getName() + " Value: " + header.getValue());
        }
        String serverType = response.header("Server"); // возвращает конкретный хедер
        String acceptLanguage = response.header("Content-Encoding"); // возвращает конкретный хедер

        ResponseBody body = response.getBody(); // возвращает body
        System.out.println("Response Body is: " + body.asString());

        System.out.println("response.prettyPrint() :" + response.prettyPrint()); //возвращает боди в красивом виде
        System.out.println("response.asString() :" + response.asString()); //возвращает боди строкой

        JsonPath jsonPath = response.jsonPath(); //парсит ответ
        List<String> emails = jsonPath.get("data.email"); //возвращает любой объект jsona
        System.out.println("response.time() :" + response.time()); //возвращает время запроса


        //POST---------------------
        //Making body as MAP
        Map<String, String> user = new HashMap<>();//вместо body в постзапросе можно вкинуть мапу
        user.put("email", "eve.holt@reqres.in");
        user.put("password", "pistol");

        //Making body as jsonobject (library org.json.simple)
        RequestSpecification request = RestAssured.given();
        JSONObject requestParams = new JSONObject();
        requestParams.put("userId", "TQ123");
        requestParams.put("isbn", "9781449325862");
        request.header("Content-Type", "application/json"); // Add the Json to the body of the request
        request.body(requestParams.toJSONString());

        Response response2 = given()
                .body(user)
                .contentType(ContentType.JSON)//тип контента
                .when()
                .post(BASE_URL + "api/register")
                .then()
                .statusCode(200)//ожидаемый статус код
                .log().all()
                .body("id", equalTo(4))
                .body("token", equalTo("QpwL5tke4Pnpja7X4"))
                .extract().response();

        JsonPath jsonPath1 = response2.jsonPath();
        String token = jsonPath1.get("token");
    }





    /**
     * json schema validation
     */
    @Test
    public void jsonSchemaValidation() {
            // manual https://www.baeldung.com/rest-assured-json-schema
            // method GET_singleUserTest
            // json schema converter https://www.liquid-technologies.com/online-json-to-schema-converter
            // response to convert here https://reqres.in/
            //dependency add https://mvnrepository.com/artifact/io.rest-assured/json-schema-validator/5.3.2

            Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
            RestAssured.given()
                    .when()
                    .get("/api/users/2")
                    .then()
                    .assertThat()
                    .body(matchesJsonSchemaInClasspath("GET_singleUser_jsonSchema.json"))
                    .log().all();
    }
}
