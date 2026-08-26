package com.example.yummyplan.utills;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.yummyplan.model.Meal;
import com.example.yummyplan.model.User;

import java.util.ArrayList;
import java.util.List;

// هاد الكلاس هو الي بتعامل من خلاله مع الداتا بيز وبكتب فيه كل العمليات الي بدي اياها من الداتا بيز يعني هو كلاس واحد لكل البرنامج مش زي الأدابتر بتكرر لكل ريسايكل فيو
public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String db_name = "YummyPlan.db";
    private static String user_table = "Users_table";
    private static String meals_table = "meals_table";
    private static String user_meals_table = "user_meals_table";
    private static String weekly_plan_table = "weekly_plan_table";
    private static final int version_code = 6;
    Context context;

    public DatabaseHelper(@Nullable Context context) {
        super(context, db_name, null, version_code);
        this.context = context;
    }

    @Override
    public void onCreate(SQLiteDatabase sqLiteDatabase) {
        String query = " CREATE TABLE " + user_table + "( ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                " ,NAME TEXT, EMAIL  TEXT UNIQUE, PASSWORD TEXT, BIRTH_DATE  TEXT, ROLE TEXT, USER_IMG TEXT);";

        sqLiteDatabase.execSQL(query);


        String queryMeals = " CREATE TABLE " + meals_table + "( ID INTEGER PRIMARY KEY AUTOINCREMENT" +
                " ,TITLE TEXT, CATEGORY TEXT, DIET_TYPE TEXT, INGREDIENTS TEXT, INSTRUCTIONS TEXT" +
                " ,COOK_TIME INTEGER, CALORIES INTEGER, PROTEIN INTEGER, CARBS INTEGER, IMAGE_PATH TEXT);";

        sqLiteDatabase.execSQL(queryMeals);

        String queryUserMeals = " CREATE TABLE " + user_meals_table + "( USER_ID INTEGER, MEAL_ID INTEGER," +
                " PRIMARY KEY (USER_ID, MEAL_ID), FOREIGN KEY (USER_ID) REFERENCES " + user_table + "(ID) ON DELETE CASCADE, " +
                " FOREIGN KEY (MEAL_ID) REFERENCES " + meals_table + "(ID) ON DELETE CASCADE);";

        sqLiteDatabase.execSQL(queryUserMeals);

        String queryWeeklyPlan = " CREATE TABLE " + weekly_plan_table + "( USER_ID INTEGER, MEAL_ID INTEGER, " +
                "DAY_NAME TEXT, CATEGORY_NAME TEXT,PRIMARY KEY (USER_ID, MEAL_ID, DAY_NAME, CATEGORY_NAME), " +
                "FOREIGN KEY (USER_ID) REFERENCES " + user_table + "(ID) ON DELETE CASCADE, " +
                "FOREIGN KEY (MEAL_ID) REFERENCES " + meals_table + "(ID) ON DELETE CASCADE);";

        sqLiteDatabase.execSQL(queryWeeklyPlan);

    }

    @Override
    public void onUpgrade(SQLiteDatabase sqLiteDatabase, int i, int i1) {
        // هان بتحذف كلشي
        String query = "DROP TABLE IF EXISTS " + user_table;
        sqLiteDatabase.execSQL(query);

        String queryMeals = "DROP TABLE IF EXISTS " + meals_table;
        sqLiteDatabase.execSQL(queryMeals);

        String queryUserMeals = "DROP TABLE IF EXISTS " + user_meals_table;
        sqLiteDatabase.execSQL(queryUserMeals);

        String queryWeeklyPlan = "DROP TABLE IF EXISTS " + weekly_plan_table;
        sqLiteDatabase.execSQL(queryWeeklyPlan);

        // هان بترجع تبني كلشي
        onCreate(sqLiteDatabase);
    }

    // دالة إضافة مستخدم جديد في ال  Sign Up
    public long insertUser(User user) {
        // دالة insert يبقى أنا قاعدة بكتب على الداتا بيز
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("NAME", user.getFullName());
        cv.put("EMAIL", user.getEmail());
        cv.put("PASSWORD", user.getPassword());
        cv.put("BIRTH_DATE", user.getBirthDate());
        cv.put("ROLE", user.getRole());
        cv.put("USER_IMG", user.getUser_img());

        long result = db.insert(user_table, null, cv);

        if (result != -1) {
            Toast.makeText(context, "Register Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Register Failed", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    //هاد دالة التحقق من تسجيل الدخول Login وبترجعلي أوبجكت لليوزر الي سجل لو صار تطابق
    public User checkUserLogin(String email, String password) {
        // هان بدي أتحقق يبقى بدي أقرا بيانات
        SQLiteDatabase db = this.getReadableDatabase();

        // هاد استعلام بدور عالمستخدم بناء على الايميل و الباسوورد الي بدخلهم
        Cursor c = db.rawQuery("SELECT * FROM " + user_table + " WHERE EMAIL = ? AND PASSWORD = ?", new String[]{email, password});

        User user = null;
        // هاد معناها حرك المؤشر عند اول صف نتيجة طلع من الاستعلام
        if (c.moveToNext()) {
            //  عملت مؤشر عشان يأشرلي عالقيم الي بدي اياها وكل مؤشر بعطيهى رقم وبقله يأشر على عشان يخزنلي اياها في اوبجكت
            int id = c.getInt(0);
            String name = c.getString(1);
            String u_email = c.getString(2);
            String pass = c.getString(3);
            String birth = c.getString(4);
            String role = c.getString(5);
            String img = c.getString(6);

            user = new User(id, name, u_email, pass, birth, role, img);
        }
        c.close();
        return user;
    }

    //دالة بتجيب كلمة المرور المرتبطة بالايميل
    public String getPasswordByEmail(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        String password = null;

        // استعلام بجيب الباسوورد بناءً على الإيميل
        Cursor cursor = db.rawQuery(" SELECT PASSWORD FROM "+ user_table +" WHERE EMAIL = ?", new String[]{email});
         if (cursor.moveToFirst()) {
                password = cursor.getString(0);
            }

         cursor.close();
         return password;
    }

    // دالة حساب عدد المستخدمين الي بستخدموا التطبيق
    public int getTotalUsersCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM " + user_table, null);
        int count = 0;
        // هاد تشيك بيروح للصف تبع الجدول الي طلعته دالة ال count في الاستعلام والمكون من صف واحد في عدد المستخدمين الكلي
        if (cursor.moveToFirst()) {
            count = cursor.getInt(0);
        }
        cursor.close();
        return count;
    }

    // دالة حساب عدد الوجبات الي ضافها الادمن
    public int getTotalMealsCount() {
        SQLiteDatabase db = this.getReadableDatabase();
        int count = 0;
        // هاد الtry عشان لو ما كان ضايف شي لسا ما ينهار البرنامج
        try {
            Cursor cursor = db.rawQuery("SELECT COUNT(*) FROM meals_table ", null);
            if (cursor.moveToFirst()) {
                count = cursor.getInt(0);
            }
            cursor.close();
        } catch (Exception e) {
            count = 0;
        }
        return count;
    }

    // هاد دالة بتجبلي معلومات اخر خمس مستخدمين عشان اعرضهم في الداشبورد تبعت الادمن
    public List<User> getRecentUsers() {
        List<User> recent_user_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // هاد الاستعلام الي بدي أطبقه على قاعدة البيانات عشان يجبلي المستخدمين ويرتبهم من الاخر للأول
        Cursor cursor = db.rawQuery("SELECT * FROM " + user_table + " ORDER BY ID DESC LIMIT 5", null);

        // هاد تشيك عشان تروح لأول صف وتشوف هل في جواته بيانات
        if (cursor.moveToFirst()) {
            //هان حلقة لان هاخد كذا نتيجة فبدي حلقة تمر عليهم
            do {
                //هان بجيب القيم عن طريق المؤشر
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String u_email = cursor.getString(2);
                String pass = cursor.getString(3);
                String birth = cursor.getString(4);
                String role = cursor.getString(5);
                String img = cursor.getString(6);
                // بخزنها في اوبجكت وبضيفه للست
                recent_user_list.add(new User(id, name, u_email, pass, birth, role, img));
                // لو لقيت صف بعده وبردو فيه بيانات انتقله وجيبه
            } while (cursor.moveToNext());
        }
        cursor.close();
        return recent_user_list;
    }

    // دالة بتجيب كل المستخدمين المسجلين
    public List<User> getAllUsers() {
        List<User> all_users_list = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // استعلام بجيب كل المستخدمين ومرتبين من الأحدث للأقدم
        Cursor cursor = db.rawQuery("SELECT * FROM " + user_table + " ORDER BY ID DESC", null);

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String name = cursor.getString(1);
                String u_email = cursor.getString(2);
                String pass = cursor.getString(3);
                String birth = cursor.getString(4);
                String role = cursor.getString(5);
                String img = cursor.getString(6);

                all_users_list.add(new User(id, name, u_email, pass, birth, role, img));
            } while (cursor.moveToNext());
        }
        cursor.close();
        return all_users_list;
    }

    // دالة بتجيبلي بيانات مستخدم عن طريق ال Id تبعه عشان أعرض بياناته في البروفايل تبعه او اخد بعض بياناته لاعرشها في شاشات تانية
    public User getUserById(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT * FROM " + user_table + " WHERE ID = ?", new String[]{String.valueOf(userId)});

        User user = null;
        if (c.moveToNext()) {
            int id = c.getInt(0);
            String name = c.getString(1);
            String email = c.getString(2);
            String pass = c.getString(3);
            String birth = c.getString(4);
            String role = c.getString(5);
            String img = c.getString(6);

            user = new User(id, name, email, pass, birth, role, img);
        }
        c.close();
        return user;
    }

    // دالة تحديث بيانات المستخدم
    public int updateUserProfile(User user) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        cv.put("NAME", user.getFullName());
        cv.put("EMAIL", user.getEmail());
        cv.put("PASSWORD", user.getPassword());
        cv.put("BIRTH_DATE", user.getBirthDate());
        cv.put("ROLE", user.getRole());
        cv.put("USER_IMG", user.getUser_img());

        //عملنا مصفوفة هان لأن لغة جافا ودالة الupdate بيجبرونا على انه نمرر قيم الشروط داخل مصفوفة نصوص ليييييش؟!!
        // لأن أحيانا ممكن يكون عندي كذا شرط فالمصفوفة بتسهلي أستقبل عدد من الشروط وهان عملت تحويل من رقم لنص لأن المصفوفة ما بتقبل الا نصوص
        int result = db.update(user_table, cv, "ID = ?", new String[]{String.valueOf(user.getId())});
        if (result > 0) {
            Toast.makeText(context, "Profile Updated Successfully", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Profile Update Failed", Toast.LENGTH_SHORT).show();
        }
        return result;
    }


    // ============================================================================= دوال جدول الوجبات =============================================================================
    // ==============================================================================================================================================================================

    // دالة إضافة وجبة جديدة
    public long insertMeal(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues cv = new ContentValues();

        cv.put("TITLE", meal.getTitle());
        cv.put("CATEGORY", meal.getCategory());
        cv.put("DIET_TYPE", meal.getDietType());
        cv.put("INGREDIENTS", meal.getIngredients());
        cv.put("INSTRUCTIONS", meal.getInstructions());
        cv.put("COOK_TIME", meal.getCookTime());
        cv.put("CALORIES", meal.getCalories());
        cv.put("PROTEIN", meal.getProtein());
        cv.put("CARBS", meal.getCarbs());
        cv.put("IMAGE_PATH", meal.getImagePath());

        // هاد بتاخد البيانات الي جهزناها وبتخزنها في صف جديد قي جدول البيانات وبترجعلي رقم الصف لو فشلت هترجع 1-
        long result = db.insert(meals_table, null, cv);
        if (result != -1) {
            Toast.makeText(context, "Meal Insert Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Meal Insert Failed", Toast.LENGTH_SHORT).show();
        }
        return result;
    }

    // دالة بتجيبلي بيانات وجبة محددة عن طريق ال Id للتعديل او الحذف
    public Meal getMealById(int id) {
        SQLiteDatabase db = this.getReadableDatabase();

        Cursor c = db.rawQuery("SELECT * FROM " + meals_table + " WHERE ID = ?", new String[]{String.valueOf(id)});
        Meal meal = null;
        if (c.moveToNext()) {
            int mealId = c.getInt(0);
            String title = c.getString(1);
            String category = c.getString(2);
            String dietType = c.getString(3);
            String ingredients = c.getString(4);
            String instructions = c.getString(5);
            int cookTime = c.getInt(6);
            int calories = c.getInt(7);
            int protein = c.getInt(8);
            int carbs = c.getInt(9);
            String imagePath = c.getString(10);

            meal = new Meal(mealId, title, category, dietType, ingredients, instructions, cookTime, calories, protein, carbs, imagePath);
        }
        c.close();
        return meal;
    }


    // دالة تعديل وجبة
    public int updateMeal(Meal meal) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues cv = new ContentValues();

        // بنحط البيانات الجديدة الي بدنا نعدلها
        cv.put("TITLE", meal.getTitle());
        cv.put("CATEGORY", meal.getCategory());
        cv.put("DIET_TYPE", meal.getDietType());
        cv.put("INGREDIENTS", meal.getIngredients());
        cv.put("INSTRUCTIONS", meal.getInstructions());
        cv.put("COOK_TIME", meal.getCookTime());
        cv.put("CALORIES", meal.getCalories());
        cv.put("PROTEIN", meal.getProtein());
        cv.put("CARBS", meal.getCarbs());
        cv.put("IMAGE_PATH", meal.getImagePath());

        // هاد الاستعلام تبع تعديل صف معين بالجدول بناء على ال Id
        int result = db.update(meals_table, cv, "ID = ?", new String[]{String.valueOf(meal.getId())});

        if (result > 0) {
            Toast.makeText(context, "Meal Update Success", Toast.LENGTH_SHORT).show();
        } else {
            Toast.makeText(context, "Meal Update Failed", Toast.LENGTH_SHORT).show();
        }

        return result;
    }


    // دالة حذف وجبة
    public boolean deleteMeal(int id) {
        SQLiteDatabase db = this.getWritableDatabase();

        // هاد استعلام الحذف
        int result = db.delete(meals_table, "ID = ?", new String[]{String.valueOf(id)});

        if (result > 0) {
            Toast.makeText(context, "Meal Deleted Successfully", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Delete Failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // دالة بتجيب كل الوجبات
    public ArrayList<Meal> getAllMeals() {

        ArrayList<Meal> mealsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // استعلام بجيب كل الوجبات
        Cursor cursor = db.rawQuery("SELECT * FROM " + meals_table , null);

        if (cursor.moveToFirst()) {
            do {
                // بنقرأ البيانات من المؤشر
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String category = cursor.getString(2);
                String dietType = cursor.getString(3);
                String ingredients = cursor.getString(4);
                String instructions = cursor.getString(5);
                int cookTime = cursor.getInt(6);
                int calories = cursor.getInt(7);
                int protein = cursor.getInt(8);
                int carbs = cursor.getInt(9);
                String imagePath = cursor.getString(10);

                // إنشاء كائن الوجبة وإضافته للقائمة
                Meal meal = new Meal(id, title, category, dietType, ingredients, instructions, cookTime, calories, protein, carbs, imagePath);
                mealsList.add(meal);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mealsList;
    }

    // دالة بتجيب الوجبات حسب الcategory تبعها
    public ArrayList<Meal> getMealsByCategory(String category) {
        ArrayList<Meal> mealsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // استعلام بجيب الوجبة المرتبطة بالـ CATEGORY مثلاً: Breakfast
        Cursor cursor = db.rawQuery("SELECT * FROM " + meals_table + " WHERE CATEGORY = ?", new String[]{category});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String mealCategory = cursor.getString(2);
                String dietType = cursor.getString(3);
                String ingredients = cursor.getString(4);
                String instructions = cursor.getString(5);
                int cookTime = cursor.getInt(6);
                int calories = cursor.getInt(7);
                int protein = cursor.getInt(8);
                int carbs = cursor.getInt(9);
                String imagePath = cursor.getString(10);

                Meal meal = new Meal(id, title, mealCategory, dietType, ingredients, instructions, cookTime, calories, protein, carbs, imagePath);
                mealsList.add(meal);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mealsList;
    }


    // ============================================================================= دوال جدول الربط بين المستخدم والوجبات =============================================================================
    // ==============================================================================================================================================================================


    // دالة بتجيب الوجبات حسب نوع الرجيم (DIET_TYPE)
    public ArrayList<Meal> getMealsByDietType(String dietType) {
        ArrayList<Meal> mealsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // استعلام بجيب الوجبات حسب نوع الدايت
        Cursor cursor = db.rawQuery("SELECT * FROM " + meals_table + " WHERE DIET_TYPE = ?", new String[]{dietType});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String category = cursor.getString(2);
                String dType = cursor.getString(3);
                String ingredients = cursor.getString(4);
                String instructions = cursor.getString(5);
                int cookTime = cursor.getInt(6);
                int calories = cursor.getInt(7);
                int protein = cursor.getInt(8);
                int carbs = cursor.getInt(9);
                String imagePath = cursor.getString(10);

                Meal meal = new Meal(id, title, category, dType, ingredients, instructions, cookTime, calories, protein, carbs, imagePath);
                mealsList.add(meal);
            } while (cursor.moveToNext());
        }
        cursor.close();
        return mealsList;
    }

    // ============================================================================= دوال جدول الربط تبع الخطة الاسبوعية   =============================================================================
    // ==============================================================================================================================================================================


    // دالة بتربط المستخدم بالوجبة باليوم بالتصنيف تبعها
    public boolean insertToPlan(int userId, int mealId, String day, String category) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();

        values.put("USER_ID", userId);
        values.put("MEAL_ID", mealId);
        values.put("DAY_NAME", day);
        values.put("CATEGORY_NAME", category);

        // هاد السطر الي بدخلي البيانات عالجدول
        long result = db.insert("weekly_plan_table", null, values);

        if (result != -1) {
            Toast.makeText(context, "Added to Plan Success", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Added to Plan Failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // دالة بتجيب كل الوجبات المضافة لخطة مستخدم معين في يوم محدد
    public ArrayList<Meal> getMealsByUserAndDay(int userId, String dayName) {
        ArrayList<Meal> mealsList = new ArrayList<>();
        SQLiteDatabase db = this.getReadableDatabase();

        // استعلام لربط جدول الوجبات بجدول الخطة الأسبوعية بناءً على الـ ID للمستخدم واليوم
        String query = "SELECT m.*, wp.CATEGORY_NAME AS plan_category FROM " + meals_table + " m " +
                "INNER JOIN " + weekly_plan_table + " wp ON m.ID = wp.MEAL_ID " +
                "WHERE wp.USER_ID = ? AND wp.DAY_NAME = ?";

        Cursor cursor = db.rawQuery(query, new String[]{String.valueOf(userId), dayName});

        if (cursor.moveToFirst()) {
            do {
                int id = cursor.getInt(0);
                String title = cursor.getString(1);
                String category = cursor.getString(11);
                String dietType = cursor.getString(3);
                String ingredients = cursor.getString(4);
                String instructions = cursor.getString(5);
                int cookTime = cursor.getInt(6);
                int calories = cursor.getInt(7);
                int protein = cursor.getInt(8);
                int carbs = cursor.getInt(9);
                String imagePath = cursor.getString(10);

                Meal meal = new Meal(id, title, category, dietType, ingredients, instructions, cookTime, calories, protein, carbs, imagePath);
                mealsList.add(meal);

            } while (cursor.moveToNext());
        }
        cursor.close();
        return mealsList;
    }

    // دالة حذف الوجبة من الخطة الأسبوعية لمستخدم معين في يوم وتصنيف معين
    public boolean deleteMealFromPlan(int userId, int mealId, String day, String category) {
        SQLiteDatabase db = this.getWritableDatabase();

        // هان بنحدد شروط الحذف بالزبط عشان نحذف الوجبة من خطة هاد اليوم بس
        int result = db.delete(weekly_plan_table, "USER_ID = ? AND MEAL_ID = ? AND DAY_NAME = ? AND CATEGORY_NAME = ?",
                new String[]{String.valueOf(userId), String.valueOf(mealId), day, category});

        if (result > 0) {
            Toast.makeText(context, "Removed from Plan Successfully", Toast.LENGTH_SHORT).show();
            return true;
        } else {
            Toast.makeText(context, "Remove Failed", Toast.LENGTH_SHORT).show();
            return false;
        }
    }

    // دالة حذف الخطة الأسبوعية كاملة لمستخدم معين
    public boolean clearFullWeeklyPlan(int userId) {
        SQLiteDatabase db = this.getWritableDatabase();
        int result = db.delete(weekly_plan_table, "USER_ID = ?", new String[]{String.valueOf(userId)});
        if (result > 0) {
            Toast.makeText(context, "Weekly Plan Cleared", Toast.LENGTH_SHORT).show();
            return true;
        }
        return false;
    }

}






