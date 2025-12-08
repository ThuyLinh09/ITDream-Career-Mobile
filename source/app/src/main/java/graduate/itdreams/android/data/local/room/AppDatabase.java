package graduate.itdreams.android.data.local.room;

import androidx.annotation.NonNull;
import androidx.room.Database;
import androidx.room.RoomDatabase;
import androidx.room.migration.AutoMigrationSpec;
import androidx.sqlite.db.SupportSQLiteDatabase;

import graduate.itdreams.android.data.model.room.UserEntity;

@Database(entities = {
//        OrderEntity.class,
        UserEntity.class},
        version = 1,
        autoMigrations = {
//                @AutoMigration(from = 1, to = 2),
//                @AutoMigration(from = 2, to = 3,spec = AppDatabase.TwoThree.class),

        },
        exportSchema = true)
public abstract class AppDatabase extends RoomDatabase {

//    public abstract DbOrderDao getOrderDao();
    public abstract DbUserDao getUserDao();

    public static class AppDatabaseMigration implements AutoMigrationSpec {
        @Override
        public void onPostMigrate(@NonNull SupportSQLiteDatabase db) {
            AutoMigrationSpec.super.onPostMigrate(db);
        }
    }
}
