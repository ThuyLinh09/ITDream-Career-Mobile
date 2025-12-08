package graduate.itdreams.android.di.module;

import android.app.Application;
import android.content.Context;
import android.os.Environment;

import androidx.annotation.NonNull;
import androidx.room.Room;
import androidx.room.RoomDatabase;
import androidx.sqlite.db.SupportSQLiteDatabase;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.File;
import java.io.IOException;

import javax.inject.Named;
import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;
import graduate.itdreams.android.data.remote.UploadApiService;
import graduate.itdreams.android.di.qualifier.UploadApiInfo;
import hu.akarnokd.rxjava3.retrofit.RxJava3CallAdapterFactory;
import graduate.itdreams.android.BuildConfig;
import graduate.itdreams.android.constant.Constants;
import graduate.itdreams.android.data.AppRepository;
import graduate.itdreams.android.data.Repository;
import graduate.itdreams.android.data.local.prefs.AppPreferencesService;
import graduate.itdreams.android.data.local.prefs.PreferencesService;
import graduate.itdreams.android.data.local.room.AppDatabase;
import graduate.itdreams.android.data.local.room.AppDbService;
import graduate.itdreams.android.data.local.room.RoomService;
import graduate.itdreams.android.data.remote.ApiService;
import graduate.itdreams.android.data.remote.AuthInterceptor;
import graduate.itdreams.android.di.qualifier.ApiInfo;
import graduate.itdreams.android.di.qualifier.DatabaseInfo;
import graduate.itdreams.android.di.qualifier.PreferenceInfo;
import okhttp3.OkHttpClient;
import okhttp3.logging.HttpLoggingInterceptor;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;
import timber.log.Timber;

@Module
public class AppModule {

    @Provides
    @ApiInfo
    @Singleton
    String provideBaseUrl() {
        return BuildConfig.BASE_URL;
    }

    @Provides
    @UploadApiInfo
    @Singleton
    String provideUrlFile() {
        return BuildConfig.URL_FILE;
    }

    @Provides
    @Singleton
    Context provideContext(Application application) {
        return application;
    }

    @Provides
    @Singleton
    Gson provideGson() {
        return new GsonBuilder().excludeFieldsWithoutExposeAnnotation().create();
    }


    @Provides
    @PreferenceInfo
    @Singleton
    String providePreferenceName() {
        return Constants.PREF_NAME;
    }


    @Provides
    @Singleton
    PreferencesService providePreferencesService(AppPreferencesService appPreferencesService) {
        return appPreferencesService;
    }

    @Provides
    @Singleton
    AuthInterceptor proviceAuthInterceptor(PreferencesService appPreferencesService, Application application){
        return new AuthInterceptor(appPreferencesService, application);
    }

    // Config OK HTTP
    @Provides
    @Singleton
    public OkHttpClient getClient(AuthInterceptor authInterceptor) {
        HttpLoggingInterceptor loggingInterceptor = new HttpLoggingInterceptor();
        if (BuildConfig.DEBUG) {
            loggingInterceptor.level(HttpLoggingInterceptor.Level.BODY);
        } else {
            loggingInterceptor.level(HttpLoggingInterceptor.Level.NONE);
        }
        return new OkHttpClient.Builder()
                .addInterceptor(authInterceptor)
                .addInterceptor(loggingInterceptor)
                .build();
    }

    // Create Retrofit
    @Provides
    @Singleton
    public Retrofit retrofitBuild(OkHttpClient client, @ApiInfo String url) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    @Provides
    @Singleton
    @Named("UploadRetrofit")
    public Retrofit provideUploadRetrofit(OkHttpClient client, @UploadApiInfo String url) {
        return new Retrofit.Builder()
                .client(client)
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .addCallAdapterFactory(RxJava3CallAdapterFactory.create())
                .build();
    }

    // Create api service
    @Provides
    @Singleton
    public ApiService apiService(Retrofit retrofit) {
        return retrofit.create(ApiService.class);
    }

    @Provides
    @Singleton
    public UploadApiService uploadApiService(@Named("UploadRetrofit") Retrofit retrofit) {
        return retrofit.create(UploadApiService.class);
    }

    @Provides
    @Singleton
    public Repository provideDataManager(AppRepository appRepository) {
        return appRepository;
    }

    @Provides
    @DatabaseInfo
    @Singleton
    String provideDatabaseName() {
        return Constants.DB_NAME;
    }

    @Provides
    @Singleton
    AppDatabase provideAppDatabase(@DatabaseInfo String dbName, Context context) {
        File div = new File(Environment.getExternalStorageDirectory(),
                "do_not_delete");
        if (!div.exists()) {
            div.mkdirs();
        }
        String dbPath;
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.R) {
            boolean grant = Environment.isExternalStorageManager();
            if (grant){
                dbPath = Environment.getExternalStorageDirectory().getAbsolutePath()+"/do_not_delete";
            } else {
                dbPath = context.getExternalFilesDir(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath();
            }
        } else {
            dbPath = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOCUMENTS).getAbsolutePath()+"/do_not_delete";
        }

        File databasePath = new File(dbPath, dbName);

        // Check if the database file exists
        if (!databasePath.exists()) {
            try {
                // Create a new empty database file
                Timber.d("🟩"+String.valueOf(databasePath.createNewFile()));
                Timber.d("🟩CREATE NEW FILE");
            } catch (IOException e) {
                Timber.d(e);
            }
        }
        return Room.databaseBuilder(context, AppDatabase.class, databasePath.getAbsolutePath())
//                .createFromAsset("mvvm_db.db")
                .addCallback(new RoomDatabase.Callback() {
                    @Override
                    public void onCreate(@NonNull SupportSQLiteDatabase db) {
                        super.onCreate(db);
                        Timber.d("🟩"+db.getPath());
                    }

                    @Override
                    public void onOpen(@NonNull SupportSQLiteDatabase db) {
                        super.onOpen(db);
                        Timber.d("🟩"+db.getPath());
                    }
                })
                .setJournalMode(RoomDatabase.JournalMode.TRUNCATE)
                .build();
    }
    @Provides
    @Singleton
    RoomService provideDbService(AppDbService roomService) {
        return roomService;
    }

}
