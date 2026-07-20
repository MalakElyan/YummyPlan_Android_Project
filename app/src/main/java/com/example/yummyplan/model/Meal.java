package com.example.yummyplan.model;

public class Meal {
        private int id;
        private String title;
        private String category;
        private String dietType;
        private String ingredients;
        private String instructions;
        private int cookTime;
        private int calories;
        private int protein;
        private int carbs;
        private String imagePath;

        public Meal() {
        }

        // 2. Constructor كامل لإنشاء وجبة بجميع التفاصيل
        public Meal(int id, String title, String category, String dietType, String ingredients,
                    String instructions, int cookTime, int calories, int protein, int carbs, String imagePath) {
            this.id = id;
            this.title = title;
            this.category = category;
            this.dietType = dietType;
            this.ingredients = ingredients;
            this.instructions = instructions;
            this.cookTime = cookTime;
            this.calories = calories;
            this.protein = protein;
            this.carbs = carbs;
            this.imagePath = imagePath;
        }

    public int getId() {
        return id;
    }

    public String getTitle() {
        return title;
    }

    public String getCategory() {
        return category;
    }

    public String getDietType() {
        return dietType;
    }

    public String getInstructions() {
        return instructions;
    }

    public String getIngredients() {
        return ingredients;
    }

    public int getCookTime() {
        return cookTime;
    }

    public int getCalories() {
        return calories;
    }

    public int getProtein() {
        return protein;
    }

    public int getCarbs() {
        return carbs;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setId(int id) {
        this.id = id;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public void setCategory(String category) {
        this.category = category;
    }

    public void setDietType(String dietType) {
        this.dietType = dietType;
    }

    public void setIngredients(String ingredients) {
        this.ingredients = ingredients;
    }

    public void setInstructions(String instructions) {
        this.instructions = instructions;
    }

    public void setCookTime(int cookTime) {
        this.cookTime = cookTime;
    }

    public void setCalories(int calories) {
        this.calories = calories;
    }

    public void setProtein(int protein) {
        this.protein = protein;
    }

    public void setCarbs(int carbs) {
        this.carbs = carbs;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
