package app.titova.vkr.view.app_activity

import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import androidx.activity.compose.rememberLauncherForActivityResult
import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.asImageBitmap
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import androidx.navigation.NavHostController
import app.titova.vkr.util.AiModel
import app.titova.vkr.view.preview.CameraPreview
import java.io.InputStream

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun AiScreen(navController: NavHostController, modelPath: String) {
    var query by remember { mutableStateOf("") }
    var response by remember { mutableStateOf("Здесь появится ответ искусственного интеллекта.") }
    var capturedImage by remember { mutableStateOf<Bitmap?>(null) }
    var showCamera by remember { mutableStateOf(false) }
    var showGallery by remember { mutableStateOf(false) }
    val context = LocalContext.current

    // Инициализация модели AI
    val aiModel = remember { AiModel(context, modelPath) }

    val galleryLauncher = rememberLauncherForActivityResult(ActivityResultContracts.GetContent()) { uri: Uri? ->
        uri?.let {
            val inputStream: InputStream? = context.contentResolver.openInputStream(it)
            val bitmap = BitmapFactory.decodeStream(inputStream)
            capturedImage = bitmap
            response = aiModel.runInference(bitmap)
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Brush.verticalGradient(
                colors = listOf(Color(0xFF4CAF50), Color(0xFF81C784))
            )),
        contentAlignment = Alignment.TopCenter
    ) {
        Column(
            modifier = Modifier
                .padding(24.dp)
                .background(Color.White, shape = RoundedCornerShape(16.dp))
                .padding(24.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = "AI Взаимодействие",
                style = MaterialTheme.typography.headlineMedium.copy(
                    fontWeight = FontWeight.Bold,
                    color = Color(0xFF388E3C),
                    fontSize = 28.sp
                ),
                modifier = Modifier.padding(bottom = 16.dp)
            )

            Button(
                onClick = { showCamera = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp)
                    .padding(bottom = 8.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF388E3C)
                )
            ) {
                Text("Открыть камеру", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Button(
                onClick = { showGallery = true },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(50.dp),
                shape = RoundedCornerShape(12.dp),
                colors = ButtonDefaults.buttonColors(
                    containerColor = Color(0xFF66BB6A)
                )
            ) {
                Text("Выбрать фотографию из галереи", color = Color.White, fontWeight = FontWeight.Bold, fontSize = 16.sp)
            }

            Spacer(modifier = Modifier.height(16.dp))

            // Отображение изображения, если оно было захвачено или выбрано
            capturedImage?.let { bitmap ->
                Image(
                    bitmap = bitmap.asImageBitmap(),
                    contentDescription = null,
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(200.dp)
                        .border(2.dp, Color(0xFF388E3C), shape = RoundedCornerShape(8.dp))
                        .padding(8.dp)
                )
            }

            Spacer(modifier = Modifier.height(16.dp))

            Text(
                response,
                style = MaterialTheme.typography.bodyMedium.copy(
                    fontWeight = FontWeight.Medium,
                    color = Color(0xFF2E7D32)
                ),
                modifier = Modifier.padding(top = 16.dp)
            )
        }

        if (showCamera) {
            CameraPreview(
                onImageCaptured = { file ->
                    val bitmap = BitmapFactory.decodeFile(file.absolutePath)
                    capturedImage = bitmap
                    showCamera = false
                    response = aiModel.runInference(bitmap)
                },
                modifier = Modifier
                    .fillMaxSize()
                    .padding(16.dp)
            )
        }

        if (showGallery) {
            galleryLauncher.launch("image/*")
            showGallery = false
        }
    }
}