import api.*;
import io.restassured.RestAssured;
import org.junit.Assert;
import org.junit.Test;

import java.time.Clock;
import java.time.Instant;
import java.time.temporal.TemporalField;
import java.util.HashMap;
import java.util.List;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.notNullValue;

//Lesson here:  https://www.youtube.com/watch?v=gxzXOMxIt4w

public class reqresSiteTestSuit {

    private static String BASE_URL = "https://reqres.in";

    @Test
    public void GET_listUsersTest() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
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
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
        UserData user = RestAssured.given()
                .when()
                .get("/api/users/2")
                .then()
                .log().all()
                .extract().body().jsonPath().getObject("data",UserData.class);
        Assert.assertEquals("incorrect", "2", user.getId().toString());
        Assert.assertEquals("incorrect", "janet.weaver@reqres.in", user.getEmail());
        Assert.assertEquals("incorrect", "Janet", user.getFirst_name());
        Assert.assertEquals("incorrect", "Weaver", user.getLast_name());
        Assert.assertEquals("incorrect", "https://reqres.in/img/faces/2-image.jpg", user.getAvatar());
    }

    @Test
    public void GET_singleUserNotFoundTest() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(404));
        RestAssured.given()
                .when()
                .get("/api/users/23")
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    public void GET_listResource() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
        List<ResourceData> resource = given()
                .when()//условия запроса
                .get("/api/unknown") //method + endpoint
                .then()//что делать с ответом
                .log().all() //вывести в консоль
                .extract().body().jsonPath().getList("data", ResourceData.class); //извлечение из ответа
        //проверки
        resource.stream().forEach(x -> Assert.assertTrue(x.getColor().startsWith("#")));
        //Проверка что у каждого пользователя есть поля
        resource.stream().forEach(x -> Assert.assertTrue(!x.getName().equals("")));
        //проверка что емейл оканчивается на reqres.in
        Assert.assertTrue(resource.stream().findFirst().get().getId() == 1);
        //количество
        Assert.assertTrue(resource.stream().count() == 6);
    }

    @Test
    public void GET_singleResource() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
        ResourceData resource = RestAssured.given()
                .when()
                .get("/api/unknown/2")
                .then()
                .log().all()
                .extract().body().jsonPath().getObject("data",ResourceData.class);
        Assert.assertEquals("incorrect", "2", resource.getId().toString());
        Assert.assertEquals("incorrect", "fuchsia rose", resource.getName());
        Assert.assertEquals("incorrect", "2001", resource.getYear().toString());
        Assert.assertEquals("incorrect", "#C74375", resource.getColor());
        Assert.assertEquals("incorrect", "17-2031", resource.getPantone_value());
    }

    @Test
    public void GET_singleResourceNotFound() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(404));
        RestAssured.given()
                .when()
                .get("/api/unknown/21")
                .then()
                .log().all()
                .statusCode(404);
    }

    @Test
    public void POST_create() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(201));
        //готовим боди пост запроса - pojo класс
        UserJob req = new UserJob("morpheus", "leader");
        //реквест
        UserJobResponseSuccess resp = given()
                .body(req)
                .when()
                .post("/api/users")
                .then()
                .log().all()
                .extract().as(UserJobResponseSuccess.class);//ответ в pojo класс
        //проверки
        Assert.assertTrue(!resp.getId().equals(""));
        Assert.assertTrue(resp.getName().equals("morpheus"));
        Assert.assertTrue(resp.getJob().equals("leader"));
        //regex проверка времен
        String regex = "(.{6}$)";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");//Текущее время
        Assert.assertEquals(currentTime, resp.getCreatedAt().replaceAll(regex, ""));
    }

    @Test
    public void PUT_update() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(201));
        //готовим боди пост запроса - pojo класс
        UserJob req = new UserJob("morpheus", "zion resident");
        //реквест
        UserJobResponseSuccess resp = given()
                .body(req)
                .when()
                .post("/api/users/2")
                .then()
                .log().all()
                .extract().as(UserJobResponseSuccess.class);//ответ в pojo класс
        //проверки
        Assert.assertTrue(!resp.getId().equals(""));
        Assert.assertTrue(resp.getName().equals("morpheus"));
        Assert.assertTrue(resp.getJob().equals("zion resident"));
        //regex проверка времен
        String regex = "(.{6}$)";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");//Текущее время
        Assert.assertEquals(currentTime, resp.getCreatedAt().replaceAll(regex, ""));
    }

    @Test
    public void PATCH_update() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(201));
        //готовим боди пост запроса - pojo класс
        UserJob req = new UserJob("morpheus", "hacker");
        //реквест
        UserJobResponseSuccess resp = given()
                .body(req)
                .when()
                .post("/api/users/2")
                .then()
                .log().all()
                .extract().as(UserJobResponseSuccess.class);//ответ в pojo класс
        //проверки
        Assert.assertTrue(!resp.getId().equals(""));
        Assert.assertTrue(resp.getName().equals("morpheus"));
        Assert.assertTrue(resp.getJob().equals("hacker"));
        //regex проверка времен
        String regex = "(.{6}$)";
        String currentTime = Clock.systemUTC().instant().toString().replaceAll(regex, "");//Текущее время
        Assert.assertEquals(currentTime, resp.getCreatedAt().replaceAll(regex, ""));
    }

    @Test
    public void DELETE_delete() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(204));
        //реквест
        given()
                .when()
                .delete("/api/users/2")
                .then()
                .log().all();
    }

    @Test
    public void POST_registerSuccessfulTest() {
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
        Assert.assertEquals(4, resp.getId());
        Assert.assertEquals("QpwL5tke4Pnpja7X4", resp.getToken());
    }

    @Test
    public void POST_registerUnsuccessfulTest() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(400));
        //готовим боди пост запроса - pojo класс
        HashMap <String, String> req = new HashMap<>();
        req.put("email", "sydney@fife");
        //реквест
        RegisterResponseUnsuccess resp = given()
                .body(req)
                .when()
                .post("/api/register")
                .then()
                .log().all()
                .extract().as(RegisterResponseUnsuccess.class);//ответ в pojo класс
        //проверки
        Assert.assertEquals("Missing password", resp.getError());
    }

    @Test
    public void POST_loginSuccessfulTest() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
        //готовим боди пост запроса - pojo класс
        String email = "eve.holt@reqres.in";
        String password = "cityslicka";
        LoginRequest req = new LoginRequest(email, password);
        //реквест
        LoginResponse resp = given()
                .body(req)
                .when()
                .post("/api/login")
                .then()
                .log().all()
                .extract().as(LoginResponse.class);//ответ в pojo класс
        //проверки
        Assert.assertEquals("QpwL5tke4Pnpja7X4", resp.getToken());
    }

    @Test
    public void POST_loginUnsuccessfulTest() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(400));
        //готовим боди пост запроса - pojo класс
        HashMap <String, String> req = new HashMap<>();
        req.put("email", "peter@klaven");
        //реквест
        LoginResponseUnsuccess resp = given()
                .body(req)
                .when()
                .post("/api/login")
                .then()
                .log().all()
                .extract().as(LoginResponseUnsuccess.class);//ответ в pojo класс
        //проверки
        Assert.assertEquals("Missing password", resp.getError());
    }

    @Test
    public void GET_delayedResponseTest() {
        Specifications.setSpecifications(Specifications.requestSpecification(BASE_URL), Specifications.responseSpecification(200));
        Instant currentTime1 = Clock.systemUTC().instant();
        List<UserData> resource = RestAssured.given()
                .when()
                .get("/api/users?delay=3")
                .then()
                .log().all()
                .extract().body().jsonPath().getList("data", UserData.class);
        Instant currentTime2 = Clock.systemUTC().instant();
        long factDelay = currentTime2.getEpochSecond() - currentTime1.getEpochSecond();
        Assert.assertTrue(factDelay > 3 - 1);
        Assert.assertTrue(factDelay < 3 + 1);
    }
}
