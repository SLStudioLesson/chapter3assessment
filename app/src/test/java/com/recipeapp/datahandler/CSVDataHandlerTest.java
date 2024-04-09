package com.recipeapp.datahandler;

import static org.junit.jupiter.api.Assertions.*;

import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import com.recipeapp.model.Ingredient;
import com.recipeapp.model.Recipe;

class CSVDataHandlerTest {

    private CSVDataHandler handler;
    private final String testFilePath = "src/test/resources/test_recipes.csv";

    @BeforeEach
    void setUp() throws IOException {
        System.out.println(System.getProperty("user.dir"));
        handler = new CSVDataHandler(testFilePath);
        // テスト用のファイルを準備
        try (BufferedWriter writer = Files.newBufferedWriter(Paths.get(testFilePath))) {
            writer.write("Tomato Soup,Tomatoes, Onion, Garlic, Vegetable Stock\n");
            writer.write("Chicken Curry,Chicken, Curry Powder, Onion, Garlic, Ginger\n");
        }
    }

    @AfterEach
    void tearDown() throws IOException {
        // テスト終了後にファイルを削除
        Files.deleteIfExists(Paths.get(testFilePath));
    }

    @Test
    void testGetMode() {
        assertEquals("CSV", handler.getMode(), "Mode should be CSV.");
    }

    @Test
    void testReadData() throws IOException {
        List<Recipe> recipes = handler.readData();
        assertEquals(2, recipes.size(), "Should read 2 recipes.");
        assertEquals("Tomato Soup", recipes.get(0).getName(), "First recipe should be Tomato Soup.");
    }

    @Test
    void testWriteData() throws IOException {
        Recipe newRecipe = new Recipe("Pancakes", new ArrayList<>(List.of(new Ingredient("Flour"), new Ingredient("Milk"))));
        handler.writeData(newRecipe);

        List<Recipe> recipes = handler.readData();
        assertTrue(recipes.stream().anyMatch(recipe -> recipe.getName().equals("Pancakes")));
    }

    @Test
    void testSearchData() throws IOException {
        List<Recipe> foundRecipes = handler.searchData("name=Soup");
        assertEquals(1, foundRecipes.size(), "Should find 1 recipe with 'Soup' in the name.");
        assertEquals("Tomato Soup", foundRecipes.get(0).getName(), "Found recipe should be Tomato Soup.");
    }
}
