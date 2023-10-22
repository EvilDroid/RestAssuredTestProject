import api.RegisterRequest;
import api.RegisterResponseSuccess;
import api.UserData;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import io.restassured.path.json.JsonPath;
import io.restassured.response.Response;
import org.junit.Assert;
import org.junit.Test;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.notNullValue;

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
}
