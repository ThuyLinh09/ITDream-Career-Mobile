package graduate.itdreams.android.ui.main.taskdetail;

import android.content.ContentValues;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Environment;
import android.provider.MediaStore;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.ActionBarDrawerToggle;
import androidx.core.view.GravityCompat;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.navigation.NavigationView;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.text.Normalizer;
import java.util.List;

import eu.davidea.flexibleadapter.databinding.BR;
import graduate.itdreams.android.R;
import graduate.itdreams.android.data.model.api.response.task.SubTaskResponse;
import graduate.itdreams.android.data.model.api.response.task.TaskResponse;
import graduate.itdreams.android.data.socket.dto.Message;
import graduate.itdreams.android.databinding.ActivityPdfBinding;
import graduate.itdreams.android.databinding.ActivityTaskDetailBinding;
import graduate.itdreams.android.di.component.ActivityComponent;
import graduate.itdreams.android.ui.base.activity.BaseActivity;

public class PdfActivity extends BaseActivity<ActivityPdfBinding,PdfViewModel> {

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        viewBinding.setA(this);
        viewBinding.setVm(viewModel);
        String pdfUrl = getIntent().getStringExtra("url_pdf");
        String titlePdf = getIntent().getStringExtra("title_pdf");
        if (pdfUrl != null) {
            viewModel.loadDocument(pdfUrl);
        }

        // Quan sát dữ liệu PDF (khi tải xong)
        viewModel.getPdfData().observe(this, bytes -> {
            if (bytes != null && bytes.length > 0) {
                Log.d("PdfActivity", "PDF byte size: " + bytes.length);

                // Tùy chọn: lưu file tạm để test
                try {
                    File tempFile = new File(getCacheDir(), "temp.pdf");
                    FileOutputStream fos = new FileOutputStream(tempFile);
                    fos.write(bytes);
                    fos.close();
                    Log.d("PdfActivity", "Temp PDF path: " + tempFile.getAbsolutePath());
                } catch (IOException e) {
                    e.printStackTrace();
                }

                // Load PDF trực tiếp từ bytes
                viewBinding.pdfView.post(() -> {
                    viewBinding.pdfView.fromBytes(bytes)
                            .enableSwipe(true)
                            .swipeHorizontal(false)
                            .enableDoubletap(true)
                            .load();
                });

                viewBinding.btnDownload.setOnClickListener(v ->{
                    savePdfToDownloads(toFileName(titlePdf), bytes);
                });
            } else {
                Log.w("PdfActivity", "PDF dữ liệu trống hoặc null");
            }
        });
        viewBinding.btnClose.setOnClickListener(v ->{
            finish();
        });
    }
    public static String toFileName(String input) {
        if (input == null) return "default";

        // 1. Chuẩn hóa về NFC
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);

        // 2. Loại bỏ dấu tiếng Việt
        String noAccent = normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");

        // 3. Thay khoảng trắng hoặc ký tự lạ bằng dấu gạch dưới
        noAccent = noAccent.replaceAll("[^a-zA-Z0-9]+", "_");

        // 4. Xóa "_" ở đầu/cuối nếu có
        noAccent = noAccent.replaceAll("^_+|_+$", "");

        return noAccent + ".pdf";
    }
    private void savePdfToDownloads(String fileName, byte[] bytes) {
        if (bytes == null || bytes.length == 0) {
            Toast.makeText(this, "Không có dữ liệu để tải", Toast.LENGTH_SHORT).show();
            return;
        }

        try {
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.Q) {
                // ⭐ API 29+ (Android 10 trở lên): Dùng MediaStore, không cần permission
                ContentValues values = new ContentValues();
                values.put(MediaStore.Downloads.DISPLAY_NAME, fileName);
                values.put(MediaStore.Downloads.MIME_TYPE, "application/pdf");
                values.put(MediaStore.Downloads.RELATIVE_PATH, Environment.DIRECTORY_DOWNLOADS);

                Uri uri = getContentResolver().insert(
                        MediaStore.Downloads.EXTERNAL_CONTENT_URI, values
                );

                if (uri != null) {
                    try (OutputStream out = getContentResolver().openOutputStream(uri)) {
                        out.write(bytes);
                    }
                    Toast.makeText(this, "Đã lưu (API29+): " + fileName, Toast.LENGTH_LONG).show();
                }

            } else {
                // ⭐ API 24–28: Lưu trực tiếp vào /Downloads → CẦN permission WRITE_EXTERNAL_STORAGE
                File downloads = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_DOWNLOADS);
                if (!downloads.exists()) downloads.mkdirs();

                File outFile = new File(downloads, fileName);

                FileOutputStream fos = new FileOutputStream(outFile);
                fos.write(bytes);
                fos.close();

                // Quét vào MediaStore để hiện trong ứng dụng Files
                Intent scanIntent = new Intent(Intent.ACTION_MEDIA_SCANNER_SCAN_FILE);
                scanIntent.setData(Uri.fromFile(outFile));
                sendBroadcast(scanIntent);

                Toast.makeText(this, "Đã lưu (API<29): " + fileName, Toast.LENGTH_LONG).show();
            }

        } catch (Exception e) {
            e.printStackTrace();
            Toast.makeText(this, "Lỗi khi lưu file", Toast.LENGTH_SHORT).show();
        }
    }


    @Override
    public int getLayoutId() {
        return R.layout.activity_pdf;
    }

    @Override
    public int getBindingVariable() {
        return BR.vm;
    }

    @Override
    public void performDependencyInjection(ActivityComponent buildComponent) {
        buildComponent.inject(this);
    }

    @Override
    public void onMessageReceived(Message message) {

    }

    @Override
    public void onConnectionClosed() {

    }

    @Override
    public void onConnectionClosing() {

    }
}
