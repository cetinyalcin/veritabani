import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.ArrayAdapter;
import android.widget.EditText;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TableLayout;
import android.widget.TableRow;
import android.widget.TextView;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class SQLiteManager extends SQLiteOpenHelper {
    private static final String DB_NAME = "app_database.db";
    private static final int DB_VERSION = 1;
    private static final String TAG = "SQLiteManager";

    public SQLiteManager(Context context) {
        super(context, DB_NAME, null, DB_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.d(TAG, "Veritabanı oluşturuldu: " + DB_NAME);
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        Log.d(TAG, "Veritabanı güncellendi: " + oldVersion + " -> " + newVersion);
    }

    // Tablo oluştur
    public void createTable(String tableName, Map<String, String> columns) {
        SQLiteDatabase db = getWritableDatabase();
        StringBuilder sql = new StringBuilder("CREATE TABLE IF NOT EXISTS " + tableName + " (");
        for (Map.Entry<String, String> column : columns.entrySet()) {
            sql.append(column.getKey()).append(" ").append(column.getValue()).append(", ");
        }
        sql.setLength(sql.length() - 2); // Son virgülü kaldır
        sql.append(")");

        try {
            db.beginTransaction();
            db.execSQL(sql.toString());
            db.setTransactionSuccessful();
            Log.d(TAG, "Tablo oluşturuldu: " + tableName);
        } catch (Exception e) {
            Log.e(TAG, "Tablo oluşturma hatası: " + e.getMessage());
            throw new RuntimeException("Tablo oluşturulamadı", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Veri ekleme
    public void insertData(String tableName, Map<String, Object> data) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof String) {
                values.put(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                values.put(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Double) {
                values.put(entry.getKey(), (Double) entry.getValue());
            } // Diğer veri türleri için ekleme yapılabilir
        }

        try {
            db.beginTransaction();
            db.insertOrThrow(tableName, null, values);
            db.setTransactionSuccessful();
            Log.d(TAG, "Veri eklendi: " + tableName);
        } catch (Exception e) {
            Log.e(TAG, "Veri ekleme hatası: " + e.getMessage());
            throw new RuntimeException("Veri eklenemedi", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Veri güncelleme
    public void updateData(String tableName, Map<String, Object> data, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();
        ContentValues values = new ContentValues();

        for (Map.Entry<String, Object> entry : data.entrySet()) {
            if (entry.getValue() instanceof String) {
                values.put(entry.getKey(), (String) entry.getValue());
            } else if (entry.getValue() instanceof Integer) {
                values.put(entry.getKey(), (Integer) entry.getValue());
            } else if (entry.getValue() instanceof Double) {
                values.put(entry.getKey(), (Double) entry.getValue());
            }
        }

        try {
            db.beginTransaction();
            db.update(tableName, values, whereClause, whereArgs);
            db.setTransactionSuccessful();
            Log.d(TAG, "Veri güncellendi: " + tableName);
        } catch (Exception e) {
            Log.e(TAG, "Veri güncelleme hatası: " + e.getMessage());
            throw new RuntimeException("Veri güncellenemedi", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Veri silme
    public void deleteData(String tableName, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getWritableDatabase();

        try {
            db.beginTransaction();
            db.delete(tableName, whereClause, whereArgs);
            db.setTransactionSuccessful();
            Log.d(TAG, "Veri silindi: " + tableName);
        } catch (Exception e) {
            Log.e(TAG, "Veri silme hatası: " + e.getMessage());
            throw new RuntimeException("Veri silinemedi", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Sütun ekleme
    public void addColumn(String tableName, String columnName, String columnType) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "ALTER TABLE " + tableName + " ADD " + columnName + " " + columnType;

        try {
            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            Log.d(TAG, "Sütun eklendi: " + tableName + " -> " + columnName);
        } catch (Exception e) {
            Log.e(TAG, "Sütun ekleme hatası: " + e.getMessage());
            throw new RuntimeException("Sütun eklenemedi", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Tabloyu silme
    public void dropTable(String tableName) {
        SQLiteDatabase db = getWritableDatabase();
        String sql = "DROP TABLE IF EXISTS " + tableName;

        try {
            db.beginTransaction();
            db.execSQL(sql);
            db.setTransactionSuccessful();
            Log.d(TAG, "Tablo silindi: " + tableName);
        } catch (Exception e) {
            Log.e(TAG, "Tablo silme hatası: " + e.getMessage());
            throw new RuntimeException("Tablo silinemedi", e);
        } finally {
            db.endTransaction();
            db.close();
        }
    }

    // Tüm tabloları listele
    public List<String> getAllTables() {
        List<String> tables = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT name FROM sqlite_master WHERE type='table' AND name NOT LIKE 'sqlite_%'", null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    tables.add(cursor.getString(0));
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Tüm tablolar listelendi: " + tables.size());
            return tables;
        } catch (Exception e) {
            Log.e(TAG, "Tablo listeleme hatası: " + e.getMessage());
            throw new RuntimeException("Tablolar listelenemedi", e);
        } finally {
            cursor.close();
            db.close();
        }
    }

    // Tablonun sütunlarını listele
    public List<String> getColumns(String tableName) {
        List<String> columns = new ArrayList<>();
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("PRAGMA table_info(" + tableName + ")", null);

        try {
            if (cursor.moveToFirst()) {
                do {
                    columns.add(cursor.getString(cursor.getColumnIndexOrThrow("name")));
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "Sütunlar listelendi: " + tableName + " -> " + columns.size());
            return columns;
        } catch (Exception e) {
            Log.e(TAG, "Sütun listeleme hatası: " + e.getMessage());
            throw new RuntimeException("Sütunlar listelenemedi", e);
        } finally {
            cursor.close();
            db.close();
        }
    }

    // Spinner (Dropbox) doldurma
    public void fillSpinner(Spinner spinner, Context context, String tableName, String columnName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + columnName + " FROM " + tableName, null);
        List<String> items = new ArrayList<>();

        try {
            if (cursor.moveToFirst()) {
                do {
                    items.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)));
                } while (cursor.moveToNext());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_spinner_item, items);
            adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
            spinner.setAdapter(adapter);
            Log.d(TAG, "Spinner dolduruldu: " + tableName + " -> " + columnName);
        } catch (Exception e) {
            Log.e(TAG, "Spinner doldurma hatası: " + e.getMessage());
            throw new RuntimeException("Spinner doldurulamadı", e);
        } finally {
            cursor.close();
            db.close();
        }
    }

    // ListView doldurma
    public void fillListView(ListView listView, Context context, String tableName, String columnName) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + columnName + " FROM " + tableName, null);
        List<String> items = new ArrayList<>();

        try {
            if (cursor.moveToFirst()) {
                do {
                    items.add(cursor.getString(cursor.getColumnIndexOrThrow(columnName)));
                } while (cursor.moveToNext());
            }
            ArrayAdapter<String> adapter = new ArrayAdapter<>(context, android.R.layout.simple_list_item_1, items);
            listView.setAdapter(adapter);
            Log.d(TAG, "ListView dolduruldu: " + tableName + " -> " + columnName);
        } catch (Exception e) {
            Log.e(TAG, "ListView doldurma hatası: " + e.getMessage());
            throw new RuntimeException("ListView doldurulamadı", e);
        } finally {
            cursor.close();
            db.close();
        }
    }

    // TableLayout doldurma (filtreli sütunlar)
    public void fillTableLayout(TableLayout tableLayout, Context context, String tableName, List<String> selectedColumns) {
        SQLiteDatabase db = getReadableDatabase();
        String sql = "SELECT " + String.join(", ", selectedColumns) + " FROM " + tableName;
        Cursor cursor = db.rawQuery(sql, null);

        try {
            // Başlık satırı ekle
            TableRow headerRow = new TableRow(context);
            for (String column : selectedColumns) {
                TextView header = new TextView(context);
                header.setText(column);
                header.setPadding(8, 8, 8, 8);
                headerRow.addView(header);
            }
            tableLayout.addView(headerRow);

            // Veri satırlarını ekle
            if (cursor.moveToFirst()) {
                do {
                    TableRow row = new TableRow(context);
                    for (String column : selectedColumns) {
                        TextView cell = new TextView(context);
                        cell.setText(cursor.getString(cursor.getColumnIndexOrThrow(column)));
                        cell.setPadding(8, 8, 8, 8);
                        row.addView(cell);
                    }
                    tableLayout.addView(row);
                } while (cursor.moveToNext());
            }
            Log.d(TAG, "TableLayout dolduruldu: " + tableName);
        } catch (Exception e) {
            Log.e(TAG, "TableLayout doldurma hatası: " + e.getMessage());
            throw new RuntimeException("TableLayout doldurulamadı", e);
        } finally {
            cursor.close();
            db.close();
        }
    }

    // TextView doldurma
    public void fillTextView(TextView textView, String tableName, String columnName, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + columnName + " FROM " + tableName + " WHERE " + whereClause, whereArgs);

        try {
            if (cursor.moveToFirst()) {
                textView.setText(cursor.getString(cursor.getColumnIndexOrThrow(columnName)));
            }
            Log.d(TAG, "TextView dolduruldu: " + tableName + " -> " + columnName);
        } catch (Exception e) {
            Log.e(TAG, "TextView doldurma hatası: " + e.getMessage());
            throw new RuntimeException("TextView doldurulamadı", e);
        } finally {
            cursor.close();
            db.close();
        }
    }

    // EditText doldurma
    public void fillEditText(EditText editText, String tableName, String columnName, String whereClause, String[] whereArgs) {
        SQLiteDatabase db = getReadableDatabase();
        Cursor cursor = db.rawQuery("SELECT " + columnName + " FROM " + tableName + " WHERE " + whereClause, whereArgs);

        try {
            if (cursor.moveToFirst()) {
                editText.setText(cursor.getString(cursor.getColumnIndexOrThrow(columnName)));
            }
            Log.d(TAG, "EditText dolduruldu: " + tableName + " -> " + columnName);
        } catch (Exception e) {
            Log.e(TAG, "EditText doldurma hatası: " + e.getMessage());
            throw new RuntimeException("EditText doldurulamadı", e);
        } finally {
            cursor.close();
            db.close();
        }
    }
}