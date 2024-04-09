package com.recipeapp.ui;

import static org.mockito.Mockito.*;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.PrintStream;
import java.util.ArrayList;
import java.util.Arrays;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import com.recipeapp.datahandler.DataHandler;
import com.recipeapp.model.Ingredient;
import com.recipeapp.model.Recipe;

@ExtendWith(MockitoExtension.class)
public class RecipeUITest {

    private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
    private final PrintStream originalOut = System.out;
    @Mock
    private DataHandler mockDataHandler;

    @BeforeEach
    public void setUp() {
        System.setOut(new PrintStream(outContent));
    }

    @AfterEach
    public void restore() {
        System.setOut(originalOut);
        System.setIn(System.in);
    }

    @Test
    public void testDisplayMenuOption1DisplaysRecipes() throws Exception {
        // ユーザー入力を模擬（オプション1を選択し、その後アプリケーションを終了）
        String input = "1\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // モックの振る舞いを設定（レシピリストを返す）
        when(mockDataHandler.getMode()).thenReturn("CSV");
        ArrayList<Recipe> recipes = new ArrayList<>(Arrays.asList(
            new Recipe("Tomato Soup", new ArrayList<>(Arrays.asList(new Ingredient("Tomatoes"), new Ingredient("Onion")))),
            new Recipe("Chicken Curry", new ArrayList<>(Arrays.asList(new Ingredient("Chicken"), new Ingredient("Curry Powder"))))
        ));
        when(mockDataHandler.readData()).thenReturn(recipes);

        RecipeUI ui = new RecipeUI(mockDataHandler);
        ui.displayMenu();

        // 出力内容を検証
        Assertions.assertThat(outContent.toString()).contains("Current mode: CSV");
        Assertions.assertThat(outContent.toString()).contains("Tomato Soup");
        Assertions.assertThat(outContent.toString()).contains("Chicken Curry");
    }

    @Test
    public void testAddNewRecipe() throws Exception {
        // レシピ名と材料の入力を模擬し、最後に'done'を入力
        String input = "2\nPancakes\nFlour\nMilk\nEggs\ndone\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // データハンドラーのモックを設定して、writeDataメソッドが呼び出されたことを検証
        Mockito.doNothing().when(mockDataHandler).writeData(Mockito.any(Recipe.class));

        RecipeUI ui = new RecipeUI(mockDataHandler);
        ui.displayMenu();

        // モックのwriteDataメソッドが正しく呼び出されたことを検証
        Mockito.verify(mockDataHandler, Mockito.times(1)).writeData(Mockito.any(Recipe.class));
        Assertions.assertThat(outContent.toString()).contains("Recipe added successfully.");
    }

    @Test
    public void testSearchRecipe() throws Exception {
        // 検索クエリを模擬
        String input = "3\nname=Pancakes\n4\n";
        System.setIn(new ByteArrayInputStream(input.getBytes()));

        // 検索結果のモックを設定
        ArrayList<Recipe> searchResults = new ArrayList<>(Arrays.asList(
            new Recipe("Pancakes", new ArrayList<>(Arrays.asList(new Ingredient("Flour"), new Ingredient("Milk"), new Ingredient("Eggs"))))
        ));
        when(mockDataHandler.searchData("name=Pancakes")).thenReturn(searchResults);

        RecipeUI ui = new RecipeUI(mockDataHandler);
        ui.displayMenu();

        // 検索結果が表示されていることを検証
        Assertions.assertThat(outContent.toString()).contains("Matching Recipes:");
        Assertions.assertThat(outContent.toString()).contains("Pancakes");
    }


}
