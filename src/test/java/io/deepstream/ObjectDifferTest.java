package io.deepstream;

import com.google.gson.Gson;
import com.google.gson.JsonArray;
import com.google.gson.JsonObject;
import javafx.util.Pair;
import org.junit.After;
import org.junit.Assert;
import org.junit.Before;

import java.net.URISyntaxException;

public class ObjectDifferTest {

    UtilObjectDiffer comparer;
    Gson gson;

    @Before
    public void setUp() throws URISyntaxException {
        comparer = new UtilObjectDiffer();
        gson = new Gson();
    }

    @After
    public void tearDown() {

    }

    @org.junit.Test
    public void sameObjectSendsNoUpdateOrPatch() {
        JsonObject p = new JsonObject();
        p.addProperty("age", 20);
        p.addProperty("name", "Alex");

        Pair pair = comparer.getUpdateObject(p, p);

        Assert.assertEquals("", pair.getKey());
        Assert.assertEquals(null, pair.getValue());
    }

    @org.junit.Test
    public void simpleObjectSendsPatch() {
        JsonObject p = new JsonObject();
        p.addProperty("age", 20);
        p.addProperty("name", "Alex");

        JsonObject q = new JsonObject();
        q.addProperty("age", 20);
        q.addProperty("name", "Craig");

        Pair pair = comparer.getUpdateObject(p, q);

        Assert.assertEquals("name", pair.getKey());
        Assert.assertEquals("\"Craig\"", pair.getValue().toString());
    }

    @org.junit.Test
    public void simpleObjectSendsPatchForWholeList() {
        JsonObject p = new JsonObject();
        p.addProperty("name", "Alex");
        JsonArray jsonArray = new JsonArray();
        jsonArray.add( "coffee" );
        jsonArray.add( "wine" );
        p.add("drinks", jsonArray);

        JsonObject q = new JsonObject();
        q.addProperty("name", "Alex");
        JsonArray jsonArray1 = new JsonArray();
        jsonArray1.add( "coffee" );
        jsonArray1.add( "beer" );
        q.add("drinks", jsonArray1);

        Pair pair = comparer.getUpdateObject(p, q);

        Assert.assertEquals("drinks", pair.getKey());
        Assert.assertEquals( "[\"coffee\",\"beer\"]", getJson(pair.getValue()) );
    }

    @org.junit.Test
    public void simpleObjectSendsUpdate() {
        JsonObject p = new JsonObject();
        p.addProperty("name", "Alex");
        JsonArray jsonArray = new JsonArray();
        jsonArray.add( "coffee" );
        jsonArray.add( "wine" );
        p.add("drinks", jsonArray);

        JsonObject q = new JsonObject();
        q.addProperty("name", "Craig");
        JsonArray jsonArray1 = new JsonArray();
        jsonArray1.add( "coffee" );
        jsonArray1.add( "beer" );
        q.add("drinks", jsonArray1);

        Pair pair = comparer.getUpdateObject(p, q);

        Assert.assertEquals("", pair.getKey());
        Assert.assertEquals( "{\"name\":\"Craig\",\"drinks\":[\"coffee\",\"beer\"]}", getJson(pair.getValue()) );
    }

    @org.junit.Test
    public void nestedObjectSendsPatchForField() {
        JsonObject p = new JsonObject();
        p.addProperty("name", "Alex");
        JsonObject coffee = new JsonObject();
        coffee.addProperty("name", "Latte");
        coffee.addProperty("price", 5);
        p.add("favouriteCoffee", coffee);

        JsonObject q = new JsonObject();
        q.addProperty("name", "Alex");
        JsonObject coffee1 = new JsonObject();
        coffee1.addProperty("name", "Flat White");
        coffee1.addProperty("price", 5);
        q.add("favouriteCoffee", coffee1);

        Pair pair = comparer.getUpdateObject(p, q);

        Assert.assertEquals("favouriteCoffee.name", pair.getKey());
        Assert.assertEquals("\"Flat White\"", pair.getValue().toString());
    }

    @org.junit.Test
    public void nestedObjectSendsPatchForObject() {
        JsonObject p = new JsonObject();
        p.addProperty("name", "Alex");
        JsonObject coffee = new JsonObject();
        coffee.addProperty("name", "Latte");
        coffee.addProperty("price", 5);
        p.add("favouriteCoffee", coffee);

        JsonObject q = new JsonObject();
        q.addProperty("name", "Alex");
        JsonObject coffee1 = new JsonObject();
        coffee1.addProperty("name", "Flat White");
        coffee1.addProperty("price", 6);
        q.add("favouriteCoffee", coffee1);

        Pair pair = comparer.getUpdateObject(p, q);

        Assert.assertEquals("favouriteCoffee", pair.getKey());
        Assert.assertEquals( "{\"name\":\"Flat White\",\"price\":6}", getJson(pair.getValue()) );
    }

    private String getJson( Object obj ) {
        return gson.toJson( obj );
    }
}

