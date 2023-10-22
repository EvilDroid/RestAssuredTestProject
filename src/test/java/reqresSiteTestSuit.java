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

public class reqresSiteTestSuit {

    private static String BASE_URL = "https://reqres.in";

    @Test
    public void GET_listUsersTest() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification());
        List<UserData> users = given()
                .when()//условия запроса
                .get("/api/users?page=2") //method + endpoint
                .then()//что делать с ответом
                .log().all() //вывести в консоль
                .extract().body().jsonPath().getList("data", UserData.class); //извлечение из ответа
        //проверки
        //Проверка что у каждого пользователя в имени файла автара есть айди
        users.stream().forEach(x -> Assert.assertTrue(x.getAvatar().contains(x.getId().toString())));
        //Проверка что у каждого пользователя есть поля
        users.stream().forEach(x -> Assert.assertTrue(!x.getFirst_name().equals("")));
        users.stream().forEach(x -> Assert.assertTrue(!x.getLast_name().equals("")));
        //проверка что емейл оканчивается на reqres.in
        Assert.assertTrue(users.stream().findFirst().get().getId() == 7);
        //проверка что емейл оканчивается на reqres.in
        Assert.assertTrue(users.stream().allMatch(x -> x.getEmail().endsWith("@reqres.in")));
        //проверка что емейл оканчивается на reqres.in
        Assert.assertTrue(users.stream().allMatch(x -> x.getAvatar().startsWith("https://")));
        //количество юзеров
        Assert.assertTrue(users.stream().count() == 6);
    }

    @Test
    public void GET_singleUserTest() {
        Assert.assertTrue(true);
    }

    @Test
    public void GET_singleUserNotFoundTest() {
        Assert.assertTrue(true);
    }

    @Test
    public void GET_listResoucse() {
        Assert.assertTrue(true);
    }

    @Test
    public void GET_singleResource() {
        Assert.assertTrue(true);
    }

    @Test
    public void GET_singleResourceNotFound() {
        Assert.assertTrue(true);
    }

    @Test
    public void POST_create() {
        Assert.assertTrue(true);
    }

    @Test
    public void PUT_update() {
        Assert.assertTrue(true);
    }

    @Test
    public void PATCH_update() {
        Assert.assertTrue(true);
    }

    @Test
    public void DELETE_delete() {
        Assert.assertTrue(true);
    }

    @Test
    public void POST_registerSuccessfulTest() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification());
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
        Assert.assertEquals(4, resp.getId());
        Assert.assertEquals("QpwL5tke4Pnpja7X4", resp.getToken());
    }
}
