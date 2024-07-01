package com.example.rickandmortycomposeapp

import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.content.IntentFilter
import android.graphics.Bitmap
import android.graphics.ColorMatrix
import android.graphics.ColorMatrixColorFilter
import android.graphics.Paint
import android.net.ConnectivityManager
import android.net.NetworkCapabilities
import android.os.Build
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.grid.GridCells
import androidx.compose.foundation.lazy.grid.LazyVerticalGrid
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Button
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.draw.scale
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.IntOffset
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.compose.ui.zIndex
import androidx.core.graphics.applyCanvas
import androidx.core.graphics.createBitmap
import androidx.lifecycle.compose.collectAsStateWithLifecycle
import coil.compose.AsyncImage
import coil.imageLoader
import coil.request.CachePolicy
import coil.request.ImageRequest
import coil.transform.CircleCropTransformation
import coil.transform.Transformation
import org.koin.androidx.viewmodel.ext.android.viewModel
import com.example.rickandmortycomposeapp.ui.theme.RickAndMortyComposeAppTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContent {
            RickAndMortyComposeAppTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val viewModel: CharacterViewModel by viewModel()
                    val charactersItems by viewModel.items.collectAsStateWithLifecycle()
                    LaunchedEffect(key1 = Unit, block = {
                        viewModel.loadItems()
                    })

                    ItemGridScreen(charactersItems)
                }
            }
        }
    }
}

@Composable
fun ItemGridScreen(charactersItems: List<Character>) {
    ItemGrid(items = charactersItems)
}

@Composable
fun ItemGrid(items: List<Character>) {
    LazyVerticalGrid(
        columns = GridCells.Fixed(2), // Dos columnas
        contentPadding = PaddingValues(8.dp)
    ) {
        items(items.size) { index ->
            ItemCard(item = items[index])
        }
    }
}

@Composable
fun ItemCard(item: Character) {
    val context = LocalContext.current.applicationContext as App

    Card(
        shape = RoundedCornerShape(8.dp),
        elevation = CardDefaults.cardElevation(
            defaultElevation = 4.dp
        ),
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(4.dp)
        ) {
            val placeholder = R.drawable.bg_placeholder

            /* val imageRequest = ImageRequest.Builder(context)
                 .data(item.imageUrl)
                 .memoryCacheKey(item.imageUrl)
                 .diskCacheKey(item.imageUrl)
                 .placeholder(placeholder)
                 .error(placeholder)
                 .fallback(placeholder)
                 .diskCachePolicy(CachePolicy.ENABLED)
                 .memoryCachePolicy(CachePolicy.ENABLED)
                 .transformations(GrayscaleTransformation())
                 .transformations(RoundedCornersTransformation(30f, 30f, 30f, 30f))
                 .build()

             AsyncImage(
                 model = imageRequest,
                 modifier = Modifier
                     .size(100.dp)
                     .padding(4.dp),
                 contentScale = ContentScale.Crop,
                 contentDescription = null,
             )*/

            ReloadableImage(item.imageUrl)


            // LoadImageFromUrl(item.imageUrl, context)
            // LoadImageFromDrawable(imageFromDrawable = placeholder, context)
            Spacer(modifier = Modifier.height(8.dp))
            Text(text = item.name, fontSize = 16.sp)
        }
    }
}

@Composable
fun ReloadableImage(imageUrl: String) {
    var reloadTrigger by remember { mutableStateOf(0) }

    val context = LocalContext.current.applicationContext as App
    val imageLoader = context.imageLoader

    Column {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(imageUrl)
                //.networkCachePolicy(CachePolicy.DISABLED) // Deshabilitar caché de red para forzar recarga
                //.diskCachePolicy(CachePolicy.ENABLED) // Deshabilitar caché en disco para forzar recarga
                .setParameter("reloadTrigger", reloadTrigger) // Parámetro para forzar recarga
                .crossfade(true)
                .build(),
            imageLoader = imageLoader,
            contentDescription = "Reloadable Image",
            modifier = Modifier
                .fillMaxWidth()
                .height(200.dp),
            contentScale = ContentScale.Crop
        )

        Button(onClick = { prefetchImage(context, imageUrl) }) {
            Text("Recargar Imagen")
        }
    }
}

fun prefetchImage(context: Context, url: String) {
    val request = ImageRequest.Builder(context)
        .data(url)
        .build()

    CoroutineScope(Dispatchers.IO).launch {
        context.imageLoader.execute(request)
    }
}


fun isWifiConnected(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}


@Composable
fun LoadImageFromUrl(imageFromUrl: String, context: App) {
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageFromUrl)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LoadImageFromDrawable(imageFromDrawable: Int, context: App) {
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageFromDrawable)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LoadImageWithCircleCrop(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .transformations(CircleCropTransformation())
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LoadImageWithGrayFilter(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .transformations(GrayscaleTransformation())
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LoadImageWithSize(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .size(300, 300)
            .crossfade(true)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .width(50.dp)
            .height(50.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun ScaleTransitionImage(imageUrl: String) {
    var isLoaded by remember { mutableStateOf(false) }
    val scale by animateFloatAsState(if (isLoaded) 1f else 0.8f)

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .listener(
                onSuccess = { _, _ ->
                    isLoaded = true
                }
            )
            .build(),
        contentDescription = "Example Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .scale(scale),
        contentScale = ContentScale.Crop
    )
}


fun isInternetAvailable(context: Context): Boolean {
    val connectivityManager =
        context.getSystemService(Context.CONNECTIVITY_SERVICE) as ConnectivityManager

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
        val network = connectivityManager.activeNetwork ?: return false
        val activeNetwork = connectivityManager.getNetworkCapabilities(network) ?: return false
        return activeNetwork.hasCapability(NetworkCapabilities.NET_CAPABILITY_INTERNET)
    } else {
        val networkInfo = connectivityManager.activeNetworkInfo ?: return false
        return networkInfo.isConnected
    }
}

@Composable
fun LoadImageWithCache(imageUrl: String) {
    val context = LocalContext.current.applicationContext as App
    val imageLoader = context.imageLoader
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .networkCachePolicy(CachePolicy.ENABLED) // Habilita la caché de red
            .diskCachePolicy(CachePolicy.ENABLED) // Habilita la caché en disco
            .crossfade(500)
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        imageLoader = imageLoader,
        contentScale = ContentScale.Crop
    )
}

@Composable
fun CustomCrossFadeImage(imageUrl: String) {
    val context = LocalContext.current.applicationContext as App
    val imageLoader = context.imageLoader
    AsyncImage(
        model = ImageRequest.Builder(context)
            .data(imageUrl)
            .crossfade(1000) // Duración de la transición en milisegundos
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        imageLoader = imageLoader,
        contentScale = ContentScale.Crop
    )
}

@Composable
fun RotateTransitionImage(imageUrl: String) {
    var isLoaded by remember { mutableStateOf(false) }
    val rotation by animateFloatAsState(if (isLoaded) 0f else 360f, label = "")

    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .listener(
                onSuccess = { _, _ ->
                    isLoaded = true
                }
            )
            .build(),
        contentDescription = null,
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp)
            .rotate(rotation),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LoadImageWithCrossFade(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .crossfade(true)
            .build(),
        contentDescription = "Example Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}

@Composable
fun LoadImageWithListeners(imageUrl: String) {
    AsyncImage(
        model = ImageRequest.Builder(LocalContext.current)
            .data(imageUrl)
            .listener(
                onStart = { request ->
                    Log.i("TAG", "onStart")
                },
                onSuccess = { request, result ->
                    Log.i("TAG", "onSuccess")
                },
                onError = { request, throwable ->
                    Log.i("TAG", "onError")
                }
            )
            .crossfade(true)
            .build(),
        contentDescription = "Example Image",
        modifier = Modifier
            .fillMaxWidth()
            .height(200.dp),
        contentScale = ContentScale.Crop
    )
}

class GrayscaleTransformation() : Transformation {

    override val cacheKey: String = GrayscaleTransformation::class.java.name

    override fun equals(other: Any?) = other is GrayscaleTransformation

    override fun hashCode() = javaClass.hashCode()

    override fun toString() = "GrayscaleTransformation()"
    override suspend fun transform(input: Bitmap, size: coil.size.Size): Bitmap {
        val paint = Paint(Paint.ANTI_ALIAS_FLAG or Paint.FILTER_BITMAP_FLAG)
        paint.colorFilter = ColorMatrixColorFilter(ColorMatrix().apply { setSaturation(0f) })

        val output = createBitmap(input.width, input.height, input.config)
        output.applyCanvas {
            drawBitmap(input, 0f, 0f, paint)
        }

        return output
    }
}




