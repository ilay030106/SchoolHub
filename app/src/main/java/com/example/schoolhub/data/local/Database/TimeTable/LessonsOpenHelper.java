package com.example.schoolhub.data.local.Database.TimeTable;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import com.example.schoolhub.data.local.model.TimeTable.Lesson;
import com.example.schoolhub.data.local.model.TimeTable.Teacher;

import java.util.ArrayList;
import java.util.List;

public class LessonsOpenHelper extends SQLiteOpenHelper {
    private static LessonsOpenHelper instance;

    public static final String DATABASENAME = "LessonsDB";//שם מסד נתונים
    public static final String TABLE_LESSONS = "lessons";//שם הטבלה
    public static final String TABLE_TEACHERS = "teachers";//שם הטבלה
    public static final int DATABASEVERSION = 1;

    public static final String COLUMN_ID = "ID";//מפתח ראשי - מספור אוטומטי
    public static final String COLUMN_CLASS_NAME = "CLASS_NAME";
    public static final String COLUMN_TEACHER_NAME = "TEACHER_NAME";
    public static final String COLUMN_ROOM_NUM = "ROOM_NUM";
    public static final String COLUMN_DAY = "DAY";
    public static final String COLUMN_START_TIME = "START_TIME";
    public static final String COLUMN_END_TIME = "END_TIME";
    public static final String COLUMN_COLOR = "COLOR";
    public static final String COLUMN_TEACHER_EMAIL = "TEACHER_EMAIL";
    private static final String CREATE_TABLE_ALL_LESSONS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_LESSONS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_CLASS_NAME + " VARCHAR, "
            + COLUMN_TEACHER_NAME + " VARCHAR,"
            + COLUMN_ROOM_NUM + " VARCHAR,"
            + COLUMN_DAY + " VARCHAR,"
            + COLUMN_START_TIME + " VARCHAR, "
            + COLUMN_END_TIME + " VARCHAR, "
            + COLUMN_COLOR + " VARCHAR " + ");";
    String[] allLessonsColumns = {COLUMN_ID, COLUMN_CLASS_NAME, COLUMN_TEACHER_NAME, COLUMN_ROOM_NUM,
            COLUMN_DAY, COLUMN_START_TIME, COLUMN_END_TIME, COLUMN_COLOR};

    private static final String CREATE_TABLE_ALL_TEACHERS = "CREATE TABLE IF NOT EXISTS " +
            TABLE_TEACHERS + "(" + COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT,"
            + COLUMN_TEACHER_NAME + " VARCHAR,"
            + COLUMN_TEACHER_EMAIL + " VARCHAR " + ");";

    String[] allTeachersColumns = {COLUMN_ID, COLUMN_TEACHER_NAME, COLUMN_TEACHER_EMAIL};

    SQLiteDatabase database;

    private LessonsOpenHelper(Context context) {//פעולה בונה
        super(context, DATABASENAME, null, DATABASEVERSION);
    }
    public static synchronized LessonsOpenHelper getInstance(Context context) {
        if (instance == null) {
            instance = new LessonsOpenHelper(context.getApplicationContext());
        }
        return instance;
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_ALL_LESSONS);
        db.execSQL(CREATE_TABLE_ALL_TEACHERS);
        Log.i("data", "Table Spendings created");
    }

    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_LESSONS);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_TEACHERS);
        onCreate(db);
    }

    public void open() {//פתיחת מסד נתונים
        database = this.getWritableDatabase();
        Log.i("data", "Database connection open");
    }

    public void createLesson(Lesson lesson) {// DB
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, lesson.getName());
        values.put(COLUMN_TEACHER_NAME, lesson.getTeacher().getName());
        values.put(COLUMN_ROOM_NUM, lesson.getRoomNum());
        values.put(COLUMN_DAY, lesson.getDay());
        values.put(COLUMN_START_TIME, lesson.getStartTime());
        values.put(COLUMN_END_TIME, lesson.getEndTime());
        values.put(COLUMN_COLOR, lesson.getColor());

        if (lesson.getTeacher().getEmail() == null) {
            createTeacher(lesson.getTeacher());
        }


        long insertId = database.insert(LessonsOpenHelper.TABLE_LESSONS, null, values);
        Log.i("data", "Lesson " + insertId + "insert to database");
        lesson.setId(insertId);
    }

    public Lesson updateLesson(Lesson lesson) {// DB
        ContentValues values = new ContentValues();
        values.put(COLUMN_CLASS_NAME, lesson.getName());
        values.put(COLUMN_TEACHER_NAME, lesson.getTeacher().getName());
        values.put(COLUMN_ROOM_NUM, lesson.getRoomNum());
        values.put(COLUMN_DAY, lesson.getDay());
        values.put(COLUMN_START_TIME, lesson.getStartTime());
        values.put(COLUMN_END_TIME, lesson.getEndTime());
        values.put(COLUMN_COLOR, lesson.getColor());

        if (lesson.getTeacher().getEmail() == null) {
            ContentValues teacherValues = new ContentValues();
            teacherValues.put(COLUMN_TEACHER_NAME, lesson.getTeacher().getName());
            teacherValues.put(COLUMN_TEACHER_EMAIL, lesson.getTeacher().getEmail());
            long teacherId = database.insert(LessonsOpenHelper.TABLE_TEACHERS, null, teacherValues);
            Log.i("data", "Teacher " + teacherId + "insert to database");
        }


        long updateId = database.update(LessonsOpenHelper.TABLE_LESSONS, values, COLUMN_ID + "=" + lesson.getId(), null);
        Log.i("data", "lesson " + updateId + " updated in database");
        lesson.setId(updateId);
        return lesson;
    }

    public long deleteLessonById(long id) {
        long deleteid = database.delete(LessonsOpenHelper.TABLE_LESSONS, COLUMN_ID + "=" + id, null);
        Log.i("data", "lesson " + deleteid + "deleted from database");
        return deleteid;
    }

    public boolean isLessonOverlapping(Lesson newLesson) {
        String query = "SELECT * FROM " + TABLE_LESSONS + " WHERE " + COLUMN_DAY + " = ? AND " +
                "((" + COLUMN_START_TIME + " < ? AND " + COLUMN_END_TIME + " > ?) OR " +
                "(" + COLUMN_START_TIME + " < ? AND " + COLUMN_END_TIME + " > ?) OR " +
                "(" + COLUMN_START_TIME + " >= ? AND " + COLUMN_END_TIME + " <= ?))";

        String[] selectionArgs = new String[]{
                newLesson.getDay(),
                newLesson.getEndTime(), newLesson.getStartTime(),
                newLesson.getEndTime(), newLesson.getStartTime(),
                newLesson.getStartTime(), newLesson.getEndTime()
        };

        Cursor cursor = database.rawQuery(query, selectionArgs);
        boolean isOverlapping = cursor.getCount() > 0;
        cursor.close();
        return isOverlapping;
    }

    public List<Lesson> getLessonsForDay(String day) {
        List<Lesson> lessons = new ArrayList<>();
        String[] columns = {
                COLUMN_ID, COLUMN_CLASS_NAME, COLUMN_TEACHER_NAME, COLUMN_ROOM_NUM,
                COLUMN_DAY, COLUMN_START_TIME, COLUMN_END_TIME, COLUMN_COLOR
        };
        String selection = COLUMN_DAY + " = ?";
        String[] selectionArgs = {day};
        this.open();

        Cursor cursor = database.query(TABLE_LESSONS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Lesson lesson = new Lesson();
                lesson.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)));
                lesson.setTeacher(new Teacher(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_NAME))));
                lesson.setRoomNum(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM_NUM)));
                lesson.setDay(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY)));
                lesson.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
                lesson.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
                lesson.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOR)));
                lessons.add(lesson);
            }
            cursor.close();
        }
        return lessons;
    }

    // Retrieve a Lesson by ID
    public Lesson getLessonById(long id) {
        Lesson lesson = null;
        String[] columns = {
                COLUMN_ID, COLUMN_CLASS_NAME, COLUMN_TEACHER_NAME, COLUMN_ROOM_NUM,
                COLUMN_DAY, COLUMN_START_TIME, COLUMN_END_TIME, COLUMN_COLOR
        };
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        this.open();

        Cursor cursor = database.query(TABLE_LESSONS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                lesson = new Lesson();
                lesson.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)));
                lesson.setTeacher(new Teacher(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_NAME))));
                lesson.setRoomNum(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM_NUM)));
                lesson.setDay(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY)));
                lesson.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
                lesson.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
                lesson.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOR)));
            }
            cursor.close();
        }
        return lesson;
    }

    // Retrieve All Lessons
    public List<Lesson> getAllLessons() {
        List<Lesson> lessons = new ArrayList<>();
        String[] columns = {
                COLUMN_ID, COLUMN_CLASS_NAME, COLUMN_TEACHER_NAME, COLUMN_ROOM_NUM,
                COLUMN_DAY, COLUMN_START_TIME, COLUMN_END_TIME, COLUMN_COLOR
        };
        this.open();

        Cursor cursor = database.query(TABLE_LESSONS, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Lesson lesson = new Lesson();
                lesson.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                lesson.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_CLASS_NAME)));
                lesson.setTeacher(new Teacher(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_NAME))));
                lesson.setRoomNum(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_ROOM_NUM)));
                lesson.setDay(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_DAY)));
                lesson.setStartTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_START_TIME)));
                lesson.setEndTime(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_END_TIME)));
                lesson.setColor(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_COLOR)));
                lessons.add(lesson);
            }
            cursor.close();
        }
        return lessons;
    }
    // Create a new Teacher
    public void createTeacher(Teacher teacher) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER_NAME, teacher.getName());
        values.put(COLUMN_TEACHER_EMAIL, teacher.getEmail());

        long insertId = database.insert(TABLE_TEACHERS, null, values);
        Log.i("data", "Teacher " + insertId + " inserted into database");
        teacher.setId(insertId);
    }

    // Update an existing Teacher
    public void updateTeacher(Teacher teacher) {
        ContentValues values = new ContentValues();
        values.put(COLUMN_TEACHER_NAME, teacher.getName());
        values.put(COLUMN_TEACHER_EMAIL, teacher.getEmail());

        long updateId = database.update(TABLE_TEACHERS, values, COLUMN_ID + "=" + teacher.getId(), null);
        Log.i("data", "Teacher " + updateId + " updated in database");
        teacher.setId(updateId);
    }

    // Delete a Teacher by ID
    public long deleteTeacherById(long id) {
        long deleteId = database.delete(TABLE_TEACHERS, COLUMN_ID + "=" + id, null);
        Log.i("data", "Teacher " + deleteId + " deleted from database");
        return deleteId;
    }

    // Retrieve a Teacher by ID
    public Teacher getTeacherById(long id) {
        Teacher teacher = null;
        String[] columns = {COLUMN_ID, COLUMN_TEACHER_NAME, COLUMN_TEACHER_EMAIL};
        String selection = COLUMN_ID + " = ?";
        String[] selectionArgs = {String.valueOf(id)};
        this.open();

        Cursor cursor = database.query(TABLE_TEACHERS, columns, selection, selectionArgs, null, null, null);

        if (cursor != null) {
            if (cursor.moveToFirst()) {
                teacher = new Teacher();
                teacher.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                teacher.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_NAME)));
                teacher.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_EMAIL)));
            }
            cursor.close();
        }
        return teacher;
    }

    // Retrieve all Teachers
    public List<Teacher> getAllTeachers() {
        List<Teacher> teachers = new ArrayList<>();
        String[] columns = {COLUMN_ID, COLUMN_TEACHER_NAME, COLUMN_TEACHER_EMAIL};
        this.open();

        Cursor cursor = database.query(TABLE_TEACHERS, columns, null, null, null, null, null);

        if (cursor != null) {
            while (cursor.moveToNext()) {
                Teacher teacher = new Teacher();
                teacher.setId(cursor.getLong(cursor.getColumnIndexOrThrow(COLUMN_ID)));
                teacher.setName(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_NAME)));
                teacher.setEmail(cursor.getString(cursor.getColumnIndexOrThrow(COLUMN_TEACHER_EMAIL)));
                teachers.add(teacher);
            }
            cursor.close();
        }
        return teachers;
    }
}
